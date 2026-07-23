import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

class RevenueReportProvider {

  /// get Revenue Report filter
  void getRevenueReport({
    required int? custId,
    required String? startDate,
    required String? endDate,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.customer_revenue_report}?custid=$custId&startdate=$startDate&endate=$endDate").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  /// get BalanceAndCommissionInfoForShiftLocation

  void getBalanceAndCommissionInfoForShiftLocation({
    required int? custId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.balanceAndCommissionInfoForShiftLocation}/$custId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


}