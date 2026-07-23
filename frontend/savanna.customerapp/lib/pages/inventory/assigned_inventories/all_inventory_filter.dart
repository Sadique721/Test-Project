import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/inventory/assigned_inventories/all_inventory_filter_controller.dart';
import 'package:savbill/pages/inventory/module/response/bulk_consumption_inward_res.dart';
import 'package:savbill/pages/inventory/module/response/filter_data.dart';
import 'package:savbill/pages/inventory/module/response/item_type_res.dart';
import 'package:savbill/pages/inventory/module/response/ownership_res.dart';
import 'package:savbill/pages/inventory/module/response/status_res.dart';
import 'package:savbill/pages/inventory/module/response/warranty_status_res.dart';
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

class AllInventoryFilter extends StatefulWidget {
  @override
  _AllInventoryFilterState createState() => _AllInventoryFilterState();
}

class _AllInventoryFilterState extends State<AllInventoryFilter> {
  final allInventoriesFilterController =
      Get.put(AllInventoriesFilterController());

  final GlobalKey<FormFieldState> keyOwner = GlobalKey<FormFieldState>();
  final GlobalKey<FormFieldState> keyOwnerType = GlobalKey<FormFieldState>();

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
      child: GetBuilder<AllInventoriesFilterController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: allInventoriesFilterController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return Stack(children: [
      Positioned(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.only(
                top: Constant.MEDIUM_PADDING,
                left: Constant.SCREEN_PADDING,
                right: Constant.SCREEN_PADDING,
                bottom: Constant.SCREEN_PADDING * 2.5),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                InputTitleRequire(title: Strings.owner_type, require: false),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
                DropdownButtonHideUnderline(
                  child: DropdownButtonFormField(
                    key: keyOwnerType,
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
                        Strings.owner_type,
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
                    value: allInventoriesFilterController.selectedOwnerType,
                    items: allInventoriesFilterController.ownerTypeList
                        ?.map((DropdownDetail value) {
                      return DropdownMenuItem<DropdownDetail>(
                        value: value,
                        child: Text(value.text!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedOwnerType =
                          value as DropdownDetail?;

                      allInventoriesFilterController.selectedOwner = null;

                      allInventoriesFilterController.ownerList!.clear();

                      allInventoriesFilterController.update();
                      allInventoriesFilterController.manageOwner();
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(title: Strings.owner, require: false),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
                DropdownButtonHideUnderline(
                  child: DropdownButtonFormField(
                    key: keyOwner,
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
                        Strings.owner,
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
                    value: allInventoriesFilterController.selectedOwner,
                    items: allInventoriesFilterController.ownerList
                        ?.map((DropdownDetail value) {
                      return DropdownMenuItem<DropdownDetail>(
                        value: value,
                        child: Text(value.text!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedOwner =
                          value as DropdownDetail?;
                      allInventoriesFilterController.update();
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(title: Strings.product, require: false),
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
                    value: allInventoriesFilterController.selectedProduct,
                    items: allInventoriesFilterController.productList
                        ?.map((ProductDetail value) {
                      return DropdownMenuItem<ProductDetail>(
                        value: value,
                        child: Text(value.name!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedProduct =
                          value as ProductDetail?;
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),


                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(title: Strings.serial_no, require: false),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
                CoustomTextField(
                    labelText: Strings.enter_serial_no,
                    textEditingController: allInventoriesFilterController
                        .serialNumberController,
                    keyboardType: TextInputType.text,
                    borderEnableColors: AppTheme.colorDisableGray,
                    textInputAction: TextInputAction.next,
                    hintColor: AppTheme.colorIconGrey,
                    onTextValidator: (String? value) {
                      return null;
                    },
                    borderCorner: Constant.INPUT_ROUNDED_CORNER,
                    contentPadding: const EdgeInsets.symmetric(
                        horizontal: Constant.LARGE_PADDING),
                    readOnly: false),

                /*const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(title: Strings.inwards, require: false),
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
                    value: allInventoriesFilterController.selectedInward,
                    items: allInventoriesFilterController.inwardList
                        ?.map((BulkConsumptionInward value) {
                      return DropdownMenuItem<BulkConsumptionInward>(
                        value: value,
                        child: Text(value.inwardNumber!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedInward =
                          value as BulkConsumptionInward?;
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),*/
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(title: Strings.ownership, require: false),
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
                        Strings.ownership,
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
                    value: allInventoriesFilterController.selectedOwnership,
                    items: allInventoriesFilterController.ownershipList
                        ?.map((OwnershipDetail value) {
                      return DropdownMenuItem<OwnershipDetail>(
                        value: value,
                        child: Text(value.text!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedOwnership =
                          value as OwnershipDetail?;
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(title: Strings.status, require: false),
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
                    value: allInventoriesFilterController.selectedStatus,
                    items: allInventoriesFilterController.statusList
                        ?.map((StatusDetail value) {
                      return DropdownMenuItem<StatusDetail>(
                        value: value,
                        child: Text(value.text!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedStatus =
                          value as StatusDetail?;
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(title: Strings.item_type, require: false),
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
                        Strings.item_type,
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
                    value: allInventoriesFilterController.selectedItemType,
                    items: allInventoriesFilterController.itemTypeList
                        ?.map((ItemTypeDetail value) {
                      return DropdownMenuItem<ItemTypeDetail>(
                        value: value,
                        child: Text(value.text!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedItemType =
                          value as ItemTypeDetail?;
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                InputTitleRequire(
                    title: Strings.warranty_status, require: false),
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
                        Strings.warranty_status,
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
                        allInventoriesFilterController.selectedWarrantyStatus,
                    items: allInventoriesFilterController.warrantyStatusList
                        ?.map((WarrantyStatusDetail value) {
                      return DropdownMenuItem<WarrantyStatusDetail>(
                        value: value,
                        child: Text(value.text!),
                      );
                    }).toList(),
                    onChanged: (value) {
                      allInventoriesFilterController.selectedWarrantyStatus =
                          value as WarrantyStatusDetail?;
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
              ],
            ),
          ),
        ),
      ),
      Positioned(
          child: Align(
        alignment: FractionalOffset.bottomCenter,
        child: Row(
          children: [_buttonView(Strings.reset), _buttonView(Strings.apply)],
        ),
      )),
    ]);
  }

  _buttonView(String btnName) {
    return Expanded(
      child: SimpleButton(
        onTap: () {
          if (btnName.equalsIgnoreCase(Strings.apply)) {
            if (allInventoriesFilterController.selectedOwnerType == null &&
                allInventoriesFilterController.selectedOwner == null &&
                allInventoriesFilterController.selectedProduct == null &&
                // allInventoriesFilterController.selectedInward == null &&
                allInventoriesFilterController.serialNumberController.text.isEmpty &&
                allInventoriesFilterController.selectedOwnership == null &&
                allInventoriesFilterController.selectedStatus == null &&
                allInventoriesFilterController.selectedItemType == null &&
                allInventoriesFilterController.selectedWarrantyStatus == null) {
              Utils.showSnackbar(Strings.ERROR, "Please select filter data",
                  AppTheme.colorWhite, AppTheme.colorRed);
              return;
            }

            if (allInventoriesFilterController.selectedOwnerType != null &&
                allInventoriesFilterController.selectedOwner == null) {
              Utils.showSnackbar(Strings.ERROR, "Please select owner data",
                  AppTheme.colorWhite, AppTheme.colorRed);
              return;
            }

            FilterData data = FilterData(
                identify: Strings.apply,
                ownerType:
                    allInventoriesFilterController.selectedOwnerType != null
                        ? allInventoriesFilterController.selectedOwnerType!.text
                        : "",
                owner: allInventoriesFilterController.selectedOwner != null
                    ? allInventoriesFilterController.selectedOwner!.id
                    : "",
                productId:
                    allInventoriesFilterController.selectedProduct != null
                        ? allInventoriesFilterController.selectedProduct!.id!
                        : null,
                /*inwardId: allInventoriesFilterController.selectedInward != null
                    ? allInventoriesFilterController.selectedInward!.id!
                    : null,*/
                ownership: allInventoriesFilterController.selectedOwnership !=
                        null
                    ? allInventoriesFilterController.selectedOwnership!.value!
                    : null,
                status: allInventoriesFilterController.selectedStatus != null
                    ? allInventoriesFilterController.selectedStatus!.value!
                    : "",
                itemType: allInventoriesFilterController.selectedItemType != null
                    ? allInventoriesFilterController.selectedItemType!.value!
                    : "",
                warrantyStatus: allInventoriesFilterController.selectedWarrantyStatus != null
                    ? allInventoriesFilterController.selectedWarrantyStatus!.value!
                    : "",

                serialNumber: allInventoriesFilterController.serialNumberController.text.isNotEmpty
                    ? allInventoriesFilterController.serialNumberController.text
                    : ""
            );

            Get.back(result: data);
          }
          if (btnName.equalsIgnoreCase(Strings.reset)) {
            FilterData data = FilterData(identify: Strings.reset);
            Get.back(result: data);
          }
        },
        radius: 0,
        height: Constant.BOTTOM_BTN_HEIGHT,
        bgColors: btnName.equalsIgnoreCase(Strings.apply)
            ? AppTheme.colorPrimary
            : AppTheme.colorBlack,
        borderColors: btnName.equalsIgnoreCase(Strings.apply)
            ? AppTheme.colorPrimary
            : AppTheme.colorBlack,
        child: CustomText(
          title: btnName,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.w400,
        ),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.filter, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }
}
