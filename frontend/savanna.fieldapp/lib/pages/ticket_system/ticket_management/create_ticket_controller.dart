import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/cust_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/case_type_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/pages/ticket_system/model/request/create_ticket_request.dart';
import 'package:savbill/pages/ticket_system/model/request/edit_ticket_request.dart';
import 'package:savbill/pages/ticket_system/model/response/create_ticket_active_service_res.dart';
import 'package:savbill/pages/ticket_system/model/response/department_type_res.dart';
import 'package:savbill/pages/ticket_system/model/response/get_reason_category_active_services_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_classification.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_get_serial_number_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_source_type_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_resolution_reasons_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart' as dia;
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class CreateTicketController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false;
  GetStorage getStorage = GetStorage();

  TextEditingController caseTitleController = TextEditingController();
  TextEditingController customerController = TextEditingController();
  TextEditingController serviceController = TextEditingController();
  TextEditingController registerMobileNoController = TextEditingController();
  TextEditingController mobileNoController = TextEditingController();
  TextEditingController registerEmailController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController remarksController = TextEditingController();
  TextEditingController cusUsernameController = TextEditingController();
  TextEditingController cusServiceAreaController = TextEditingController();
  TextEditingController custSerialNumberController = TextEditingController();
  TextEditingController followupDateTimeController = TextEditingController();
  DateTime? selectedFollowUpDate;
  String? followUpScheduleDate;
  String? followUpScheduleTime;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat apiTimeFormat = DateFormat(Constant.TIME_FORMAT_24);
  DateFormat dateFormat = DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  // List<ServicesAreaDetail>? servicesAreaList = [];
  // List<ServicesAreaDetail>? selectedServicesArea = [];

  List<GetActiveServiceDataList>? servicesAreaList = [];
  List<GetActiveServiceDataList>? selectedServicesArea = [];

  List<CustomerDetail>? customerList = [];
  CustomerDetail? selectedCustomer;
  CustomerDetail? customerDetail;
  CustomerCreditList? selectedCust;

  List<CaseTypeDetail>? caseTypeList = [];
  CaseTypeDetail? selectedCaseType;

  List<DepartmentType>? departmentTypeList = [];
  DepartmentType? selectedDepartment;

  List<ProblemDomainDetail>? problemDomainList = [];
  ProblemDomainDetail? selectedProblemDomain;

  List<SubProblemDomainDetail>? subProblemDomainList = [];
  SubProblemDomainDetail? selectedSubProblemDomain;

  List<TicketSubCategoryGroupReasonMappingList>? ticketReasonMappingList = [];
  TicketSubCategoryGroupReasonMappingList? selectedReasonMapping;

  List<TicketPriority>? ticketPriorityList = [];
  TicketPriority? selectedTicketPriority;

  List<TicketSourceType>? ticketSourceTypeList = [];
  TicketSourceType? selectedSourceType;
  TicketSourceType? selectedSubSourceType;

  List<TicketClassificationType>? ticketClassificationList = [];
  TicketClassificationType? selectedClassification;

  List<CaseStatusDetail>? caseStatusList = [];
  CaseStatusDetail? selectedCaseStatus;

  List<String>? rootCauseList = [];
  String? selectedRootCause;

  List<String>? resolutionList = [];
  String? selectedResolution;

  // List<CaseReasonDetail>? caseReasonList = [];
  //CaseReasonDetail? selectedCaseReason;

  UserDetail? userDetail;

  //String userServiceArea = "";

  TicketDetail? ticketDetail;
  String from = Strings.add;

  FileDetail? fileDetail;

  List<ReasonCategoryDataList>? reasonCategoryDataList = [];
  ReasonCategoryDataList? selectedReasonCategoryData;
  GetReasonCategoryByActiveServicesRes? getReasonCategoryByActiveServicesRes;

  List<SerialNumberDataList>? getSerialNumberDataList = [];
  List<SerialNumberDataList>? selectedSerialNumberDataList = [];

  SerialNumberDataList? selectedSerialNumberData;

  List<int>? selectedServiceIDs = [];

  List<ReasonCategoryDataList>?filteredReasonCategoryList = [];

  List<int> serviceIDS = [];
  int? ticketReasonSubCategoryId;

  bool chkCustomer = false,
      chkCaseType = false,
      chkDepartment = false,
      chkProblemDomain = false,
      chkSubProblemDomain = false,
      chkTicketReason = false,
      chkSource = false,
      chkSubSource = false,
      chkStatus = false,
      chkClassification = false;


  List<ResolutionReasonsDataList>? resolutionReasonsList = [];
  ResolutionReasonsDataList? selectedResolutionReason;

  List<RootCauseResolutionMappingList>? rootCauseResolutionList = [];
  RootCauseResolutionMappingList? selectedRootCauseResolution;


  final createTicketData = {};

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.TICKET_DETAIL] != null) {
        ticketDetail = arguments[Constant.TICKET_DETAIL];
      }

      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
    }
    update();
    setTicketDetail();
    initPlatformState();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  setTicketDetail() {
    if (ticketDetail != null) {
      if (ticketDetail!.caseTitle != null &&
          ticketDetail!.caseTitle!.isNotEmpty) {
        caseTitleController.text = ticketDetail!.caseTitle!;
      }

      if (ticketDetail!.mobile != null && ticketDetail!.mobile!.isNotEmpty) {
        registerMobileNoController.text = ticketDetail!.mobile!;
      }

      if (ticketDetail!.customerAdditionalMobileNumber != null &&
          ticketDetail!.customerAdditionalMobileNumber!.isNotEmpty) {
        mobileNoController.text = ticketDetail!.customerAdditionalMobileNumber!;
      }

      if (ticketDetail!.email != null && ticketDetail!.email!.isNotEmpty) {
        registerEmailController.text = ticketDetail!.email!;
      }

      if (ticketDetail!.customerAdditionalEmail != null &&
          ticketDetail!.customerAdditionalEmail!.isNotEmpty) {
        emailController.text = ticketDetail!.customerAdditionalEmail!;
      }

      if (ticketDetail!.userName != null &&
          ticketDetail!.userName!.isNotEmpty) {
        cusUsernameController.text = ticketDetail!.userName!;
      }

      if (ticketDetail!.userName != null &&
          ticketDetail!.userName!.isNotEmpty) {
        cusUsernameController.text = ticketDetail!.userName!;
      }

      if (ticketDetail!.serviceAreaName != null &&
          ticketDetail!.serviceAreaName!.isNotEmpty) {
        cusServiceAreaController.text = ticketDetail!.serviceAreaName!;
      }

      if (ticketDetail!.firstRemark != null &&
          ticketDetail!.firstRemark!.isNotEmpty) {
        remarksController.text = ticketDetail!.firstRemark!;
      }

      if (ticketDetail!.customersId != null) {
        chkCustomer = true;
      } else {
        chkCustomer = false;
      }

      if (ticketDetail!.caseType != null &&
          ticketDetail!.caseType!.isNotEmpty) {
        chkCaseType = true;
      } else {
        chkCaseType = false;
      }
      if (ticketDetail!.department != null &&
          ticketDetail!.department!.isNotEmpty) {
        chkDepartment = true;
      } else {
        chkDepartment = false;
      }
      if (ticketDetail!.ticketReasonCategoryId != null) {
        chkProblemDomain = true;
      } else {
        chkProblemDomain = false;
      }
      if (ticketDetail!.reasonSubCategoryId != null) {
        chkSubProblemDomain = true;
      } else {
        chkSubProblemDomain = false;
      }
      if (ticketDetail!.groupReasonId != null) {
        chkTicketReason = true;
      } else {
        chkTicketReason = false;
      }

      if (ticketDetail!.source != null && ticketDetail!.source!.isNotEmpty) {
        chkSource = true;
      } else {
        chkSource = false;
      }
      if (ticketDetail!.subSource != null &&
          ticketDetail!.subSource!.isNotEmpty) {
        chkSubSource = true;
      } else {
        chkSubSource = false;
      }
      if (ticketDetail!.caseStatus != null &&
          ticketDetail!.caseStatus!.isNotEmpty) {
        chkStatus = true;
      } else {
        chkStatus = false;
      }

      if (ticketDetail!.ticketClassification != null &&
          ticketDetail!.ticketClassification!.isNotEmpty) {
        chkClassification = true;
      } else {
        chkClassification = false;
      }

      if (ticketDetail!.customersId != null) {
        customerController.text = ticketDetail!.userName!;
        getUpdateCustomerValueDetail(ticketDetail!.customersId!);
      }

      /*if(ticketDetail!.filename != null &&
          ticketDetail!.filename!.isNotEmpty){

      }*/
    }
    update();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    /*if (getStorage.hasData(Constant.USER_SERVICES_AREA)) {
      userServiceArea = await getStorage.read(Constant.USER_SERVICES_AREA);
    }*/
    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
    // getCustomerListData();
    getCaseTypeListData();
    getTicketClassification();
  }

  /*getCustomerListData() {
    isLoading = true;
    customerList?.clear();
    update();
    GetAllCaseRequest getAllCaseRequest =
        GetAllCaseRequest(page: 1, pageSize: 100000);

    PaymentProvider().getCustomerList(
      getAllCaseRequest: getAllCaseRequest,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerListResponse responseData =
                  CustomerListResponse.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  customerList?.addAll(responseData.customerList!);
                  if (chkCustomer) {
                    for (CustomerDetail element in customerList!) {
                      if (element.id != null &&
                          element.id == ticketDetail!.customersId) {
                        selectedCustomer = element;
                        chkCustomer = false;
                        break;
                      }
                    }
                  }
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
        isLoading = false;
        update();
        getCaseTypeListData();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getCaseTypeListData();
      },
    );
  }*/

  getCaseTypeListData() {
    isLoading = true;
    caseTypeList?.clear();
    update();
    SavbillCareProvider().getCaseTypeList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseTypeResponse responseData = CaseTypeResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  caseTypeList?.addAll(responseData.dataList!);
                  if (chkCaseType) {
                    for (CaseTypeDetail element in caseTypeList!) {
                      if (element.value != null && element.value!.isNotEmpty) {
                        if (element.value!
                            .equalsIgnoreCase(ticketDetail!.caseType!)) {
                          selectedCaseType = element;
                          chkCaseType = false;
                          break;
                        }
                      }
                    }
                  }
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
        isLoading = false;
        update();
        getDepartmentType();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getDepartmentType();
      },
    );
  }

  getDepartmentType() {
    isLoading = true;
    departmentTypeList!.clear();
    update();
    TicketSystemProvider().getDepartmentType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DepartmentTypeRes responseData = DepartmentTypeRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  departmentTypeList!.addAll(responseData.dataList!);
                  if (chkDepartment) {
                    for (DepartmentType element in departmentTypeList!) {
                      if (element.value != null && element.value!.isNotEmpty) {
                        if (element.value!
                            .equalsIgnoreCase(ticketDetail!.department!)) {
                          selectedDepartment = element;
                          chkDepartment = false;
                          break;
                        }
                      }
                    }
                  }
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
        isLoading = false;
        update();
        getTicketPriority();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getTicketPriority();
      },
    );
  }

  getTicketPriority() {
    isLoading = true;
    ticketPriorityList!.clear();
    update();
    TicketSystemProvider().getTicketPriority(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketPriorityRes responseData = TicketPriorityRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketPriorityList!.addAll(responseData.dataList!);



                  for (TicketPriority element in ticketPriorityList!) {
                    if (element.id == 1093) {
                      selectedTicketPriority = element;
                      // break;
                    } else if (element.id == 1092) {
                      selectedTicketPriority = element;
                      // break;
                    } else if(element.id == 1091) {
                      selectedTicketPriority = element;
                      break;
                    }
                  }

                  log("message==>${selectedTicketPriority!.id}");
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
        isLoading = false;
        update();
        getTicketSourceType();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getTicketSourceType();
      },
    );
  }

  getTicketSourceType() {
    isLoading = true;
    ticketSourceTypeList!.clear();
    update();
    TicketSystemProvider().getTicketSourceType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketSourceTypeRes responseData =
                  TicketSourceTypeRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketSourceTypeList!.addAll(responseData.dataList!);

                  if (chkSource || chkSubSource) {
                    for (TicketSourceType element in ticketSourceTypeList!) {
                      if (element.value != null && element.value!.isNotEmpty) {
                        if (chkSource &&
                            element.value!
                                .equalsIgnoreCase(ticketDetail!.source!)) {
                          selectedSourceType = element;
                          chkSource = false;
                        }
                        if (chkSubSource &&
                            element.value!
                                .equalsIgnoreCase(ticketDetail!.subSource!)) {
                          selectedSubSourceType = element;
                          chkSubSource = false;
                        }
                        if (!chkSource && !chkSubSource) {
                          break;
                        }
                      }
                    }
                  }
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
        isLoading = false;
        update();
        getCaseStatusListData();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getCaseStatusListData();
      },
    );
  }


  checkTicketResolutionReasons() {
    isLoading = true;
    resolutionReasonsList!.clear();
    rootCauseResolutionList!.clear();
    selectedResolutionReason = null;
    update();
    TicketSystemProvider().viewResolutionReasonsTickets(
      id: ticketReasonSubCategoryId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketResolutionReasonsRes responseData =
              TicketResolutionReasonsRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  resolutionReasonsList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 404) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        handleApiError(error);
      },
    );
  }

  getCaseStatusListData() {
    isLoading = true;
    caseStatusList?.clear();
    update();
    SavbillCareProvider().getCaseStatusList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseStatusResponse responseData =
                  CaseStatusResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  responseData.dataList!.forEach((element) {
                    if (element.id == 978 ||
                        element.id == 544 ||
                        element.id == 543) {
                      caseStatusList!.add(element);
                    }
                  });
                  if (chkStatus) {
                    for (CaseStatusDetail element in caseStatusList!) {
                      if (element.value != null && element.value!.isNotEmpty) {
                        selectedCaseStatus = element;
                        chkStatus = false;
                        break;
                      }
                    }
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
        if (ticketDetail != null &&
            selectedCust != null &&
            selectedCust!.id != null) {
          getCustomerDetail();
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        if (ticketDetail != null &&
            selectedCust != null &&
            selectedCust!.id != null) {
          getCustomerDetail();
        }
      },
    );
  }

  // for customer username and service area
  getCustomerDetail() {
    isLoading = true;
    update();
    CustomerProvider().getCustomerDetail(
      customerId: selectedCust!.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustDetailResponse responseData =
                  CustDetailResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerDetail = responseData.customers!;

                if (customerDetail!.username != null &&
                    customerDetail!.username!.isNotEmpty) {
                  cusUsernameController.text = customerDetail!.username!;
                }

                if (customerDetail!.email != null &&
                    customerDetail!.email!.isNotEmpty) {
                  registerEmailController.text = customerDetail!.email!;
                }

                if (customerDetail!.mobile != null &&
                    customerDetail!.mobile!.isNotEmpty) {
                  registerMobileNoController.text = customerDetail!.mobile!;
                }
                if (customerDetail!.serviceareaName != null &&
                    customerDetail!.serviceareaName!.isNotEmpty) {
                  cusServiceAreaController.text =
                      customerDetail!.serviceareaName!;
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
        // getCustomerReasonCategory();
        getActiveServiceForSubscribers();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        // getCustomerReasonCategory();
        getActiveServiceForSubscribers();
      },
    );
  }

  getUpdateCustomerValueDetail(int customerId) {
    isLoading = true;
    update();
    CustomerProvider().getCustomerDetail(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustDetailResponse responseData =
                  CustDetailResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerDetail = responseData.customers!;
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
        // getCustomerReasonCategory();
        getUpdateActiveServiceForSubscribers(customerId);
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        // getCustomerReasonCategory();
        getUpdateActiveServiceForSubscribers(customerId);
      },
    );
  }

  getCustomerReasonCategory() {
    isLoading = true;
    selectedSubProblemDomain = null;
    selectedProblemDomain = null;
    selectedReasonMapping = null;
    problemDomainList!.clear();
    subProblemDomainList!.clear();
    ticketReasonMappingList!.clear();
    update();
    TicketSystemProvider().getReasonCategoryByCustomer(
      customerId: selectedCust!.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProblemDomainListRes responseData =
                  ProblemDomainListRes.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  problemDomainList!.addAll(responseData.dataList!);
                  if (chkProblemDomain) {
                    for (ProblemDomainDetail element in problemDomainList!) {
                      if (element.id == ticketDetail!.ticketReasonCategoryId) {
                        selectedProblemDomain = element;
                        chkProblemDomain = false;
                        break;
                      }
                    }
                  }
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
        /*if (chkSubProblemDomain &&
            selectedProblemDomain != null &&
            selectedProblemDomain!.id != null) {
          getSubCategory();
        }*/
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
    if (selectedSubProblemDomain != null &&
        selectedSubProblemDomain!.ticketSubCategoryGroupReasonMappingList !=
            null &&
        selectedSubProblemDomain!
            .ticketSubCategoryGroupReasonMappingList!.isNotEmpty) {
      ticketReasonMappingList!.addAll(
          selectedSubProblemDomain!.ticketSubCategoryGroupReasonMappingList!);
      if (chkTicketReason) {
        for (TicketSubCategoryGroupReasonMappingList element
            in ticketReasonMappingList!) {
          if (element.reason!.equalsIgnoreCase(ticketDetail!.caseReason!)) {
            selectedReasonMapping = element;
            chkTicketReason = false;
            break;
          }
        }
      }
      update();
    }
  }

  getSubCategory(int? categoryId) {
    isLoading = true;
    selectedSubProblemDomain = null;
    selectedReasonMapping = null;
    subProblemDomainList!.clear();
    ticketReasonMappingList!.clear();
    update();
    TicketSystemProvider().getSubCategoryByParentCategory(
      categoryId: categoryId,
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
                  subProblemDomainList!.addAll(responseData.dataList!);
                  if (chkSubProblemDomain) {
                    for (SubProblemDomainDetail element
                        in subProblemDomainList!) {
                      if (element.subCategoryName!.equalsIgnoreCase(
                          ticketDetail!.caseReasonSubCategory!)) {
                        selectedSubProblemDomain = element;
                        chkSubProblemDomain = false;
                        break;
                      }
                    }
                  }
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
        if (chkTicketReason &&
            selectedSubProblemDomain != null &&
            selectedSubProblemDomain!.id != null) {
          setReasonData();
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  /*  getCaseReasonListData() {
    isLoading = true;
    update();
    AdoptCareProvider().getCaseReasonList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseReasonResponse responseData =
                  CaseReasonResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  caseReasonList?.clear();
                  caseReasonList?.addAll(responseData.dataList!);
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
        handleApiError(error);
      },
    );
  }*/

  getTicketClassification() {
   // isLoading = true;
    ticketClassificationList!.clear();
   // update();
    TicketSystemProvider().getTicketClassification(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketClassificationTypeRes responseData =
              TicketClassificationTypeRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketClassificationList!.addAll(responseData.dataList!);

                  if (chkClassification) {
                    for (TicketClassificationType element in ticketClassificationList!) {
                      if (element.value != null && element.value!.isNotEmpty) {
                        selectedClassification = element;
                        chkClassification = false;
                        break;
                      }
                    }
                  } else {
                    if (ticketClassificationList != null && ticketClassificationList!.isNotEmpty) {
                      selectedClassification = ticketClassificationList?.first ?? TicketClassificationType();
                    }
                  }
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
        //isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);

      },
    );
  }

  getActiveServiceForSubscribers() {
    isLoading = true;
    servicesAreaList!.clear();
    update();
    TicketSystemProvider().getActiveServiceForSubscribers(
      id: selectedCust!.id!,
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

  getUpdateActiveServiceForSubscribers(int customerId) {
    isLoading = true;
    servicesAreaList!.clear();
    update();
    TicketSystemProvider().getActiveServiceForSubscribers(
      id: customerId,
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
                  for (GetActiveServiceDataList element in servicesAreaList!) {
                    serviceIDS.add(element.id!);
                    serviceController.text = element.serviceName!;
                  }
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
        getTicketReasonCategoryByActiveServices(serviceIDS);
      },
      onError: (ResponseModel error) {
        update();
        handleApiError(error);
      },
    );
  }

  getSerialNumberTicket(String? custServiceId) {
    isLoading = true;
    getSerialNumberDataList!.clear();
    update();
    TicketSystemProvider().getTicketSerialNumber(
      custId: selectedCust!.id!,
      serviceId: custServiceId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketGetSerialNumberRes responseData =
                  TicketGetSerialNumberRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  getSerialNumberDataList!.addAll(responseData.dataList!);
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

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
                  if (ticketDetail != null) {
                    for (ReasonCategoryDataList element
                        in reasonCategoryDataList!) {
                      if (element.categoryName != null &&
                          element.categoryName!.isNotEmpty) {
                        if (element.categoryName!.equalsIgnoreCase(
                            ticketDetail!.caseReasonCategory!)) {
                          selectedReasonCategoryData = element;
                          break;
                        }
                      }
                    }
                  }
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
        if (selectedReasonCategoryData != null) {
          getSubCategory(selectedReasonCategoryData!.id);
        }
      },
      onError: (ResponseModel error) {
        if (selectedReasonCategoryData != null) {
          getSubCategory(selectedReasonCategoryData!.id);
          update();
        }
        handleApiError(error);
      },
    );
  }


  departmentSelected(DepartmentType? selectedDepartment){
    if(departmentTypeList!.isNotEmpty){
     return  filteredReasonCategoryList = reasonCategoryDataList!.where((element) => element.department!.equalsIgnoreCase(selectedDepartment!.value!)).toList();
    }
  }



  // responseTimeSet() {
  //   DateTime date = DateTime.now();
  //   TatTicketDetail? ticketDetail;
  //   List<TicketSubCategoryTatMappingList>? ticketSubCategoryTatMappingList = [];
  //   if(subProblemDomainList!.isNotEmpty){
  //      for (var element in subProblemDomainList!) {
  //        ticketSubCategoryTatMappingList.addAll(element.ticketSubCategoryTatMappingList!);
  //      }
  //      ticketSubCategoryTatMappingList.forEach((element) {
  //        ticketDetail = element.ticketTatMatrix;
  //      });
  //   }
  //
  //   // Determine the time unit and time value, defaulting to 1 day if not found
  //   final String? timeUnit = ticketDetail != null ? ticketDetail!.runit : "DAY";
  //   final int? time = ticketDetail != null ? ticketDetail!.rtime: 1;
  //
  //   // Adjust the date and time according to the TAT unit
  //   if (timeUnit!.equalsIgnoreCase("DAY")) {
  //     date = date.add(Duration(days: time!));
  //   } else if (timeUnit.equalsIgnoreCase("HOUR")) {
  //     date = date.add(Duration(hours: time!));
  //   } else {
  //     date = date.add(Duration(minutes: time!));
  //   }
  //   CreateTicketRequest().nextFollowupDate = DateFormat('yyyy-MM-dd').format(date);
  //   CreateTicketRequest().nextFollowupTime = DateFormat('HH:mm:ss').format(date);
  //   // Format and assign the next follow-up date and time
  //   // createTicketData['nextFollowupDate'] = DateFormat('yyyy-MM-dd').format(date);
  //   // createTicketData['nextFollowupTime'] = DateFormat('HH:mm:ss').format(date);
  // }


  void createTicketApiCall() async {
    isLoading = true;
    update();
    DateTime date = DateTime.now();
    String? nextFollowupDate,nextFollowupTime;
    // String currentDate = DateFormat(Constant.API_DATE_FORMAT).format(date);
    // String currentTime = DateFormat(Constant.TIME_FORMAT_24).format(date);
    List<TicketServicemappingList>? ticketServiceMappingList = [];
    ticketServiceMappingList.clear();
    selectedServiceIDs!.forEach((element) {
      ticketServiceMappingList
          .add(TicketServicemappingList(serviceid: element));
    });

    log("ticketServiceMappingList=>>>>${jsonEncode(ticketServiceMappingList)}");
    log("serviceareaName=>>>>${customerDetail!.serviceareaName}");




    TatTicketDetail? ticketTATDetail;
    List<TicketSubCategoryTatMappingList>? ticketSubCategoryTatMappingList = [];
    if(subProblemDomainList!.isNotEmpty){
      for (var element in subProblemDomainList!) {
        ticketSubCategoryTatMappingList.addAll(element.ticketSubCategoryTatMappingList!);
      }
      ticketSubCategoryTatMappingList.forEach((element) {
        ticketTATDetail = element.ticketTatMatrix;
      });

      // Determine the time unit and time value, defaulting to 1 day if not found
      final String? timeUnit = ticketTATDetail != null ? ticketTATDetail!.runit : "DAY";
      final int? time = ticketTATDetail != null ? ticketTATDetail!.rtime: 1;

      // Adjust the date and time according to the TAT unit
      if (timeUnit!.equalsIgnoreCase("DAY")) {
        date = date.add(Duration(days: time!));
      } else if (timeUnit.equalsIgnoreCase("HOUR")) {
        date = date.add(Duration(hours: time!));
      } else {
        date = date.add(Duration(minutes: time!));
      }
      nextFollowupDate = DateFormat(Constant.API_DATE_FORMAT).format(date);
      nextFollowupTime = DateFormat(Constant.TIME_FORMAT_24).format(date);

    }

    CreateTicketRequest createTicketRequest = CreateTicketRequest(
        // ticketId: ticketDetail != null ? ticketDetail!.caseId : null,
        caseTitle: caseTitleController.text,
        customersId: selectedCust != null ? selectedCust!.id : null,
        // userName: customerDetail != null ? customerDetail!.username : "",
        // serviceAreaName: customerDetail != null ? customerDetail!.serviceareaName : "",
        caseType: selectedCaseType != null ? selectedCaseType!.value : "",
        ticketReasonCategoryId: selectedReasonCategoryData != null
            ? selectedReasonCategoryData!.id
            : null,
        reasonSubCategoryId: selectedSubProblemDomain != null
            ? selectedSubProblemDomain!.id
            : null,
        groupReasonId:
            selectedReasonMapping != null ? selectedReasonMapping!.id : null,
        priority: "Low",
        nextFollowupDate: followUpScheduleDate ?? nextFollowupDate,
        nextFollowupTime: followUpScheduleTime ?? nextFollowupTime,
        caseStatus: (ticketDetail != null &&
                ticketDetail!.caseStatus != null &&
                ticketDetail!.caseStatus!.isNotEmpty)
            ? ticketDetail!.caseStatus!
            : selectedCaseStatus != null
                ? selectedCaseStatus!.value
                : "Open",
        /*  status: (ticketDetail != null &&
                ticketDetail!.caseStatus != null &&
                ticketDetail!.caseStatus!.isNotEmpty)
            ? ticketDetail!.caseStatus!
            : "",*/
        currentAssigneeId:
            (ticketDetail != null && ticketDetail!.currentAssigneeId != null)
                ? ticketDetail!.currentAssigneeId!
                : null,
        finalResolutionId:selectedResolutionReason?.id,
        firstRemark: remarksController.text,
        rootCauseReasonId: selectedRootCauseResolution?.id,
        source: selectedSourceType != null ? selectedSourceType!.value : "",
        ticketClassification: selectedClassification!= null ? selectedClassification?.value : "",
        subSource:
            selectedSubSourceType != null ? selectedSubSourceType!.value : "",
        department: selectedDepartment != null ? selectedDepartment!.value : "",
        customerAdditionalMobileNumber: mobileNoController.text,
        customerAdditionalEmail: emailController.text.trim(),
        serialNumber: custSerialNumberController.text,
        file: "",
        /*    attachment: "",
        filename: "",
        assignee: null,
        remarkType: "",*/
        helperName: "",
        caseForPartner: "Customer",
        caseFor: "Customer",
        caseOrigin: "Phone",
        ticketServicemappingList: ticketServiceMappingList);

    EditTicketRequest? editRequest;
    if (ticketDetail != null) {
      editRequest = EditTicketRequest(
        ticketId: ticketDetail != null ? ticketDetail!.caseId : null,
        status: (ticketDetail != null &&
                ticketDetail!.caseStatus != null &&
                ticketDetail!.caseStatus!.isNotEmpty)
            ? ticketDetail!.caseStatus!
            : "",


        caseType: selectedCaseType != null ? selectedCaseType!.value : "",
        assignee: null,
        priority: "Low",
        attachment: "",
        filename: "",
        helperName: "",
        finalResolutionId:
            (ticketDetail != null && ticketDetail!.finalResolutionId != null)
                ? ticketDetail!.finalResolutionId!
                : null,
        remarkType: "",
        groupReasonId:
            selectedReasonMapping != null ? selectedReasonMapping!.id : null,
        reasonSubCategoryId: selectedSubProblemDomain != null
            ? selectedSubProblemDomain!.id
            : null,
        ticketReasonCategoryId:
            selectedProblemDomain != null ? selectedProblemDomain!.id : null,
        caseTitle: caseTitleController.text,
        rootCauseReasonId: (ticketDetail != null &&
            ticketDetail!.rootCauseReasonId != null)
            ? ticketDetail!.rootCauseReasonId!
            : null,
        source: selectedSourceType != null ? selectedSourceType!.value : "",
        subSource:
            selectedSubSourceType != null ? selectedSubSourceType!.value : "",
        customerAdditionalMobileNumber: mobileNoController.text,
        customerAdditionalEmail: emailController.text.trim(),
      );
    }

    Map<String, dynamic> map = {};
    if (fileDetail != null &&
        fileDetail!.filePathLocal != null &&
        fileDetail!.filePathLocal!.isNotEmpty) {
      File f = File(fileDetail!.filePathLocal!);
      String fileName = f.path.split('/').last;
      dia.MultipartFile multipartFile =
          await dia.MultipartFile.fromFile(f.path, filename: fileName);
      map["file"] = multipartFile;
      createTicketRequest.file = fileName;
      if (editRequest != null) {
        editRequest.filename = fileName;
      }
    }
   // isLoading = false;
    if (ticketDetail != null && editRequest != null) {
      print("Request Data ==> ${jsonEncode(editRequest)}");
      map["caseUpdate"] = jsonEncode(editRequest);
    } else {
      print("Request Data ==> ${jsonEncode(createTicketRequest)}");
      map["entityDTO"] = jsonEncode(createTicketRequest);
    }

    dia.FormData formData = dia.FormData.fromMap(map);

    TicketSystemProvider().addEditCaseTicketsRequest(
      isAdd: ticketDetail != null ? false : true,
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
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
        isLoading = false;
        update();
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
    }else if (error.statusCode == 400) {
      Utils.showSnackbar(Strings.INFO, Strings.badRequest,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }  /*else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }*/
    update();
  }
}
