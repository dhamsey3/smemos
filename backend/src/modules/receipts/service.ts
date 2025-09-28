import { db } from '../../db/knex.js';
import { AppError } from '../../middleware/error.js';

export const buildReceipt = async (merchantId: string, saleId: string) => {
  const sale = await db('sales').where({ id: saleId, merchant_id: merchantId }).first();
  if (!sale) {
    throw new AppError('Sale not found', 404);
  }
  const merchant = await db('merchants').where({ id: merchantId }).first();
  const items = await db('sale_items')
    .join('products', 'sale_items.product_id', 'products.id')
    .select('products.name', 'sale_items.quantity', 'sale_items.unit_price_cents')
    .where('sale_items.sale_id', saleId);

  const lines = items.map(
    (item: any) => `${item.name} x${item.quantity} - ₦${(item.unit_price_cents * item.quantity) / 100}`,
  );

  return {
    sale,
    merchant,
    message: `KoloOS Receipt\nMerchant: ${merchant.name}\n${lines.join('\n')}\nTotal: ₦${sale.total_amount_cents / 100}\nStatus: ${sale.status}`,
  };
};
