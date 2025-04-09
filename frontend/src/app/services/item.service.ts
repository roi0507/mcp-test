import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Item } from '../models/item.model';

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
} 