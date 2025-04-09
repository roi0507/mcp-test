import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ItemDetail } from '../../models/item.model';
import { ItemService } from '../../services/item.service';

@Component({
  selector: 'app-item-form',
  templateUrl: './item-form.component.html',
  styleUrls: ['./item-form.component.scss']
})
export class ItemFormComponent implements OnInit {
  @Input() item: ItemDetail | null = null;
  @Output() formSubmitted = new EventEmitter<ItemDetail>();
  @Output() formCancelled = new EventEmitter<void>();
  
  itemForm!: FormGroup;
  isSubmitting = false;
  error = '';
  isVisible = false;
  
  constructor(
    private fb: FormBuilder,
    private itemService: ItemService
  ) { }
  
  ngOnInit(): void {
    this.initForm();
  }
  
  private initForm(): void {
    this.itemForm = this.fb.group({
      id: [this.item?.id || 0],
      name: [this.item?.name || '', [Validators.required, Validators.maxLength(255)]],
      warehouse: [this.item?.warehouse || '', [Validators.required, Validators.maxLength(255)]],
      amount: [this.item?.amount || 0, [Validators.required, Validators.min(0)]]
    });
  }
  
  open(item?: ItemDetail): void {
    this.item = item || null;
    this.initForm();
    this.isVisible = true;
    this.error = '';
  }
  
  close(): void {
    this.isVisible = false;
    this.formCancelled.emit();
  }
  
  onSubmit(): void {
    if (this.itemForm.invalid) {
      return;
    }
    
    this.isSubmitting = true;
    this.error = '';
    
    const formValue = this.itemForm.value as ItemDetail;
    
    if (formValue.id) {
      // Update existing item
      this.itemService.updateItem(formValue).subscribe({
        next: (updatedItem) => {
          this.isSubmitting = false;
          this.formSubmitted.emit(updatedItem);
          this.close();
        },
        error: (err) => {
          this.error = 'Failed to update item. Please try again.';
          console.error('Error updating item:', err);
          this.isSubmitting = false;
        }
      });
    } else {
      // Create new item
      this.itemService.createItem(formValue).subscribe({
        next: (newItem) => {
          this.isSubmitting = false;
          this.formSubmitted.emit(newItem);
          this.close();
        },
        error: (err) => {
          this.error = 'Failed to create item. Please try again.';
          console.error('Error creating item:', err);
          this.isSubmitting = false;
        }
      });
    }
  }
} 