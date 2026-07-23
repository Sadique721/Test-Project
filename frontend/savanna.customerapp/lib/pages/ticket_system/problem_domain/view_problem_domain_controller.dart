import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/problem_domain/add_edit_problem_domain.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ViewProblemDomainController extends GetxController {
  bool isLoading = false,
      isShowLoadMore = false,
      isFilterApply = false,
      filterViewOpen = false;
  ScrollController? controller;
  int page = 1;

  GetStorage getStorage = GetStorage();
  List<ProblemDomainDetail>? problemDomainList = [];
  ProblemDomainListRes? problemDomainListRes;

  TextEditingController searchController = TextEditingController();

  List<PlanServiceDetail>? planServiceList = [];
  PlanServiceDetail? selPlanService;

  @override
  void onInit() {
    super.onInit();
    getPlanServicesDetail();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (problemDomainListRes != null &&
            problemDomainListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewProblemDomainData();
        }
      }
    });
  }

  applyFilter() {
    if (searchController.text.isNullOrEmpty() && selPlanService == null) {
      isFilterApply = false;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    viewProblemDomainData();
  }

  clearFilter() {
    selPlanService = null;
    searchController.clear();
    page = 1;
    isFilterApply = false;
    filterViewOpen = false;
    update();
    viewProblemDomainData();
  }

  getPlanServicesDetail() {
    isLoading = true;
    planServiceList!.clear();
    update();
    CustomerProvider().getPlanService(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanServicesRes responseData = PlanServicesRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.serviceList != null &&
                    responseData.serviceList!.isNotEmpty) {
                  planServiceList!.addAll(responseData.serviceList!);
                }
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        viewProblemDomainData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        viewProblemDomainData();
      },
    );
  }

  viewProblemDomainData() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();

    if (isFilterApply) {
      List<Filters>? filters = [];
      if (!searchController.text.isNullOrEmpty()) {
        filters.add(Filters(
            filterColumn: "name",
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: searchController.text));
      }
      if (selPlanService != null) {
        filters.add(Filters(
            filterColumn: "service",
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: selPlanService!.name));
      }

      searchReq.filters = filters;
      searchReq.page = page;
      searchReq.pageSize = 10;
      searchReq.sortBy = "createdate";
      searchReq.sortOrder = 0;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TicketSystemProvider().viewProblemDomainList(
      isSearch: isFilterApply,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProblemDomainListRes responseData =
                  ProblemDomainListRes.fromJson(map);

              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                problemDomainListRes = responseData;
                if (page == 1) {
                  problemDomainList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  problemDomainList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  problemDomainList?.clear();
                }
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (page == 1) {
            problemDomainList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          problemDomainList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  addEditProblemDomainScreen(String from, ProblemDomainDetail? item) async {
    var result = await Get.to(AddEditProblemDomain(),
        arguments: {Constant.FROM: from, Constant.PD_DETAIL: item});

    if (result != null && result == true) {
      clearFilter();
    }
  }

  deleteProblemDomain(ProblemDomainDetail item, int index) {
    isLoading = true;
    update();
    TicketSystemProvider().deleteProblemDomain(
      request: item,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      (responseData.responseCode == 200 ||
                          responseData.responseCode == 0))) {
                problemDomainList!.removeAt(index);
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
