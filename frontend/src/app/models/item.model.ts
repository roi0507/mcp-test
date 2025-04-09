export interface Item {
  id: number;
  name: string;
}

export interface ItemDetail extends Item {
  warehouse: string;
  amount: number;
} 