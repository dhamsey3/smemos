export interface Sale {
  id: string;
  merchant_id: string;
  total_amount_cents: number;
  status: string;
  payment_reference: string | null;
  created_at: string;
}

export interface SaleItem {
  id: string;
  sale_id: string;
  product_id: string;
  unit_price_cents: number;
  quantity: number;
}
