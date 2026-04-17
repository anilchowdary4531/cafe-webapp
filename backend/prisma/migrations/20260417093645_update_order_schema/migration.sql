-- CreateEnum
CREATE TYPE "PaymentStatus" AS ENUM ('unpaid', 'paid');

-- AlterTable
ALTER TABLE "Order" ADD COLUMN     "completionEmailSentAt" TIMESTAMP(3),
ADD COLUMN     "customerEmail" TEXT,
ADD COLUMN     "paidAt" TIMESTAMP(3),
ADD COLUMN     "paymentStatus" "PaymentStatus" NOT NULL DEFAULT 'unpaid',
ALTER COLUMN "customerPhone" DROP NOT NULL,
ALTER COLUMN "customerPhoneMasked" DROP NOT NULL;
