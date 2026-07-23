import 'package:savbill/pages/contact/contact.dart';
import 'package:savbill/pages/customer/customer_selection.dart';
import 'package:savbill/pages/customer_caf/add_edit_caf_customer.dart';
import 'package:savbill/pages/dashboard/home_tab_controller.dart';
import 'package:savbill/pages/dashboard/inventory_dashboard_dialog.dart';
import 'package:savbill/pages/dashboard/lead_dashboard_dialog.dart';
import 'package:savbill/pages/dashboard/model/data_list_item.dart';
import 'package:savbill/pages/dashboard/penidng_approvals_dialog.dart';
import 'package:savbill/pages/inventory_dashboard/product_qty_staff.dart';
import 'package:savbill/pages/lead_approval/assigne_lead/pa_assign_lead.dart';
import 'package:savbill/pages/lead_approval/lead_followup/pa_follow_up_lead.dart';
import 'package:savbill/pages/lead_approval/team_approval/pa_team_approval.dart';
import 'package:savbill/pages/lead_approval/team_follow_up/pa_team_follow_up_approval.dart';
import 'package:savbill/pages/lead_management/add_edit_lead/add_edit_lead_screen.dart';
import 'package:savbill/pages/pending_approvals/change_discount/pa_change_discount.dart';
import 'package:savbill/pages/pending_approvals/customers/pending_approval_customer.dart';
import 'package:savbill/pages/pending_approvals/document_cust/customer_document.dart';
import 'package:savbill/pages/pending_approvals/invoice/pa_invoice.dart';
import 'package:savbill/pages/pending_approvals/open_lead/pa_open_lead.dart';
import 'package:savbill/pages/pending_approvals/partner/pa_partner.dart';
import 'package:savbill/pages/pending_approvals/payments/pa_payment.dart';
import 'package:savbill/pages/pending_approvals/pending_inventory/inventory_pending_approval.dart';
import 'package:savbill/pages/pending_approvals/plan/pa_plan.dart';
import 'package:savbill/pages/pending_approvals/plan_group/pa_plan_group.dart';
import 'package:savbill/pages/pending_approvals/special_plan/pa_special_plan.dart';
import 'package:savbill/pages/pending_approvals/termination/pa_customer_termination.dart';
import 'package:savbill/pages/pending_approvals/tickets/pa_ticket.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/list_loader.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class HomeTab extends StatefulWidget {
  final HomeItemClickEvent homeItemClickEvent;

  HomeTab({Key? key, required this.homeItemClickEvent}) : super(key: key);

  @override
  _HomeTabState createState() => _HomeTabState();
}

class _HomeTabState extends State<HomeTab>with TickerProviderStateMixin
    implements PendingApprovalsItemAction,LeadDashboardItemAction,InventoryDashboardItemAction {
  List<ItemList> _dataList = [];
  final homeTabController = Get.put(HomeTabController());
  // late Animation<double> _animation;
  // late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    // _dataList.add(ItemList(id: 0, title: Strings.customers, icon: customers));

    if(PermissionService().hasAclPermission([AclSalesCRMs.CREATE_LEAD]) == true) {
      _dataList.add(ItemList(
          id: 8, title: Strings.create_lead, icon: inventory_dahboard));
    }
    // if(PermissionService().hasAclPermission([AclPreCustConstants.CREATE_PRE_CUST]) == true) {
    if(PermissionService().hasAclPermission([AclPreCustConstants.CREATE_PRE_CUST_CAF_LIST]) == true) {
      _dataList.add(ItemList(
          id: 9, title: Strings.create_caf, icon: inventory_dahboard));
    }
    if(PermissionService().hasAclPermission([AclDashboards.DASHBOARD_SALES_CRM]) == true) {
      _dataList.add(
          ItemList(id: 0, title: Strings.my_lead_pending_approvals, icon: lead_dashboad));
    }

    if(PermissionService().hasAclPermission([AclDashboards.DASHBOARD_APPROVAL]) == true) {
      _dataList.add(
          ItemList(id: 1, title: Strings.my_caf_pending_approvals, icon: customers));
    }

    if(PermissionService().hasAclPermission([AclTicketingSystems.TICKETING_SYSTEM]) == true) {
      _dataList.add(
          ItemList(id: 2, title: Strings.my_ticket, icon: openTicket));
    }

    if( PermissionService().hasAclPermission([AclPaymentSystems.PAYMENT_SYSTEM]) == true ) {
      _dataList.add(
          ItemList(id: 3, title: Strings.payment_records, icon: payments));
    }

    if(PermissionService().hasAclPermission([AclDashboards.DASHBOARD_APPROVAL]) == true) {
      _dataList.add(ItemList(
          id: 5, title: Strings.pending_approvals, icon: pending_approvals));
    }

    if(PermissionService().hasAclPermission([AclDashboards.DASHBOARD_SALES_CRM, AclSalesCRMs.LEAD]) == true) {
      _dataList.add(
          ItemList(id: 6, title: Strings.lead_dashboard, icon: lead_dashboad));
    }

    if(PermissionService().hasAclPermission([AclDashboards.DASHBOARD_INVENTORY]) == true) {
      _dataList.add(ItemList(
          id: 7, title: Strings.inventory_dashboard, icon: inventory_dahboard));
    }

    _dataList.add(ItemList(id: 4, title: Strings.contact, icon: connect));
    // _controller = AnimationController(
    //   duration: const Duration(seconds: 2),
    //   vsync: this,
    // )..repeat(reverse: true);
    // final tween = Tween<double>(begin: 0.8, end: 1.0);
    // _animation = ReverseTween(tween).animate(_controller);
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<HomeTabController>(builder: (controller) {
      return Stack(children: <Widget>[
        homeTabController.isLoading
            ? Padding(
                padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                child: ListView.separated(
                  itemCount: 5,
                  itemBuilder: (context, index) => const ListLoader(),
                  separatorBuilder: (context, index) =>
                      const SizedBox(height: Constant.SCREEN_PADDING),
                ),
              )
            : Container(
                width: MediaQuery.of(context).size.width,
                padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                decoration: BoxDecoration(
                  color: AppTheme.colorWhite
                  // image: DecorationImage(
                  //   fit: BoxFit.cover,
                  //   image: AssetImage(
                  //     dashboardBgYellow,
                  //   ),
                  // ),
                ),
                child: Column(children: <Widget>[
                  Expanded(
                    child: GridView.builder(
                      itemCount: _dataList.length,
                      gridDelegate:
                          const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 2,
                        childAspectRatio: 1,
                      ),
                      itemBuilder: (BuildContext context, int index) {
                        return makeDashboardItem(_dataList[index]);
                      },
                    ),
                  )
                ]),
              ),
        // ProgressBar(isLoader: homeTabController.isLoading)
      ]);
    });
  }

  Card makeDashboardItem(ItemList itemDetail) {
    return Card(
      elevation: 2,
      margin: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: InkWell(
        onTap: () {
          //customer dialog
          // if (itemDetail.id == 0) {
          //   openCustomerScreen();
          // }
          if (itemDetail.id == 0) {
            openMyAssignLeadApprovalScreen();
          }
          if (itemDetail.id == 1) {
            openCustomerPendingApprovalScreen();
          }
          if (itemDetail.id == 2) {
            myTicketsScreen();
          }
          if (itemDetail.id == 3) {
            openRenewScreen();
          }

          if (itemDetail.id == 5) {
            showApprovalPendingDialog();
          }

          if (itemDetail.id == 6) {
            showLeadDashboardDialog();
          }

          if (itemDetail.id == 7) {
            showInventoryDashboardDialog();
          }
          if (itemDetail.id == 4) {
            openContactsScreen();
          }


          if (itemDetail.id == 8) {
            openCreateLeadScreen(Strings.add);
          }
          if (itemDetail.id == 9) {
            openCustomerCAFScreen();
          }
        },
        child: Container(
          alignment: Alignment.center,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(Constant.SMALL_PADDING),
            color: AppTheme.colorWhite,
            boxShadow: [
              BoxShadow(
                color: AppTheme.colorPrimary.withOpacity(1),
                blurRadius: 5,
                offset: Offset(4, 5),
              )
            ],
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            mainAxisSize: MainAxisSize.min,
            verticalDirection: VerticalDirection.down,
            children: [
              // AnimatedBuilder(
              // animation: _animation,
              // builder: (context, child) {
              //   return Transform.scale(
              //     scale: _animation.value,
              //     child: Image.asset(
              //       itemDetail.icon!,
              //       color: Colors.black,
              //       height: Constant.BIG_ICON_SIZE,
              //       width: Constant.BIG_ICON_SIZE,
              //     ),
              //   );
              // }
              // ),
              Image.asset(
                itemDetail.icon!,
                color: Colors.black,
                height: Constant.BIG_ICON_SIZE,
                width: Constant.BIG_ICON_SIZE,
              ),
              const SizedBox(height: Constant.LARGE_PADDING),
              CustomText(
                  title: itemDetail.title,
                  fontSize: AppTheme.medium + 1,
                  fontWeight: FontWeight.w500,
                  colors: AppTheme.colorBlack,
                  maxLines: 2),
            ],
          ),
        ),
      ),
    );
  }

  showCustomerSelectionPopup() {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return const CustomerSelectionDialog();
        });
  }

  openCustomerScreen() async {
    showCustomerSelectionPopup();
     /*Get.to(CustomerList(), arguments: {
      Constant.CUSTOMER_TYPE: Strings.prepaid,
    });*/
  }

  openCustomerPendingApprovalScreen() async {
    Get.to(()=>PendingApprovalCustomer());
  }

  openMyAssignLeadApprovalScreen() async {
    Get.to(()=>PendingApprovalAssignLead());
  }

  myTicketsScreen() async {
    /*bool chkRefresh = await Get.to(CreateTicket());
    if (chkRefresh) {
      widget.homeItemClickEvent.itemClick(3);
    }*/
    widget.homeItemClickEvent.itemClick(2);
  }

  openRenewScreen() async {
    widget.homeItemClickEvent.itemClick(1);
  }

  openContactsScreen() async {
    bool chkRefresh = await Get.to(Contact());
  }

  showApprovalPendingDialog() {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return PendingApprovalsDialog(pendingApprovalsItemAction: this);
        });
  }

  openCreateLeadScreen(String? from) async {
    var result = await Get.to(() => AddEditLeadScreen(), arguments: {
      Constant.FROM: from,
      Constant.LEAD_DETAIL: null,
    });
  }
  openCustomerCAFScreen() {
    Get.to(
      AddEditCafCustomer(),
      arguments: {
        Constant.ACTION: Strings.add,
        Constant.CUSTOMER_TYPE: Strings.prepaid,
      },
    );
  }

  showLeadDashboardDialog() {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return LeadDashboardDialog(leadDashboardDialogItemAction: this);
        });
  }

  showInventoryDashboardDialog() {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return InventoryDashboardDialog(inventoryDashboardDialogItemAction: this);
        });
  }

  @override
  void pendingApprovalsItemAction({ItemList? item}) {
    // Get.back();
    if (item!.id == 1) {
      Get.to(()=>PendingApprovalCustomer());
    } else if (item.id == 2) {
      Get.to(()=>PACustomerTermination());
    } else if (item.id == 3) {
      Get.to(()=>PendingApprovalPlan());
    } else if (item.id == 4) {
      Get.to(()=>PendingApprovalPlanGroup());
    } else if (item.id == 5) {
      Get.to(()=>PendingApprovalPayment());
    } else if (item.id == 6) {
      Get.to(()=>PendingApprovalTicket());
    } else if (item.id == 7) {
      Get.to(()=>PendingApprovalChangeDiscount());
    } else if (item.id == 8) {
      Get.to(()=>PendingApprovalInvoice());
    } else if (item.id == 9) {
      Get.to(()=>PendingApprovalPartner());
    }else if (item.id == 10) {
      Get.to(()=>PendingApprovalOpenLead());
    }else if (item.id == 11){
      Get.to(()=>CustomerDocumentApproval());
    }else if (item.id == 12){
      Get.to(()=>SpecialPlanMapping());
    }  else if (item.id == 13){
      Get.to(()=>InventoryPendingApproval());
    }
  }

  @override
  void leadDashboardItemAction({ItemList?item}) {
    if (item!.id == 1) {
      Get.to(()=>PendingApprovalAssignLead());
    } else if (item.id == 2) {
      Get.to(()=>PendingApprovalTeamApprovalLead());
    } else if (item.id == 3) {
      Get.to(()=>PendingApprovalFollowUpLead());
    } else if (item.id == 4) {
      Get.to(()=>PendingApprovalTeamFollowUpApprovalLead());
    }
  }

  @override
  void inventoryDashboardItemAction({ItemList? item}) {
    if (item!.id == 1) {
      Get.to(()=>GetProductQtyStaff(),arguments:  {
      Constant.INVENTORY_TYPE: Strings.staff,
      });
    } else if (item.id == 2) {
      Get.to(()=>GetProductQtyStaff(),arguments:  {
        Constant.INVENTORY_TYPE: Strings.ware_house,
      });
    }
  }
}

abstract class HomeItemClickEvent {
  void itemClick(int identity);
}
