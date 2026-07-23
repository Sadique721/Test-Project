import 'package:savbill/pages/customer/assign_inventory_controller.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/select_mac_dialog.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AssignInventory extends StatefulWidget {
  @override
  _AssignInventoryState createState() => _AssignInventoryState();
}

class _AssignInventoryState extends State<AssignInventory>
    implements MacAddressAction {
  final assignInventoryController = Get.put(AssignInventoryController());
  final assignInventoryFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<AssignInventoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: assignInventoryController.isLoading),
        ]);
      }), /**/
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Expanded(
              child: SingleChildScrollView(
                child: Padding(
                  padding: const EdgeInsets.only(
                      left: Constant.SCREEN_PADDING,
                      right: Constant.SCREEN_PADDING),
                  child: Form(
                    key: assignInventoryFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.service, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H,
                              width: Constant.DROP_DOWN_ARROW_W_H,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.service,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: false,
                            isDense: true,
                            value: assignInventoryController.selectedPlanService,
                            items: assignInventoryController.planServiceList
                                ?.map((CustomerPlanServiceDetail value) {
                              return DropdownMenuItem<
                                  CustomerPlanServiceDetail>(
                                value: value,
                                child: Text(value.service!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              assignInventoryController.selectedPlanService =
                                  value as CustomerPlanServiceDetail?;
                            },
                            validator: (value) {
                              if (value == null ||
                                  assignInventoryController
                                          .selectedPlanService ==
                                      null) {
                                return Strings.select_service;
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.product, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H,
                              width: Constant.DROP_DOWN_ARROW_W_H,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.product,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: assignInventoryController.selectedProduct,
                            items: assignInventoryController.productList
                                ?.map((ProductDetail value) {
                              return DropdownMenuItem<ProductDetail>(
                                value: value,
                                child: Text(value.name!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              assignInventoryController.selectedProduct =
                                  value as ProductDetail?;
                              assignInventoryController.getInwardsData();
                            },
                            validator: (value) {
                              if (value == null ||
                                  assignInventoryController.selectedProduct ==
                                      null) {
                                return Strings.select_product;
                              }
                              return null;
                            },
                          ),
                        ),

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.inwards, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H,
                              width: Constant.DROP_DOWN_ARROW_W_H,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.inwards,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: false,
                            isDense: true,
                            value: assignInventoryController.selectedInward,
                            items: assignInventoryController.inwardList
                                ?.map((InwardsDetail value) {
                              return DropdownMenuItem<InwardsDetail>(
                                value: value,
                                child: Text(value.inwardNumber!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              assignInventoryController.selectedInward =
                                  value as InwardsDetail?;
                              assignInventoryController.update();
                              assignInventoryController.getMacFromInward();
                            },
                            validator: (value) {
                              if (value == null ||
                                  assignInventoryController.selectedInward ==
                                      null) {
                                return Strings.please_select_inward;
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.mac_address, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.assign_mac_address,
                            textEditingController:
                                assignInventoryController.assignMacController,
                            borderEnableColors: AppTheme.colorBlack,
                            textInputAction: TextInputAction.next,
                            hintColor: AppTheme.colorIconGrey,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.select_mac_address;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              showMacAddressDialog();
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: true),
                        /*Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            CustomText(
                              title: Strings.mac_mpappping_list,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.normal,
                            ),
                            InkWell(
                              onTap: () {
                                showMacAddressDialog();
                              },
                              child: CustomText(
                                title: Strings.add_mac_address,
                                colors: AppTheme.colorPrimary,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium,
                                fontWeight: FontWeight.normal,
                              ),
                            ),
                          ],
                        ),*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        CustomText(
                          title:
                              "${Strings.available_quantity} : ${assignInventoryController.availableQtyPics}",
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.normal,
                        ),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.quantity_in_piece, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.quantity_in_piece,
                            textEditingController:
                                assignInventoryController.qtyPicsController,
                            keyboardType: TextInputType.number,
                            borderEnableColors: AppTheme.colorBlack,
                            textInputAction: TextInputAction.next,
                            hintColor: AppTheme.colorIconGrey,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.enter_quantity;
                              } else {
                                int qty = int.parse(value);
                                if (qty == 0 ||
                                    assignInventoryController.availableQtyPics <
                                        qty) {
                                  return Strings.enter_valid_quantity;
                                }
                              }
                              return null;
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            inputFormatters: [
                              /*FilteringTextInputFormatter.allow(
                                  RegExp(r'^\d+\.?\d{0,2}')),*/
                            ],
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: false),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.inward_date, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.outward_date,
                            suffixIcon: Padding(
                              padding: const EdgeInsetsDirectional.all(
                                  Constant.MEDIUM_PADDING),
                              child: SvgPicture.asset(
                                calendarSvg,
                                color: AppTheme.colorBlack,
                                width: Constant.ICON_SIZE_S,
                                height: Constant.ICON_SIZE_S,
                                // myIcon is a 48px-wide widget.
                              ),
                            ),
                            textEditingController:
                                assignInventoryController.outwardDateController,
                            borderEnableColors: AppTheme.colorBlack,
                            textInputAction: TextInputAction.next,
                            hintColor: AppTheme.colorIconGrey,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.please_select_inward_date;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              selectDate(
                                  Strings.inward_date,
                                  DateTime(DateTime.now().year - 10),
                                  DateTime(DateTime.now().year + 10));
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: true),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(title: Strings.status, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H,
                              width: Constant.DROP_DOWN_ARROW_W_H,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.status,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: false,
                            isDense: true,
                            value: assignInventoryController.selectedStatus,
                            items: assignInventoryController.statusList
                                ?.map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              assignInventoryController.selectedStatus =
                                  value as DropdownDetail?;
                            },
                            validator: (value) {
                              if (value == null ||
                                  assignInventoryController.selectedStatus ==
                                      null) {
                                return Strings.select_status;
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.LARGE_PADDING,
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
            Row(
              children: [
                Expanded(
                  child: SimpleButton(
                    onTap: () {
                      validateForm();
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.submit,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.inward_date) {
      if (assignInventoryController.selectedInwordDateTime != null) {
        selectedDate = assignInventoryController.selectedInwordDateTime;
      } else {
        selectedDate = DateTime.now();
      }
    }

    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate!,
      firstDate: firstDate,
      lastDate: lastDate,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      builder: (BuildContext? context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            primaryColor: AppTheme.colorPrimary,
            colorScheme: ColorScheme.light(primary: AppTheme.colorPrimary),
            buttonTheme:
                const ButtonThemeData(textTheme: ButtonTextTheme.primary),
          ),
          child: child!,
        );
      },
    );
    if (picked != null) {
      if (identity == Strings.inward_date) {
        assignInventoryController.selectedInwordDateTime = picked;
        assignInventoryController.update();
        _selectDateTime();
      }
    }
  }

  Future<void> _selectDateTime() async {
    TimeOfDay? selectedDateTime = TimeOfDay.now();
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedDateTime,
      builder: (BuildContext? context, Widget? child) {
        return MediaQuery(
          data: MediaQuery.of(context!).copyWith(alwaysUse24HourFormat: false),
          child: child!,
        );
      },
    );
    if (picked != null) {
      DateTime dt = DateTime(
        assignInventoryController.selectedInwordDateTime!.year,
        assignInventoryController.selectedInwordDateTime!.month,
        assignInventoryController.selectedInwordDateTime!.day,
        picked.hour,
        picked.minute,
      );
      assignInventoryController.outwardDateController.text =
          assignInventoryController.dateFormat.format(dt);
      assignInventoryController.inwardDateTime =
          assignInventoryController.apiDateTimeFormat.format(dt);
      assignInventoryController.update();
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.assign_inventory, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (assignInventoryFormKey.currentState!.validate()) {
      assignInventoryController.assignInventory();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  showMacAddressDialog() {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return MacAddressDialog(
              macAddressAction: this,
              macAddressLst: assignInventoryController.inventoryMacList!);
        });
  }

  @override
  void macAddressBtnAction({List<InwardMacMapDetail>? selectedItem}) {
    Get.back();
    if (selectedItem != null) {
      assignInventoryController.selectedMacList!.clear();
      assignInventoryController.selectedMacList!.addAll(selectedItem);
      assignInventoryController.availableQtyPics =
          assignInventoryController.selectedMacList!.length;
      String macAdd = "";
      for (int i = 0; i < selectedItem.length; i++) {
        InwardMacMapDetail element = selectedItem[i];
        if (i == selectedItem.length - 1) {
          macAdd = "$macAdd${element.serialNumber!}-${element.macAddress!}";
        } else {
          macAdd = "$macAdd${element.serialNumber!}-${element.macAddress!}, ";
        }
      }
      assignInventoryController.assignMacController.text = macAdd;
      assignInventoryController.update();
    }
  }
}
