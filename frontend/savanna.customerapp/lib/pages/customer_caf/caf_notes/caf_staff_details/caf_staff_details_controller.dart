import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_caf/response/cust_caf_staff_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CafStaffDetailsController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  CustomerDetail? customerDetail;
  CafStaffNotesData? staffDetail;

  String? customerType;
  List<CustomerPlanServiceDetail>? customerServiceList = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }



  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }

    }
    if(customerDetail != null){
      getStaff();
    }

  }

  getStaff() {
    isLoading = true;
    update();
    CustomerProvider().getStaffUser(
      Id: customerDetail?.createdById,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustCafStaffRes responseData = CustCafStaffRes.fromJson(map);
              if ((responseData.status == 200 && responseData.status != null)) {
                if (responseData.staff != null) {
                  staffDetail = responseData.staff;
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.status,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
