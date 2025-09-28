import type { Request, Response } from 'express';

import { buildReceipt } from './service.js';

export const generateReceiptHandler = async (req: Request, res: Response) => {
  const payload = await buildReceipt(req.user!.id, req.params.saleId);
  res.json({ data: payload });
};
