import { db } from '../../db/knex.js';
import { AppError } from '../../middleware/error.js';
import { getProductById } from '../products/repository.js';
import { pushSyncEvent } from '../sync/service.js';
import { createSale, createSaleItems, listSales } from './repository.js';

interface SaleItemInput {
  productId: string;
  quantity: number;
}

export const recordSale = async (merchantId: string, items: SaleItemInput[]) => {
  if (!items.length) {
    throw new AppError('Sale must contain at least one item');
  }

  return db.transaction(async (trx) => {
    let total = 0;
    const saleItemsPayload = [] as {
      sale_id: string;
      product_id: string;
      unit_price_cents: number;
      quantity: number;
    }[];

    for (const item of items) {
      const product = await getProductById(trx, merchantId, item.productId);
      if (!product) {
        throw new AppError('Product not found', 404);
      }
      if (product.quantity < item.quantity) {
        throw new AppError(`Insufficient stock for ${product.name}`);
      }

      await trx('products')
        .where({ id: item.productId })
        .decrement('quantity', item.quantity);

      total += product.price_cents * item.quantity;
      saleItemsPayload.push({
        sale_id: '',
        product_id: product.id,
        unit_price_cents: product.price_cents,
        quantity: item.quantity,
      });
    }

    const [sale] = await createSale(trx, {
      merchant_id: merchantId,
      total_amount_cents: total,
      status: 'PENDING',
    });

    await createSaleItems(
      trx,
      saleItemsPayload.map((item) => ({ ...item, sale_id: sale.id })),
    );

    await pushSyncEvent(merchantId, 'sale', sale.id, sale, trx);

    return sale;
  });
};

export const getSales = (merchantId: string) => listSales(db, merchantId);
