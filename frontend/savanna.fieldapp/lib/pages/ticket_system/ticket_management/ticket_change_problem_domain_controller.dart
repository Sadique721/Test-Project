import 'dart:convert';

import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/model/request/edit_ticket_request.dart';
import 'package:savbill/pages/ticket_system/model/response/create_ticket_active_service_res.dart';
import 'package:savbill/pages/ticket_system/model/response/get_reason_category_active_services_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart' as dia;
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class TicketChangeProblemDomainController extends GetxController {
  bool isLoading = false, isShowLoadMore = false;
  TicketDetail? ticketDetail;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  String? castTitle = "";

  // List<ProblemDomainDetail>? problemDomainList = [];
  // ProblemDomainDetail? selectedProblemDomain;

  // List<SubProblemDomainDetail>? subProblemDomainList = [];
  // SubProblemDomainDetail? selectedSubProblemDomain;

  TextEditingController remarksController = TextEditingController();

  List<GetActiveServiceDataList>? servicesAreaList = [];
  List<GetActiveServiceDataList>? selectedServicesArea = [];
  GetActiveServiceDataList? selectServiceListData;

  List<ReasonCategoryDataList>? reasonCategoryDataList = [];
  ReasonCategoryDataList? selectTicketProblemDomain;

  List<SubProblemDomainDetail>? ticketSubProblemDomainList = [];
  SubProblemDomainDetail? selectedTicketSubProblemDomain;

  List<TicketSubCategoryGroupReasonMappingList>? ticketReasonMappingList = [];
  TicketSubCategoryGroupReasonMappingList? selectedReasonMapping;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
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
    // getCustomerReasonCategory();
    getActiveServiceForSubscribers();
  }

  // getCustomerReasonCategory() {
  //   isLoading = true;
  //   selectedSubProblemDomain = null;
  //   selectedProblemDomain = null;
  //   selectedReasonMapping = null;
  //   problemDomainList!.clear();
  //   subProblemDomainList!.clear();
  //   ticketReasonMappingList!.clear();
  //   update();
  //   TicketSystemProvider().getReasonCategoryByCustomer(
  //     customerId: ticketDetail!.customersId!,
  //     onSuccess: (ResponseModel responseModel) {
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ProblemDomainListRes responseData =
  //                 ProblemDomainListRes.fromJson(map);
  //             if ((responseData.status != null && responseData.status == 200) ||
  //                 (responseData.responseCode != null &&
  //                     responseData.responseCode == 200)) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 problemDomainList!.addAll(responseData.dataList!);
  //               }
  //             } else {
  //               if (responseData.responseMessage != null &&
  //                   responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       handleApiError(error);
  //     },
  //   );
  // }

  getActiveServiceForSubscribers() {
    isLoading = true;
    servicesAreaList!.clear();
    update();
    TicketSystemProvider().getActiveServiceForSubscribers(
      id: ticketDetail!.customersId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CreateTicketActiveServiceRes responseData =
                  CreateTicketActiveServiceRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  servicesAreaList!.addAll(responseData.dataList!);
                  update();
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        update();
        handleApiError(error);
      },
    );
  }

  // getSubCategory() {
  //   isLoading = true;
  //   selectedSubProblemDomain = null;
  //   selectedReasonMapping = null;
  //   subProblemDomainList!.clear();
  //   ticketReasonMappingList!.clear();
  //   update();
  //   TicketSystemProvider().getSubCategoryByParentCategory(
  //     categoryId: selectedProblemDomain!.id!,
  //     onSuccess: (ResponseModel responseModel) {
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             SubProblemDomainListRes responseData =
  //                 SubProblemDomainListRes.fromJson(map);
  //             if ((responseData.status != null && responseData.status == 200) ||
  //                 (responseData.responseCode != null &&
  //                     responseData.responseCode == 200)) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 subProblemDomainList!.addAll(responseData.dataList!);
  //               }
  //             } else {
  //               if (responseData.responseMessage != null &&
  //                   responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       handleApiError(error);
  //     },
  //   );
  // }

  getTicketReasonCategoryByActiveServices(List<int>? ids) {
    isLoading = true;
    reasonCategoryDataList!.clear();
    update();
    TicketSystemProvider().ticketGetReasonCategoryByActiveServices(
      ids: ids,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetReasonCategoryByActiveServicesRes responseData =
                  GetReasonCategoryByActiveServicesRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  reasonCategoryDataList!.addAll(responseData.dataList!);
                  update();
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        update();
        handleApiError(error);
      },
    );
  }

  getTicketSubProblemDomainCategory(int? ticketProblemCategoryId) {
    isLoading = true;
    selectedTicketSubProblemDomain = null;
    ticketSubProblemDomainList!.clear();
    // selectedReasonMapping = null;
    ticketReasonMappingList!.clear();
    ticketReasonMappingList!.clear();
    selectedReasonMapping = null;

    update();
    TicketSystemProvider().getSubCategoryByParentCategory(
      categoryId: ticketProblemCategoryId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SubProblemDomainListRes responseData =
                  SubProblemDomainListRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketSubProblemDomainList!.addAll(responseData.dataList!);
                  // for (SubProblemDomainDetail element
                  // in ticketSubProblemDomainList!) {
                  //   if (element.id == ticketDetail!.reasonSubCategoryId) {
                  //     selectedTicketSubProblemDomain = element;
                  //     break;
                  //   }
                  // }
                }
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
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  setReasonData() {
    selectedReasonMapping = null;
    ticketReasonMappingList!.clear();
    update();
    if (selectedTicketSubProblemDomain != null &&
        selectedTicketSubProblemDomain!
            .ticketSubCategoryGroupReasonMappingList !=
            null &&
        selectedTicketSubProblemDomain!
            .ticketSubCategoryGroupReasonMappingList!.isNotEmpty) {
      ticketReasonMappingList!.addAll(selectedTicketSubProblemDomain!
          .ticketSubCategoryGroupReasonMappingList!);
      update();
    }
  }

  ticketUpdateCall() {
    EditTicketRequest editRequest = EditTicketRequest(
      ticketId: ticketDetail!.caseId,
      status: ticketDetail!.caseStatus!,
      remark: remarksController.text,
      remarkType: "Change Problem Domain",
      groupReasonId:
          selectedReasonMapping != null ? selectedReasonMapping!.id : null,
      // reasonSubCategoryId: selectedSubProblemDomain != null
      //     ? selectedSubProblemDomain!.id!
      //     : null,

      reasonSubCategoryId: selectedTicketSubProblemDomain != null ? selectedTicketSubProblemDomain!.id : null,
      ticketReasonCategoryId:
      selectTicketProblemDomain != null ? selectTicketProblemDomain!.id! : null,
    );
    Map<String, dynamic> map = {};
    map["caseUpdate"] = jsonEncode(editRequest);
    dia.FormData formData = dia.FormData.fromMap(map);
    isLoading = true;
    update();
    TicketSystemProvider().addEditCaseTicketsRequest(
      isAdd: false,
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode != null &&
                responseData.responseCode == 200) {
              Get.back(result: true);
            } else {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
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
