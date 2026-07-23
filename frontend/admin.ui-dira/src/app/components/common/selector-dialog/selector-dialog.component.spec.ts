import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectorDialogComponent } from './selector-dialog.component';

describe('SelectServiceAreaDialogComponent', () => {
    let component: SelectorDialogComponent;
    let fixture: ComponentFixture<SelectorDialogComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [SelectorDialogComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(SelectorDialogComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
