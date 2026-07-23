import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/ticket_system/model/response/condition_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_for_ticket_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
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

import '../model/request/sub_problem_domain_req.dart';

class AddEditSubProblemDomainController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController subProblemDomainNameController =
      TextEditingController();
  TextEditingController reasonController = TextEditingController();
  TextEditingController conditionController = TextEditingController();
  TextEditingController parentCategoryController = TextEditingController();

  List<int> parentCategoryId = [];
  int page = 1;
  UserDetail? userDetail;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  List<ProblemDomainDetail>? parentCategoryList = [];
  List<ProblemDomainDetail>? newParentCategoryList = [];
  ProblemDomainDetail? selParentCategory;

  List<int> selectedParentCategoryIds = [];

  List<TatTicketDetail>? tatTicketList = [];
  TatTicketDetail? selectedTatTicket;

  List<SubProblemDomainDetail>? subProblemTATTicketList = [];
  SubProblemDomainDetail? selectedSubProblemDomainData;
  SubProblemDomainListRes? subProblemDomainListRes;

  List<TatQueryFieldMappingList>? selectedCondition;
  List<ConditionDetail>? conditionList = [];

  List<TatTicketDetail>tatForDataList =[];
  TatTicketDetail? selectedTatForData;

  List<TicketSubCategoryGroupReasonMappingList>?
      ticketSubCategoryGroupReasonMappingList = [];

  List<TicketSubCategoryTatMappingList>? ticketSubCategoryTatMappingList = [];

  List<TicketSubCategoryReasonCategoryMappingList>?
      ticketSubCategoryReasonCategoryMappingList = [];

  TicketSubCategoryReasonCategoryMappingList?
      selectTicketSubCategoryReasonCategoryMappingList;

  String from = Strings.add;
  SubProblemDomainDetail? subProblemDomainDetail;
  bool chkParentCategory = false;
  int orderId = 1;

  List<TicketSubCategoryReasonCategoryMappingList>?
      selectedSubCategoryResMappingList = [];

  @override
  void onInit() {
    super.onInit();
    statusList!.add(DropdownDetail(
        id: Strings.active.toUpperCase(),
        text: Strings.active,
        type: Strings.status));
    statusList!.add(DropdownDetail(
        id: Strings.in_active.toUpperCase(),
        text: Strings.in_active,
        type: Strings.status));
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.SPD_DETAIL] != null) {
        subProblemDomainDetail = arguments[Constant.SPD_DETAIL];
      }
      if (subProblemDomainDetail != null) {
        subProblemDomainNameController.text =
            subProblemDomainDetail!.subCategoryName!;

        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(subProblemDomainDetail!.status!)) {
            selectedStatus = element;
            break;
          }
        }

        if (subProblemDomainDetail!.parentCategory != null) {
          chkParentCategory = true;
        }

        if (subProblemDomainDetail!.ticketSubCategoryGroupReasonMappingList !=
                null &&
            subProblemDomainDetail!
                .ticketSubCategoryGroupReasonMappingList!.isNotEmpty) {
          ticketSubCategoryGroupReasonMappingList!.addAll(
              subProblemDomainDetail!.ticketSubCategoryGroupReasonMappingList!);
        }

        if (subProblemDomainDetail!.ticketSubCategoryTatMappingList != null &&
            subProblemDomainDetail!
                .ticketSubCategoryTatMappingList!.isNotEmpty) {
          for (TicketSubCategoryTatMappingList value
              in subProblemDomainDetail!.ticketSubCategoryTatMappingList!) {
            String conditionText = "";
            if (value.tatQueryFieldMappingList != null &&
                value.tatQueryFieldMappingList!.isNotEmpty) {
              for (TatQueryFieldMappingList element
                  in value.tatQueryFieldMappingList!) {
                if (element.queryCondition != null &&
                    element.queryCondition!.isNotEmpty) {
                  conditionText =
                      "$conditionText${element.queryField!} ${element.queryOperator!} ${element.queryValue!} ${element.queryCondition!} ";
                } else {
                  conditionText =
                      "$conditionText${element.queryField!} ${element.queryOperator!} ${element.queryValue!} ";
                }
              }
            }
            value.txtCondition = conditionText;
            ticketSubCategoryTatMappingList!.add(value);
          }
        }

        if (subProblemDomainDetail!
                    .ticketSubCategoryReasonCategoryMappingList !=
                null &&
            subProblemDomainDetail!
                .ticketSubCategoryReasonCategoryMappingList!.isNotEmpty) {
          ticketSubCategoryReasonCategoryMappingList!.addAll(
              subProblemDomainDetail!
                  .ticketSubCategoryReasonCategoryMappingList!);
        }

        orderId = ticketSubCategoryTatMappingList!.length + 1;
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
    getParentCategory();
  }

  addTicketMapping() {
    ticketSubCategoryTatMappingList!.add(TicketSubCategoryTatMappingList(
        ticketReasonSubCategoryId:
            subProblemDomainDetail != null ? subProblemDomainDetail!.id : null,
        // ticketTatMatrix: selectedTatTicket,
        ticketTatMatrix: selectedTatForData,
        tatQueryFieldMappingList: selectedCondition,
        orderid: orderId,
        txtCondition: conditionController.text));
    conditionController.clear();
    // selectedTatTicket = null;
    orderId = orderId + 1;
    update();
  }

  addTicketSubCategoryReasonMapping() {
    ticketSubCategoryReasonCategoryMappingList!
        .add(TicketSubCategoryReasonCategoryMappingList(
      ticketReasonSubCategoryId:
          subProblemDomainDetail != null ? subProblemDomainDetail!.id! : null,
      ticketReasonCategoryId:
          selParentCategory != null ? selParentCategory!.id : null,
    ));
    update();
  }

  getParentCategory() {
    isLoading = true;
    parentCategoryList!.clear();
    update();
    TicketSystemProvider().getAllActiveReasonCategory(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProblemDomainListRes responseData =
                  ProblemDomainListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  parentCategoryList!.addAll(responseData.dataList!);
                  newParentCategoryList = parentCategoryList;

                  if (chkParentCategory) {
                    for (ProblemDomainDetail element in parentCategoryList!) {
                      if (element.id ==
                          subProblemDomainDetail!.parentCategory!.id) {
                        selParentCategory = element;
                        chkParentCategory = false;
                        break;
                      }
                    }
                  }
                  // for (var element in parentCategoryList!) {
                  //   subProblemDomainDetail!
                  //       .ticketSubCategoryReasonCategoryMappingList!
                  //       .forEach((subProblemElement) {
                  //     if (element.id ==
                  //         subProblemElement.ticketReasonSubCategoryId) {
                  //       selectTicketSubCategoryReasonCategoryMappingList =
                  //           subProblemElement;
                  //     }
                  //   });
                  // }
                  if (subProblemDomainDetail != null &&
                      subProblemDomainDetail!
                              .ticketSubCategoryReasonCategoryMappingList !=
                          null &&
                      subProblemDomainDetail!
                          .ticketSubCategoryReasonCategoryMappingList!
                          .isNotEmpty) {
                    String serviceAreaName = "";
                    for (ProblemDomainDetail element in parentCategoryList!) {
                      for (TicketSubCategoryReasonCategoryMappingList value
                          in subProblemDomainDetail!
                              .ticketSubCategoryReasonCategoryMappingList!) {
                        if (value.ticketReasonCategoryId == element.id) {
                          selectedParentCategoryIds.add(element.id!);
                          selectedSubCategoryResMappingList!
                              .add(TicketSubCategoryReasonCategoryMappingList(
                            ticketReasonCategoryId: element.id,
                          ));
                          serviceAreaName =
                              "$serviceAreaName${element.categoryName!}, ";
                          element.selected = true;
                        }
                      }
                    }
                    if (!serviceAreaName.isNullOrEmpty() &&
                        serviceAreaName.contains(",") &&
                        serviceAreaName.length >= 2) {
                      serviceAreaName = serviceAreaName.substring(
                          0, serviceAreaName.length - 2);
                    }
                    parentCategoryController.text = serviceAreaName;
                  }
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
        viewSubProblemDomainTATData();
      },
      onError: (ResponseModel error) {
        log("ResponseModelResponseModelResponseModel==>>>$error");
        viewSubProblemDomainTATData();
        _handleApiError(error);
      },
    );
  }

  viewSubProblemDomainTATData() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    isLoading = true;
    update();
    TicketSystemProvider().viewSubProblemDomainTATList(
      requestNormal: normalRequest,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SubProblemDomainListRes responseData =
                  SubProblemDomainListRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                subProblemDomainListRes = responseData;
                if (page == 1) {
                  subProblemTATTicketList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  subProblemTATTicketList?.addAll(responseData.dataList!);
                  // if (subProblemDomainDetail != null &&
                  //     subProblemDomainDetail!.ticketSubCategoryReasonCategoryMappingList != null &&
                  //     subProblemDomainDetail!.ticketSubCategoryReasonCategoryMappingList!.isNotEmpty) {
                  //   String serviceAreaName = "";
                  //   for (SubProblemDomainDetail element
                  //   in subProblemTATTicketList!) {
                  //     for (TicketSubCategoryReasonCategoryMappingList value
                  //     in subProblemDomainDetail!.ticketSubCategoryReasonCategoryMappingList!) {
                  //       if (value.ticketReasonCategoryId == element.id) {
                  //         selectedParentServiceArea.add(element.id!);
                  //         serviceAreaName =
                  //         "$serviceAreaName${element.name!}, ";
                  //         element.selected = true;
                  //       }
                  //     }
                  //   }
                  //   if (!serviceAreaName.isNullOrEmpty() &&
                  //       serviceAreaName.contains(",") &&
                  //       serviceAreaName.length >= 2) {
                  //     serviceAreaName = serviceAreaName.substring(
                  //         0, serviceAreaName.length - 2);
                  //   }
                  //   parentServicesAreaController.text = serviceAreaName;
                  // }
                  //
                }
              } else {
                if (page == 1) {
                  subProblemTATTicketList?.clear();
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
            subProblemTATTicketList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        getTATForTicket();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          subProblemTATTicketList?.clear();
        }
        getTATForTicket();
        _handleApiError(error);
      },
    );
  }

  getTicketConditions() {
    isLoading = true;
    conditionList!.clear();
    update();
    TicketSystemProvider().viewAllTatForTickets(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ConditionRes responseData = ConditionRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  conditionList!.addAll(responseData.dataList!);
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
        viewSubProblemDomainTATData();
      },
      onError: (ResponseModel error) {
        viewSubProblemDomainTATData();
        _handleApiError(error);
      },
    );
  }

  getTATForTicket() {
    isLoading = true;
    tatForDataList.clear();
    update();
    TicketSystemProvider().getTatForTickets(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TatForTicketRes responseData = TatForTicketRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  tatForDataList.addAll(responseData.dataList!);
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

      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getTatTicketList() {
    isLoading = true;
    tatTicketList!.clear();
    update();
    TicketSystemProvider().viewAllTatForTickets(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TatTicketListRes responseData = TatTicketListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  tatTicketList!.addAll(responseData.dataList!);
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
        // getTicketConditions();
      },
      onError: (ResponseModel error) {
        // getTicketConditions();
        _handleApiError(error);
      },
    );
  }

  void addEditProblemDomainApiCall() {
    // isLoading = true;
    update();
    SubProblemDomainListReq request = SubProblemDomainListReq(
        subCategoryName: subProblemDomainNameController.text,
        status: selectedStatus != null ? selectedStatus!.text : "",
        id: subProblemDomainDetail != null ? subProblemDomainDetail!.id : null,
        parentCategory: selParentCategory != null
            ? ParentCategory(id: selParentCategory!.id)
            : null,
        ticketSubCategoryGroupReasonMappingList:
            ticketSubCategoryGroupReasonMappingList,
        ticketSubCategoryTatMappingList: ticketSubCategoryTatMappingList,
        ticketSubCategoryReasonCategoryMappingList:
            selectedSubCategoryResMappingList);

    log("SubProblemDomainListReq>> ${jsonEncode(request)}");

    TicketSystemProvider().addEditSubProblemDomain(
      isAdd: subProblemDomainDetail != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              Get.back(result: true);
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
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
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
