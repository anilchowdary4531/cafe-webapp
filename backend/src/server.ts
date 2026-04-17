import 'dotenv/config';

import cors from '@fastify/cors';
import Fastify from 'fastify';
import { Prisma, PrismaClient } from '@prisma/client';
import nodemailer from 'nodemailer';
import twilio from 'twilio';
import { z } from 'zod';

const prisma = new PrismaClient();
const app = Fastify({ logger: true });

const OTP_EXPIRY_MS = 5 * 60 * 1000;
const OTP_RESEND_COOLDOWN_MS = 30 * 1000;
const smtpPort = Number.parseInt(process.env.SMTP_PORT ?? '587', 10);
const smtpHost = process.env.SMTP_HOST;
const smtpUser = process.env.SMTP_USER;
const smtpPass = process.env.SMTP_PASS;
const emailFrom = process.env.EMAIL_FROM ?? 'Sunset Cafe <no-reply@sunsetcafe.local>';
const otpMode = process.env.OTP_MODE ?? 'demo';
const twilioAccountSid = process.env.TWILIO_ACCOUNT_SID;
const twilioAuthToken = process.env.TWILIO_AUTH_TOKEN;
const twilioPhoneNumber = process.env.TWILIO_PHONE_NUMBER;
const twilioClient = twilioAccountSid && twilioAuthToken ? twilio(twilioAccountSid, twilioAuthToken) : null;
let smtpNotConfiguredLogged = false;
let twilioNotConfiguredLogged = false;

const menuCreateSchema = z.object({
  name: z.string().min(1),
  category: z.string().min(1),
  price: z.number().min(0),
  description: z.string().optional(),
  restricted: z.boolean().optional().default(false)
});

const orderCreateSchema = z.object({
  table: z.string().min(1),
  note: z.string().optional(),
  customerPhone: z.string().regex(/^\d{10,15}$/).optional(),
  customerPhoneMasked: z.string().optional(),
  customerEmail: z.string().email().optional(),
  phoneVerifiedAt: z.string().datetime().optional(),
  items: z.array(z.object({
    menuItemId: z.number().int().optional(),
    name: z.string().min(1),
    qty: z.number().int().positive(),
    price: z.number().min(0).optional(),
    restricted: z.boolean().optional().default(false)
  })).min(1)
});

const requestCreateSchema = z.object({
  table: z.string().min(1),
  type: z.string().min(1),
  note: z.string().min(1),
  items: z.array(z.object({
    id: z.number().int().optional(),
    name: z.string().min(1),
    qty: z.number().int().positive()
  })).optional(),
  customerPhone: z.string().regex(/^\d{10,15}$/).optional(),
  customerPhoneMasked: z.string().optional(),
  phoneVerifiedAt: z.string().datetime().optional()
});

const otpSendSchema = z.object({
  table: z.string().min(1),
  phone: z.string().regex(/^\d{10,15}$/)
});

const otpVerifySchema = z.object({
  table: z.string().min(1),
  phone: z.string().regex(/^\d{10,15}$/),
  code: z.string().regex(/^\d{6}$/)
});

const paymentStatusSchema = z.object({
  status: z.enum(['unpaid', 'paid'])
});

const loginSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1)
});

function asMoney(value: number): Prisma.Decimal {
  return new Prisma.Decimal(value.toFixed(2));
}

function asNumber(value: Prisma.Decimal | null): number {
  return value ? Number(value) : 0;
}

function maskPhoneNumber(phone: string): string {
  if (phone.length <= 4) return phone;
  return `${'x'.repeat(Math.max(4, phone.length - 4))}${phone.slice(-4)}`;
}

function parseId(value: string): number | null {
  const parsed = Number.parseInt(value, 10);
  return Number.isNaN(parsed) ? null : parsed;
}

async function sendOtpViaSms(phone: string, code: string): Promise<boolean> {
  if (otpMode !== 'sms' || !twilioClient || !twilioPhoneNumber) {
    if (!twilioNotConfiguredLogged && otpMode === 'sms') {
      app.log.warn('Twilio is not configured. Using demo OTP instead.');
      twilioNotConfiguredLogged = true;
    }
    return false;
  }

  try {
    await twilioClient.messages.create({
      body: `Your Sunset Cafe OTP is: ${code}\n\nValid for 5 minutes. Do not share with anyone.`,
      from: twilioPhoneNumber,
      to: phone.startsWith('+') ? phone : `+${phone}`
    });
    app.log.info({ phone }, 'OTP sent via SMS');
    return true;
  } catch (error) {
    app.log.error({ error, phone }, 'Failed to send OTP via SMS');
    return false;
  }
}

function getSmtpTransporter() {
  if (!smtpHost || !smtpUser || !smtpPass) {
    if (!smtpNotConfiguredLogged) {
      app.log.warn('SMTP is not configured. Email notifications are disabled.');
      smtpNotConfiguredLogged = true;
    }
    return null;
  }

  return nodemailer.createTransport({
    host: smtpHost,
    port: smtpPort,
    secure: smtpPort === 465,
    auth: {
      user: smtpUser,
      pass: smtpPass
    }
  });
}

async function sendCompletionEmail(order: {
  id: string;
  tableLabel: string;
  total: Prisma.Decimal;
  customerEmail: string;
}): Promise<boolean> {
  const transporter = getSmtpTransporter();
  if (!transporter) return false;

  try {
    const fullOrder = await prisma.order.findUnique({
      where: { id: order.id },
      include: { items: true }
    });

    if (!fullOrder) return false;

    const itemsList = fullOrder.items
      .map((item) => `  • ${item.quantity}x ${item.name} - $${asNumber(item.unitPrice).toFixed(2)}`)
      .join('\n');

    const emailBody = `Hi,

Thank you for ordering at Sunset Cafe!

Your order has been completed and payment has been successfully processed.

═══════════════════════════════════════════════════════════
ORDER DETAILS
═══════════════════════════════════════════════════════════
Order ID: #${fullOrder.id}
Table: ${fullOrder.tableLabel}
Timestamp: ${fullOrder.createdAt.toLocaleString()}

ITEMS ORDERED:
${itemsList}

═══════════════════════════════════════════════════════════
PAYMENT SUMMARY
═══════════════════════════════════════════════════════════
Subtotal: $${asNumber(fullOrder.subtotal).toFixed(2)}
Tax (5%): $${asNumber(fullOrder.tax).toFixed(2)}
───────────────────────────────────────────────────────────
Total Amount Paid: $${asNumber(fullOrder.total).toFixed(2)}

Payment Status: PAID ✓
Paid At: ${fullOrder.paidAt?.toLocaleString() || 'N/A'}

${fullOrder.note ? `Special Notes: ${fullOrder.note}\n` : ''}
═══════════════════════════════════════════════════════════

We truly appreciate your business! We hope you enjoyed your 
meal at Sunset Cafe. Please visit us again soon!

If you have any questions or feedback, please don't hesitate 
to reach out.

Warm regards,
Sunset Cafe Team

---
This is an automated email. Please do not reply directly.`;

    await transporter.sendMail({
      from: emailFrom,
      to: order.customerEmail,
      subject: `Order Confirmed & Paid - Sunset Cafe Order #${order.id}`,
      text: emailBody,
      html: `<pre style="font-family: monospace; white-space: pre-wrap; line-height: 1.6; color: #333; background: #f5f5f5; padding: 16px; border-radius: 8px;">${emailBody.replace(/</g, '&lt;').replace(/>/g, '&gt;')}</pre>`
    });
    return true;
  } catch (error) {
    app.log.error({ error, orderId: order.id }, 'Failed to send completion email');
    return false;
  }
}

async function maybeSendCompletionAndPaymentEmail(orderId: string) {
  const order = await prisma.order.findUnique({ where: { id: orderId } });
  if (!order) return;
  if (!order.customerEmail || order.completionEmailSentAt) return;
  if (order.status !== 'served' || order.paymentStatus !== 'paid') return;

  const sent = await sendCompletionEmail({
    id: order.id,
    tableLabel: order.tableLabel,
    total: order.total,
    customerEmail: order.customerEmail
  });

  if (sent) {
    await prisma.order.update({
      where: { id: order.id },
      data: { completionEmailSentAt: new Date() }
    });
  }
}

app.register(cors, {
  origin: true, // Allow all origins
  credentials: true
});

app.get('/health', async () => ({ ok: true }));

app.post('/api/auth/login', async (request, reply) => {
  const payload = loginSchema.parse(request.body);
  const user = await prisma.user.findUnique({ where: { username: payload.username } });
  if (!user || user.password !== payload.password) {
    reply.code(401);
    return { error: 'Invalid credentials' };
  }
  return { id: user.id, username: user.username, role: user.role };
});

app.get('/api/menu', async () => {
  const menu = await prisma.menuItem.findMany({ orderBy: { id: 'asc' } });
  return menu.map((item) => ({
    id: item.id,
    name: item.name,
    category: item.category,
    price: asNumber(item.price),
    description: item.description,
    restricted: item.restricted
  }));
});

app.post('/api/menu', { preHandler: authenticate }, async (request, reply) => {
  const payload = menuCreateSchema.parse(request.body);
  const created = await prisma.menuItem.create({
    data: {
      ...payload,
      price: asMoney(payload.price)
    }
  });

  reply.code(201);
  return {
    id: created.id,
    name: created.name,
    category: created.category,
    price: asNumber(created.price),
    description: created.description,
    restricted: created.restricted
  };
});

app.delete('/api/menu/:id', { preHandler: authenticate }, async (request, reply) => {
  const id = parseId((request.params as { id: string }).id);
  if (!id) {
    reply.code(400);
    return { error: 'Invalid menu id' };
  }

  await prisma.menuItem.delete({ where: { id } });
  reply.code(204);
  return null;
});

app.get('/api/orders', { preHandler: authenticate }, async (request) => {
  const query = request.query as { table?: string };
  const orders = await prisma.order.findMany({
    where: query.table ? { tableLabel: query.table } : undefined,
    include: { items: true },
    orderBy: { createdAt: 'desc' }
  });

  return orders.map((order) => ({
    id: order.id,
    table: order.tableLabel,
    note: order.note,
    status: order.status,
    paymentStatus: order.paymentStatus,
    paidAt: order.paidAt,
    subtotal: asNumber(order.subtotal),
    tax: asNumber(order.tax),
    total: asNumber(order.total),
    customerPhone: order.customerPhone,
    customerPhoneMasked: order.customerPhoneMasked,
    customerEmail: order.customerEmail,
    phoneVerifiedAt: order.phoneVerifiedAt,
    createdAt: order.createdAt,
    items: order.items.map((item) => ({
      id: item.id,
      menuItemId: item.menuItemId,
      name: item.name,
      qty: item.quantity,
      price: asNumber(item.unitPrice),
      restricted: item.restricted
    }))
  }));
});

app.post('/api/orders', async (request, reply) => {
  const payload = orderCreateSchema.parse(request.body);
  const regularItems = payload.items.filter((item) => !item.restricted);
  const subtotal = regularItems.reduce((sum, item) => sum + (item.price ?? 0) * item.qty, 0);
  const tax = subtotal * 0.05;
  const total = subtotal + tax;

  const data: any = {
    tableLabel: payload.table,
    note: payload.note,
    status: 'received',
    paymentStatus: 'unpaid',
    subtotal: asMoney(subtotal),
    tax: asMoney(tax),
    total: asMoney(total),
    customerEmail: payload.customerEmail || null,
    phoneVerifiedAt: payload.phoneVerifiedAt ? new Date(payload.phoneVerifiedAt) : null,
    items: {
      create: payload.items.map((item) => ({
        menuItemId: item.menuItemId,
        name: item.name,
        quantity: item.qty,
        unitPrice: item.price === undefined ? null : asMoney(item.price),
        restricted: item.restricted
      }))
    }
  };

  if (payload.customerPhone) data.customerPhone = payload.customerPhone;
  if (payload.customerPhoneMasked) data.customerPhoneMasked = payload.customerPhoneMasked;

  const created = await prisma.order.create({
    data,
    include: { items: true }
  });

  reply.code(201);
  return {
    id: created.id,
    table: created.tableLabel,
    status: created.status,
    paymentStatus: created.paymentStatus,
    paidAt: created.paidAt,
    subtotal: asNumber(created.subtotal),
    tax: asNumber(created.tax),
    total: asNumber(created.total),
    customerEmail: created.customerEmail,
    createdAt: created.createdAt,
    items: created.items.map((item) => ({
      id: item.id,
      menuItemId: item.menuItemId,
      name: item.name,
      qty: item.quantity,
      price: asNumber(item.unitPrice),
      restricted: item.restricted
    }))
  };
});

app.patch('/api/orders/:id/status', { preHandler: authenticate }, async (request) => {
  const params = request.params as { id: string };
  const body = z.object({ status: z.enum(['received', 'preparing', 'served']) }).parse(request.body);
  const updated = await prisma.order.update({
    where: { id: params.id },
    data: { status: body.status }
  });
  await maybeSendCompletionAndPaymentEmail(updated.id);
  return updated;
});

app.patch('/api/orders/:id/payment', { preHandler: authenticate }, async (request) => {
  const params = request.params as { id: string };
  const body = paymentStatusSchema.parse(request.body);
  const updated = await prisma.order.update({
    where: { id: params.id },
    data: {
      paymentStatus: body.status,
      paidAt: body.status === 'paid' ? new Date() : null
    }
  });
  await maybeSendCompletionAndPaymentEmail(updated.id);
  return updated;
});

app.get('/api/requests', { preHandler: authenticate }, async () => {
  const requests = await prisma.serviceRequest.findMany({ orderBy: { createdAt: 'desc' } });
  return requests.map((request) => ({
    id: request.id,
    table: request.tableLabel,
    type: request.type,
    note: request.note,
    status: request.status,
    items: request.items,
    customerPhone: request.customerPhone,
    customerPhoneMasked: request.customerPhoneMasked,
    phoneVerifiedAt: request.phoneVerifiedAt,
    createdAt: request.createdAt
  }));
});

app.post('/api/requests', async (request, reply) => {
  const payload = requestCreateSchema.parse(request.body);
  const created = await prisma.serviceRequest.create({
    data: {
      tableLabel: payload.table,
      type: payload.type,
      note: payload.note,
      status: 'pending',
      items: payload.items as Prisma.InputJsonValue,
      customerPhone: payload.customerPhone,
      customerPhoneMasked: payload.customerPhoneMasked,
      phoneVerifiedAt: payload.phoneVerifiedAt ? new Date(payload.phoneVerifiedAt) : null
    }
  });

  reply.code(201);
  return created;
});

app.patch('/api/requests/:id/status', { preHandler: authenticate }, async (request) => {
  const params = request.params as { id: string };
  const body = z.object({ status: z.enum(['pending', 'completed']) }).parse(request.body);
  return prisma.serviceRequest.update({
    where: { id: params.id },
    data: { status: body.status }
  });
});

app.delete('/api/requests/completed', { preHandler: authenticate }, async () => {
  const result = await prisma.serviceRequest.deleteMany({ where: { status: 'completed' } });
  return { deleted: result.count };
});

app.post('/api/auth/otp/send', async (request, reply) => {
  const payload = otpSendSchema.parse(request.body);
  const existing = await prisma.otpChallenge.findUnique({ where: { tableLabel: payload.table } });
  const now = Date.now();

  if (existing && existing.phone === payload.phone && existing.cooldownUntil.getTime() > now) {
    reply.code(429);
    return {
      error: 'Cooldown active',
      retryInSeconds: Math.ceil((existing.cooldownUntil.getTime() - now) / 1000)
    };
  }

  const code = String(Math.floor(100000 + Math.random() * 900000));
  const challenge = await prisma.otpChallenge.upsert({
    where: { tableLabel: payload.table },
    create: {
      tableLabel: payload.table,
      phone: payload.phone,
      code,
      attempts: 0,
      expiresAt: new Date(now + OTP_EXPIRY_MS),
      cooldownUntil: new Date(now + OTP_RESEND_COOLDOWN_MS)
    },
    update: {
      phone: payload.phone,
      code,
      attempts: 0,
      expiresAt: new Date(now + OTP_EXPIRY_MS),
      cooldownUntil: new Date(now + OTP_RESEND_COOLDOWN_MS)
    }
  });

  const smsSent = await sendOtpViaSms(payload.phone, code);

  return {
    table: payload.table,
    maskedPhone: maskPhoneNumber(payload.phone),
    demoCode: challenge.code,
    smsSent,
    expiresAt: challenge.expiresAt,
    cooldownUntil: challenge.cooldownUntil
  };
});

app.post('/api/auth/otp/verify', async (request, reply) => {
  const payload = otpVerifySchema.parse(request.body);
  const challenge = await prisma.otpChallenge.findUnique({ where: { tableLabel: payload.table } });

  if (!challenge) {
    reply.code(400);
    return { error: 'Send OTP first' };
  }

  if (challenge.phone !== payload.phone) {
    reply.code(400);
    return { error: 'Phone mismatch' };
  }

  if (challenge.expiresAt.getTime() < Date.now()) {
    await prisma.otpChallenge.delete({ where: { tableLabel: payload.table } });
    reply.code(400);
    return { error: 'OTP expired' };
  }

  if (challenge.code !== payload.code) {
    const attempts = challenge.attempts + 1;
    if (attempts >= 5) {
      await prisma.otpChallenge.delete({ where: { tableLabel: payload.table } });
      reply.code(400);
      return { error: 'Too many attempts' };
    }

    await prisma.otpChallenge.update({
      where: { tableLabel: payload.table },
      data: { attempts }
    });

    reply.code(400);
    return { error: `Incorrect OTP. ${5 - attempts} attempts left.` };
  }

  const session = await prisma.customerSession.upsert({
    where: { tableLabel: payload.table },
    create: {
      tableLabel: payload.table,
      phone: payload.phone,
      verifiedAt: new Date()
    },
    update: {
      phone: payload.phone,
      verifiedAt: new Date()
    }
  });

  await prisma.otpChallenge.delete({ where: { tableLabel: payload.table } });

  return {
    table: session.tableLabel,
    phone: session.phone,
    phoneMasked: maskPhoneNumber(session.phone),
    verifiedAt: session.verifiedAt
  };
});

app.get('/api/auth/session/:table', async (request, reply) => {
  const params = request.params as { table: string };
  const session = await prisma.customerSession.findUnique({ where: { tableLabel: params.table } });

  if (!session) {
    reply.code(404);
    return { error: 'No verified session for table' };
  }

  return {
    table: session.tableLabel,
    phone: session.phone,
    phoneMasked: maskPhoneNumber(session.phone),
    verifiedAt: session.verifiedAt
  };
});

const port = Number.parseInt(process.env.PORT ?? '4000', 10);

async function bootstrap() {
  try {
    await app.listen({ port, host: '0.0.0.0' });
  } catch (error) {
    app.log.error(error);
    process.exit(1);
  }
}

void bootstrap();

const shutdown = async () => {
  await app.close();
  await prisma.$disconnect();
};

process.on('SIGINT', () => {
  void shutdown();
});

process.on('SIGTERM', () => {
  void shutdown();
});

