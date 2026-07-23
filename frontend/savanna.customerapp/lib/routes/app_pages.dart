import 'package:savbill/pages/change_discount/change_discount.dart';
import 'package:savbill/pages/change_plan/change_plan.dart';
import 'package:savbill/pages/change_plan/select_plan_group.dart';
import 'package:savbill/pages/connection_history/connection_history.dart';
import 'package:savbill/pages/contact/contact.dart';
import 'package:savbill/pages/credit_note/view_credit_note.dart';
import 'package:savbill/pages/customer/add_edit_customer.dart';
import 'package:savbill/pages/customer/assign_inventory.dart';
import 'package:savbill/pages/customer/customer_detail.dart';
import 'package:savbill/pages/customer/customer_document.dart';
import 'package:savbill/pages/customer/customer_list.dart';
import 'package:savbill/pages/customer/doc_create/create_doc_cust.dart';
import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer_caf/customer_caf_list.dart';
import 'package:savbill/pages/customer_change_status/change_status_list.dart';
import 'package:savbill/pages/customer_charge/add_charge.dart';
import 'package:savbill/pages/customer_charge/charge_management.dart';
import 'package:savbill/pages/customer_inventory/inventory_detail.dart';
import 'package:savbill/pages/customer_invoice/customer_invoice.dart';
import 'package:savbill/pages/customer_ledger/customer_ledger.dart';
import 'package:savbill/pages/customer_payment/customer_paymentlist.dart';
import 'package:savbill/pages/customer_plan/customer_plan.dart';
import 'package:savbill/pages/customer_ticket/customer_ticket.dart';
import 'package:savbill/pages/dashboard/case_assign.dart';
import 'package:savbill/pages/dashboard/dashboard.dart';
import 'package:savbill/pages/dashboard/record_payment.dart';
import 'package:savbill/pages/dashboard/ticket_detail.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_inventories.dart';
import 'package:savbill/pages/inventory/category/add_edit_category.dart';
import 'package:savbill/pages/inventory/category/view_category.dart';
import 'package:savbill/pages/inventory/inventory_home.dart';
import 'package:savbill/pages/inventory/inwards/add_edit_inwards.dart';
import 'package:savbill/pages/inventory/inwards/inward_mapping.dart';
import 'package:savbill/pages/inventory/inwards/view_inwards.dart';
import 'package:savbill/pages/inventory/outwards/add_edit_outwards.dart';
import 'package:savbill/pages/inventory/outwards/outward_mapping.dart';
import 'package:savbill/pages/inventory/outwards/view_outwards.dart';
import 'package:savbill/pages/inventory/pop/add_edit_pop.dart';
import 'package:savbill/pages/inventory/pop/view_pop.dart';
import 'package:savbill/pages/inventory/product/add_edit_product.dart';
import 'package:savbill/pages/inventory/product/view_product.dart';
import 'package:savbill/pages/inventory/warehouse/add_edit_warehouse.dart';
import 'package:savbill/pages/inventory/warehouse/view_warehouse.dart';
import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/pages/lead_management/view_lead.dart';
import 'package:savbill/pages/login/login.dart';
import 'package:savbill/pages/network_management/device_list.dart';
import 'package:savbill/pages/network_management/ip/ip_management.dart';
import 'package:savbill/pages/shift_location/shift_location.dart';
import 'package:savbill/pages/splash/splash.dart';
import 'package:savbill/pages/task_management/task_system_home.dart';
import 'package:savbill/pages/ticket_system/ticket_management/create_ticket.dart';
import 'package:savbill/pages/ticket_system/ticket_system_home.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:get/get.dart';

class AppPages {
  static var list = [
    GetPage(
      name: AppRoutes.SPLASH,
      page: () => const SplashScreen(),
    ),
    GetPage(
      name: AppRoutes.LOGIN,
      page: () => LoginScreen(),
    ),
    GetPage(
      name: AppRoutes.DASHBOARD,
      page: () => DashboardScreen(),
    ),
    GetPage(
      name: AppRoutes.CREATE_TICKET,
      page: () => CreateTicket(),
    ),
    GetPage(
      name: AppRoutes.CASE_ASSIGN,
      page: () => CaseAssign(),
    ),
    GetPage(
      name: AppRoutes.TICKET_DETAIL,
      page: () => TicketDetailScreen(),
    ),
    GetPage(
      name: AppRoutes.CONTACT,
      page: () => Contact(),
    ),
    GetPage(
      name: AppRoutes.RECORD_PAYMENT,
      page: () => RecordPayment(),
    ),
    GetPage(
      name: AppRoutes.CUSTOMER_LIST,
      page: () => CustomerList(),
    ),
    GetPage(
      name: AppRoutes.CUSTOMER_DETAIL,
      page: () => CustomerDetailScreen(),
    ),
    GetPage(
      name: AppRoutes.ASSIGN_INVENTORY,
      page: () => AssignInventory(),
    ),
    GetPage(
      name: AppRoutes.CUSTOMER_DOCUMENT,
      page: () => CustomerDocumentList(),
    ),
    GetPage(
      name: AppRoutes.ADD_EDIT_CUSTOMER,
      page: () => AddEditCustomer(),
    ),
    GetPage(
      name: AppRoutes.PARENT_CUSTOMER_LIST,
      page: () => ParentCustomerList(),
    ),
    GetPage(
      name: AppRoutes.LOCATION_LIST,
      page: () => LocationList(),
    ),
    GetPage(
      name: AppRoutes.PLAN_DETAIL,
      page: () => CustomerPlanDetail(),
    ),
    GetPage(
      name: AppRoutes.CUST_INVOICE_DETAIL,
      page: () => CustomerInvoiceDetail(),
    ),
    GetPage(
      name: AppRoutes.CUST_LEDGER_DETAIL,
      page: () => CustomerLedgerDetail(),
    ),
    GetPage(
      name: AppRoutes.CUST_PAYMENT_LIST,
      page: () => CustomerPaymentList(),
    ),
    GetPage(
      name: AppRoutes.CONNECTION_HISTORY,
      page: () => ConnectionHistory(),
    ),
    GetPage(
      name: AppRoutes.CUST_TICKETS,
      page: () => CustomerTicketDetail(),
    ),
    GetPage(
      name: AppRoutes.CUST_INVENTORY,
      page: () => CustomerInventoryDetail(),
    ),
    GetPage(
      name: AppRoutes.CHANGE_PLAN,
      page: () => ChangePlan(),
    ),
    GetPage(
      name: AppRoutes.CHANGE_DISCOUNT,
      page: () => CustomerChangeDiscount(),
    ),
    GetPage(
      name: AppRoutes.CUST_CHANGE_STATUS,
      page: () => CustomerChangeStatus(),
    ),
    GetPage(
      name: AppRoutes.CUST_CHARGE_MANAGEMENT,
      page: () => ChargeManagement(),
    ),
    GetPage(
      name: AppRoutes.CREATE_CHARGE,
      page: () => AddCharge(),
    ),
    GetPage(
      name: AppRoutes.SHIFT_LOCATION,
      page: () => ShiftLocation(),
    ),
    GetPage(
      name: AppRoutes.PLAN_GROUP_TO_PLAN,
      page: () => SelectPlanGroup(),
    ),
    GetPage(
      name: AppRoutes.INVENTORY_HOME,
      page: () => InventoryHome(),
    ),
    GetPage(
      name: AppRoutes.VIEW_INVENTORY_CATEGORY,
      page: () => ViewCategory(),
    ),
    GetPage(
      name: AppRoutes.ADD_EDIT_INVENTORY_CATEGORY,
      page: () => AddEditCategory(),
    ),
    GetPage(
      name: AppRoutes.VIEW_INVENTORY_PRODUCT,
      page: () => ViewProduct(),
    ),
    GetPage(
      name: AppRoutes.ADD_EDIT_INVENTORY_PRODUCT,
      page: () => AddEditProduct(),
    ),
    GetPage(
      name: AppRoutes.VIEW_INVENTORY_POP,
      page: () => ViewPopList(),
    ),
    GetPage(
      name: AppRoutes.ADD_EDIT_INVENTORY_POP,
      page: () => AddEditPop(),
    ),
    GetPage(
      name: AppRoutes.VIEW_INVENTORY_WAREHOUSE,
      page: () => ViewWareHouse(),
    ),
    GetPage(
      name: AppRoutes.ADD_EDIT_INVENTORY_WAREHOUSE,
      page: () => AddEditWareHouse(),
    ),
    GetPage(
      name: AppRoutes.VIEW_INVENTORY_WAREHOUSE,
      page: () => ViewInwards(),
    ),
    GetPage(
      name: AppRoutes.ADD_EDIT_INVENTORY_INWARDS,
      page: () => AddEditInward(),
    ),
    GetPage(
      name: AppRoutes.INWARD_MAC_MAP_DETAIL,
      page: () => InwardMappingDetail(),
    ),
    GetPage(
      name: AppRoutes.VIEW_INVENTORY_OUTWARDS,
      page: () => ViewOutwards(),
    ),
    GetPage(
      name: AppRoutes.ADD_EDIT_INVENTORY_OUTWARDS,
      page: () => AddEditOutwards(),
    ),
    GetPage(
      name: AppRoutes.OUTWARD_MAC_MAP_DETAIL,
      page: () => OutwardMappingDetail(),
    ),
    GetPage(
      name: AppRoutes.ASSIGNED_INVENTORIES,
      page: () => AssignedInventories(),
    ),
    GetPage(
      name: AppRoutes.TICKET_SYSTEM,
      page: () => TicketSystemHome(),
    ),
    GetPage(
      name: AppRoutes.NETWORK_MANAGEMENT,
      page: () => DeviceList(),
    ),
    GetPage(
      name: AppRoutes.IP_MANAGEMENT,
      page: () => IpManagementList(),
    ),
    GetPage(
      name: AppRoutes.CREDIT_NOTE,
      page: () => ViewCreditNote(),
    ),
    GetPage(
      name: AppRoutes.CREATE_CUST_DOCUMENT,
      page: () => CreateDocumentCustomer(),
    ),

    GetPage(
      name: AppRoutes.CUSTOMER_CAF_LIST,
      page: () => CustomerCafList(),
    ),

    GetPage(
      name: AppRoutes.LEAD_MANAGEMENT,
      page: () => ViewLead(),
    ),
    GetPage(
      name: AppRoutes.TASK_MANAGEMENT,
      page: () => TaskSystemHome(),
    ),
  ];
}
