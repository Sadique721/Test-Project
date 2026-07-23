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
import 'package:savbill/pages/ticket_system/model/response/ticket_get_serial_number_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_source_type_res.dart';
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

class CustomerCreateTicketController extends GetxController {
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


  // List<ServicesAreaDetail>? servicesAreaList = [];
  // List<ServicesAreaDetail>? selectedServicesArea = [];

  List<GetActiveServiceDataList>? servicesAreaList =[];
  List<GetActiveServiceDataList>? selectedServicesArea =[];

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

  String? nextFollowUpDate ="", nextFollowUpTime = "";

  List<ReasonCategoryDataList>? reasonCategoryDataList =[];
  ReasonCategoryDataList? selectedReasonCategoryData;
  GetReasonCategoryByActiveServicesRes? getReasonCategoryByActiveServicesRes;

  List<SerialNumberDataList>? getSerialNumberDataList =[];
  List<SerialNumberDataList>? selectedSerialNumberDataList =[];

  SerialNumberDataList? selectedSerialNumberData;

  List<int>? selectedServiceIDs = [];
  List<int>? selectedSerialNumberIDs = [];


  bool chkCustomer = false,
      chkCaseType = false,
      chkDepartment = false,
      chkProblemDomain = false,
      chkSubProblemDomain = false,
      chkTicketReason = false,
      chkSource = false,
      chkSubSource = false,
      chkStatus = false;

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

      if (ticketDetail!.customerAdditionalMobileNumber != null &&
          ticketDetail!.customerAdditionalMobileNumber!.isNotEmpty) {
        mobileNoController.text = ticketDetail!.customerAdditionalMobileNumber!;
      }

      if (ticketDetail!.customerAdditionalEmail != null &&
          ticketDetail!.customerAdditionalEmail!.isNotEmpty) {
        emailController.text = ticketDetail!.customerAdditionalEmail!;
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
    // getCaseTypeListData();
    getCustomerDetail(customerDetail!.id!);
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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
                    if (element.id == Constant.DEFAULT_TICKET_PRIORITY) {
                      selectedTicketPriority = element;
                      break;
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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
                    if (element.id == Constant.OPEN_CASE_STATUS || element.id == Constant.RISE_CLOSE_CASE_STATUS || element.id == Constant.FOLLOW_UP_CASE_STATUS) {
                      caseStatusList!.add(element);
                    }
                  });
                   /*if (chkStatus) {
                    for (CaseStatusDetail element in caseStatusList!) {
                      if (element.value != null && element.value!.isNotEmpty) {
                        if (element.value!
                            .equalsIgnoreCase(ticketDetail!.caseStatus!)) {
                          selectedCaseStatus = element;
                          chkStatus = false;
                          break;
                        }
                      }
                    }
                  }*/
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getActiveServiceForSubscribers();
        // if (ticketDetail != null &&
        //     customerDetail != null &&
        //     customerDetail!.id! != null) {
        //   // getCustomerDetail();
        // }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getActiveServiceForSubscribers();
        // if (ticketDetail != null &&
        //     customerDetail != null &&
        //     customerDetail!.id! != null) {
        //   // getCustomerDetail();
        // }
      },
    );
  }

  // for customer username and service area
  getCustomerDetail(int customerId) {
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

                if (customerDetail!.custname != null &&
                    customerDetail!.custname!.isNotEmpty) {
                  customerController.text = customerDetail!.custname!;
                }

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
        // getActiveServiceForSubscribers();
        getCaseTypeListData();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        // getCustomerReasonCategory();
        // getActiveServiceForSubscribers();
        getCaseTypeListData();
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
      customerId: customerDetail!.id!!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProblemDomainListRes responseData =
              ProblemDomainListRes.fromJson(map);
              if ((responseData.responseCode != null && responseData.responseCode == 200)) {
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
          if (element.id == ticketDetail!.groupReasonId) {
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
                      if (element.id == ticketDetail!.reasonSubCategoryId) {
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

  void responseTimeSet() {
    DateTime date = DateTime.now();
    List<TicketSubCategoryTatMappingList>? tATDetails = [];
    for (SubProblemDomainDetail element in subProblemDomainList!) {
      if (element.id == selectedSubProblemDomain!.id) {
        tATDetails = element.ticketSubCategoryTatMappingList;
      }
    }

    TatTicketDetail? ticket;
    for (TicketSubCategoryTatMappingList element in tATDetails!) {
      if(element.ticketReasonSubCategoryId == selectedSubProblemDomain!.id){
        ticket = element.ticketTatMatrix;
      }
    }

    String? timeUnit = ticket != null ? ticket.runit : "DAY";
    int? time = ticket != null ? ticket.rtime : 1;

    if (timeUnit == "DAY") {
      date = date.add(Duration(days: time!));
    } else if (timeUnit == "HOUR") {
      date = date.add(Duration(hours: time!));
    } else {
      date = date.add(Duration(minutes: time!));
    }

    final dateFormatter = DateFormat('yyyy-MM-dd');
    final timeFormatter = DateFormat('HH:mm:ss');


    log("nextFollowupDate===>${dateFormatter.format(date)}");
    log("nextFollowupTime===>${ timeFormatter.format(date)}");

    nextFollowUpDate = dateFormatter.format(date);
    nextFollowUpTime = timeFormatter.format(date);
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


  getActiveServiceForSubscribers() {
    isLoading = true;
    servicesAreaList!.clear();
    update();
    TicketSystemProvider().getActiveServiceForSubscribers(
      id: customerDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CreateTicketActiveServiceRes responseData = CreateTicketActiveServiceRes.fromJson(map);
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

  getSerialNumberTicket(String? custServiceId) {
    isLoading = true;
    getSerialNumberDataList!.clear();
    update();
    TicketSystemProvider().getTicketSerialNumber(
      custId: customerDetail!.id!, serviceId:custServiceId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketGetSerialNumberRes responseData = TicketGetSerialNumberRes.fromJson(map);
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
              GetReasonCategoryByActiveServicesRes responseData = GetReasonCategoryByActiveServicesRes.fromJson(map);
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


  String formatTime(dynamic fromTime) {
    if (fromTime is! String) {
      DateTime dateTime = DateTime.fromMillisecondsSinceEpoch(fromTime);
      int hour = dateTime.hour;
      int minute = dateTime.minute;

      String hourStr = hour < 10 ? '0$hour' : '$hour';
      String minuteStr = minute < 10 ? '0$minute' : '$minute';

      return '$hourStr:$minuteStr';
    } else {
      return fromTime;
    }
  }



  void createTicketApiCall() async {
    isLoading = true;
    update();
    DateTime now = DateTime.now();
    String currentDate = DateFormat(Constant.API_DATE_FORMAT).format(now);
    String currentTime = DateFormat(Constant.TIME_FORMAT_24).format(now);

    List<TicketServicemappingList>? ticketServiceMappingList = [];
    ticketServiceMappingList.clear();
    selectedServiceIDs!.forEach((element) {
      ticketServiceMappingList.add(TicketServicemappingList(serviceid: element));
    });


    if (selectedCaseStatus != null &&
        selectedCaseStatus!.value == "Open") {
      responseTimeSet();
    }

    CreateTicketRequest createTicketRequest = CreateTicketRequest(
      // ticketId: ticketDetail != null ? ticketDetail!.caseId : null,
        caseTitle: caseTitleController.text,
        customersId: customerDetail?.id!,
        userName: customerDetail != null ? customerDetail!.username : "",
        serviceAreaName:
        customerDetail != null ? customerDetail!.serviceareaName : "",
        caseType: selectedCaseType != null ? selectedCaseType!.value : "",
        ticketReasonCategoryId:
        selectedReasonCategoryData?.id,
        reasonSubCategoryId: selectedSubProblemDomain?.id,
        groupReasonId:
        selectedReasonMapping?.id,
        priority: "Low",
        nextFollowupDate: (ticketDetail != null &&
            ticketDetail!.nextFollowupDate != null &&
            ticketDetail!.nextFollowupDate!.isNotEmpty)
            ? ticketDetail!.nextFollowupDate
            : nextFollowUpDate,
        nextFollowupTime: (ticketDetail != null &&
            ticketDetail!.nextFollowupTime != null &&
            ticketDetail!.nextFollowupTime!.isNotEmpty)
            ? formatTime(ticketDetail!.nextFollowupTime)
            : nextFollowUpTime,
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
        finalResolutionId:
        (ticketDetail != null && ticketDetail!.finalResolutionId != null)
            ? ticketDetail!.finalResolutionId!
            : null,
        firstRemark: remarksController.text,
        rootCauseReasonId: null,
        source: selectedSourceType != null ? selectedSourceType!.value : "",
        subSource:
        selectedSubSourceType != null ? selectedSubSourceType!.value : "",
        department: selectedDepartment != null ? selectedDepartment!.value : "",
        customerAdditionalMobileNumber: mobileNoController.text,
        customerAdditionalEmail: emailController.text,
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

    log("ticketDetail===>${jsonEncode(ticketDetail)}");


    EditTicketRequest? editRequest;
    if (ticketDetail != null) {
      editRequest = EditTicketRequest(
        ticketId: ticketDetail?.caseId,
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
        selectedReasonMapping?.id,
        reasonSubCategoryId: selectedSubProblemDomain?.id,
        ticketReasonCategoryId:
        selectedProblemDomain?.id,
        caseTitle: caseTitleController.text,
        rootCauseReasonId: null,
        source: selectedSourceType != null ? selectedSourceType!.value : "",
        subSource:
        selectedSubSourceType != null ? selectedSubSourceType!.value : "",
        customerAdditionalMobileNumber: mobileNoController.text,
        customerAdditionalEmail: emailController.text,
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
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
