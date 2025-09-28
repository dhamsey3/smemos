export interface PaymentIntentRequest {
  saleId: string;
  channel: 'qr' | 'transfer';
}

export interface PaymentIntentResponse {
  provider: string;
  reference: string;
  amountCents: number;
  qrImage?: string;
  bankDetails?: {
    accountNumber: string;
    bankName: string;
  };
}
