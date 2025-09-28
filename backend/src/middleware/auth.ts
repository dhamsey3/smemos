import type { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';

import { appConfig } from '../config/env.js';
import type { MerchantClaims } from '../modules/auth/types.js';

export const authenticate = (req: Request, res: Response, next: NextFunction) => {
  const token = (req.headers.authorization || '').replace('Bearer ', '');
  if (!token) {
    return res.status(401).json({ message: 'Unauthorized' });
  }

  try {
    const payload = jwt.verify(token, appConfig.jwtSecret);
    req.user = payload as MerchantClaims;
    return next();
  } catch (error) {
    return res.status(401).json({ message: 'Invalid token' });
  }
};
