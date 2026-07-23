import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

import '../../util/constant.dart';
import '../model/page_request.dart';

class CustomerPlanProvider {
  // get customer ticket
  void getCustomerTickets({
    int? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    PageRequest pageRequest = PageRequest(page: 1, pageSize: Constant.PAGE_LOAD_DATA_LIMIT);
    ApiRequest(url: "${UrlConstants.customer_ticket}/$id", data: pageRequest).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}