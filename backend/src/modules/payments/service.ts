import crypto from 'node:crypto';

import { db } from '../../db/knex.js';
import { AppError } from '../../middleware/error.js';
import { pushSyncEvent } from '../sync/service.js';
import { getSaleById, updateSaleStatus } from '../sales/repository.js';
import type { PaymentIntentRequest, PaymentIntentResponse } from './types.js';

export const initiatePayment = async (
  merchantId: string,
  payload: PaymentIntentRequest,
): Promise<PaymentIntentResponse> => {
  const sale = await getSaleById(db, merchantId, payload.saleId);
  if (!sale) {
    throw new AppError('Sale not found', 404);
  }

  const reference = `PSP_${crypto.randomBytes(6).toString('hex')}`;
  await updateSaleStatus(db, sale.id, 'PENDING', reference);

  return {
    provider: 'paystack',
    reference,
    amountCents: sale.total_amount_cents,
    qrImage: `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${reference}`,
    bankDetails: {
      accountNumber: '1234567890',
      bankName: 'Mock Bank',
    },
  };
};

export const handleWebhook = async (
  signature: string | undefined,
  rawBody: string,
  event: { reference: string; status: 'success' | 'failed' },
) => {
  if (!signature) {
    throw new AppError('Missing signature', 400);
  }

  // MVP stub signature verification
  const expected = crypto.createHash('sha256').update(rawBody).digest('hex');
  if (signature !== expected) {
    throw new AppError('Invalid signature', 401);
  }

  const [sale] = await db('sales')
    .where({ payment_reference: event.reference })
    .returning('*');

  if (!sale) {
    return;
  }

  const status = event.status === 'success' ? 'PAID' : 'FAILED';
  const [updated] = await updateSaleStatus(db, sale.id, status, event.reference);
  await pushSyncEvent(updated.merchant_id, 'sale', updated.id, updated);
};
