import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_problem_domain_req.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_root_cause_req.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_tat_ticket_req.dart';
import 'package:savbill/pages/ticket_system/model/request/sub_problem_domain_req.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/root_cause_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_etr/model/ticket_etr_customer_request.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as multi;

class TicketETRCustomerProvider {
//Send ETR to Customer

  void sendTicketETRCustomer({
    TicketETRCustomerReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.ticket_etr_customer,
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
