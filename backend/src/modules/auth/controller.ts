import type { Request, Response } from 'express';
import Joi from 'joi';

import { AppError } from '../../middleware/error.js';
import { validate } from '../../middleware/validate.js';
import { passwordLogin, requestOtp, verifyOtp } from './service.js';

export const requestOtpSchema = Joi.object({
  phone: Joi.string().required(),
});

export const verifyOtpSchema = Joi.object({
  phone: Joi.string().required(),
  name: Joi.string().required(),
  otp: Joi.string().required(),
});

export const loginSchema = Joi.object({
  phone: Joi.string().required(),
  password: Joi.string().required(),
});

export const requestOtpHandler = [
  validate(requestOtpSchema),
  async (req: Request, res: Response) => {
    const { phone } = req.body as { phone: string };
    const payload = await requestOtp(phone);
    res.json({ data: payload });
  },
];

export const verifyOtpHandler = [
  validate(verifyOtpSchema),
  async (req: Request, res: Response) => {
    const { phone, name, otp } = req.body as { phone: string; name: string; otp: string };

    try {
      const payload = await verifyOtp(phone, name, otp);
      res.json({ data: payload });
    } catch (error) {
      if (error instanceof AppError) {
        return res.status(401).json({ message: error.message });
      }

      throw error;
    }
  },
];

export const loginHandler = [
  validate(loginSchema),
  async (req: Request, res: Response) => {
    const { phone, password } = req.body as { phone: string; password: string };
    const payload = await passwordLogin(phone, password);
    if (!payload) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }
    res.json({ data: payload });
  },
];
