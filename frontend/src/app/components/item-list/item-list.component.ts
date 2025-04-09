import { Component, OnInit, ViewChild } from '@angular/core';
import { ItemService } from '../../services/item.service';
import { Item } from '../../models/item.model';
import { ItemDetailModalComponent } from '../item-detail-modal/item-detail-modal.component';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.scss']
})
export class ItemListComponent implements OnInit {
  @ViewChild(ItemDetailModalComponent) detailModal!: ItemDetailModalComponent;
  
  items: Item[] = [];
  loading = false;
  error = '';

  constructor(private itemService: ItemService) { }

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems(): void {
    this.loading = true;
    this.error = '';

    this.itemService.getItems().subscribe({
      next: (response) => {
        this.items = response.items;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load items. Please try again later.';
        console.error('Error loading items:', err);
        this.loading = false;
      }
    });
  }
  
  openItemDetails(item: Item): void {
    this.detailModal.open(item.id);
  }
} 