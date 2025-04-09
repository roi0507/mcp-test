import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Item, ItemDetail } from '../models/item.model';

interface ItemsResponse {
  items: Item[];
}

@Injectable({
  providedIn: 'root'
})
export class ItemService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getItems(): Observable<ItemsResponse> {
    return this.http.get<ItemsResponse>(`${this.apiUrl}/data`);
  }

  getItemDetails(id: number): Observable<ItemDetail> {
    return this.http.get<ItemDetail>(`${this.apiUrl}/items/${id}`);
  }
  
  createItem(item: ItemDetail): Observable<ItemDetail> {
    return this.http.post<ItemDetail>(`${this.apiUrl}/items`, item);
  }
  
  updateItem(item: ItemDetail): Observable<ItemDetail> {
    return this.http.put<ItemDetail>(`${this.apiUrl}/items/${item.id}`, item);
  }
  
  deleteItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/items/${id}`);
  }
} 