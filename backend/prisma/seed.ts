import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

const defaultMenu = [
  { name: 'Veg Sandwich', category: 'Food', price: 5.5, description: 'Fresh grilled sandwich', restricted: false },
  { name: 'Chicken Burger', category: 'Food', price: 7.9, description: 'Served with sauce', restricted: false },
  { name: 'French Fries', category: 'Snacks', price: 3.8, description: 'Crispy salted fries', restricted: false },
  { name: 'Samosa Plate', category: 'Snacks', price: 4.2, description: '2 pieces with chutney', restricted: false },
  { name: 'Cappuccino', category: 'Coffee', price: 4.0, description: 'Classic foam coffee', restricted: false },
  { name: 'Cold Coffee', category: 'Coffee', price: 4.8, description: 'Chilled with ice', restricted: false },
  { name: 'Masala Tea', category: 'Tea', price: 2.5, description: 'Indian spiced tea', restricted: false },
  { name: 'Green Tea', category: 'Tea', price: 2.3, description: 'Light and refreshing', restricted: false },
  { name: 'Orange Juice', category: 'Juices', price: 3.9, description: 'Fresh juice', restricted: false },
  { name: 'Mango Shake', category: 'Juices', price: 4.5, description: 'Thick and sweet', restricted: false },
  { name: 'Vanilla Scoop', category: 'Ice Cream', price: 2.9, description: 'Single scoop', restricted: false },
  { name: 'Chocolate Sundae', category: 'Ice Cream', price: 4.9, description: 'With chocolate syrup', restricted: false },
  { name: 'Cigarette Request', category: 'Cigarettes', price: 0, description: 'Staff approval required', restricted: true }
];

const defaultUsers = [
  { username: 'admin', password: 'admin123', role: 'admin' as const },
  { username: 'staff', password: 'staff123', role: 'staff' as const },
  { username: 'survetraadmintest@gmail.com', password: 'survetra@123', role: 'admin' as const }
];

async function main() {
  const existingMenuCount = await prisma.menuItem.count();
  if (existingMenuCount === 0) {
    await prisma.menuItem.createMany({ data: defaultMenu });
    console.log(`Seeded ${defaultMenu.length} menu items.`);
  }

  const existingUserCount = await prisma.user.count();
  if (existingUserCount === 0) {
    await prisma.user.createMany({ data: defaultUsers });
    console.log(`Seeded ${defaultUsers.length} users.`);
  } else {
    // Check and add individual users if they don't exist
    for (const user of defaultUsers) {
      const existingUser = await prisma.user.findUnique({ where: { username: user.username } });
      if (!existingUser) {
        await prisma.user.create({ data: user });
        console.log(`Added user: ${user.username}`);
      }
    }
  }
}

main()
  .catch((error) => {
    console.error(error);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
