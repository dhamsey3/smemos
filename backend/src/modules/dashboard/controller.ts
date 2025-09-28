import type { Request, Response } from 'express';

import { getSummary } from './service.js';

export const summaryHandler = async (req: Request, res: Response) => {
  const payload = await getSummary(req.user!.id);
  res.json({ data: payload });
};
