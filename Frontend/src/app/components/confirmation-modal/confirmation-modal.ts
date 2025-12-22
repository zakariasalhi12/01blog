import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-confirmation-modal',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './confirmation-modal.html',
    styleUrl: './confirmation-modal.css',
})
export class ConfirmationModal {
    @Input() title = 'Confirm Action';
    @Input() show = false; // Not used but kept for compatibility if needed, though parent uses *ngIf
    @Input() message = 'Are you sure?';
    @Input() confirmText = 'Confirm';
    @Input() cancelText = 'Cancel';

    @Output() confirmed = new EventEmitter<void>();
    @Output() cancelled = new EventEmitter<void>();

    onConfirm(): void {
        this.confirmed.emit();
    }

    onCancel(): void {
        this.cancelled.emit();
    }



    onOverlayClick(): void {
        this.onCancel();
    }
}
