import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/inventory/external_group/add_edit_external_grp_controller.dart';
import 'package:savbill/pages/inventory/external_group/external_item_owner_list.dart';
import 'package:savbill/pages/inventory/module/response/external_group_owner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/external_partner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
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
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AddEditExternalGrp extends StatefulWidget {
  @override
  _AddEditExternalGrpState createState() => _AddEditExternalGrpState();
}

class _AddEditExternalGrpState extends State<AddEditExternalGrp> {
  final addEditExternalGrpController = Get.put(AddEditExternalGrpController());
  final addEditExternalGrpFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddEditExternalGrpController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditExternalGrpController.isLoading),
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
                      key: addEditExternalGrpFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          addEditExternalGrpController.externalGroupDetail !=
                                  null
                              ? InputTitleRequire(
                                  title:
                                      "${Strings.external_groups_item_no} :- ${addEditExternalGrpController.externalGroupDetail!.externalItemGroupNumber!}",
                                  require: false)
                              : Container(),
                          addEditExternalGrpController.externalGroupDetail !=
                                  null
                              ? const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
                                )
                              : Container(),
                          InputTitleRequire(
                              title: Strings.product, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditExternalGrpController
                                        .externalGroupDetail !=
                                    null
                                ? true
                                : false,
                            child: DropdownButtonHideUnderline(
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
                                isExpanded: false,
                                isDense: true,
                                value: addEditExternalGrpController
                                    .selectedProduct,
                                items: addEditExternalGrpController.productList
                                    ?.map((ProductDetail value) {
                                  return DropdownMenuItem<ProductDetail>(
                                    value: value,
                                    child: Text(value.name!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditExternalGrpController.selectedProduct =
                                      value as ProductDetail?;
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditExternalGrpController
                                              .selectedProduct ==
                                          null) {
                                    return Strings.select_product;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.service_area, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditExternalGrpController
                                        .externalGroupDetail !=
                                    null
                                ? true
                                : false,
                            child: DropdownButtonHideUnderline(
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
                                    Strings.service_area,
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
                                value: addEditExternalGrpController
                                    .selectedServiceArea,
                                items: addEditExternalGrpController
                                    .serviceAreaList
                                    ?.map((StaffServiceAreaDetail value) {
                                  return DropdownMenuItem<
                                      StaffServiceAreaDetail>(
                                    value: value,
                                    child: Text(value.name!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditExternalGrpController
                                          .selectedServiceArea =
                                      value as StaffServiceAreaDetail?;
                                  addEditExternalGrpController.selectedType = null;
                                  addEditExternalGrpController.ownerController.clear();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditExternalGrpController
                                              .selectedServiceArea ==
                                          null) {
                                    return Strings.select_service_area;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.ownership_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditExternalGrpController
                                        .externalGroupDetail !=
                                    null
                                ? true
                                : false,
                            child: DropdownButtonHideUnderline(
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
                                    Strings.ownership_type,
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
                                value:
                                    addEditExternalGrpController.selectedType,
                                items: addEditExternalGrpController.typeList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditExternalGrpController.selectedType =
                                      value as DropdownDetail?;
                                  addEditExternalGrpController.ownerController.clear();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditExternalGrpController
                                              .selectedType ==
                                          null) {
                                    return Strings.select_type;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.quantity_in, require: true),
                          addEditExternalGrpController.externalGroupDetail !=
                                  null
                              ? const SizedBox(
                                  width: Constant.VERY_SMALL_PADDING,
                                )
                              : Container(),
                          addEditExternalGrpController.externalGroupDetail !=
                                  null
                              ? Row(
                                  children: [
                                    CustomText(
                                      title:
                                          "${Strings.used_quantity} :- ${addEditExternalGrpController.externalGroupDetail!.usedQty} ",
                                      colors: AppTheme.lable_noramal,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.normal,
                                    ),
                                    const SizedBox(
                                      width: Constant.SMALL_PADDING,
                                    ),
                                    CustomText(
                                      title:
                                          "${Strings.total_mac_serial_qty} :- ${addEditExternalGrpController.externalGroupDetail!.totalMacSerial} ",
                                      colors: AppTheme.lable_noramal,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.normal,
                                    ),
                                  ],
                                )
                              : Container(),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.quantity_in,
                              textEditingController:
                                  addEditExternalGrpController.qtyController,
                              borderEnableColors: AppTheme.colorIconGrey,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.number,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_quantity_in;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),

                          InputTitleRequire(
                              title: Strings.owner, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.select_owner,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                              addEditExternalGrpController.ownerController,
                              suffixIcon: Padding(
                                padding: const EdgeInsetsDirectional.all(
                                    Constant.LARGE_PADDING - 2),
                                child: SvgPicture.asset(
                                  downArrowSvg,
                                  color: AppTheme.colorBlack,
                                  width: Constant.ICON_SIZE_S,
                                  height: Constant.ICON_SIZE_S,
                                ),
                              ),
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              keyboardType: TextInputType.text,
                              fontSize: AppTheme.small,
                              textInputAction: TextInputAction.done,
                              fontWeight: FontWeight.w500,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING,
                                  vertical: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onTextValidator: (String? value) {
                                if (value == null ||
                                    addEditExternalGrpController
                                        .ownerController.text.isEmpty) {
                                  return Strings.please_select_owner;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {
                                openOwnerCustomerScreen();
                              },
                              readOnly: true),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          InputTitleRequire(
                              title: Strings.status, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
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
                              value:
                                  addEditExternalGrpController.selectedStatus,
                              items: addEditExternalGrpController.statusList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditExternalGrpController.selectedStatus =
                                    value as DropdownDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditExternalGrpController
                                            .selectedStatus ==
                                        null) {
                                  return Strings.please_select_status;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.EXTRA_LARGE_PADDING,
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
            ]),
      ),
    );
  }

  validateForm() {
    if (addEditExternalGrpFormKey.currentState!.validate()) {
      addEditExternalGrpController.addEditExternalGroupApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  openOwnerCustomerScreen() async {
    var result = await Get.to(ExternalOwnerList(),
        arguments: {Constant.OWNER_TYPE: addEditExternalGrpController.selectedType!.text!,Constant.ID : addEditExternalGrpController.selectedServiceArea!.id});
    if (result != null) {
      if(addEditExternalGrpController.selectedType!.text!.equalsIgnoreCase(Strings.customer_owned)) {
        ExternalOwnerDataList data = result;
        if (data != null) {
          addEditExternalGrpController.selectedOwnerCustomer = data;
          addEditExternalGrpController.ownerController.text = data.fullName!;
          addEditExternalGrpController.update();
        }
      }else if(addEditExternalGrpController.selectedType!.text!.equalsIgnoreCase(Strings.partner_owned)) {
        ExternalPartnerDataList data = result;
        if (data != null) {
          addEditExternalGrpController.selectedOwnerPartner = data;
          addEditExternalGrpController.ownerController.text = data.name!;
          addEditExternalGrpController.update();
        }
      }

    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditExternalGrpController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_external_item
            : Strings.create_external_item,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
