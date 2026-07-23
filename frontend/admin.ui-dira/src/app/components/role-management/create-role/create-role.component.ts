// import { Component, NgZone, OnInit, Output, EventEmitter, Input, OnDestroy } from "@angular/core";
// import { FormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from "@angular/forms";
// import { NgxSpinnerService } from "ngx-spinner";
// import { ConfirmationService, MessageService, TreeNode } from "primeng/api";
// import { Observable, Observer } from "rxjs";
// import { AclEntry, Role } from "src/app/models/RoleManagement";
// import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
// import { LoginService } from "src/app/service/login.service";
// import { RoleService } from "src/app/service/role.service";

// @Component({
//     selector: "app-create-role",
//     templateUrl: "./create-role.component.html",
//     styleUrls: ["./create-role.component.css"],
//     standalone: false
// })
// export class CreateRoleComponent implements OnInit, OnDestroy {
//   @Output() roleSaveorUpdated = new EventEmitter();
//   @Input() roleData = null;
//   data: TreeNode[] = [];
//   selectedNodes!: TreeNode[];

//   rolePermissionList: any = [];

//   isSelectAll: boolean = false;
//   editMode: boolean = false;
//   submitted: boolean = false;

//   commonStatusList: any = [];

//   roleGroupForm: UntypedFormGroup;

//   constructor(
//     private roleService: RoleService,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     public loginService: LoginService
//   ) {}

//   ngOnInit(): void {
//     this.roleGroupForm = new UntypedFormGroup({
//       rolename: new UntypedFormControl("", [Validators.required]),
//       status: new UntypedFormControl("", [Validators.required]),
//       product: new UntypedFormControl("BSS", [Validators.required]),
//       //   aclEntryPojoList: new FormControl(this.saveSelectedPermission.aclEntryPojoList),
//     });

//     if (this.roleData) {
//       this.editMode = true;
//       this.roleGroupForm.patchValue(this.roleData);
//       this.data = this.roleData.aclMenus;
//     } else {
//       this.getAclData();
//     }

//     this.roleService.getCommonList().subscribe(res => {
//       this.commonStatusList = res.dataList;
//     });
//   }

//   ngOnDestroy(): void {
//     this.roleGroupForm = null;
//     this.roleData = null;
//     this.data = [];
//     this.commonStatusList = [];
//   }

//   getAclData() {
//     this.roleService.getAllACLMenu().subscribe(
//       (response: any) => {
//         this.data = response.datalist;
//       },
//       (error: any) => {
//         console.log(error);
//       }
//     );
//   }

//   onNodeExpand(event) {
//     this.collapseAllNodes(this.data); // Collapse all nodes first
//     this.expandNodeAndParents(event.node); // Expand the selected node and its parents
//   }

//   collapseAllNodes(nodes) {
//     nodes.forEach(node => {
//       node.expanded = false;
//       if (node.children) {
//         this.collapseAllNodes(node.children);
//       }
//     });
//   }

//   expandNodeAndParents(node) {
//     node.expanded = true;
//     let parent = node.parent;
//     while (parent) {
//       parent.expanded = true;
//       parent = parent.parent;
//     }
//   }

//   //Click event on checkbox
//   onNodeSelect(event: any, rowNode: any) {
//     if (rowNode) {
//       this.checkAllChildNode(event.checked, rowNode.node);
//       this.checkParentNodes(event.checked, rowNode.node);
//     } else this.selectAllNodes(event.checked, this.data);
//   }
//   selectAllNodes(checked, nodes) {
//     nodes.forEach(node => {
//       node.expanded = false;
//       node.data.isSelected = checked;
//       if (node.children) {
//         this.selectAllNodes(checked, node.children);
//       }
//     });
//   }
//   // Method used to set checked all children items of the selected item for role
//   checkAllChildNode(checked: boolean, node: any) {
//     var childs = node.children;
//     if (childs != null && childs.length > 0) {
//       childs.map((child: any) => {
//         child.data.isSelected = checked;
//         if (child.children != null && child.children.length > 0) {
//           this.checkAllChildNode(checked, child);
//         }
//       });
//     }
//   }
//   // Method used to set checked all parent items of the selected item for role
//   checkParentNodes(checked: boolean, node: any) {
//     var parent = node.parent;
//     if (checked && parent != null) {
//       parent.data.isSelected = checked;
//       this.checkParentNodes(checked, parent);
//     }
//   }

//   addUpdateRole() {
//     this.submitted = true;
//     if (!this.roleGroupForm.valid) {
//       return;
//     }
//     this.saveRolePermissions();
//     if (this.rolePermissionList.length == 0) {
//       this.messageService.add({
//         severity: "error",
//         summary: "Error",
//         detail: "Please select atleast one operation permission.",
//         icon: "far fa-times-circle",
//       });
//       return;
//     }

//     const role = new Role();
//     role.rolename = this.roleGroupForm.value.rolename;
//     role.status = this.roleGroupForm.value.status;
//     role.product = this.roleGroupForm.value.product;
//     role.aclMenu = this.rolePermissionList;
//     let url = "/saveRole";
//     if (this.editMode) {
//       role.id = this.roleData.id;
//       url = "/updateRole";
//     } else {
//       url = "/saveRole";
//     }
//     this.roleService.addUpdateRole(url, role, this.editMode).subscribe(
//       (response: any) => {
//         // if (response.responseCode == 200) {   // Delete 31-8-2025 
//           this.roleGroupForm.reset();
//           this.rolePermissionList = [];
//           this.submitted = false;
//           this.loginService.refreshToken();
//           this.loginService.getAclEntry();
//           this.roleSaveorUpdated.emit();
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: response.responseMessage,
//             icon: "far fa-check-circle",
//           });
//         // } else {
//         //   this.messageService.add({  // Delete 31-8-2025
//         //     severity: "error",
//         //     summary: "Error",
//         //     detail: response.responseMessage,
//         //     icon: "far fa-times-circle",
//         //   });
//         // }
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.errorMessage,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   // Use for store all selected permissions into regarding array list
//   saveRolePermissions() {
//     this.rolePermissionList = [];
//     this.data.forEach((permission: any) => {
//       //Save permission
//       this.createRolePermissionList(permission);
//       if (permission.children?.length! > 0) {
//         this.saveRolePermissionsChildren(permission.children);
//       }
//     });
//   }

//   // Use for store all selected child permissions into regarding array list and it will recursively calling until there is no any child item.
//   saveRolePermissionsChildren(childItem: any) {
//     childItem.map((permission: any) => {
//       this.createRolePermissionList(permission);
//       if (permission.children?.length! > 0) {
//         this.saveRolePermissionsChildren(permission.children);
//       }
//     });
//   }

//   // Use for store all selected permissions into role permission array list.
//   createRolePermissionList(object: any) {
//     if (object.data.isSelected) {
//       const aclEntry = new AclEntry();
//       aclEntry.menuid = object.data.id;
//       aclEntry.code = object.data.code;
//       this.rolePermissionList.push(aclEntry);
//     }
//   }

//   canExit() {
//     if (!this.roleGroupForm.dirty) return true;
//     {
//       return Observable.create((observer: Observer<boolean>) => {
//         this.confirmationService.confirm({
//           header: "Alert",
//           message: "The filled data will be lost. Do you want to continue? (Yes/No)",
//           icon: "pi pi-info-circle",
//           accept: () => {
//             observer.next(true);
//             observer.complete();
//           },
//           reject: () => {
//             observer.next(false);
//             observer.complete();
//           },
//         });
//         return false;
//       });
//     }
//   }
// }

import { Component, Inject, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ConfirmationService, MessageService, TreeNode } from 'primeng/api';
import { RoleService } from 'src/app/service/role.service';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { ToastrService } from 'ngx-toastr';

export interface RoleDialogData {
    roleData: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description?: string;
    yesLabel?: string;
    noLabel?: string;
    inputName?: string;
    inputStatus?: string;
}

interface ExtendedTreeNode extends TreeNode {
    isSelected?: boolean;
    expanded?: boolean;
    children?: ExtendedTreeNode[];
    data: {
        id: number;
        code: string;
        isSelected?: boolean;
    };
    name: string;
}

@Component({
    selector: 'app-create-edit-role',
    templateUrl: './create-role.component.html',
    styleUrls: ['./create-role.component.css'],
    standalone: false,
})
export class CreateRoleComponent implements OnInit, OnDestroy {
    treeControl = new NestedTreeControl<ExtendedTreeNode>((node) => node.children);
    dataSource = new MatTreeNestedDataSource<ExtendedTreeNode>();
    @Output() roleSaveorUpdated = new EventEmitter<any>();
    editMode: boolean;

    roleGroupForm: UntypedFormGroup;
    createAccess = true;
    editAccess = false;
    submitted = false;

    data: ExtendedTreeNode[] = [];
    rolePermissionList: any[] = [];

    commonStatusList = [
        { value: 'Active', text: 'Active' },
        { value: 'Inactive', text: 'Inactive' },
    ];
    isSelectAll: boolean;

    constructor(
        private roleService: RoleService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService, private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        public dialogRef: MatDialogRef<CreateRoleComponent>,
        @Inject(MAT_DIALOG_DATA) public dataDlg: RoleDialogData
    ) { }

    ngOnInit(): void {
        this.roleGroupForm = this.fb.group({
            rolename: ['', Validators.required],
            status: ['', Validators.required],
            product: ['BSS', Validators.required],
        });

        this.createAccess = this.dataDlg.createAcS;
        this.editAccess = this.dataDlg.editAcs;
        this.editMode = this.dataDlg.isEdit;

        if (this.editMode && this.dataDlg.roleData) {
            this.roleGroupForm.patchValue({
                rolename: this.dataDlg.roleData.rolename,
                status: this.dataDlg.roleData.status,
                product: this.dataDlg.roleData.product,
            });

            this.data = this.dataDlg.roleData.aclMenus || [];
            this.setNodeNames(this.data);
            this.setNodeSelection(this.data);
            this.updateDataSource();
        } else {
            this.getAclData();
        }

        this.treeControl.dataNodes = this.data;
        this.dataSource.data = this.data;
    }

    hasChild = (_: number, node: ExtendedTreeNode) =>
        !!node.children && node.children.length > 0;

    ngOnDestroy(): void {
        this.roleGroupForm = null;
        this.data = [];
    }

    getAclData(): void {
        this.roleService.getAllACLMenu().subscribe(
            (response: any) => {
                this.data = response.datalist || [];
                this.setNodeNames(this.data);
                this.updateDataSource();
            },
            (error) => {
                console.error(error);
            }
        );
    }

    setNodeNames(nodes: ExtendedTreeNode[]): void {
        nodes.forEach((node) => {
            if (!node.name && node.data && node.data.code) {
                node.name = node.data.code;
            }
            if (node.children) {
                this.setNodeNames(node.children);
            }
        });
    }

    updateDataSource(): void {
        this.dataSource.data = this.data;
        this.treeControl.dataNodes = this.data;
        this.syncSelectAllState();
    }

    onSelectAllChange(checked: boolean): void {
        this.isSelectAll = checked;
        this.toggleAllNodes(this.data, checked);
        // this.syncSelectAllState();
    }

    syncSelectAllState(): void {
        const flatten = (nodes: ExtendedTreeNode[]): ExtendedTreeNode[] =>
            nodes.reduce(
                (acc, node) =>
                    acc.concat(node, node.children ? flatten(node.children) : []),
                []
            );
        const flatData = flatten(this.data);
        const total = flatData.length;
        const selectedCount = flatData.filter((n) => n.isSelected).length;
        this.isSelectAll = total > 0 && selectedCount === total;
    }

    onPermissionChange(node: ExtendedTreeNode, checked: boolean): void {
        node.isSelected = checked;
        if (node.data) node.data.isSelected = checked;

        // Propagate selection to all children
        if (node.children) {
            this.toggleAllNodes(node.children, checked);
        }
        // Propagate selection upwards to parents
        if (checked) {
            this.selectParentNodes(node);
        }
        this.syncSelectAllState();
    }

    // Helper to select parents recursively
    selectParentNodes(node: ExtendedTreeNode): void {
        const parent = this.getParentNode(node);
        if (parent) {
            // If any child is selected, select the parent as well
            if (node.isSelected) {
                parent.isSelected = true;
                if (parent.data) parent.data.isSelected = true;
                this.selectParentNodes(parent); // recurse upwards
            } else {
                // Optional: If no child of parent selected, unselect parent - add this logic if needed
                const siblingsSelected = parent.children?.some(child => child.isSelected);
                if (!siblingsSelected) {
                    parent.isSelected = false;
                    if (parent.data) parent.data.isSelected = false;
                    this.selectParentNodes(parent);
                }
            }
        }
    }

    // Utility to find parent of a node by traversing the tree
    getParentNode(node: ExtendedTreeNode): ExtendedTreeNode | null {
        let parent: ExtendedTreeNode | null = null;

        // Helper function to recursively find parent
        const findParent = (currentNode: ExtendedTreeNode[], target: ExtendedTreeNode): boolean => {
            for (const child of currentNode) {
                if (child.children && child.children.indexOf(target) !== -1) {
                    parent = child;
                    return true;
                }
                if (child.children) {
                    if (findParent(child.children, target)) {
                        return true;
                    }
                }
            }
            return false;
        };

        findParent(this.data, node);

        return parent;
    }


    toggleAllNodes(nodes: ExtendedTreeNode[], checked: boolean): void {
        nodes.forEach((node) => {
            node.isSelected = checked;
            if (node.data) node.data.isSelected = checked;
            if (node.children) {
                this.toggleAllNodes(node.children, checked);
            }
        });
    }

    // New method: Toggle node expansion and load children dynamically
    toggleNode(node: ExtendedTreeNode): void {
        if (this.treeControl.isExpanded(node)) {
            this.treeControl.collapse(node);
        } else {
            this.loadChildNodes(node).then(() => {
                this.treeControl.expand(node);
            });
        }
    }

    setNodeSelection(nodes: ExtendedTreeNode[]): void {
        nodes.forEach((node) => {
            // Ensure node name exists for display
            if (!node.name && node.data && node.data.code) {
                node.name = node.data.code;
            }
            // Sync isSelected between node and node.data for binding consistency
            node.isSelected = node.data?.isSelected ?? false;

            // Recursively sync children
            if (node.children && node.children.length > 0) {
                this.setNodeSelection(node.children);
            }
        });
    }

    loadChildNodes(node: ExtendedTreeNode): Promise<void> {
        return new Promise((resolve, reject) => {
            if (node.children && node.children.length > 0) {
                resolve(); // already loaded
                return;
            }
            this.roleService.getAllACLMenu().subscribe(
                (response: any) => {
                    const allMenus = response.datalist || [];
                    const children = allMenus.filter(
                        (menu: any) => menu.parentId === node.data.id
                    );
                    node.children = children.map((child) => ({
                        ...child,
                        name: child.code,
                        isSelected: false,
                        children: [],
                    }));
                    this.setNodeNames(node.children);
                    this.updateDataSource();
                    resolve();
                },
                (error) => {
                    console.error('Failed to load child nodes', error);
                    reject(error);
                }
            );
        });
    }


    addUpdateRole(): void {
        this.submitted = true;
        if (this.roleGroupForm.invalid) return;

        this.saveRolePermissions();

        if (this.rolePermissionList.length === 0) {
            (error: any) => { this.toastr.error(`${error.error.ERROR}`, 'Please select at least one operation permission.'); }
            return;
        }

        const role = {
            rolename: this.roleGroupForm.value.rolename,
            status: this.roleGroupForm.value.status,
            product: this.roleGroupForm.value.product,
            aclMenu: this.rolePermissionList,
        };

        let url = '/saveRole';
        if (this.editMode) {
            role['id'] = this.dataDlg.roleData.id;
            url = '/updateRole';
        }

        this.roleService.addUpdateRole(url, role, this.editMode).subscribe(
            (response: any) => {
                this.roleGroupForm.reset();
                this.rolePermissionList = [];
                this.submitted = false;
                this.toastr.success(`${response.responseMessage}`, "Successfully ");
                this.roleSaveorUpdated.emit(true);
                this.dialogRef.close(true);
            },
            (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    saveRolePermissions(): void {
        this.rolePermissionList = [];
        const traverseNodes = (nodes: ExtendedTreeNode[]): void => {
            nodes.forEach((node) => {
                this.createRolePermissionList(node);
                if (node.children) {
                    traverseNodes(node.children);
                }
            });
        };
        traverseNodes(this.data);
    }

    createRolePermissionList(node: ExtendedTreeNode): void {
        if (node.isSelected) {
            this.rolePermissionList.push({
                menuid: node.data.id,
                code: node.data.code,
            });
        }
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }
}
