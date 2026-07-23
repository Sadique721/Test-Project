

import 'package:savbill/pages/task_management/model/request/task_etr_customer_req.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_etr/model/ticket_etr_customer_request.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

class TaskETRCustomerProvider {
//Send ETR to Customer

  void sendTaskETRCustomer({
    TaskETRCustomerReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.task_etr_customer,
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