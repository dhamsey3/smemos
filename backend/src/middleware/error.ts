import type { NextFunction, Request, Response } from 'express';

import { logger } from '../config/logger.js';

export class AppError extends Error {
  statusCode: number;

  constructor(message: string, statusCode = 400) {
    super(message);
    this.statusCode = statusCode;
  }
}

export const errorHandler = (
  err: unknown,
  _req: Request,
  res: Response,
  _next: NextFunction,
) => {
  if (err instanceof AppError) {
    return res.status(err.statusCode).json({ message: err.message });
  }

  logger.error('Unexpected error', err);
  return res.status(500).json({ message: 'Internal server error' });
};
