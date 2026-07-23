import 'package:savbill/pages/change_plan/active_plan_selection_dialog.dart';
import 'package:savbill/pages/change_plan/remark_dialog.dart';
import 'package:savbill/pages/change_plan/response/customer_pojo.dart';
import 'package:savbill/pages/change_plan/select_plan_group_dialog.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart'
    as custPlanMap;
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/parent_staff_list.dart';
import 'package:savbill/pages/customer_charge/response/active_plan_list_res.dart';
import 'package:savbill/pages/enum/enum.dart';
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
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:moment_dart/moment_dart.dart';

import 'change_plan_caf_controller.dart';

class ChangePlanCafScreen extends StatefulWidget {
  @override
  _ChangePlanCafState createState() => _ChangePlanCafState();
}

class _ChangePlanCafState extends State<ChangePlanCafScreen>
    implements
        ActivePlanSelectionAction,
        SelectPlanAction,
        PromisePayRemarkBtnAction {
  final changePlanController = Get.put(ChangePlanCAFController());

  final changePlanFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<ChangePlanCAFController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: changePlanController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      child: SingleChildScrollView(
        physics: const ScrollPhysics(),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.only(
                  top: Constant.SCREEN_PADDING,
                  left: Constant.SCREEN_PADDING,
                  right: Constant.SCREEN_PADDING),
              child: CustomText(
                  title:
                      "${changePlanController.customerName}  ${Strings.current_plan}"
                          .capitalize,
                  colors: AppTheme.colorBlack,
                  textAlign: TextAlign.start,
                  fontSize: AppTheme.medium + 1,
                  fontWeight: FontWeight.w500),
            ),
            (changePlanController.customerPlanServiceDetailList != null &&
                    changePlanController
                        .customerPlanServiceDetailList!.isNotEmpty)
                ? ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.MEDIUM_PADDING,
                        vertical: Constant.SMALL_PADDING),
                    itemCount: changePlanController.planServiceList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      return _itemList(context, index);
                    })
                : SizedBox(
                    height: Constant.VERY_EXTRA_LARGE_PADDING,
                    child: noDataFound(),
                  ),
            Container(
                padding: const EdgeInsets.only(
                    top: Constant.SCREEN_PADDING,
                    left: Constant.SCREEN_PADDING,
                    right: Constant.SCREEN_PADDING),
                child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(
                        child: CustomText(
                            title:
                                "${changePlanController.customerName} ${Strings.change_plan}",
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.medium + 1,
                            fontWeight: FontWeight.w500),
                      )
                    ])),
            Padding(
              padding: const EdgeInsets.only(
                  left: Constant.SCREEN_PADDING,
                  right: Constant.SCREEN_PADDING),
              child: Form(
                key: changePlanFormKey,
                autovalidateMode: autoValidateMode,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Expanded(
                          flex: 1,
                          child: cardDataRow(
                              Strings.service_name,
                              ((changePlanController
                                  .customerPlanServiceDetailList!
                                  .isNotEmpty &&
                                  changePlanController
                                      .customerPlanServiceDetailList![0]
                                      .service !=
                                      null))
                                  ? changePlanController
                                  .customerPlanServiceDetailList![0].service
                                  .toString()
                                  : "--",
                              AppTheme.lable_noramal),
                        ),
                        Expanded(
                          flex: 1,
                          child: cardDataRow(
                              Strings.nick_name, "-", AppTheme.lable_noramal),
                        ),
                      ],
                    ),
                    ///Change Plan Type
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    InputTitleRequire(
                        title: Strings.connection_no, require: true),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    IgnorePointer(
                      ignoring: false,
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
                              Strings.select_connection_no,
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
                          value: changePlanController.activePlanListDetail,
                          items: changePlanController.activePlanList
                              ?.map((ActivePlanListDataList value) {
                            return DropdownMenuItem<ActivePlanListDataList>(
                              value: value,
                              child: Text(value.connectionNo!),
                            );
                          }).toList(),
                          onChanged: (value) {
                            changePlanController.activePlanListDetail =
                                value as ActivePlanListDataList?;
                            changePlanController.update();
                            changePlanController.getServicePlanModeServiceAreaAPI(false);
                          },
                          validator: (value) {
                            if (value == null) {
                              return Strings.select_connection_no;
                            }
                            return null;
                          },
                        ),
                      ),
                    ),
                    // const SizedBox(
                    //   height: Constant.MEDIUM_PADDING,
                    // ),

                    // InputTitleRequire(
                    //     title: Strings.change_plan_type, require: true),
                    // const SizedBox(
                    //   height: Constant.VERY_SMALL_PADDING,
                    // ),
                    // IgnorePointer(
                    //   ignoring: false,
                    //   child: DropdownButtonHideUnderline(
                    //     child: DropdownButtonFormField(
                    //       icon: SvgPicture.asset(
                    //         downArrowSvg,
                    //         height: Constant.DROP_DOWN_ARROW_W_H,
                    //         width: Constant.DROP_DOWN_ARROW_W_H,
                    //         color: AppTheme.colorBlack,
                    //         fit: BoxFit.fill,
                    //       ),
                    //       decoration: Utils.ddlDecoration(),
                    //       hint: Align(
                    //         alignment: Alignment.centerLeft,
                    //         child: Text(
                    //           changePlanController.selectedChangePlanList?.text ?? "",
                    //           style: TextStyle(
                    //             fontSize: AppTheme.medium,
                    //             color: AppTheme.colorIconGrey,
                    //             fontFamily: AppTheme.appFontName,
                    //           ),
                    //         ),
                    //       ),
                    //       style: AppTheme.dropdownTextStyle,
                    //       isExpanded: true,
                    //       isDense: true,
                    //       value: changePlanController.selectedChangePlanList,
                    //       items: changePlanController.changePlanTypeList?.map((ChangePlanTypeList value) {
                    //         return DropdownMenuItem<
                    //             ChangePlanTypeList>(
                    //           value: value,
                    //           child: Text(value.text!),
                    //         );
                    //       }).toList(),
                    //       onChanged: (value) {
                    //         changePlanController.selectedChangePlanList =
                    //         value as ChangePlanTypeList?;
                    //         changePlanController.getServicePlanModeServiceAreaAPI(false);
                    //       },
                    //       validator: (value) {
                    //         // if (value == null ||
                    //         //     changePlanController.selectedPlanType == null ||
                    //         //     changePlanController.selectedPlan?.id == 0) {
                    //         //   return Strings.please_select_plan_type;
                    //         // }
                    //         return null;
                    //       },
                    //     ),
                    //   ),
                    // ),

                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),

                    InputTitleRequire(
                        title: Strings.new_change_plan, require: true),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    IgnorePointer(
                      ignoring: false,
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
                              Strings.select_change_plan_type,
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
                              changePlanController.selectedServiceAreaPlanList,
                          items: changePlanController.serviceAreaAllPlanList
                              ?.map((ServiceAreaPlanPostpaidplanList value) {
                            return DropdownMenuItem<
                                ServiceAreaPlanPostpaidplanList>(
                              value: value,
                              child: Text(value.name!),
                            );
                          }).toList(),
                          onChanged: (value) {
                            changePlanController.selectedServiceAreaPlanList =
                                value as ServiceAreaPlanPostpaidplanList?;
                            changePlanController.newPlanId = value!.id;
                            changePlanController
                                .getCustomerPlanDetailAPI(value.id);
                            changePlanController.update();
                          },
                          validator: (value) {
                            if (value == null ||
                                changePlanController.selectedServiceAreaPlanList == null ||
                                changePlanController.newPlanId == 0) {
                              return Strings.please_select_plan_type;
                            }
                            return null;
                          },
                        ),
                      ),
                    ),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),

                    // ////////////////////////////////////
                    // InputTitleRequire(
                    //     title: Strings.payment_owner, require: true),
                    // const SizedBox(height: Constant.SMALL_PADDING),
                    // CoustomTextField(
                    //     labelText: Strings.select_staff,
                    //     hintColor: AppTheme.colorIconGrey,
                    //     textEditingController:
                    //         changePlanController.paymentOwnerController,
                    //     suffixIcon: Padding(
                    //       padding: const EdgeInsetsDirectional.all(
                    //           Constant.LARGE_PADDING - 2),
                    //       child: SvgPicture.asset(
                    //         downArrowSvg,
                    //         color: AppTheme.colorBlack,
                    //         width: Constant.ICON_SIZE_S,
                    //         height: Constant.ICON_SIZE_S,
                    //       ),
                    //     ),
                    //     borderEnableColors: AppTheme.colorIconGrey,
                    //     borderFocusColors: AppTheme.colorIconGrey,
                    //     textColor: AppTheme.colorBlack,
                    //     keyboardType: TextInputType.text,
                    //     fontSize: AppTheme.small,
                    //     textInputAction: TextInputAction.done,
                    //     fontWeight: FontWeight.w500,
                    //     contentPadding: const EdgeInsets.symmetric(
                    //         horizontal: Constant.MEDIUM_PADDING,
                    //         vertical: Constant.MEDIUM_PADDING),
                    //     borderCorner: Constant.BTN_ROUNDED_CORNER,
                    //     onTextValidator: (String? value) {
                    //       if (value!.isEmpty) {
                    //         return Strings.please_select_payment_owner;
                    //       }
                    //       return null;
                    //     },
                    //     onTextFiledOnTap: () {
                    //       openParentStaffScreen(Strings.payment_owner);
                    //     },
                    //     readOnly: true),
                    // ////////////////////////////////////////
                    //
                    // const SizedBox(
                    //   height: Constant.MEDIUM_PADDING,
                    // ),
                    // InputTitleRequire(
                    //     title: Strings.billableTo, require: false),
                    // const SizedBox(height: Constant.SMALL_PADDING),
                    // CoustomTextField(
                    //     labelText: Strings.select_billable_to,
                    //     hintColor: AppTheme.colorIconGrey,
                    //     textEditingController:
                    //         changePlanController.billableToController,
                    //     suffixIcon: Padding(
                    //       padding: const EdgeInsetsDirectional.all(
                    //           Constant.LARGE_PADDING - 2),
                    //       child: SvgPicture.asset(
                    //         downArrowSvg,
                    //         color: AppTheme.colorBlack,
                    //         width: Constant.ICON_SIZE_S,
                    //         height: Constant.ICON_SIZE_S,
                    //       ),
                    //     ),
                    //     borderEnableColors: AppTheme.colorIconGrey,
                    //     borderFocusColors: AppTheme.colorIconGrey,
                    //     textColor: AppTheme.colorBlack,
                    //     keyboardType: TextInputType.text,
                    //     fontSize: AppTheme.small,
                    //     textInputAction: TextInputAction.done,
                    //     fontWeight: FontWeight.w500,
                    //     contentPadding: const EdgeInsets.symmetric(
                    //         horizontal: Constant.MEDIUM_PADDING,
                    //         vertical: Constant.MEDIUM_PADDING),
                    //     borderCorner: Constant.BTN_ROUNDED_CORNER,
                    //     onTextValidator: (String? value) {
                    //       return null;
                    //     },
                    //     onTextFiledOnTap: () {
                    //       openParentCustomerScreen(Strings.billableTo);
                    //     },
                    //     readOnly: true),
                    //
                    // const SizedBox(
                    //   height: Constant.MEDIUM_PADDING,
                    // ),
                    // ListView.builder(
                    //     shrinkWrap: true,
                    //     physics: const NeverScrollableScrollPhysics(),
                    //     padding: const EdgeInsets.symmetric(
                    //         vertical: Constant.SMALL_PADDING),
                    //     itemCount: changePlanController
                    //         .selectedPlanListDetails!.length,
                    //     itemBuilder: (BuildContext context, int index) {
                    //       return changePlanList(context, index);
                    //     }),
                    // const SizedBox(
                    //   height: Constant.SMALL_PADDING,
                    // ),
                    // InputTitleRequire(
                    //     title: Strings.payment_received, require: false),
                    // const SizedBox(
                    //   height: Constant.SMALL_PADDING,
                    // ),
                    // Row(
                    //   children: [
                    //     radioButton(
                    //         SingingCharacter.yes,
                    //         changePlanController.paymentTypeSelection,
                    //         Strings.yes),
                    //     const SizedBox(
                    //       width: Constant.SMALL_PADDING,
                    //     ),
                    //     radioButtonText(Strings.yes, Strings.yes),
                    //     const SizedBox(
                    //       width: Constant.MEDIUM_PADDING,
                    //     ),
                    //     radioButton(
                    //         SingingCharacter.no,
                    //         changePlanController.paymentTypeSelection,
                    //         Strings.no),
                    //     const SizedBox(
                    //       width: Constant.SMALL_PADDING,
                    //     ),
                    //     radioButtonText(Strings.no, Strings.no),
                    //   ],
                    // ),
                    // const SizedBox(
                    //   height: Constant.SMALL_PADDING,
                    // ),
                    // changePlanController.paymentTypeSelection ==
                    //         SingingCharacter.no
                    //     ? const SizedBox(
                    //         height: Constant.MEDIUM_PADDING,
                    //       )
                    //     : const SizedBox.shrink(),
                    // changePlanController.paymentTypeSelection ==
                    //         SingingCharacter.yes
                    //     ? Row(
                    //         crossAxisAlignment: CrossAxisAlignment.center,
                    //         mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    //         children: [
                    //           Flexible(
                    //             flex: 1,
                    //             child: titleWithRequireWidget(
                    //                 Strings.amount, true),
                    //           ),
                    //           const SizedBox(
                    //             width: Constant.SMALL_PADDING,
                    //           ),
                    //           Flexible(
                    //             flex: 2,
                    //             child: CoustomTextField(
                    //                 labelText: Strings.amount,
                    //                 hintColor: AppTheme.colorIconGrey,
                    //                 textEditingController:
                    //                     changePlanController.amountController,
                    //                 maxLength: 30,
                    //                 borderEnableColors: AppTheme.colorIconGrey,
                    //                 borderFocusColors: AppTheme.colorIconGrey,
                    //                 textColor: AppTheme.colorBlack,
                    //                 keyboardType: TextInputType.text,
                    //                 fontSize: AppTheme.small,
                    //                 textInputAction: TextInputAction.next,
                    //                 fontWeight: FontWeight.w500,
                    //                 contentPadding: const EdgeInsets.symmetric(
                    //                     horizontal: Constant.MEDIUM_PADDING,
                    //                     vertical: Constant.MEDIUM_PADDING),
                    //                 borderCorner: Constant.BTN_ROUNDED_CORNER,
                    //                 onTextValidator: (String? value) {
                    //                   if (value!.isEmpty) {
                    //                     return Strings.enter_amount;
                    //                   }
                    //                   return null;
                    //                 },
                    //                 onTextFiledOnTap: () {},
                    //                 readOnly: false),
                    //           ),
                    //         ],
                    //       )
                    //     : const SizedBox.shrink(),
                    // const SizedBox(
                    //   height: Constant.MEDIUM_PADDING,
                    // ),
                    // changePlanController.paymentTypeSelection ==
                    //         SingingCharacter.yes
                    //     ? Column(
                    //         children: [
                    //           Row(
                    //             crossAxisAlignment: CrossAxisAlignment.center,
                    //             mainAxisAlignment:
                    //                 MainAxisAlignment.spaceBetween,
                    //             children: [
                    //               Flexible(
                    //                 flex: 1,
                    //                 child: titleWithRequireWidget(
                    //                     Strings.payment_date, false),
                    //               ),
                    //               const SizedBox(
                    //                 width: Constant.SMALL_PADDING,
                    //               ),
                    //               Flexible(
                    //                 flex: 2,
                    //                 child: CoustomTextField(
                    //                     labelText: Strings.payment_date,
                    //                     hintColor: AppTheme.colorIconGrey,
                    //                     textEditingController:
                    //                         changePlanController
                    //                             .paymentDateController,
                    //                     borderEnableColors:
                    //                         AppTheme.colorIconGrey,
                    //                     borderFocusColors:
                    //                         AppTheme.colorIconGrey,
                    //                     textColor: AppTheme.colorBlack,
                    //                     keyboardType: TextInputType.text,
                    //                     maxLength: 6,
                    //                     fontSize: AppTheme.small,
                    //                     textInputAction: TextInputAction.next,
                    //                     fontWeight: FontWeight.w500,
                    //                     contentPadding:
                    //                         const EdgeInsets.symmetric(
                    //                             horizontal:
                    //                                 Constant.MEDIUM_PADDING,
                    //                             vertical:
                    //                                 Constant.MEDIUM_PADDING),
                    //                     borderCorner:
                    //                         Constant.BTN_ROUNDED_CORNER,
                    //                     onTextValidator: (String? value) {
                    //                       return null;
                    //                     },
                    //                     onTextFiledOnTap: () {
                    //                       selectDate(
                    //                         context,
                    //                         Strings.payment_date,
                    //                         DateTime(DateTime.now().year,
                    //                             DateTime.now().month, 1),
                    //                         DateTime(DateTime.now().year,
                    //                             DateTime.now().month + 1, 0),
                    //                       );
                    //                     },
                    //                     readOnly: true),
                    //               ),
                    //             ],
                    //           ),
                    //           const SizedBox(
                    //             height: Constant.MEDIUM_PADDING,
                    //           ),
                    //           Row(
                    //             crossAxisAlignment: CrossAxisAlignment.center,
                    //             mainAxisAlignment:
                    //                 MainAxisAlignment.spaceBetween,
                    //             children: [
                    //               Flexible(
                    //                 flex: 1,
                    //                 child: titleWithRequireWidget(
                    //                     Strings.payment_mode, true),
                    //               ),
                    //               const SizedBox(
                    //                 width: Constant.SMALL_PADDING,
                    //               ),
                    //               Flexible(
                    //                 flex: 2,
                    //                 child: DropdownButtonHideUnderline(
                    //                   child: DropdownButtonFormField(
                    //                     icon: SvgPicture.asset(
                    //                       downArrowSvg,
                    //                       height: Constant.DROP_DOWN_ARROW_W_H,
                    //                       width: Constant.DROP_DOWN_ARROW_W_H,
                    //                       color: AppTheme.colorBlack,
                    //                       fit: BoxFit.fill,
                    //                     ),
                    //                     decoration: Utils.ddlDecoration(),
                    //                     hint: Align(
                    //                         alignment: Alignment.centerLeft,
                    //                         child: Text(Strings.payment_mode,
                    //                             style: TextStyle(
                    //                               fontSize: AppTheme.medium,
                    //                               color: AppTheme.colorIconGrey,
                    //                               fontFamily:
                    //                                   AppTheme.appFontName,
                    //                             ))),
                    //                     style: AppTheme.dropdownTextStyle,
                    //                     isExpanded: true,
                    //                     isDense: true,
                    //                     value: changePlanController
                    //                         .selectedPayMode,
                    //                     items: changePlanController.payMode!
                    //                         .map((DropdownDetail value) {
                    //                       return DropdownMenuItem<
                    //                           DropdownDetail>(
                    //                         value: value,
                    //                         child: Align(
                    //                           alignment: Alignment.centerLeft,
                    //                           child: CustomText(
                    //                             title: value.text!,
                    //                             colors: AppTheme.colorBlack,
                    //                             textAlign: TextAlign.start,
                    //                             fontSize: AppTheme.small,
                    //                             fontWeight: FontWeight.w500,
                    //                           ), //Text(value.desig!),
                    //                         ),
                    //                       );
                    //                     }).toList(),
                    //                     onChanged: (value) {
                    //                       changePlanController.selectedPayMode =
                    //                           value as DropdownDetail?;
                    //                       changePlanController.update();
                    //                     },
                    //                     validator: (value) {
                    //                       // if (value == null ||
                    //                       //     addEditCustomerController.selectedParentExperience ==
                    //                       //         null) {
                    //                       //   return Strings.please_select_parent_experience;
                    //                       // }
                    //                       return null;
                    //                     },
                    //                   ),
                    //                 ),
                    //               ),
                    //             ],
                    //           ),
                    //           const SizedBox(
                    //             height: Constant.MEDIUM_PADDING,
                    //           ),
                    //           Row(
                    //             crossAxisAlignment: CrossAxisAlignment.center,
                    //             mainAxisAlignment:
                    //                 MainAxisAlignment.spaceBetween,
                    //             children: [
                    //               Flexible(
                    //                 flex: 1,
                    //                 child: titleWithRequireWidget(
                    //                     Strings.payment_remarks, true),
                    //               ),
                    //               const SizedBox(
                    //                 width: Constant.SMALL_PADDING,
                    //               ),
                    //               Flexible(
                    //                 flex: 2,
                    //                 child: CoustomTextField(
                    //                     labelText: Strings.payment_remarks,
                    //                     hintColor: AppTheme.colorIconGrey,
                    //                     textEditingController:
                    //                         changePlanController
                    //                             .paymentRemarkController,
                    //                     maxLength: 250,
                    //                     borderEnableColors:
                    //                         AppTheme.colorIconGrey,
                    //                     borderFocusColors:
                    //                         AppTheme.colorIconGrey,
                    //                     textColor: AppTheme.colorBlack,
                    //                     keyboardType: TextInputType.text,
                    //                     fontSize: AppTheme.small,
                    //                     textInputAction: TextInputAction.done,
                    //                     fontWeight: FontWeight.w500,
                    //                     contentPadding:
                    //                         const EdgeInsets.symmetric(
                    //                             horizontal:
                    //                                 Constant.MEDIUM_PADDING,
                    //                             vertical:
                    //                                 Constant.MEDIUM_PADDING),
                    //                     borderCorner:
                    //                         Constant.BTN_ROUNDED_CORNER,
                    //                     onTextValidator: (String? value) {
                    //                       return null;
                    //                     },
                    //                     onTextFiledOnTap: () {},
                    //                     readOnly: false),
                    //               ),
                    //             ],
                    //           )
                    //         ],
                    //       )
                    //     : const SizedBox.shrink(),
                    // const SizedBox(
                    //   height: Constant.MEDIUM_PADDING,
                    // ),

                    InputTitleRequire(title: Strings.remarks, require: true),
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
                    remarksView(),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                  ],
                ),
              ),
            ),
            Row(
              children: [
                Expanded(
                  child: SimpleButton(
                    onTap: () {
                      //changePlanController.changePlans();
                      validatEForm();
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: changePlanController.selectedPlanType != null
                          ? changePlanController.selectedPlanType!.value!
                                  .equalsIgnoreCase("Changeplan")
                              ? Strings.change_plan
                              : "${changePlanController.selectedPlanType!.text} Plan"
                          : Strings.change_plan,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.change_plan, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }

  Widget radioButton(value, groupValue, type) {
    return SizedBox(
      width: 20,
      height: 20,
      child: Radio<SingingCharacter>(
        value: value,
        groupValue: groupValue,
        activeColor: AppTheme.colorPrimary,
        onChanged: (SingingCharacter? value) {
          if (type.toString().equalsIgnoreCase(Strings.yes) ||
              type.toString().equalsIgnoreCase(Strings.no)) {
            changePlanController.paymentTypeSelection = value;
            changePlanController.update();
          }
        },
      ),
    );
  }

  Widget radioButtonText(text, type) {
    return InkWell(
        child: Text(
          text,
          style: AppTheme.textStyle(
              fontSize: AppTheme.medium,
              color: Colors.black,
              fontWeight: FontWeight.normal),
        ),
        onTap: () {
          changeRadioAction(type);
        });
  }

  changeRadioAction(String type) {
    if (type.toString().equalsIgnoreCase(Strings.yes)) {
      changePlanController.paymentTypeSelection = SingingCharacter.yes;
    } else if (type.toString().equalsIgnoreCase(Strings.no)) {
      changePlanController.paymentTypeSelection = SingingCharacter.no;
    }
    changePlanController.update();
  }

  labelWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small,
      fontWeight: FontWeight.normal,
    );
  }

  valueWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small,
      fontWeight: FontWeight.w400,
    );
  }

  remarksView() {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(7.0),
        color: AppTheme.colorWhite,
      ),
      child: TextFormField(
        controller: changePlanController.remarksController,
        maxLines: 3,
        maxLength: 250,
        style: const TextStyle(fontSize: AppTheme.medium),
        decoration: InputDecoration(
          hintText: Strings.remarks,
          alignLabelWithHint: true,
          contentPadding:
              const EdgeInsets.all(Constant.TEXT_FIELD_CONTENT_PADDING),
          focusColor: Colors.transparent,
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(color: AppTheme.colorPrimary, width: 1.0),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(
              color: AppTheme.colorIconGrey,
              width: 1.0,
            ),
          ),
          border: OutlineInputBorder(
              borderRadius:
                  BorderRadius.circular(Constant.TEXT_FIELD_CONTENT_PADDING)),
          isDense: true,
          labelStyle: TextStyle(
            color: AppTheme.colorGrey,
            fontSize: AppTheme.medium,
            fontWeight: FontWeight.normal,
            height: 1,
            fontFamily: AppTheme.appFontName,
            decoration: TextDecoration.none,
          ),
          counterText: "",
        ),
        keyboardType: TextInputType.multiline,
        validator: (value) {
          if (value!.isEmpty) {
            return Strings.please_enter_remarks;
          }
          return null;
        },
      ),
    );
  }

  validatEForm() {
    if (changePlanFormKey.currentState!.validate()) {
      changePlanController.changePlans();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.add_on_start_date) {
      if (changePlanController.selectedAddonDate != null) {
        selectedDate = changePlanController.selectedAddonDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.payment_date) {
      if (changePlanController.selectedPaymentDate != null) {
        selectedDate = changePlanController.selectedPaymentDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.cheque_date) {
      if (changePlanController.selectedChequeDate != null) {
        selectedDate = changePlanController.selectedChequeDate;
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
    if (picked != null && picked != selectedDate) {
      if (identity == Strings.add_on_start_date) {
        changePlanController.selectedAddonDate = picked;
        changePlanController.addOnStartDateController.text =
            changePlanController.dateFormat.format(picked);
        changePlanController.selectedAddonApi =
            changePlanController.apiDateFormat.format(picked);
      }
      if (identity == Strings.payment_date) {
        changePlanController.selectedPaymentDate = picked;
        changePlanController.paymentDateController.text =
            changePlanController.dateFormat.format(picked);
        changePlanController.selectedPaymentDateApi =
            changePlanController.apiDateFormat.format(picked);
      }
      if (identity == Strings.cheque_date) {
        changePlanController.selectedChequeDate = picked;
        changePlanController.chequeDateController.text =
            changePlanController.dateFormat.format(picked);
        changePlanController.selectedChequeDateApi =
            changePlanController.apiDateFormat.format(picked);
      }
      changePlanController.update();
    }
  }

  @override
  void activePlanSelectionBtnAction(
      {custPlanMap.PlanMappingDetail? selectedItem}) {
    Get.back();
    // changePlanController.selectedActivePlan = selectedItem;
    changePlanController.activePlanController.text = selectedItem!.planName!;
    changePlanController.update();
  }

  @override
  void selectPlanGroupBtnAction({custPlanMap.PlanMappingDetail? selectedItem}) {
    Get.back();
    // changePlanController.selectedActivePlan = selectedItem;
    changePlanController.activePlanController.text = selectedItem!.planName!;
    changePlanController.update();
  }

  cardDataRow(String label, String value, Color? textColor) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomText(
              title: label,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small + 1,
              height: 1,
              fontWeight: FontWeight.w500),
          const SizedBox(height: Constant.VERY_SMALL_PADDING),
          CustomText(
              title: value.isNotEmpty ? value : "-",
              colors: textColor ?? AppTheme.lable_noramal,
              textAlign: TextAlign.end,
              fontSize: AppTheme.small,
              maxLines: 2,
              height: 1,
              fontWeight: FontWeight.w400)
        ],
      ),
    );
  }

  _itemList(BuildContext context, int index) {
    Color currentStatusChange = AppTheme.title_dark;
    Color billingHoldColor = AppTheme.lable_noramal;

    if (changePlanController
                .customerPlanServiceDetailList![index].custPlanStatus !=
            null &&
        changePlanController
            .customerPlanServiceDetailList![index].custPlanStatus!.isNotEmpty) {
      if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase(Strings.active)) {
        currentStatusChange = AppTheme.colorGreen;
      } else if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase(Strings.stop)) {
        currentStatusChange = AppTheme.colorRed;
      }
    }

    if (changePlanController
            .customerPlanServiceDetailList![index].isinvoicestop !=
        null) {
      if (changePlanController
              .customerPlanServiceDetailList![index].isinvoicestop ==
          true) {
        billingHoldColor = AppTheme.colorRed;
      } else {
        billingHoldColor = AppTheme.colorGreen;
      }
    }

    Color? statusColor, statusTextColor = AppTheme.colorLightBlack;
    if (changePlanController
                .customerPlanServiceDetailList![index].custPlanStatus !=
            null &&
        changePlanController
            .customerPlanServiceDetailList![index].custPlanStatus!.isNotEmpty) {
      if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase("Active")) {
        statusColor = AppTheme.statusClosedGreen;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase("Rejected")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase("Approve")) {
        statusColor = AppTheme.statusApprove;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase("InActive")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase("Pending")) {
        statusColor = AppTheme.statusPending;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController
          .customerPlanServiceDetailList![index].custPlanStatus!
          .equalsIgnoreCase("Hold")) {
        statusColor = AppTheme.colorDisableGray;
        statusTextColor = AppTheme.colorWhite;
      }
    } else {
      statusColor = AppTheme.statusClosedGreen;
    }

    return Card(
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                CustomText(
                    title: changePlanController
                            .customerPlanServiceDetailList![index].service ??
                        "--",
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small + 1,
                    height: 1,
                    fontWeight: FontWeight.w500),
                // CustomText(
                //     title: changePlanController
                //         .planServiceList![index].custPlanStatus,
                //     colors: currentStatusChange,
                //     textAlign: TextAlign.start,
                //     fontSize: AppTheme.small + 1,
                //     height: 1,
                //     fontWeight: FontWeight.w500),
                Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: Constant.VERY_SMALL_PADDING,
                  ),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING,
                        vertical: Constant.VERY_SMALL_PADDING),
                    decoration: BoxDecoration(
                      borderRadius:
                          BorderRadius.circular(Constant.LARGE_PADDING),
                      color: statusColor,
                    ),
                    child: CustomText(
                        title: (changePlanController
                                        .customerPlanServiceDetailList![index]
                                        .custPlanStatus !=
                                    null &&
                                changePlanController
                                    .customerPlanServiceDetailList![index]
                                    .custPlanStatus!
                                    .isNotEmpty)
                            ? changePlanController
                                .customerPlanServiceDetailList![index]
                                .custPlanStatus
                            : "",
                        colors: AppTheme.colorWhite,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500),
                  ),
                ),
              ],
            ),
          ),
          const Divider(),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: cardDataRow(
                    Strings.connection_no,
                    changePlanController.customerPlanServiceDetailList![index]
                            .connectionNo ??
                        "--",
                    AppTheme.lable_noramal),
                flex: 1,
              ),
              // Expanded(
              //   child: cardDataRow(
              //       Strings.nick_name,
              //       changePlanController.planServiceList![index].nickname ??
              //           "--",
              //       AppTheme.lable_noramal),
              //   flex: 1,
              // ),
              Expanded(
                child: cardDataRow(
                    Strings.validity,
                    changePlanController.customerType
                            .equalsIgnoreCase(Strings.prepaid)
                        ? "${changePlanController.customerPlanServiceDetailList![index].validity ?? "--"}"
                        : "N/A",
                    AppTheme.lable_noramal),
                flex: 1,
              ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 1,
                child: cardDataRow(
                    Strings.plan_name,
                    changePlanController
                            .customerPlanServiceDetailList![index].planName!
                            .toString()
                            .trim() ??
                        "--",
                    AppTheme.lable_noramal),
              ),
              Expanded(
                flex: 1,
                child: cardDataRow(
                    Strings.plan_group,
                    changePlanController.customerPlanServiceDetailList?[index]
                            .planGroupName ??
                        "--",
                    AppTheme.lable_noramal),
              ),
              // Expanded(
              //   flex: 1,
              //   child: cardDataRow(
              //       Strings.billing_hold,
              //       changePlanController
              //                   .planServiceList![index].isinvoicestop ==
              //               false
              //           ? "No"
              //           : "Yes",
              //       billingHoldColor),
              // ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 1,
                child: cardDataRow(
                    Strings.start_date,
                    changePlanController
                            .customerPlanServiceDetailList![index].startDate!
                            .toString()
                            .trim() ??
                        "--",
                    AppTheme.lable_noramal),
              ),
              Expanded(
                flex: 1,
                child: cardDataRow(
                    Strings.expiry_date,
                    changePlanController
                            .customerPlanServiceDetailList?[index].endDate ??
                        "--",
                    AppTheme.lable_noramal),
              ),
            ],
          ),
          // Row(
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   crossAxisAlignment: CrossAxisAlignment.start,
          //   children: [
          //     Expanded(
          //       child: cardDataRow(
          //           Strings.start_date,
          //           changePlanController.apiDateAMPMFormat.format(
          //                   DateTime.parse(changePlanController
          //                       .customerPlanServiceDetailList![index].startDate!
          //                       .toString())) ??
          //               "--",
          //           AppTheme.lable_noramal),
          //       flex: 1,
          //     ),
          //     Expanded(
          //       child: cardDataRow(
          //           Strings.service_expiry_date,
          //           changePlanController.customerType
          //                   .equalsIgnoreCase(Strings.prepaid)
          //               ? changePlanController.apiDateAMPMFormat.format(
          //                       DateTime.parse(changePlanController
          //                           .customerPlanServiceDetailList![index].endDate!
          //                           .toString())) ??
          //                   "--"
          //               : "N/A",
          //           AppTheme.lable_noramal),
          //       flex: 1,
          //     ),
          //   ],
          // ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Expanded(
              //   child: cardDataRow(
              //       Strings.billing_end_date,
              //       changePlanController.customerType
              //               .equalsIgnoreCase(Strings.prepaid)
              //           ? changePlanController.apiDateAMPMFormat.format(
              //                   DateTime.parse(changePlanController
              //                       .planServiceList![index].dbExpiryDate!
              //                       .toString())) ??
              //               "--"
              //           : "N/A",
              //       AppTheme.lable_noramal),
              //   flex: 1,
              // ),
              Expanded(
                child: cardDataRow(
                    Strings.validity,
                    changePlanController.customerType
                            .equalsIgnoreCase(Strings.prepaid)
                        ? remainingDuration(changePlanController
                                .customerPlanServiceDetailList![index]
                                .dbEndDate!) ??
                            "--"
                        : "N/A",
                    AppTheme.lable_noramal),
                flex: 1,
              ),
            ],
          ),
          // Row(
          //   mainAxisAlignment: MainAxisAlignment.end,
          //   crossAxisAlignment: CrossAxisAlignment.start,
          //   children: [
          //     changePlanController
          //                 .planServiceList![index].isPromiseToPayTaken ==
          //             false
          //         ? Padding(
          //             padding: const EdgeInsets.symmetric(
          //               horizontal: Constant.SMALL_PADDING,
          //             ),
          //             child: Container(
          //               padding: const EdgeInsets.symmetric(
          //                   horizontal: Constant.LARGE_PADDING,
          //                   vertical: Constant.VERY_SMALL_PADDING),
          //               decoration: BoxDecoration(
          //                 borderRadius:
          //                     BorderRadius.circular(Constant.LARGE_PADDING),
          //                 color: AppTheme.colorDisableGray,
          //               ),
          //               child: CustomText(
          //                   title: Strings.no,
          //                   colors: AppTheme.colorWhite,
          //                   textAlign: TextAlign.start,
          //                   fontSize: AppTheme.small,
          //                   maxLines: 1,
          //                   height: 1,
          //                   fontWeight: FontWeight.w500),
          //             ),
          //           )
          //         : Padding(
          //             padding: const EdgeInsets.symmetric(
          //               horizontal: Constant.SMALL_PADDING,
          //             ),
          //             child: InkWell(
          //               onTap: () {},
          //               child: Container(
          //                 padding: const EdgeInsets.symmetric(
          //                     horizontal: Constant.LARGE_PADDING,
          //                     vertical: Constant.VERY_SMALL_PADDING),
          //                 decoration: BoxDecoration(
          //                   borderRadius:
          //                       BorderRadius.circular(Constant.LARGE_PADDING),
          //                   color: AppTheme.colorPrimaryTheme,
          //                 ),
          //                 child: CustomText(
          //                     title: Strings.yes,
          //                     colors: AppTheme.colorWhite,
          //                     textAlign: TextAlign.start,
          //                     fontSize: AppTheme.small,
          //                     maxLines: 1,
          //                     height: 1,
          //                     fontWeight: FontWeight.w500),
          //               ),
          //             ),
          //           )
          //   ],
          // ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
        ],
      ),
    );
  }

  changePlanList(BuildContext context, int index) {
    return Card(
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Column(
        children: [
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
            padding: const EdgeInsets.all(Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: cardDataRow(
                      Strings.quota_type,
                      (changePlanController
                                  .selectedPlanListDetails![index].quotatype !=
                              null)
                          ? changePlanController
                              .selectedPlanListDetails![index].quotatype
                              .toString()
                          : "",
                      AppTheme.lable_noramal),
                  flex: 3,
                ),
                Expanded(
                  child: cardDataRow(
                      Strings.data_quota,
                      ("${changePlanController.selectedPlanListDetails![index].quota}-${changePlanController.selectedPlanListDetails![index].quotaUnit}" !=
                                  "null-null" &&
                              changePlanController
                                      .selectedPlanListDetails![index].quota !=
                                  null &&
                              changePlanController.selectedPlanListDetails![index]
                                      .quotaUnit !=
                                  null)
                          ? "${changePlanController.selectedPlanListDetails![index].quota}-${changePlanController.selectedPlanListDetails![index].quotaUnit}"
                          : "--",
                      AppTheme.lable_noramal),
                  flex: 2,
                ),
              ],
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 3,
                child: cardDataRow(
                    Strings.time_quota,
                    (changePlanController
                                .selectedPlanListDetails![index].quotatime !=
                            null)
                        ? changePlanController
                            .selectedPlanListDetails![index].quotatime
                            .toString()
                        : "-",
                    AppTheme.lable_noramal),
              ),
              Expanded(
                flex: 2,
                child: cardDataRow(
                    Strings.price_incl_tax,
                    changePlanController
                            .selectedPlanListDetails![index].taxamount
                            .toString() ??
                        "-",
                    AppTheme.lable_noramal),
              )
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 3,
                child: cardDataRow(
                    Strings.discount, "0.00", AppTheme.lable_noramal),
              ),
              Expanded(
                flex: 2,
                child: cardDataRow(
                    Strings.final_offer_price,
                    changePlanController
                            .selectedPlanListDetails![index].offerprice
                            .toString() ??
                        "-",
                    AppTheme.lable_noramal),
              )
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 3,
                child: cardDataRow(
                    Strings.start_date,
                    (changePlanController.selectedPlanListDetails![index]
                                .quotaunittime !=
                            null)
                        ? changePlanController
                            .selectedPlanListDetails![index].quotaunittime
                            .toString()
                        : "",
                    AppTheme.lable_noramal),
              ),
              Expanded(
                flex: 2,
                child: cardDataRow(
                    Strings.end_date,
                    (changePlanController.selectedPlanListDetails![index]
                                .quotaunittime !=
                            null)
                        ? changePlanController
                            .selectedPlanListDetails![index].quotaunittime
                            .toString()
                        : "",
                    AppTheme.lable_noramal),
              )
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 3,
                child: cardDataRow(
                    Strings.validity,
                    changePlanController
                            .selectedPlanListDetails![index].validity
                            .toString() ??
                        "-",
                    AppTheme.lable_noramal),
              ),
            ],
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
        ],
      ),
    );
  }

  @override
  void promisePayRemarkBtnAction(
      {String? identifier, TextEditingController? remarkController}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.save)) {
      changePlanController.getPromiseToRemarkApi(remarkController!);
    }
  }

  openParentCustomerScreen(String? type) async {
    var result = await Get.to(ParentCustomerList(),
        arguments: {Constant.CUSTOMER_TYPE: changePlanController.customerType});
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        if (type!.equalsIgnoreCase(Strings.billableTo)) {
          changePlanController.billableCustList!.add(CustomerPojo(
              id: changePlanController.customerDetail!.id,
              name:
                  '${changePlanController.customerDetail!.title} ${changePlanController.customerDetail!.custname}'));
          // changePlanController.selectedParentCustomer = data;
          // changePlanController.billableToController.text = data.name!;
          // changePlanController.billableToCustomerId = data.id!;
          changePlanController.billableToController.text = data.name!;
        }
        changePlanController.update();
      }
    }
  }

  openParentStaffScreen(String? type) async {
    var result = await Get.to(() => ParentStaffList(), arguments: {});
    if (result != null) {
      ParentStaffUserlist data = result;
      if (data != null) {
        changePlanController.custPaymentOwnerId = data.id;
        if (type!.equalsIgnoreCase(Strings.payment_owner)) {
          changePlanController.selectedPaymentOwner = data;
          changePlanController.paymentOwnerController.text = data.firstname!;
        }
        changePlanController.update();
      }
    }
  }

  titleWithRequireWidget(String title, bool require) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.normal,
        ),
        require
            ? CustomText(
                title: " *",
                colors: Colors.red,
                textAlign: TextAlign.start,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              )
            : Container(),
      ],
    );
  }
}

noDataFound() {
  return const NoDataFound();
}

bool hasNullValue(List<dynamic> items, String fieldName) {
  return items.any((element) => element == fieldName);
}

bool hasNonNullValue(
    List<dynamic> items, String fieldCheckbox, String fieldName) {
  return items.any((element) => element != fieldName);
}

String remainingDuration(String endDate) {
  final now = Moment.now();
  final endDates = DateTime.parse(endDate).toMoment();
  var remainingDays = endDates.differenceInDays(now);
  return remainingDays.toString();
}
