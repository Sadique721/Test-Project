import { Component, EventEmitter, forwardRef, Input, Optional, Output, Self, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, NG_VALUE_ACCESSOR, NgControl } from '@angular/forms';
import { MatSelect } from '@angular/material/select';
import { ErrorStateMatcher } from '@angular/material/core';

@Component({
    selector: 'app-c-dropdown',
    templateUrl: './c-dropdown.component.html',
    styleUrl: './c-dropdown.component.css',
    standalone: false,
})
export class CDropdownComponent {

    @Input() options: any[] = [];
    @Input() placeholder: string = 'Select an option';
    @Input() label: string = '';
    @Input() required: boolean = false;
    @Input() disabled: boolean = false;
    @Input() optionValue: string = 'value';
    @Input() optionLabel: string = 'text';
    @Input() searchPlaceholder: string = 'Search...';
    @Input() noEntriesFoundLabel: string = 'No results found';
    @Input() clearable: boolean = false;
    @Input() multiple: boolean = false;
    @Input() appearance: 'outline' | 'fill' | 'standard' = 'outline';
    @Input() errorMessage: string = '';
    @Input() isHideError: boolean = false;
    @Input() idHeaderInput: boolean = false;

    @Output() selectionChange = new EventEmitter<any>();
    @Output() searchChange = new EventEmitter<string>();

    @ViewChild('selectRef') selectRef!: MatSelect;

    searchValue: string = '';
    filteredOptions: any[] = [];
    value: any = null;
    isTouched = false;

    customErrorStateMatcher: ErrorStateMatcher = {
        isErrorState: (control: FormControl | null) => {
            return !!(control && control.invalid && (control.touched || this.isTouched));
        }
    };

    onChange: (value: any) => void = () => { };
    onTouched: () => void = () => { };

    constructor(
        @Optional() @Self() public ngControl: NgControl
    ) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit() {
        this.filteredOptions = [...this.options];
        this.updateRequiredState();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['options']) {
            this.filteredOptions = [...this.options];
            if (this.searchValue) {
                this.filterOptions(this.searchValue);
            }
        }
    }

    private updateRequiredState() {
        if (this.ngControl && this.ngControl.control) {
            const validator = this.ngControl.control.validator;
            if (validator) {
                const validation = validator({} as any);
                this.required = validation?.['required'] === true;
            }
        }
    }

    get isRequired(): boolean {
        if (this.ngControl && this.ngControl.control) {
            const control = this.ngControl.control;
            if (control.validator) {
                const validator = control.validator({} as any);
                return validator?.['required'] === true;
            }
        }
        return this.required;
    }

    markAsTouched() {
        this.isTouched = true;

        if (this.onTouched) {
            this.onTouched();
        }

        if (this.ngControl && this.ngControl.control) {
            this.ngControl.control.markAsTouched();
        }
    }

    getOptionLabel(option: any): string {
        return option[this.optionLabel] || option.text || '';
    }

    getOptionValue(option: any): any {
        return option[this.optionValue] || option.value;
    }

    onSelectionChange(event: any) {
        this.value = event.value;
        this.onChange(this.value);
        this.markAsTouched();
        this.selectionChange.emit(event);

        if (this.ngControl && this.ngControl.control) {
            this.ngControl.control.updateValueAndValidity();
        }
    }

    onOpenedChange(opened: boolean) {

        if (opened) {
            // this.markAsTouched();
        }

        if (!opened) {
            this.clearSearch();
        }
    }

    onFocus() {
        // this.markAsTouched();
    }

    onBlur() {
        this.markAsTouched();
    }

    clearSelection(event: Event) {
        event.stopPropagation();
        this.value = this.multiple ? [] : null;
        this.onChange(this.value);
        this.markAsTouched();
        this.selectionChange.emit(this.value);
        if (this.selectRef) {
            this.selectRef.close();
        }
    }

    clearSearch() {
        this.searchValue = '';
        this.filteredOptions = [...this.options];
    }

    onSearchChange(searchTerm: string) {
        this.filterOptions(searchTerm);
        this.searchChange.emit(searchTerm);
    }

    filterOptions(searchTerm: string) {
        if (!searchTerm || searchTerm.trim() === '') {
            this.filteredOptions = [...this.options];
            return;
        }

        const searchLower = searchTerm.toLowerCase().trim();
        this.filteredOptions = this.options.filter(option => {
            const labelValue = this.getOptionLabel(option).toLowerCase();
            return labelValue.includes(searchLower);
        });
    }

    get hasError(): boolean {
        if (this.ngControl && this.ngControl.control) {
            const control = this.ngControl.control;
            return !!(control.invalid && (control.touched || this.isTouched));
        }
        return this.isRequired && !this.value && this.isTouched;
    }

    get showError(): boolean {
        return this.hasError;
    }

    get errorText(): string {
        if (this.errorMessage.trim()) {
            return this.errorMessage;
        }
        if (this.ngControl && this.ngControl.control && this.ngControl.control.errors) {
            const errors = this.ngControl.control.errors;
            if (errors['required']) return `${this.label || 'This field'} is required`;
            if (errors['minlength']) return `Minimum length is ${errors['minlength'].requiredLength}`;
            if (errors['maxlength']) return `Maximum length is ${errors['maxlength'].requiredLength}`;
            if (errors['email']) return 'Please enter a valid email';
            if (errors['pattern']) return 'Invalid format';
            if (errors['min']) return `Minimum value is ${errors['min'].min}`;
            if (errors['max']) return `Maximum value is ${errors['max'].max}`;
        }
    }

    writeValue(value: any): void {
        this.value = value;
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

}
