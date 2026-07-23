import 'package:savbill/pages/shift_location/request/approve_customer_address_req.dart';
import 'package:savbill/pages/shift_location/request/shift_cust_location_req.dart';
import 'package:savbill/pages/shift_location/request/update_customer_address.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

class ShiftLocationProvider {
  // Get Partner Service Data
  void getPartnerServiceList({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.partner_service_data + id.toString())
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // update customer address
  void updateCustomerAddress({
    required int customerId,
    ShiftCustomerLocationReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: "${UrlConstants.update_cust_location}$customerId",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // update customer address
  void getShiftLocationNewAddressData({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.customer_new_address_shift_location_list}$customerId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // update customer address
  void approveCustomerAddressShiftLocation({
    required ApproveCustomerAddressReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.approveCustomerAddress,data: request)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

}
