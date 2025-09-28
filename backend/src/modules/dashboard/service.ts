import { db } from '../../db/knex.js';

export const getSummary = async (merchantId: string) => {
  const [totals] = await db('sales')
    .where({ merchant_id: merchantId })
    .count('* as count')
    .sum({ revenue: 'total_amount_cents' });

  const daily = await db('sales')
    .select(db.raw('DATE(created_at) as date'))
    .sum({ revenue: 'total_amount_cents' })
    .where({ merchant_id: merchantId })
    .andWhere('created_at', '>=', db.raw("now() - interval '7 days'"))
    .groupByRaw('DATE(created_at)')
    .orderBy('date', 'desc');

  return {
    totalSales: Number(totals?.count ?? 0),
    totalRevenueCents: Number(totals?.revenue ?? 0),
    daily,
  };
};
