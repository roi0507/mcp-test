import { Component, Input, OnInit } from '@angular/core';
import { ItemDetail } from '../../models/item.model';
import { ItemService } from '../../services/item.service';

@Component({
  selector: 'app-item-detail-modal',
  templateUrl: './item-detail-modal.component.html',
  styleUrls: ['./item-detail-modal.component.scss']
})
export class ItemDetailModalComponent implements OnInit {
  @Input() itemId: number | null = null;
  
  itemDetail: ItemDetail | null = null;
  loading = false;
  error = '';
  isVisible = false;
  
  constructor(private itemService: ItemService) { }
  
  ngOnInit(): void {
  }
  
  open(itemId: number): void {
    this.itemId = itemId;
    this.isVisible = true;
    this.loadItemDetails();
  }
  
  close(): void {
    this.isVisible = false;
    this.itemDetail = null;
    this.error = '';
  }
  
  private loadItemDetails(): void {
    if (!this.itemId) {
      this.error = 'No item ID provided';
      return;
    }
    
    this.loading = true;
    this.error = '';
    
    this.itemService.getItemDetails(this.itemId).subscribe({
      next: (detail) => {
        this.itemDetail = detail;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load item details. Please try again later.';
        console.error('Error loading item details:', err);
        this.loading = false;
      }
    });
  }
} 