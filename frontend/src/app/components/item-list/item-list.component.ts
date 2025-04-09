import { Component, OnInit, ViewChild } from '@angular/core';
import { ItemService } from '../../services/item.service';
import { Item, ItemDetail } from '../../models/item.model';
import { ItemDetailModalComponent } from '../item-detail-modal/item-detail-modal.component';
import { ItemFormComponent } from '../item-form/item-form.component';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.scss']
})
export class ItemListComponent implements OnInit {
  @ViewChild(ItemDetailModalComponent) detailModal!: ItemDetailModalComponent;
  @ViewChild(ItemFormComponent) itemForm!: ItemFormComponent;
  
  items: Item[] = [];
  loading = false;
  error = '';
  deleteItemId: number | null = null;
  showDeleteConfirm = false;

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
  
  openCreateItemForm(): void {
    this.itemForm.open();
  }
  
  openEditItemForm(item: Item): void {
    this.itemService.getItemDetails(item.id).subscribe({
      next: (detail) => {
        this.itemForm.open(detail);
      },
      error: (err) => {
        this.error = 'Failed to load item details for editing.';
        console.error('Error loading item details:', err);
      }
    });
  }
  
  openDeleteConfirm(item: Item, event: Event): void {
    event.stopPropagation(); // Prevent opening the item details modal
    this.deleteItemId = item.id;
    this.showDeleteConfirm = true;
  }
  
  cancelDelete(): void {
    this.showDeleteConfirm = false;
    this.deleteItemId = null;
  }
  
  confirmDelete(): void {
    if (this.deleteItemId === null) {
      return;
    }
    
    this.itemService.deleteItem(this.deleteItemId).subscribe({
      next: () => {
        this.loadItems();
        this.showDeleteConfirm = false;
        this.deleteItemId = null;
      },
      error: (err) => {
        this.error = 'Failed to delete item.';
        console.error('Error deleting item:', err);
        this.showDeleteConfirm = false;
      }
    });
  }
  
  onItemSaved(item: ItemDetail): void {
    this.loadItems();
  }
} 