import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { Observable, Observer } from "rxjs";
import { DeleteConfirmationDialogBoxComponent } from "../shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Injectable({
    providedIn: "root",
})
export class Utils {
    constructor(private matdialog: MatDialog) { }

    canExit(form) {
        if (!form) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {

                const dialogRef2 = this.matdialog.open(DeleteConfirmationDialogBoxComponent, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                        yesLabel: 'Yes',
                        noLabel: 'No'
                    }
                });

                dialogRef2.afterClosed().subscribe(result => {
                    if (result) {
                        observer.next(true);
                        observer.complete();
                    } else {
                        observer.next(false);
                        observer.complete();
                    }
                });
            });
        }
    }
}
