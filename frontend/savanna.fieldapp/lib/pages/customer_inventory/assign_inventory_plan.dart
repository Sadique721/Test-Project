import 'dart:developer';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/parent_staff_list.dart';
import 'package:savbill/pages/customer_inventory/response/all_plan_inventory_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_item_based_on_product_type_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_by_plan_id_res.dart';
import 'package:savbill/pages/dashboard/model/response/plan_detail_response.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/customer_inventory/assign_inventory_plan_controller.dart';
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
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import '../customer/model/response/parent_customer_res.dart';

class AssignInventoryPlan extends StatefulWidget {
  @override
  _AssignInventoryPlanState createState() => _AssignInventoryPlanState();
}

class _AssignInventoryPlanState extends State<AssignInventoryPlan>
    implements SelectMacSerialNoAction {
  final assignInventoryPlanController =
      Get.put(AssignInventoryPlanController());
  final assignInventoryFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();

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
      child: GetBuilder<AssignInventoryPlanController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            key: scaffoldKey,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: assignInventoryPlanController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(AssignInventoryPlanController controller) {
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
                        /*__________________ Service ____________________*/

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
                            isExpanded: true,
                            isDense: true,
                            value: controller.selectedPlanService,
                            items: controller.planServiceList
                                ?.map((CustomerPlanServiceDetail value) {
                              return DropdownMenuItem<
                                  CustomerPlanServiceDetail>(
                                value: value,
                                child: Text(value.service!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectedPlanService =
                                  value as CustomerPlanServiceDetail?;
                              controller.selectServices = value.toString();
                              if (value!.connectionNo != null ||
                                  value.connectionNo!.isNotEmpty) {
                                controller.connectionNumberController.text =
                                    value.connectionNo!;
                              } else {
                                controller.connectionNumberController.text =
                                    "--";
                              }
                              if (value.custPlanCategory != null ||
                                  value.custPlanCategory!.isNotEmpty) {
                                controller.planCategoryController.text =
                                    value.custPlanCategory!;
                              } else {
                                controller.planCategoryController.text = "--";
                              }
                              controller.serviceVisible = true;
                              controller.getActivePlanListData(value.serviceId);
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectedPlanService == null) {
                                return Strings.select_service;
                              }
                              return null;
                            },
                          ),
                        ),

                        /*__________________ Connection No ____________________*/

                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? InputTitleRequire(
                                title: Strings.connection_no, require: true)
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? CoustomTextField(
                                labelText: Strings.connection_no,
                                textEditingController:
                                    controller.connectionNumberController,
                                keyboardType: TextInputType.text,
                                borderEnableColors: AppTheme.colorGrey,
                                textInputAction: TextInputAction.next,
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: true)
                            : const SizedBox.shrink(),

                        /*__________________ Plan Category ____________________*/

                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? InputTitleRequire(
                                title: Strings.plan_category, require: true)
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? CoustomTextField(
                                labelText: Strings.plan_category,
                                textEditingController:
                                    controller.planCategoryController,
                                keyboardType: TextInputType.text,
                                borderEnableColors: AppTheme.colorGrey,
                                borderFocusColors: AppTheme.colorGrey,
                                fillColor: AppTheme.colorGrayTxtBg,
                                textInputAction: TextInputAction.next,
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: true)
                            : const SizedBox.shrink(),

                        /*__________________ Active Plan List _________________________*/

                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? InputTitleRequire(
                                title: Strings.plan, require: true)
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? DropdownButtonHideUnderline(
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
                                      Strings.select_plan,
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
                                  value: controller.selectedPlanDetail,
                                  items: controller.activePlanList
                                      ?.map((PlanDetail value) {
                                    return DropdownMenuItem<PlanDetail>(
                                      value: value,
                                      child: Text(value.planName!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedPlanDetail =
                                        value as PlanDetail?;
                                    controller.planInventoryVisible = true;
                                    controller.billToController.text =
                                        value!.billTo!;
                                    if (value!.planId != null) {
                                      controller.productByPlanId =
                                          value.planId.toString();
                                    }
                                    if (value.plangroupid != null) {
                                      controller.productByPlanGroupId =
                                          value.plangroupid;
                                    }
                                    controller.getAllPlanInventoryIdOnPlanId(
                                        value.planId);
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller.selectedPlanDetail == null) {
                                      return Strings.please_select_plan;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*__________________ Plan Inventory Id _________________________*/

                        controller.planInventoryVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.planInventoryVisible == true
                            ? InputTitleRequire(
                                title: Strings.plan_inventory_id, require: true)
                            : const SizedBox.shrink(),
                        controller.planInventoryVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.planInventoryVisible == true
                            ? DropdownButtonHideUnderline(
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
                                      Strings.select_plan_inventory_id,
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
                                  value:
                                      controller.selectAllPlanInventoryIdOnPlan,
                                  items: controller
                                      .getAllPlanInventoryIdOnPlanList
                                      .map((AllPlanInventoryDataList value) {
                                    return DropdownMenuItem<
                                        AllPlanInventoryDataList>(
                                      value: value,
                                      child: Text(value.name!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectAllPlanInventoryIdOnPlan =
                                        value as AllPlanInventoryDataList?;
                                    controller.productCategoryFlag = true;
                                    controller.productPlanMappingId = value!.id;
                                    controller.getProductCategoryByPlanIdApi(
                                        value.id);
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller
                                                .getAllPlanInventoryIdOnPlanList ==
                                            null) {
                                      return Strings
                                          .please_select_plan_inventory_id;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*__________________ Product Category ____________________*/

                        controller.productCategoryFlag == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productCategoryFlag == true
                            ? InputTitleRequire(
                                title: Strings.product_category, require: true)
                            : const SizedBox.shrink(),
                        controller.productCategoryFlag == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productCategoryFlag == true
                            ? CoustomTextField(
                                labelText: Strings.product_category,
                                textEditingController:
                                    controller.productCategoryController,
                                keyboardType: TextInputType.text,
                                fillColor: AppTheme.colorGrayTxtBg,
                                borderEnableColors: AppTheme.colorGrey,
                                borderFocusColors: AppTheme.colorGrey,
                                textInputAction: TextInputAction.next,
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: true)
                            : const SizedBox.shrink(),
                        /*____________Assembly Type____________________________*/

                        controller.getAllPlanInventoryIdOnPlanList.isNotEmpty
                            ? Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const SizedBox(
                                    height: Constant.MEDIUM_PADDING,
                                  ),
                                  InputTitleRequire(
                                      title: Strings.assembly_type,
                                      require: true),
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
                                          Strings.assembly_type,
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
                                      value: assignInventoryPlanController
                                          .selectedAssemblyType,
                                      items: assignInventoryPlanController
                                          .assemblyType
                                          ?.map((DropdownDetail value) {
                                        return DropdownMenuItem<DropdownDetail>(
                                          value: value,
                                          child: Text(value.text!),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        controller.selectedAssemblyType =
                                            value as DropdownDetail?;
                                        controller.update();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            assignInventoryPlanController
                                                    .selectedAssemblyType ==
                                                null) {
                                          return Strings.select_assembly_type;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),

                        /*___________________ Item Condition _______________*/

                        controller.productCategoryFlag == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productCategoryFlag == true
                            ? InputTitleRequire(
                                title: Strings.item_condition, require: true)
                            : const SizedBox.shrink(),
                        controller.productCategoryFlag == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productCategoryFlag == true
                            ? DropdownButtonHideUnderline(
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
                                      Strings.item_condition,
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
                                  value: assignInventoryPlanController
                                      .selectedItemCondition,
                                  items: assignInventoryPlanController
                                      .itemCondition
                                      ?.map((DropdownDetail value) {
                                    return DropdownMenuItem<DropdownDetail>(
                                      value: value,
                                      child: Text(value.text!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedItemCondition =
                                        value as DropdownDetail?;
                                    controller.productPlanVisible = true;
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        assignInventoryPlanController
                                                .selectedItemCondition ==
                                            null) {
                                      return Strings.select_item_condition;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*_________________ Product List ___________________*/

                        controller.productPlanVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productPlanVisible == true
                            ? InputTitleRequire(
                                title: Strings.product, require: true)
                            : const SizedBox.shrink(),
                        controller.productPlanVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productPlanVisible == true
                            ? DropdownButtonHideUnderline(
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
                                      Strings.select_plan,
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
                                  value: controller.selectProductByPlanDataList,
                                  items: controller.productByPlanDataList
                                      ?.map((ProductByPlanDataList value) {
                                    return DropdownMenuItem<
                                        ProductByPlanDataList>(
                                      value: value,
                                      child: Text(value.name!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectProductByPlanDataList =
                                        value as ProductByPlanDataList?;
                                    controller.productId = value!.id!;
                                    controller.hasMacFlag =
                                        value.productCategory!.hasMac;
                                    controller.hasSerialFlag =
                                        value.productCategory!.hasSerial;
                                    controller.getItemBasedOnProductTypeApiCall(
                                        ownerId: value.createdById.toString(),
                                        productId: value.id.toString(),
                                        productCategoryId: value
                                            .productCategory!.id
                                            .toString());
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller
                                                .selectProductByPlanDataList ==
                                            null) {
                                      return Strings.please_select_product;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*____________ Mac & Serial Number _____________________*/

                        controller.macNoAndSerialNoFlag == true
                            ? Container(
                                width: MediaQuery.of(context).size.width,
                                color: AppTheme.colorWhite,
                                margin: const EdgeInsets.only(
                                    top: Constant.MEDIUM_PADDING),
                                child: Form(
                                  child: Column(
                                      mainAxisSize: MainAxisSize.min,
                                      mainAxisAlignment:
                                          MainAxisAlignment.start,
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        const SizedBox(
                                            height: Constant.SMALL_PADDING),
                                        const SizedBox(
                                            height: Constant.SMALL_PADDING),
                                        Row(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.center,
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Expanded(
                                              child: CustomText(
                                                title: Strings.items,
                                                textAlign: TextAlign.center,
                                                colors: AppTheme.lable_noramal,
                                                fontSize: AppTheme.small,
                                                fontWeight: FontWeight.w500,
                                              ),
                                            ),
                                            Container(
                                              height:
                                                  Constant.EXTRA_LARGE_PADDING,
                                              width: 1,
                                              color: AppTheme.lable_noramal,
                                            ),
                                            Expanded(
                                              child: CustomText(
                                                title: Strings.item_type,
                                                textAlign: TextAlign.center,
                                                colors: AppTheme.lable_noramal,
                                                fontSize: AppTheme.small,
                                                fontWeight: FontWeight.w500,
                                              ),
                                            ),
                                            Container(
                                              height:
                                                  Constant.EXTRA_LARGE_PADDING,
                                              width: 1,
                                              color: AppTheme.lable_noramal,
                                            ),
                                            controller.hasMacFlag == true
                                                ? Expanded(
                                                    child: CustomText(
                                                      title:
                                                          Strings.mac_address,
                                                      textAlign:
                                                          TextAlign.center,
                                                      colors: AppTheme
                                                          .lable_noramal,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                          FontWeight.w500,
                                                    ),
                                                  )
                                                : const SizedBox.shrink(),
                                            controller.hasMacFlag == true
                                                ? Container(
                                                    height: Constant
                                                        .EXTRA_LARGE_PADDING,
                                                    width: 1,
                                                    color:
                                                        AppTheme.lable_noramal,
                                                  )
                                                : const SizedBox.shrink(),
                                            controller.hasSerialFlag == true
                                                ? Expanded(
                                                    child: CustomText(
                                                      title: Strings.serial_no,
                                                      textAlign:
                                                          TextAlign.center,
                                                      colors: AppTheme
                                                          .lable_noramal,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                          FontWeight.w500,
                                                    ),
                                                  )
                                                : const SizedBox.shrink(),
                                            controller.hasSerialFlag == true
                                                ? Container(
                                                    height: Constant
                                                        .EXTRA_LARGE_PADDING,
                                                    width: 1,
                                                    color:
                                                        AppTheme.lable_noramal,
                                                  )
                                                : const SizedBox.shrink(),
                                            Expanded(
                                              child: CustomText(
                                                title: Strings.action,
                                                textAlign: TextAlign.center,
                                                colors: AppTheme.lable_noramal,
                                                fontSize: AppTheme.small,
                                                fontWeight: FontWeight.w500,
                                              ),
                                            ),
                                          ],
                                        ),
                                        Flexible(
                                            child: ListView.builder(
                                          shrinkWrap: true,
                                          primary: false,
                                          itemCount: controller
                                              .productTypeDataList!.length,
                                          itemBuilder: (context, index) {
                                            ProductTypDataList item = controller
                                                .productTypeDataList![index];
                                            item.macAddressValue =
                                                item.macAddress;
                                            item.serialNumberValue =
                                                item.serialNumber;
                                            return Column(
                                              children: [
                                                InkWell(
                                                  onTap: () {
                                                    for (var f in controller
                                                        .productTypeDataList!) {
                                                      if (f.id == item.id) {
                                                        f.selected =
                                                            !f.selected!;
                                                        item.macAddressValue =
                                                            item.macAddress;
                                                        item.serialNumberValue =
                                                            item.serialNumber;
                                                      } else {
                                                        f.selected = false;
                                                      }
                                                    }
                                                    setState(() {
                                                      controller
                                                              .productTypeDataList =
                                                          controller
                                                              .productTypeDataList;
                                                      validateSelection();
                                                      controller.update();
                                                    });
                                                  },
                                                  child: Padding(
                                                    padding: const EdgeInsets
                                                            .symmetric(
                                                        vertical: Constant
                                                                .SMALL_PADDING +
                                                            1,
                                                        horizontal: Constant
                                                            .MEDIUM_PADDING),
                                                    child: Row(
                                                      children: [
                                                        item.selected == true
                                                            ? Icon(
                                                                Icons
                                                                    .check_circle,
                                                                color: AppTheme
                                                                    .colorPrimary,
                                                                size: Constant
                                                                    .ICON_SIZE,
                                                              )
                                                            : Icon(
                                                                Icons
                                                                    .radio_button_off,
                                                                color: AppTheme
                                                                    .lable_noramal,
                                                                size: Constant
                                                                    .ICON_SIZE,
                                                              ),
                                                        const SizedBox(
                                                          width: Constant
                                                              .SMALL_PADDING,
                                                        ),
                                                        Expanded(
                                                          child: CustomText(
                                                            title:
                                                                "${item.itemId!}",
                                                            textAlign:
                                                                TextAlign.start,
                                                            colors: item.selected ==
                                                                    true
                                                                ? AppTheme
                                                                    .colorPrimary
                                                                : AppTheme
                                                                    .lable_noramal,
                                                            fontSize:
                                                                AppTheme.small +
                                                                    1,
                                                            fontWeight: item
                                                                        .selected ==
                                                                    true
                                                                ? FontWeight
                                                                    .w500
                                                                : FontWeight
                                                                    .w700,
                                                          ),
                                                        ),
                                                        Expanded(
                                                          flex: 1,
                                                          child: CustomText(
                                                            title:
                                                                item.condition!,
                                                            textAlign:
                                                                TextAlign.start,
                                                            colors: item.selected ==
                                                                    true
                                                                ? AppTheme
                                                                    .colorPrimary
                                                                : AppTheme
                                                                    .lable_noramal,
                                                            fontSize:
                                                                AppTheme.small +
                                                                    1,
                                                            fontWeight: item
                                                                        .selected ==
                                                                    true
                                                                ? FontWeight
                                                                    .w500
                                                                : FontWeight
                                                                    .w700,
                                                          ),
                                                        ),
                                                        controller.hasMacFlag ==
                                                                true
                                                            ? Expanded(
                                                                child: item.selected ==
                                                                        false
                                                                    ? CustomText(
                                                                        title: item.macAddress ??
                                                                            "-",
                                                                        textAlign:
                                                                            TextAlign.center,
                                                                        colors: item.selected ==
                                                                                true
                                                                            ? AppTheme.colorPrimary
                                                                            : AppTheme.lable_noramal,
                                                                        fontSize:
                                                                            AppTheme.small,
                                                                        fontWeight: item.selected ==
                                                                                true
                                                                            ? FontWeight.w300
                                                                            : FontWeight.w400,
                                                                      )
                                                                    : Container(
                                                                        margin: const EdgeInsets.only(
                                                                            right:
                                                                                Constant.VERY_SMALL_PADDING),
                                                                        child:
                                                                            TextFormField(
                                                                          // key: Key(item.id.toString()),
                                                                          initialValue: item.macAddress != null && item.macAddress!.isNotEmpty
                                                                              ? item.macAddress
                                                                              : "",
                                                                          textAlign:
                                                                              TextAlign.start,
                                                                          textAlignVertical:
                                                                              TextAlignVertical.center,
                                                                          style:
                                                                              TextStyle(
                                                                            color:
                                                                                AppTheme.title_dark,
                                                                            fontSize:
                                                                                AppTheme.verySmall,
                                                                            fontWeight:
                                                                                FontWeight.w500,
                                                                            height:
                                                                                1.3,
                                                                            fontFamily:
                                                                                AppTheme.appFontName,
                                                                            decoration:
                                                                                TextDecoration.none,
                                                                          ),
                                                                          decoration: InputDecoration(
                                                                              counterText: "",
                                                                              border: OutlineInputBorder(
                                                                                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                                                                borderSide: BorderSide(color: AppTheme.colorPrimary, width: 1.0),
                                                                              ),
                                                                              focusColor: Colors.amberAccent,
                                                                              focusedBorder: OutlineInputBorder(
                                                                                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                                                                borderSide: BorderSide(color: AppTheme.colorIconGrey, width: 1.0),
                                                                              ),
                                                                              enabledBorder: OutlineInputBorder(
                                                                                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                                                                borderSide: BorderSide(
                                                                                  color: AppTheme.colorIconGrey,
                                                                                  width: 0.6,
                                                                                ),
                                                                              ),
                                                                              isDense: true,
                                                                              contentPadding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING, vertical: Constant.SMALL_PADDING),
                                                                              hintText: Strings.mac_address,
                                                                              alignLabelWithHint: true,
                                                                              fillColor: AppTheme.colorWhite,
                                                                              hoverColor: AppTheme.colorWhite),
                                                                          textInputAction:
                                                                              TextInputAction.next,
                                                                          keyboardType:
                                                                              TextInputType.text,
                                                                          maxLines:
                                                                              1,
                                                                          onChanged:
                                                                              (value) {
                                                                            item.macAddressValue =
                                                                                value;
                                                                            item.macAddress =
                                                                                value;
                                                                            controller.update();
                                                                          },
                                                                        ),
                                                                      ),
                                                              )
                                                            : const SizedBox
                                                                .shrink(),
                                                        controller.hasSerialFlag ==
                                                                true
                                                            ? Expanded(
                                                                child: item.selected ==
                                                                        false
                                                                    ? CustomText(
                                                                        title: item
                                                                            .serialNumber!,
                                                                        textAlign:
                                                                            TextAlign.center,
                                                                        colors: item.selected ==
                                                                                true
                                                                            ? AppTheme.colorPrimary
                                                                            : AppTheme.lable_noramal,
                                                                        fontSize:
                                                                            AppTheme.small,
                                                                        fontWeight: item.selected ==
                                                                                true
                                                                            ? FontWeight.w300
                                                                            : FontWeight.w400,
                                                                      )
                                                                    : Container(
                                                                        alignment:
                                                                            Alignment.centerRight,
                                                                        margin: const EdgeInsets.only(
                                                                            left:
                                                                                Constant.VERY_SMALL_PADDING),
                                                                        child:
                                                                            TextFormField(
                                                                          // key: Key(item.id.toString()),
                                                                          initialValue: item.serialNumber != null && item.serialNumber!.isNotEmpty
                                                                              ? item.serialNumber
                                                                              : "-",
                                                                          textAlign:
                                                                              TextAlign.start,
                                                                          textAlignVertical:
                                                                              TextAlignVertical.center,
                                                                          style:
                                                                              TextStyle(
                                                                            color:
                                                                                AppTheme.title_dark,
                                                                            fontSize:
                                                                                AppTheme.verySmall,
                                                                            fontWeight:
                                                                                FontWeight.w500,
                                                                            height:
                                                                                1.3,
                                                                            fontFamily:
                                                                                AppTheme.appFontName,
                                                                            decoration:
                                                                                TextDecoration.none,
                                                                          ),
                                                                          decoration: InputDecoration(
                                                                              counterText: "",
                                                                              border: OutlineInputBorder(
                                                                                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                                                                borderSide: BorderSide(color: AppTheme.colorPrimary, width: 1.0),
                                                                              ),
                                                                              focusColor: Colors.amberAccent,
                                                                              focusedBorder: OutlineInputBorder(
                                                                                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                                                                borderSide: BorderSide(color: AppTheme.colorIconGrey, width: 1.0),
                                                                              ),
                                                                              enabledBorder: OutlineInputBorder(
                                                                                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                                                                borderSide: BorderSide(
                                                                                  color: AppTheme.colorIconGrey,
                                                                                  width: 0.6,
                                                                                ),
                                                                              ),
                                                                              isDense: true,
                                                                              contentPadding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING, vertical: Constant.SMALL_PADDING),
                                                                              hintText: Strings.serial_no,
                                                                              alignLabelWithHint: true,
                                                                              fillColor: AppTheme.colorWhite,
                                                                              hoverColor: AppTheme.colorWhite),
                                                                          textInputAction:
                                                                              TextInputAction.done,
                                                                          keyboardType:
                                                                              TextInputType.text,
                                                                          maxLines:
                                                                              1,
                                                                          onChanged:
                                                                              (value) {
                                                                            item.serialNumberValue =
                                                                                value;
                                                                            item.serialNumber =
                                                                                value;
                                                                            controller.update();
                                                                          },
                                                                        ),
                                                                      ),
                                                              )
                                                            : const SizedBox
                                                                .shrink(),
                                                        Expanded(
                                                            child: InkWell(
                                                          onTap: () {
                                                            if (item.selected ==
                                                                true) {
                                                              controller.updateMacAndSerialNumber(
                                                                  item.itemId,
                                                                  item.serialNumber,
                                                                  item.macAddress);
                                                              controller
                                                                  .update();
                                                            }
                                                          },
                                                          child: Container(
                                                            alignment: Alignment
                                                                .bottomRight,
                                                            margin: const EdgeInsets
                                                                    .all(
                                                                Constant
                                                                    .SMALL_PADDING),
                                                            child: Material(
                                                              elevation: 1,
                                                              color: item
                                                                          .selected ==
                                                                      false
                                                                  ? AppTheme
                                                                      .custEditLight
                                                                  : AppTheme
                                                                      .colorAccent,
                                                              shape: RoundedRectangleBorder(
                                                                  borderRadius:
                                                                      BorderRadius.circular(
                                                                          Constant
                                                                              .BTN_ROUNDED_CORNER)),
                                                              child: Container(
                                                                height: Constant
                                                                        .BTN_HEIGHT_M -
                                                                    10,
                                                                width: Constant
                                                                        .BTN_HEIGHT_M -
                                                                    5,
                                                                alignment:
                                                                    Alignment
                                                                        .center,
                                                                padding: const EdgeInsets
                                                                        .all(
                                                                    Constant
                                                                        .SMALL_PADDING),
                                                                child:
                                                                    SvgPicture
                                                                        .asset(
                                                                  editSvg,
                                                                  height: Constant
                                                                      .ICON_SIZE,
                                                                  width: Constant
                                                                      .ICON_SIZE,
                                                                  color: item
                                                                              .selected ==
                                                                          false
                                                                      ? AppTheme
                                                                          .colorIconGrey
                                                                      : AppTheme
                                                                          .colorWhite,
                                                                ),
                                                              ),
                                                            ),
                                                          ),
                                                        )),
                                                      ],
                                                    ),
                                                  ),
                                                ),
                                                index ==
                                                        (controller
                                                                .productTypeDataList!
                                                                .length -
                                                            1)
                                                    ? Container()
                                                    : Padding(
                                                        padding: const EdgeInsets
                                                                .symmetric(
                                                            horizontal: Constant
                                                                    .SCREEN_PADDING -
                                                                5),
                                                        child: Divider(
                                                          height: 5,
                                                          color: AppTheme
                                                              .lable_noramal,
                                                          thickness: 0.1,
                                                        ),
                                                      ),
                                              ],
                                            );
                                          },
                                        )),
                                        const SizedBox(
                                            height: Constant.SMALL_PADDING),
                                        /* Row(
                                    children: [
                                      Expanded(
                                        child: InkWell(
                                          onTap: () {
                                            // validateSelection();
                                          },
                                          child: Container(
                                            padding: const EdgeInsets.only(
                                                top: Constant.SCREEN_PADDING,
                                                bottom: Constant.SCREEN_PADDING),
                                            decoration: BoxDecoration(
                                              border: Border.all(
                                                color: AppTheme.colorLightGrey,
                                                width: 1.0,
                                              ),
                                              borderRadius: const BorderRadius.only(
                                                  bottomLeft: Radius.circular(
                                                      Constant.MEDIUM_PADDING)),
                                            ),
                                            child: Text(
                                              Strings.select,
                                              style: TextStyle(
                                                fontWeight: FontWeight.bold,
                                                fontSize: AppTheme.medium + 1,
                                                color: AppTheme.colorPositive,
                                              ),
                                              textAlign: TextAlign.center,
                                            ),
                                          ),
                                        ),
                                      ),
                                      Expanded(
                                        child: InkWell(
                                          onTap: () {
                                            Get.back();
                                          },
                                          child: Container(
                                            padding: const EdgeInsets.only(
                                                top: Constant.SCREEN_PADDING,
                                                bottom: Constant.SCREEN_PADDING),
                                            decoration: BoxDecoration(
                                              border: Border.all(
                                                color: AppTheme.colorLightGrey,
                                                width: 1.0,
                                              ),
                                              borderRadius: const BorderRadius.only(
                                                  bottomRight: Radius.circular(
                                                      Constant.MEDIUM_PADDING)),
                                            ),
                                            child: Text(
                                              Strings.cancel,
                                              style: TextStyle(
                                                fontWeight: FontWeight.bold,
                                                fontSize: AppTheme.medium + 1,
                                                color: AppTheme.colorNagative,
                                              ),
                                              textAlign: TextAlign.center,
                                            ),
                                          ),
                                        ),
                                      ),
                                    ],
                                  ),*/
                                      ]),
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*____________ BillTo & Discount ______________________*/

                        controller.billToDiscountFlag == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.billToDiscountFlag == true
                            ? Row(
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.bill_to, require: false),
                                  ),
                                  const SizedBox(width: Constant.LARGE_PADDING),
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.discount_without,
                                        require: false),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),
                        controller.billToDiscountFlag == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.billToDiscountFlag == true
                            ? Row(
                                children: [
                                  /* Expanded(
                                      flex: 1,
                                      child: DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              Strings.bill_to,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value:
                                              controller.selectedBillToDetail,
                                          items: controller.billToList
                                              ?.map((DropdownDetail value) {
                                            return DropdownMenuItem<
                                                DropdownDetail>(
                                              value: value,
                                              child: Text(
                                                  value.text!.toUpperCase()),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            controller.selectedBillToDetail =
                                                value as DropdownDetail?;
                                            controller.update();
                                          },
                                          validator: (value) {
                                            if (value == null ||
                                                controller
                                                        .selectedBillToDetail ==
                                                    null) {
                                              return Strings
                                                  .please_select_bill_to_type;
                                            }
                                            return null;
                                          },
                                        ),
                                      )),*/

                                  Expanded(
                                      child: CoustomTextField(
                                          labelText: Strings.bill_to,
                                          textEditingController:
                                              controller.billToController,
                                          keyboardType: TextInputType.text,
                                          borderEnableColors:
                                              AppTheme.colorGrey,
                                          borderFocusColors: AppTheme.colorGrey,
                                          textInputAction: TextInputAction.next,
                                          onTextValidator: (String? value) {
                                            return null;
                                          },
                                          fillColor: AppTheme.colorGrayTxtBg,
                                          borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.LARGE_PADDING),
                                          readOnly: true)),
                                  const SizedBox(
                                    width: Constant.LARGE_PADDING,
                                  ),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: '0',
                                        keyboardType: TextInputType.phone,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            controller.discountController,
                                        onTextValidator: (String? value) {
                                          /*  if (value!.isEmpty) {
                                            return Strings.enter_quantity;
                                          }*/
                                          return null;
                                        },
                                        onChanged: (value) {},
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        readOnly: true),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),

                        /*_______________ Old  & New Offer Price ______________*/

                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? Row(
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.old_offer_price,
                                        require: false),
                                  ),
                                  const SizedBox(width: Constant.LARGE_PADDING),
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.new_offer_price,
                                        require: false),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? Row(
                                children: [
                                  Expanded(
                                      flex: 1,
                                      child: CoustomTextField(
                                          labelText: Strings.old_offer_price,
                                          keyboardType: TextInputType.phone,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          textEditingController: controller
                                              .oldOfferPriceController,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings.enter_old_price;
                                            }
                                            return null;
                                          },
                                          onChanged: (value) {},
                                          borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.MEDIUM_PADDING),
                                          readOnly: true)),
                                  const SizedBox(
                                    width: Constant.LARGE_PADDING,
                                  ),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.new_offer_price,
                                        keyboardType: TextInputType.phone,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            controller.newOfferPriceController,
                                        onTextValidator: (String? value) {
                                          log("onChanged==>>$value");
                                          if (value!.isEmpty) {
                                            return Strings.enter_new_price;
                                          }
                                          return null;
                                        },
                                        onChanged: (String? value) {
                                          double? oldPriceAmount = double.parse(controller.productMappingDataList![0].revisedCharge.toString());
                                         if(double.parse(value.toString()) > oldPriceAmount){
                                           Utils.showSnackbar(
                                               Strings.INFO,
                                               Strings.amountValidationMsg,
                                               AppTheme.colorWhite,
                                               AppTheme.colorBlueRView);
                                         }
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        readOnly: false),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),

                        /*____________________ Billable To __________________*/

                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? InputTitleRequire(
                                title: Strings.billableTo, require: true)
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true
                            ? CoustomTextField(
                                labelText: Strings.select_billable_to,
                                hintColor: AppTheme.colorIconGrey,
                                textEditingController:
                                    controller.billableToController,
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
                                  if (controller
                                      .billableToController.text.isEmpty) {
                                    return Strings.select_bill_to;
                                  }
                                  return null;
                                },
                                onTextFiledOnTap: () {
                                  openParentCustomerScreen();
                                },
                                readOnly: true)
                            : const SizedBox.shrink(),

                        /*_______________ Assign Date ______________________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.assigned_date, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.assigned_date,
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
                            textEditingController: assignInventoryPlanController
                                .outwardDateController,
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

                        /*_______________ status ___________________________*/

                       /* const SizedBox(
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
                            value: assignInventoryPlanController.selectedStatus,
                            items: assignInventoryPlanController.statusList
                                ?.map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              assignInventoryPlanController.selectedStatus =
                                  value as DropdownDetail?;
                            },
                            validator: (value) {
                              if (value == null ||
                                  assignInventoryPlanController
                                          .selectedStatus ==
                                      null) {
                                return Strings.select_status;
                              }
                              return null;
                            },
                          ),
                        ),*/

                        /*______________ payment owner _______________________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.payment_owner, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        /*DropdownButtonHideUnderline(
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
                                Strings.select_staff,
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
                            value:
                                assignInventoryPlanController.paymentOwnerData,
                            items: assignInventoryPlanController
                                .paymentOwnerList
                                .map((PaymentOwnerDataList value) {
                              return DropdownMenuItem<PaymentOwnerDataList>(
                                value: value,
                                child: Text(
                                    "${value.fullName.toString()} (Ph: ${value.phone})"),
                              );
                            }).toList(),
                            onChanged: (value) {
                              assignInventoryPlanController.paymentOwnerData =
                                  value as PaymentOwnerDataList?;
                              assignInventoryPlanController.paymentOwnerId =
                                  value!.id;
                            },
                            validator: (value) {
                              if (value == null ||
                                  assignInventoryPlanController
                                          .paymentOwnerData ==
                                      null) {
                                return Strings.select_payment_owner;
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.LARGE_PADDING,
                        ),*/

                        CoustomTextField(
                            labelText: Strings.select_staff,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                            controller.paymentOwnerStaffController,
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
                              if (controller
                                  .paymentOwnerStaffController.text.isEmpty) {
                                return Strings.select_payment_owner;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              // openParentCustomerScreen();
                              openParentStaffScreen();
                            },
                            readOnly: true),
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
      if (assignInventoryPlanController.selectedInwordDateTime != null) {
        selectedDate = assignInventoryPlanController.selectedInwordDateTime;
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
        assignInventoryPlanController.selectedInwordDateTime = picked;
        assignInventoryPlanController.update();
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
        assignInventoryPlanController.selectedInwordDateTime!.year,
        assignInventoryPlanController.selectedInwordDateTime!.month,
        assignInventoryPlanController.selectedInwordDateTime!.day,
        picked.hour,
        picked.minute,
      );
      assignInventoryPlanController.outwardDateController.text =
          assignInventoryPlanController.dateFormat.format(dt);
      assignInventoryPlanController.inwardDateTime =
          assignInventoryPlanController.apiDateTimeFormat.format(dt);
      assignInventoryPlanController.update();
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.assign_inventory_with_plan,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (assignInventoryFormKey.currentState!.validate()) {
      assignInventoryPlanController.assignPlanInventoryByPlanCallApi();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  openParentCustomerScreen() async {
    var result = await Get.to(ParentCustomerList(), arguments: {
      Constant.CUSTOMER_TYPE: assignInventoryPlanController.customerType!
    });
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        assignInventoryPlanController.selectedParentCustomer = data;
        assignInventoryPlanController.billableToController.text = data.name!;
        assignInventoryPlanController.billableCustomerId = data.id;
        assignInventoryPlanController.update();
      }
    }
  }

  openParentStaffScreen() async {
    var result = await Get.to(ParentStaffList(), arguments: {
    });
    if (result != null) {
      ParentStaffUserlist data = result;
      if (data != null) {
        assignInventoryPlanController.selectedParentStaff = data;
        assignInventoryPlanController.paymentOwnerStaffController.text = data.firstname!;
        assignInventoryPlanController.paymentOwnerId = data.id;
        assignInventoryPlanController.update();
      }
    }
  }

  validateSelection() {
    List<ProductTypDataList> selectedItem = [];
    for (var element in assignInventoryPlanController.productTypeDataList!) {
      if (element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      selectMacAndSerialNoBtnAction(selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.ERROR, Strings.select_at_list_one_item,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

  @override
  void selectMacAndSerialNoBtnAction({List<ProductTypDataList>? selectedItem}) {
    // Get.back();
    if (selectedItem != null) {
      assignInventoryPlanController.selectedMacAddressList!.clear();
      assignInventoryPlanController.selectedMacAddressList!.addAll(selectedItem);

      assignInventoryPlanController.availableQtyPics =
          assignInventoryPlanController.selectedMacAddressList!.length;
      String macAdd = "";
      for (int i = 0; i < selectedItem.length; i++) {
        ProductTypDataList element = selectedItem[i];
        assignInventoryPlanController.selectProductTypeData = element;
        assignInventoryPlanController.productItemId = element.itemId;
        if (i == selectedItem.length - 1) {
          if (element.macAddress != null) {
            macAdd =
                "$macAdd${element.itemId!} - ${element.condition!} - ${element.serialNumber!} - ${element.macAddress!}";
          } else {
            macAdd =
                "$macAdd${element.itemId!} - ${element.condition!} - ${element.serialNumber!}";
          }
        } else {
          macAdd = "$macAdd${element.serialNumber!}-${element.macAddress!}, ";
        }
      }
      assignInventoryPlanController.assignMacController.text = macAdd;
      assignInventoryPlanController.update();
    }
  }
}

abstract class SelectMacSerialNoAction {
  void selectMacAndSerialNoBtnAction({List<ProductTypDataList> selectedItem});
}
