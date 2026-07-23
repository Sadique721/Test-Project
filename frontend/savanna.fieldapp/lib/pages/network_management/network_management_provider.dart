import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/network_management/model/request/device_location_update_req.dart';
import 'package:savbill/pages/network_management/model/request/device_port_bind_req.dart';
import 'package:savbill/pages/network_management/model/request/network_add_device_req.dart';
import 'package:savbill/pages/network_management/model/request/save_ip_management_req.dart';
import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/pages/network_management/model/response/get_ip_management_list_res.dart';
import 'package:savbill/pages/network_management/model/response/ip_management_list_res.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

class NetworkManagementProvider {
  // Get device list
  void getDeviceList({
    required bool isSearch,
    CustomerListRequest? searchRequest,
    PageRequest? requestNormal,
    String? deviceType,
    String? deviceName,
    String? deviceProductName,
    String? serviceName,
    String? status,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.device_list}list";
    if (isSearch) {
      //http://59.144.18.212:30080/api/v1/NetworkDevice/searchNetworkDevices?devicetype=Router
      url = "${UrlConstants.device_list}searchNetworkDevices?";

      if (deviceType != null && deviceType.isNotEmpty) {
        url = "${url}devicetype=$deviceType&";
      }
      if (deviceName != null && deviceName.isNotEmpty) {
        url = "${url}name=$deviceName&";
      }
      if (deviceProductName != null && deviceProductName.isNotEmpty) {
        url = "${url}productName=$deviceProductName&";
      }
      if (serviceName != null && serviceName.isNotEmpty) {
        url = "${url}serviceName=$serviceName&";
      }
      if (status != null && status.isNotEmpty) {
        url = "${url}status=$status";
      }
    }
    ApiRequest(url: url, data: isSearch ? searchRequest : requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete device
  void deleteDevice({
    DeviceDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.delete_device, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Get device list
  void getDeviceDetail({
    required int deviceId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.device_detail + deviceId.toString())
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // update device detail
  void updateDeviceDetail({
    DeviceLocationUpdateReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.update_device_location, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Get Device Port Availability Data
  void getDevicePortAvailabilityData({
    required int parentDeviceId,
    required String parentPortType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.check_port_availability}?parentDeviceId=$parentDeviceId&parentPortType=$parentPortType")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Get bind port Device Data
  void getBindPortDeviceData({
    required int parentDeviceId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.get_bind_device_port_detail}?id=$parentDeviceId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Get parent device for bind port

  void getParentDeviceForBindPort({
    required int parentDeviceId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.get_parent_device_for_bind}?id=$parentDeviceId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // update device port binding
  void updateDevicePortBind({
    DevicePortBindReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.update_device_port_bind, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get network device type
  void getNetworkDeviceType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.device_type).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get network device product
  void getNetworkDeviceProduct({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.network_device_product).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // get network inward product list
  void getNetworkInwardProduct({
    required int? productId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.network_inward_product}?productId=$productId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // add network device
  void addNetworkDevice({
    NetworkAddDeviceReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.network_add_product, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }




  void getIPPoolListManagement({
    PageRequest? requestNormal,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.ip_pool_list;
    ApiRequest(url: url, data: requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


// get Ip List with particular id
  void getIPPoolListWithId({
    required int? ipPoolId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.ip_pool_list}/$ipPoolId";
    ApiRequest(url: url)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


//save Ip management

  void saveIpManagement({
    SaveIpManagmentReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.save_ip_management;
    ApiRequest(url: url, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  //update Ip management

  void updateIpManagement({
    SaveIpManagmentReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.update_ip_management;
    ApiRequest(url: url,data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  //delete Ip management
  void deleteIpManagement({
    required IpManagementDataList? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.delete_ip_management;
    ApiRequest(url: url,data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
