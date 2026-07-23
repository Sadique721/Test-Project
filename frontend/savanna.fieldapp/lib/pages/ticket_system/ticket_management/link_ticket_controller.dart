import 'dart:convert';

import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
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

class LinkTicketController extends GetxController {
  bool isLoading = false, isShowLoadMore = false;
  TicketDetail? ticketDetail;
  int page = 1;

  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  List<TicketDetail>? ticketList = [];
  ViewTicketResponse? viewTicketResponse;

  ScrollController? controller;
  String? castTitle = "";

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (viewTicketResponse != null &&
            viewTicketResponse!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewLinkTicketList();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TICKET_DETAIL] != null) {
        ticketDetail = arguments[Constant.TICKET_DETAIL];
        if (ticketDetail != null &&
            ticketDetail!.caseTitle != null &&
            ticketDetail!.caseTitle!.isNotEmpty) {
          castTitle = ticketDetail!.caseTitle!;
        }
      }
    }
    update();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
    viewLinkTicketList();
  }

  viewLinkTicketList() {
    CustomerListRequest searchReq = CustomerListRequest();

    List<Filters>? filters = [];
    if (ticketDetail!.customersId != null) {
      filters.add(Filters(
          filterColumn: "customerId",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: ticketDetail!.customersId!.toString().trim()));
    }
    if (ticketDetail!.ticketReasonCategoryId != null) {
      filters.add(Filters(
          filterColumn: "ticketReasonCategoryId",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: ticketDetail!.ticketReasonCategoryId.toString().trim()));
    }

    if (ticketDetail!.caseId != null) {
      filters.add(Filters(
          filterColumn: "ticketIdToLink",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: ticketDetail!.caseId.toString().trim()));
    }

    searchReq.filters = filters;
    searchReq.page = page;
    searchReq.pageSize = 10;
    searchReq.sortBy = "createdate";
    searchReq.sortOrder = 0;

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TicketSystemProvider().viewTicketsList(
      isSearch: true,
      requestNormal: PageRequest(page: page, pageSize: 10),
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewTicketResponse responseData =
                  ViewTicketResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                viewTicketResponse = responseData;
                if (page == 1) {
                  ticketList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  ticketList?.clear();
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
            ticketList?.clear();
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
          ticketList?.clear();
        }
        handleApiError(error);
      },
    );
  }

  linkTicketApiCall(List<int> linkTicketIds) {
    isLoading = true;
    update();
    TicketSystemProvider().linkTicket(
      linkTicketIds: linkTicketIds,
      caseId: ticketDetail!.caseId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200) || (responseData.responseCode != null &&
                  responseData.responseCode == 0)) {
                Get.back(result: true);
              } else {
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
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  handleApiError(ResponseModel error) {
    isLoading = false;
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
