import { Router } from 'express';

import { authenticate } from '../middleware/auth.js';
import { requestOtpHandler, verifyOtpHandler, loginHandler } from '../modules/auth/controller.js';
import { listProductsHandler, createProductHandler, updateProductHandler, deleteProductHandler } from '../modules/products/controller.js';
import { listSalesHandler, recordSaleHandler } from '../modules/sales/controller.js';
import { initiatePaymentHandler, webhookHandler } from '../modules/payments/controller.js';
import { generateReceiptHandler } from '../modules/receipts/controller.js';
import { pullSyncHandler, pushSyncHandler } from '../modules/sync/controller.js';
import { summaryHandler } from '../modules/dashboard/controller.js';

const router = Router();

router.post('/auth/request-otp', requestOtpHandler);
router.post('/auth/verify-otp', verifyOtpHandler);
router.post('/auth/login', loginHandler);
router.post('/payments/webhook', webhookHandler);

router.use(authenticate);

router.get('/products', listProductsHandler);
router.post('/products', createProductHandler);
router.patch('/products/:id', updateProductHandler);
router.delete('/products/:id', deleteProductHandler);

router.get('/sales', listSalesHandler);
router.post('/sales', recordSaleHandler);

router.get('/dashboard/summary', summaryHandler);

router.post('/payments/initiate', initiatePaymentHandler);

router.post('/sync/pull', pullSyncHandler);
router.post('/sync/push', pushSyncHandler);

router.get('/receipts/:saleId', generateReceiptHandler);

export default router;
