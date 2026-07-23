import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_outward_req.dart';
import 'package:savbill/pages/inventory/module/response/active_staff_user_list_res.dart';
import 'package:savbill/pages/inventory/module/response/available_qty_product_des_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_type_partner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/outward_inward_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_outward_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_list_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class AddEditOutwardsController extends GetxController {
  bool isLoading = false, isReadOnly = false;
  GetStorage getStorage = GetStorage();

  TextEditingController qtyController = TextEditingController();
  TextEditingController outwardDateController = TextEditingController();
  TextEditingController descriptionController = TextEditingController();

  final destinationDropDownKey = GlobalKey<DropdownSearchState>();

  UserDetail? userDetail;

  // product list
  List<ProductDetail>? productList = [];
  ProductDetail? selectedProduct;

  // source type
  List<DropdownDetail>? sourceTypeList = [];
  DropdownDetail? selectedSourceType;

  // source
  List<DropdownDetail>? sourceList = [];
  DropdownDetail? selectedSource;

  // inward list
  List<OutwardInwardDetail>? inwardList = [];
  OutwardInwardDetail? selectedInward;

  // destination type
  List<DropdownDetail>? destinationTypeList = [];
  DropdownDetail? selectedDestinationType;

  // source  & destination 1
  List<WareHouseDetail>? wareHouseList = [];
  WareHouseDetail? selectedWareHouseData;

  // source & destination 2
  // List<StaffUserDetail>? staffUserList = [];
  List<StaffUserDataList>? staffUserList = [];

  List<AvailableQtyDataList>? availableQtyProductList = [];
  AvailableQtyDataList? selectedAvailableQty;

  // source & destination 3
  List<PartnerTypeDataList>? partnerList = [];

  //Select Destination
  List<DropdownDetail>? destinationList = [];
  DropdownDetail? selectedDestination;

  // destination type - 1 pop while  Source Type != Warehouse
  List<PopDetail>? popList = [];
  int? availableQty = 0;

  // destination type - 2 service area  Source Type != Warehouse
  List<ServicesAreaDetail>? servicesAreaList = [];

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  String from = Strings.add;
  OutwardDetail? outwardDetail;
  String outwardDateTime = "";
  DateTime? selectedDateTime;
  DateFormat dateFormat =
      DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT_API);

  final GlobalKey<FormFieldState> keySource = GlobalKey<FormFieldState>();
  final GlobalKey<FormFieldState> keySourceType = GlobalKey<FormFieldState>();
  final GlobalKey<FormFieldState> keyDestination = GlobalKey<FormFieldState>();
  final GlobalKey<FormFieldState> keyDestinationType =
      GlobalKey<FormFieldState>();

  @override
  void onInit() {
    super.onInit();
    sourceTypeList!.add(DropdownDetail(
        id: Strings.ware_house,
        text: Strings.ware_house,
        type: Strings.source));
    sourceTypeList!.add(DropdownDetail(
        id: Strings.staff, text: Strings.staff, type: Strings.source));
    sourceTypeList!.add(DropdownDetail(
        id: Strings.partner, text: Strings.partner, type: Strings.source));

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
      if (arguments[Constant.IM_DETAIL] != null) {
        outwardDetail = arguments[Constant.IM_DETAIL];
      }

      if (outwardDetail != null) {
        isReadOnly = true;
        // set selected qty
        qtyController.text = outwardDetail!.inTransitQty!.toString();

        // set selected date time
        DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
            .parse(outwardDetail!.outwardDateTime!);
        selectedDateTime = date;
        outwardDateController.text = dateFormat.format(date);
        outwardDateTime = apiDateTimeFormat.format(date);

        // set selected source type
        if (outwardDetail!.sourceType != null &&
            outwardDetail!.sourceType!.isNotEmpty) {
          for (DropdownDetail element in sourceTypeList!) {
            if (element.text!.equalsIgnoreCase(outwardDetail!.sourceType!)) {
              selectedSourceType = element;
              break;
            }
          }
        }
        // set selected status
        if (outwardDetail!.status != null &&
            outwardDetail!.status!.isNotEmpty) {
          for (DropdownDetail element in statusList!) {
            if (element.id!.equalsIgnoreCase(outwardDetail!.status!)) {
              selectedStatus = element;
              break;
            }
          }
        }

        // set selected source
        if (outwardDetail!.sourceId != null ) {
          for (WareHouseDetail element in wareHouseList!) {
            if (int.parse(element.id.toString()) == outwardDetail!.sourceId) {
              selectedWareHouseData = element;
              break;
            }
          }
        }

      } else {
        isReadOnly = false;
      }
    }
    update();
    initPlatformState();
  }

  manageSourceType() {
    inwardList!.clear();
    selectedInward = null;
    if (selectedSourceType!.id!.equalsIgnoreCase(Strings.ware_house)) {
      if (wareHouseList != null && wareHouseList!.isNotEmpty) {
        for (var element in wareHouseList!) {
          sourceList!.add(DropdownDetail(
              id: element.id!.toString(),
              text: element.name!,
              type: Strings.source));
        }
      }
    }
    else if (selectedSourceType!.id!.equalsIgnoreCase(Strings.staff)) {
      if (staffUserList != null && staffUserList!.isNotEmpty) {
        for (var element in staffUserList!) {
          if(element.id == userDetail!.userId) {
            sourceList!.add(DropdownDetail(
                id: element.id!.toString(),
                text: element.username!,
                type: Strings.source));
          }
        }
      }
    }

    else if (selectedSourceType!.id!.equalsIgnoreCase(Strings.partner)) {
      if (partnerList != null && partnerList!.isNotEmpty) {
        for (var element in partnerList!) {
          sourceList!.add(DropdownDetail(
              id: element.id!.toString(),
              text: element.name!,
              type: Strings.source));
        }
      }
    }
    selectedDestinationType = null;
    selectedDestination = null;
    destinationTypeList!.clear();
    destinationList!.clear();

    if (selectedSourceType!.id!.equalsIgnoreCase(Strings.ware_house)) {
      destinationTypeList!.add(DropdownDetail(
          id: Strings.ware_house,
          text: Strings.ware_house,
          type: Strings.destination_type));
      destinationTypeList!.add(DropdownDetail(
          id: Strings.staff,
          text: Strings.staff,
          type: Strings.destination_type));
      destinationTypeList!.add(DropdownDetail(
          id: Strings.partner,
          text: Strings.partner,
          type: Strings.destination_type));
    }
    else  if (selectedSourceType!.id!.equalsIgnoreCase(Strings.staff)) {
      destinationTypeList!.add(DropdownDetail(
          id: Strings.ware_house,
          text: Strings.ware_house,
          type: Strings.destination_type));
    }
    else{
      destinationTypeList!.add(DropdownDetail(
          id: Strings.pop,
          text: Strings.pop,
          type: Strings.destination_type));
      destinationTypeList!.add(DropdownDetail(
          id: Strings.service_area,
          text: Strings.service_area,
          type: Strings.destination_type));
    }

    if (outwardDetail != null &&
        outwardDetail!.destinationType != null &&
        outwardDetail!.destinationType!.isNotEmpty &&
        destinationTypeList != null &&
        destinationTypeList!.isNotEmpty) {
      for (DropdownDetail element in destinationTypeList!) {
        if (element.id!.equalsIgnoreCase(outwardDetail!.destinationType!)) {
          selectedDestinationType = element;
          break;
        }
      }
    }
    update();
  }

  manageDestinationType() {
     selectedDestination = null;
    if (destinationList != null) {
      destinationList!.clear();
    }
    update();
    if (selectedDestinationType!.id!.equalsIgnoreCase(Strings.ware_house)) {
      if (wareHouseList != null && wareHouseList!.isNotEmpty) {
        for (var element in wareHouseList!) {
          if(!element.name!.equalsIgnoreCase(selectedSource!.text!)) {
            destinationList!.add(DropdownDetail(
                id: element.id!.toString(),
                text: element.name!,
                type: Strings.destination));
          }
        }
      }
    } else if (selectedDestinationType!.id!.equalsIgnoreCase(Strings.staff)) {
      if (staffUserList != null && staffUserList!.isNotEmpty) {
        for (var element in staffUserList!) {
            destinationList!.add(DropdownDetail(
                id: element.id!.toString(),
                text: element.username!,
                type: Strings.destination));
        }
      }
    } else if (selectedDestinationType!.id!.equalsIgnoreCase(Strings.partner)) {
      if (partnerList != null && partnerList!.isNotEmpty) {
        for (var element in partnerList!) {
          destinationList!.add(DropdownDetail(
              id: element.id!.toString(),
              text: element.name!,
              type: Strings.destination));
        }
      }
    } else if (selectedDestinationType!.id!.equalsIgnoreCase(Strings.pop)) {
      if (popList != null && popList!.isNotEmpty) {
        for (var element in popList!) {
          destinationList!.add(DropdownDetail(
              id: element.id!.toString(),
              text: element.name!,
              type: Strings.destination));
        }
      }
    } else if (selectedDestinationType!.id!
        .equalsIgnoreCase(Strings.service_area)) {
      if (servicesAreaList != null && servicesAreaList!.isNotEmpty) {
        for (var element in servicesAreaList!) {
          destinationList!.add(DropdownDetail(
              id: element.id!.toString(),
              text: element.name!,
              type: Strings.destination));
        }
      }
    }

    update();

    if (outwardDetail != null &&
        outwardDetail!.destinationId != null &&
        destinationList != null &&
        destinationList!.isNotEmpty) {
      for (DropdownDetail element in destinationList!) {
        if (element.id != null &&
            element.id!.isNotEmpty &&
            element.id!
                .equalsIgnoreCase(outwardDetail!.destinationId!.toString())) {
          selectedDestination = element;
        }
      }
      update();
    }
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
    // getProductsData();
    getActiveProductsData();
  }

  // getProductsData() {
  //   isLoading = true;
  //   productList?.clear();
  //   update();
  //   InventoryManagementProvider().getAllProductList(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ActiveProductRes responseData = ActiveProductRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 productList?.addAll(responseData.dataList!);
  //                 if (outwardDetail != null &&
  //                     outwardDetail!.productId != null &&
  //                     outwardDetail!.productId!.id != null) {
  //                   for (ProductDetail element in productList!) {
  //                     if (element.id != null &&
  //                         element.id == outwardDetail!.productId!.id) {
  //                       selectedProduct = element;
  //                       break;
  //                     }
  //                   }
  //                 }
  //               }
  //             } else {
  //               if (responseData.responseMessage!.isNotEmpty) {
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
  //       isLoading = false;
  //       update();
  //       getAllWareHouse();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //       getAllWareHouse();
  //     },
  //   );
  // }

  getActiveProductsData() {
    isLoading = true;
    productList?.clear();
    update();
    CustomerProvider().getActiveProductList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ActiveProductRes responseData = ActiveProductRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productList?.addAll(responseData.dataList!);
                  if (outwardDetail != null &&
                      outwardDetail!.productId != null &&
                      outwardDetail!.productId!.id != null) {
                    for (ProductDetail element in productList!) {
                      if (element.id != null &&
                          element.id == outwardDetail!.productId!.id) {
                        selectedProduct = element;
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
        // getAllWareHouse();
        getAllActiveWareHouse();
        // getAllStaffUser();
      },
      onError: (ResponseModel error) {
        // getAllWareHouse();
        getAllActiveWareHouse();
        // getAllStaffUser();
        _handleApiError(error);
      },
    );
  }

  getAllWareHouse() {
    isLoading = true;
    wareHouseList?.clear();
    update();
    InventoryManagementProvider().getAllWareHouseList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WareHouseListRes responseData = WareHouseListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  wareHouseList?.addAll(responseData.dataList!);
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
        getAllStaffUser();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllStaffUser();
      },
    );
  }

  getAllActiveWareHouse() {
    isLoading = true;
    wareHouseList?.clear();
    update();
    InventoryManagementProvider().getActiveWareHouse(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WareHouseListRes responseData = WareHouseListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  wareHouseList?.addAll(responseData.dataList!);

                  if (outwardDetail != null &&
                      outwardDetail!.destinationType != null &&
                      outwardDetail!.destinationType!.isNotEmpty) {
                    for (WareHouseDetail element in wareHouseList!) {
                      if (element.name != null &&
                          element.name == outwardDetail!.destinationType) {
                        selectedWareHouseData = element;
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
        getAllTypePartnerUserCall();
        // getAllStaffUser();
      },
      onError: (ResponseModel error) {
        getAllTypePartnerUserCall();
        // getAllStaffUser();
        _handleApiError(error);
      },
    );
  }

  getAllStaffUser() {
    isLoading = true;
    staffUserList?.clear();
    update();
    InventoryManagementProvider().getAllActiveStaffUser(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.result != null) {
          try {
            Map<String, dynamic> map = responseModel.result;
            ActiveStaffUserListRes responseData = ActiveStaffUserListRes.fromJson(map);
            if (responseData.responseCode == 200) {
              if (responseData.dataList != null &&
                  responseData.dataList!.isNotEmpty) {
                staffUserList?.addAll(responseData.dataList!);
              }
            }else {
              if (responseModel.message!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          } on Exception catch (e) {
            print(e.toString());
          }
        }
        isLoading = false;
        update();
        setFillDetail();
      },
      onError: (ResponseModel error) {
        setFillDetail();
        log("getAllStaffUserError==>${error.statusCode}");
        _handleApiError(error);
      },
    );
  }

  availableQtyProductDestination () {
    isLoading = true;
    availableQtyProductList?.clear();
    update();
    InventoryManagementProvider().getAvailableQtyDetailsByProductAndDestination(
      productId: selectedProduct!.id,
      ownerId: int.parse(selectedSource!.id!),
      ownerType: selectedSourceType!.text,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AvailableQtyProductDesRes responseData = AvailableQtyProductDesRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  availableQtyProductList?.addAll(responseData.dataList!);
                  for (var element in availableQtyProductList!) {
                    if(element.unusedQty != null ) {
                      availableQty = element.unusedQty;
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);

      },
    );
  }


  getAllTypePartnerUserCall() {
    isLoading = true;
    partnerList?.clear();
    update();
    InventoryManagementProvider().getAllTypePartnerUser(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllTypePartnerRes responseData = GetAllTypePartnerRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) || (responseData.responseCode != null && responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  partnerList?.addAll(responseData.dataList!);
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
        getAllStaffUser();
      },
      onError: (ResponseModel error) {
        getAllStaffUser();
        _handleApiError(error);
      },
    );
  }
  getAllPop() {
    isLoading = true;
    popList?.clear();
    update();
    InventoryManagementProvider().getAllPop(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewPopListRes responseData = ViewPopListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  popList?.addAll(responseData.dataList!);
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
        // getServiceArea();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getServiceArea();
      },
    );
  }
  getServiceArea() {
    isLoading = true;
    servicesAreaList!.clear();
    update();
    CustomerProvider().getNewServiceAreaData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  servicesAreaList!.addAll(responseData.dataList!);
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
        // setFillDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // setFillDetail();
      },
    );
  }
  setFillDetail() {
    if (outwardDetail != null) {
      manageSourceType();
      if (outwardDetail!.sourceId != null &&
          sourceList != null &&
          sourceList!.isNotEmpty) {
        for (DropdownDetail element in sourceList!) {
          if (element.id != null &&
              element.id!.isNotEmpty &&
              element.id!
                  .equalsIgnoreCase(outwardDetail!.sourceId!.toString())) {
            selectedSource = element;
          }
        }
        if (selectedSource != null) {
          // getInwardsDetail();
        }
      }
      manageDestinationType();
    }
  }

  // getInwardsDetail() {
  //   if (selectedSource == null ||
  //       selectedSourceType == null ||
  //       selectedProduct == null) {
  //     /* Utils.showSnackbar(Strings.ERROR, "",
  //         AppTheme.colorWhite, AppTheme.colorRed);*/
  //     return;
  //   }
  //   isLoading = true;
  //   selectedInward = null;
  //   inwardList!.clear();
  //   update();
  //   InventoryManagementProvider().getInwardDetailForOutward(
  //     productId: selectedProduct != null ? selectedProduct!.id!.toString() : "",
  //     destinationId: selectedSource != null ? selectedSource!.id! : "",
  //     destinationType:
  //         selectedSourceType != null ? selectedSourceType!.id! : "",
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             OutwardInwardListRes responseData =
  //                 OutwardInwardListRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 inwardList!.addAll(responseData.dataList!);
  //                 if (outwardDetail != null &&
  //                     outwardDetail!.inwardId != null &&
  //                     outwardDetail!.inwardId!.id != null) {
  //                   for (var element in inwardList!) {
  //                     if (element.id! == outwardDetail!.inwardId!.id!) {
  //                       selectedInward = element;
  //                       break;
  //                     }
  //                   }
  //                 }
  //               }
  //             } else {
  //               if (responseData.responseMessage!.isNotEmpty) {
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
  //       isLoading = false;
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }

  void addEditOutwardApiCall() {
    // isLoading = true;
    update();
    int? qty = 0, sourceId, destinationId;
    if(outwardDetail != null){
      qty= outwardDetail!.inTransitQty;
    }else {
      if (qtyController.text.isNotEmpty) {
        qty = int.parse(qtyController.text);
      }
    }
    if (selectedSource != null &&
        selectedSource!.id != null &&
        selectedSource!.id!.isNotEmpty) {
      sourceId = int.parse(selectedSource!.id!);
    }
    if (selectedDestination != null &&
        selectedDestination!.id != null &&
        selectedDestination!.id!.isNotEmpty) {
      destinationId = int.parse(selectedDestination!.id!);
    }


    AddEditOutwardReq request = AddEditOutwardReq(
        id: outwardDetail != null ? outwardDetail!.id : null,
        productId: selectedProduct != null ? selectedProduct!.id : null,
        qty:  outwardDetail != null ? outwardDetail!.qty : null,
        outwardDateTime: outwardDateTime,
        sourceId: sourceId,
        sourceType: selectedSourceType != null ? selectedSourceType!.id : "",
        outwardNumber:
            outwardDetail != null ? outwardDetail!.outwardNumber : null,
        destinationId: destinationId,
        destinationType: selectedDestinationType != null
            ? selectedDestinationType!.id
            : null,
        inwardId: selectedInward != null ? selectedInward!.id : null,
        usedQty: outwardDetail != null ? outwardDetail!.usedQty : null,
        unusedQty: outwardDetail != null ? outwardDetail!.unusedQty : null,
        inTransitQty:qty,
        status: selectedStatus != null ? selectedStatus!.id : "");


    log("addEditOutwardsRequest ==>${jsonEncode(request)}");

    InventoryManagementProvider().addEditOutwards(
      isAdd: outwardDetail != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200 ||
                responseData.responseCode == 0) {
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
