import { db } from '../../db/knex.js';
import { pushSyncEvent } from '../sync/service.js';
import { AppError } from '../../middleware/error.js';
import { getProductById, listProducts, createProduct as create, updateProduct as update, deleteProduct as destroy } from './repository.js';

export const getProducts = (merchantId: string) => listProducts(db, merchantId);

export const createProduct = async (
  merchantId: string,
  payload: { name: string; priceNaira: number; quantity: number },
) => {
  const [product] = await create(db, merchantId, {
    name: payload.name,
    price_cents: Math.round(payload.priceNaira * 100),
    quantity: payload.quantity,
  });
  await pushSyncEvent(merchantId, 'product', product.id, product);
  return product;
};

export const updateProductDetails = async (
  merchantId: string,
  productId: string,
  payload: Partial<{ name: string; priceNaira: number; quantity: number }>,
) => {
  const product = await getProductById(db, merchantId, productId);
  if (!product) {
    throw new AppError('Product not found', 404);
  }
  const [updated] = await update(db, merchantId, productId, {
    name: payload.name ?? product.name,
    price_cents: payload.priceNaira ? Math.round(payload.priceNaira * 100) : product.price_cents,
    quantity: payload.quantity ?? product.quantity,
  });
  await pushSyncEvent(merchantId, 'product', updated.id, updated);
  return updated;
};

export const deleteProductById = async (merchantId: string, productId: string) => {
  await destroy(db, merchantId, productId);
  await pushSyncEvent(merchantId, 'product', productId, { deleted: true });
};
