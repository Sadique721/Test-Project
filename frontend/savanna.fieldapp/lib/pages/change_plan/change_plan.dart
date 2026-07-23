import 'dart:developer';

import 'package:savbill/pages/change_plan/active_plan_selection_dialog.dart';
import 'package:savbill/pages/change_plan/change_plan_controller.dart';
import 'package:savbill/pages/change_plan/customer_payment_owner_list.dart';
import 'package:savbill/pages/change_plan/remark_dialog.dart';
import 'package:savbill/pages/change_plan/request/change_plan_group_screen.dart';
import 'package:savbill/pages/change_plan/response/change_plan_date_res.dart';
import 'package:savbill/pages/change_plan/response/child_cust_change_plan_res.dart';
import 'package:savbill/pages/change_plan/response/customer_payment_owner_res.dart';
import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/change_plan/response/customer_pojo.dart';
import 'package:savbill/pages/change_plan/select_plan_group.dart';
import 'package:savbill/pages/change_plan/select_plan_group_dialog.dart';
import 'package:savbill/pages/change_plan/select_plan_group_list.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart'
    as custPlanMap;
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/enum/enum.dart';
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

import '../customer/model/response/billing_cycle_res.dart';
import '../customer/model/response/change_plan_type_res.dart';

class ChangePlan extends StatefulWidget {
  @override
  _ChangePlanState createState() => _ChangePlanState();
}

class _ChangePlanState extends State<ChangePlan>
    implements
        ActivePlanSelectionAction,
        SelectPlanAction,
        PromisePayRemarkBtnAction {
  final changePlanController = Get.put(ChangePlanController());

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
    return GetBuilder<ChangePlanController>(builder: (controller) {
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
            (changePlanController.planServiceList != null &&
                    changePlanController.planServiceList!.isNotEmpty)
                ? ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.EXTRA_LARGE_PADDING,
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
                    ///Change Plan Type
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    InputTitleRequire(
                        title: Strings.plan_type, require: false),
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
                              Strings.please_select_plan_type,
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
                          value: changePlanController.selectedPlanType,
                          items: changePlanController.planTypeDetail
                              ?.map((PlanTypeDetail value) {
                            return DropdownMenuItem<PlanTypeDetail>(
                              value: value,
                              child: Text(value.text!),
                            );
                          }).toList(),
                          onChanged: (value) {
                            changePlanController.selectedPlanType =
                                value as PlanTypeDetail?;
                            changePlanController.update();
                            changePlanController.newPlanSelection= null;
                            changePlanController.onChangePlanType(
                                changePlanController.selectedPlanType);
                          },
                          validator: (value) {
                            if (value == null ||
                                changePlanController.selectedPlanType == null ||
                                changePlanController.selectedPlan?.id == 0) {
                              return Strings.please_select_plan_type;
                            }
                            return null;
                          },
                        ),
                      ),
                    ),
                    changePlanController.selectedPlanType != null &&
                        changePlanController.selectedPlanType!.text!
                            .equalsIgnoreCase("Change Plan") ?
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
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
                        //
                        //         final index = changePlanController.custServiceData.indexWhere(
                        //               (item) => item.changeFlag == true,
                        //         );
                        //
                        //         if (index !=-1 ) {
                        //           changePlanController.changePlanSelection(
                        //               isSelectedPlan: true,
                        //               data: changePlanController
                        //                   .custServiceData[index],
                        //               index: index,
                        //               isChildPlan: false,
                        //               childIdx: -1);
                        //         }
                        //
                        //       },
                        //       validator: (value) {
                        //         return null;
                        //       },
                        //     ),
                        //   ),
                        // ),

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.billing_cycle, require: true),
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
                                  changePlanController.selectedBillingCycle?.text ?? "",
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
                              changePlanController.selectedBillingCycle,
                              items: changePlanController.billingCycleList
                                  ?.map((BillingCycleList value) {
                                return DropdownMenuItem<
                                    BillingCycleList>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                changePlanController.selectedBillingCycle =
                                value as BillingCycleList?;
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),
                        ),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                      ],

                    ) : Container() ,
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
                    // changePlanController.selectedPlanType != null &&
                    //         !changePlanController.selectedPlanType!.text!
                    //             .equalsIgnoreCase("Addon")
                    //     ? Column(
                    //         children: [
                    //           Row(
                    //             crossAxisAlignment: CrossAxisAlignment.start,
                    //             children: [
                    //               Expanded(
                    //                 flex: 1,
                    //                 child: IgnorePointer(
                    //                   ignoring: (!(changePlanController
                    //                                   .selectedPlanType !=
                    //                               null) ||
                    //                           (changePlanController
                    //                                   .custServiceData
                    //                                   .isNotEmpty &&
                    //                               changePlanController
                    //                                       .custServiceData
                    //                                       .length ==
                    //                                   1))
                    //                       ? true
                    //                       : false,
                    //                   child: DropdownButtonHideUnderline(
                    //                     child: DropdownButtonFormField(
                    //                       icon: SvgPicture.asset(
                    //                         downArrowSvg,
                    //                         height:
                    //                             Constant.DROP_DOWN_ARROW_W_H,
                    //                         width: Constant.DROP_DOWN_ARROW_W_H,
                    //                         color: AppTheme.colorBlack,
                    //                         fit: BoxFit.fill,
                    //                       ),
                    //                       decoration: Utils.ddlDecoration(),
                    //                       hint: Align(
                    //                         alignment: Alignment.centerLeft,
                    //                         child: Text(
                    //                           Strings.select_plan_category,
                    //                           style: TextStyle(
                    //                             fontSize: AppTheme.small + 1,
                    //                             color: AppTheme.colorIconGrey,
                    //                             fontFamily:
                    //                                 AppTheme.appFontName,
                    //                           ),
                    //                         ),
                    //                       ),
                    //                       style: AppTheme.dropdownTextStyle,
                    //                       isExpanded: true,
                    //                       isDense: true,
                    //                       value: changePlanController
                    //                           .selectPlanGroup,
                    //                       items: changePlanController
                    //                           .changePlanGroup
                    //                           .map((ChangePlanGroupScreen
                    //                               value) {
                    //                         return DropdownMenuItem<
                    //                             ChangePlanGroupScreen>(
                    //                           value: value,
                    //                           child: Text(value.planGroupName!),
                    //                         );
                    //                       }).toList(),
                    //                       onChanged: (value) {
                    //                         changePlanController
                    //                                 .selectPlanGroup =
                    //                             value as ChangePlanGroupScreen?;
                    //                         changePlanController
                    //                             .selectPlanCategory(
                    //                                 changePlanController
                    //                                     .selectPlanGroup,
                    //                                 -1);
                    //                         changePlanController.update();
                    //                       },
                    //                       validator: (value) {
                    //                         return null;
                    //                       },
                    //                     ),
                    //                   ),
                    //                 ),
                    //               ),
                    //               const SizedBox(
                    //                 width: Constant.SMALL_PADDING,
                    //               ),
                    //               Expanded(
                    //                 flex: 1,
                    //                 child: IgnorePointer(
                    //                   ignoring: (!(changePlanController
                    //                                   .selectedPlanType !=
                    //                               null) ||
                    //                           changePlanController
                    //                                       .selectPlanGroup !=
                    //                                   null &&
                    //                               changePlanController
                    //                                   .selectPlanGroup!
                    //                                   .planGroupValue!
                    //                                   .equalsIgnoreCase(
                    //                                       "individual") ||
                    //                           (changePlanController
                    //                                   .custServiceData
                    //                                   .isNotEmpty &&
                    //                               changePlanController
                    //                                       .custServiceData
                    //                                       .length ==
                    //                                   1))
                    //                       ? true
                    //                       : false,
                    //                   child: DropdownButtonHideUnderline(
                    //                     child: DropdownButtonFormField(
                    //                       icon: SvgPicture.asset(
                    //                         downArrowSvg,
                    //                         height:
                    //                             Constant.DROP_DOWN_ARROW_W_H,
                    //                         width: Constant.DROP_DOWN_ARROW_W_H,
                    //                         color: AppTheme.colorBlack,
                    //                         fit: BoxFit.fill,
                    //                       ),
                    //                       decoration: Utils.ddlDecoration(),
                    //                       hint: Align(
                    //                         alignment: Alignment.centerLeft,
                    //                         child: Text(
                    //                           Strings.select_new_plan_group,
                    //                           style: TextStyle(
                    //                             fontSize: AppTheme.small + 1,
                    //                             color: AppTheme.colorIconGrey,
                    //                             fontFamily:
                    //                                 AppTheme.appFontName,
                    //                           ),
                    //                         ),
                    //                       ),
                    //                       style: AppTheme.dropdownTextStyle,
                    //                       isExpanded: true,
                    //                       isDense: true,
                    //                       value: changePlanController
                    //                           .selPlanGroupFilter,
                    //                       items: changePlanController
                    //                           .planGroupFilterList
                    //                           ?.map((PlanGroupDetail value) {
                    //                         return DropdownMenuItem<
                    //                             PlanGroupDetail>(
                    //                           value: value,
                    //                           child: Text(value.planGroupName!),
                    //                         );
                    //                       }).toList(),
                    //                       onChanged: (value) {
                    //                         changePlanController
                    //                                 .selPlanGroupFilter =
                    //                             value as PlanGroupDetail?;
                    //                         changePlanController
                    //                             .selectPlanGroupType(
                    //                                 changePlanController
                    //                                     .selPlanGroupFilter,
                    //                                 -1);
                    //                         changePlanController.update();
                    //                       },
                    //                       validator: (value) {
                    //                         return null;
                    //                       },
                    //                     ),
                    //                   ),
                    //                 ),
                    //               ),
                    //             ],
                    //           ),
                    //           changePlanController.customerType
                    //                   .equalsIgnoreCase("Postpaid")
                    //               ? const SizedBox(
                    //                   height: Constant.SMALL_PADDING,
                    //                 )
                    //               : const SizedBox.shrink(),
                    //           changePlanController.customerType
                    //                   .equalsIgnoreCase("Postpaid")
                    //               ? IgnorePointer(
                    //                   ignoring: false,
                    //                   child: DropdownButtonHideUnderline(
                    //                     child: DropdownButtonFormField(
                    //                       icon: SvgPicture.asset(
                    //                         downArrowSvg,
                    //                         height:
                    //                             Constant.DROP_DOWN_ARROW_W_H,
                    //                         width: Constant.DROP_DOWN_ARROW_W_H,
                    //                         color: AppTheme.colorBlack,
                    //                         fit: BoxFit.fill,
                    //                       ),
                    //                       decoration: Utils.ddlDecoration(),
                    //                       hint: Align(
                    //                         alignment: Alignment.centerLeft,
                    //                         child: Text(
                    //                           Strings.select_date_change_plan,
                    //                           style: TextStyle(
                    //                             fontSize: AppTheme.small + 1,
                    //                             color: AppTheme.colorIconGrey,
                    //                             fontFamily:
                    //                                 AppTheme.appFontName,
                    //                           ),
                    //                         ),
                    //                       ),
                    //                       style: AppTheme.dropdownTextStyle,
                    //                       isExpanded: true,
                    //                       isDense: true,
                    //                       value: changePlanController
                    //                           .selectChangePlanDate,
                    //                       items: changePlanController
                    //                           .changePlanDateList
                    //                           ?.map((ChangePlanDateDataList
                    //                               value) {
                    //                         return DropdownMenuItem<
                    //                             ChangePlanDateDataList>(
                    //                           value: value,
                    //                           child: Text(value.text!),
                    //                         );
                    //                       }).toList(),
                    //                       onChanged: (value) {
                    //                         changePlanController
                    //                                 .selectChangePlanDate =
                    //                             value
                    //                                 as ChangePlanDateDataList?;
                    //                         changePlanController.update();
                    //                       },
                    //                       validator: (value) {
                    //                         return null;
                    //                       },
                    //                     ),
                    //                   ),
                    //                 )
                    //               : const SizedBox.shrink(),
                    //           changePlanController.selectedPlanType != null &&
                    //                   !changePlanController
                    //                       .selectedPlanType!.text!
                    //                       .equalsIgnoreCase("Renew")
                    //               ? const SizedBox(
                    //                   height: Constant.VERY_SMALL_PADDING)
                    //               : const SizedBox.shrink(),
                    //           changePlanController.selectedPlanType != null &&
                    //                   changePlanController
                    //                       .selectedPlanType!.text!
                    //                       .equalsIgnoreCase("Renew")
                    //               ? Padding(
                    //                   padding:
                    //                       const EdgeInsets.only(bottom: 16.0),
                    //                   child: Row(
                    //                     crossAxisAlignment:
                    //                         CrossAxisAlignment.start,
                    //                     children: [
                    //                       Expanded(
                    //                         flex: 3,
                    //                         child: Row(
                    //                           children: [
                    //                             changePlanController.plansForChargeByCust.isNotEmpty ?
                    //                             Checkbox(
                    //                               value: changePlanController.isAddCharge,
                    //                               activeColor:
                    //                                   AppTheme.colorPrimary,
                    //                               onChanged:changePlanController.isPlanSelected(changePlanController.customerId) == true ? null : (bool? value) {
                    //                                 changePlanController
                    //                                         .isAddCharge =
                    //                                     value!;
                    //                                 // if(changePlanController.isPlanSelected(changePlanController.customerId) == false){
                    //                                   if(changePlanController.isAddCharge == true) {
                    //                                     changePlanController.onDirectChargeChange(changePlanController
                    //                                         .isAddCharge,changePlanController.customerId);
                    //                                     // openAddDirectChargeScreen();
                    //                                   // }
                    //                                 }
                    //                                 changePlanController.update();
                    //                               },
                    //                             ) :
                    //                             Checkbox(
                    //                               value: false,
                    //                               activeColor:
                    //                               AppTheme.colorPrimary,
                    //                               onChanged: (value) {
                    //                                 changePlanController.update();
                    //                               },
                    //                             ),
                    //                             const SizedBox(
                    //                                 width: Constant
                    //                                     .VERY_SMALL_PADDING),
                    //                             CustomText(
                    //                               title:
                    //                                   Strings.add_direct_charge,
                    //                               colors:
                    //                                   AppTheme.lable_noramal,
                    //                               fontSize: AppTheme.medium,
                    //                               fontWeight: FontWeight.normal,
                    //                             ),
                    //                           ],
                    //                         ),
                    //                       ),
                    //                       SizedBox(
                    //                         width: MediaQuery.of(context)
                    //                                 .size
                    //                                 .width *
                    //                             0.2,
                    //                         child: ElevatedButton(
                    //                           onPressed: changePlanController
                    //                                   .isAddCharge
                    //                               ? () {
                    //                             changePlanController.openChargeDetails(changePlanController.customerDetail!.id,context);
                    //                           } // Replace with actual ID
                    //                               : null,
                    //                           style: ElevatedButton.styleFrom(
                    //                             padding:
                    //                                 const EdgeInsets.symmetric(
                    //                               vertical: 8.0,
                    //                               horizontal: 10.0,
                    //                             ),
                    //                             shape: RoundedRectangleBorder(
                    //                               borderRadius:
                    //                                   BorderRadius.circular(
                    //                                       5.0),
                    //                             ),
                    //                           ),
                    //                           child:
                    //                               const Icon(Icons.visibility),
                    //                         ),
                    //                       ),
                    //                     ],
                    //                   ),
                    //                 )
                    //               : const SizedBox.shrink()
                    //         ],
                    //       )
                    //     : const SizedBox.shrink(),

                    (changePlanController.custServiceData.isNotEmpty)
                        ? ListView.builder(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            padding: const EdgeInsets.symmetric(
                                horizontal: Constant.VERY_SMALL_PADDING - 5,
                                vertical: Constant.SMALL_PADDING),
                            itemCount:
                                changePlanController.custServiceData.length,
                            itemBuilder: (BuildContext context, int index) {
                              return changePlanList(context, index);
                            })
                        : SizedBox(
                            height: Constant.VERY_EXTRA_LARGE_PADDING,
                            child: noDataFound(),
                          ),

                    const SizedBox(height: Constant.MEDIUM_PADDING),
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
                    //       // if(controller.billableToController.text.isEmpty){
                    //       //   return Strings.select_bill_to;
                    //       // }
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

                    // changePlanController.paymentTypeSelection ==
                    //         SingingCharacter.no
                    //     ? const SizedBox(
                    //         height: Constant.MEDIUM_PADDING,
                    //       )
                    //     : const SizedBox.shrink(),
                    // changePlanController.paymentTypeSelection ==
                    //         SingingCharacter.no
                    //     ? InputTitleRequire(
                    //         title: Strings.payment_owner, require: true)
                    //     : const SizedBox.shrink(),
                    // changePlanController.paymentTypeSelection ==
                    //         SingingCharacter.no
                    //     ? const SizedBox(
                    //         height: Constant.VERY_SMALL_PADDING,
                    //       )
                    //     : const SizedBox.shrink(),
                    //
                    // changePlanController.paymentTypeSelection ==
                    //         SingingCharacter.no
                    //     ? CoustomTextField(
                    //         labelText: Strings.select_staff,
                    //         hintColor: AppTheme.colorIconGrey,
                    //         textEditingController:
                    //             changePlanController.paymentOwnerController,
                    //         suffixIcon: Padding(
                    //           padding: const EdgeInsetsDirectional.all(
                    //               Constant.LARGE_PADDING - 2),
                    //           child: SvgPicture.asset(
                    //             downArrowSvg,
                    //             color: AppTheme.colorBlack,
                    //             width: Constant.ICON_SIZE_S,
                    //             height: Constant.ICON_SIZE_S,
                    //           ),
                    //         ),
                    //         borderEnableColors: AppTheme.colorIconGrey,
                    //         borderFocusColors: AppTheme.colorIconGrey,
                    //         textColor: AppTheme.colorBlack,
                    //         keyboardType: TextInputType.text,
                    //         fontSize: AppTheme.small,
                    //         textInputAction: TextInputAction.done,
                    //         fontWeight: FontWeight.w500,
                    //         contentPadding: const EdgeInsets.symmetric(
                    //             horizontal: Constant.MEDIUM_PADDING,
                    //             vertical: Constant.MEDIUM_PADDING),
                    //         borderCorner: Constant.BTN_ROUNDED_CORNER,
                    //         onTextValidator: (String? value) {
                    //           if (value!.isEmpty) {
                    //             return Strings.please_select_payment_owner;
                    //           }
                    //           return null;
                    //         },
                    //         onTextFiledOnTap: () {
                    //           openCustomerPaymentOwnerScreen(
                    //               Strings.payment_owner);
                    //         },
                    //         readOnly: true)
                    //     : const SizedBox.shrink(),
                    // const SizedBox(
                    //   height: Constant.SMALL_PADDING,
                    // ),

                    InputTitleRequire(
                        title: Strings.externalRemark, require: false),
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
                    externalRemarksView(),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),

                    InputTitleRequire(title: Strings.remarks, require: true),
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
                    remarksView(),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),

                    /// Change plan screen

                    /*(changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Change Plan"))) ||
                            (changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Renew")))
                        ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                        : const SizedBox(),

                    (changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Change Plan"))) ||
                            (changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Renew")))
                        ? InputTitleRequire(
                            title: Strings.change_plan_screen, require: true)
                        : const SizedBox(),

                    (changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Change Plan"))) ||
                            (changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Renew")))
                        ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                        : const SizedBox(),

                    (changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Change Plan"))) ||
                            (changePlanController.custPlanGrpId != null &&
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Renew")))
                        ? IgnorePointer(
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
                                    Strings.plan_type,
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
                                value: changePlanController.selectPlanGroup,
                                items: changePlanController.changePlanGroup
                                    .map((ChangePlanGroupScreen value) {
                                  return DropdownMenuItem<
                                      ChangePlanGroupScreen>(
                                    value: value,
                                    child: Text(value.planGroupName!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  changePlanController.selectPlanGroup =
                                      value as ChangePlanGroupScreen;
                                  if (value.planGroupName!
                                      .equalsIgnoreCase(Strings.individual)) {
                                    // showSelectPlanGroupDialog();
                                    openSelectPlanGroupScreen(
                                        changePlanController.custPlanGrpId!,
                                        changePlanController.customerId,
                                        changePlanController.serviceAreaId!,
                                        changePlanController
                                            .selectedPlanType!.text);
                                  } else if (value.planGroupName!
                                      .equalsIgnoreCase(Strings.plan_group)) {
                                    showRemark();
                                  }

                                  changePlanController.update();
                                },
                                validator: (value) {
                                  if (value == null) {
                                    return Strings.please_select_plan_screen;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          )
                        : const SizedBox(),

                    /// Select Plan
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
                    InputTitleRequire(
                        title: (changePlanController.custPlanGrpId == null ||
                                (changePlanController.selectedPlanType !=
                                        null &&
                                    changePlanController.selectedPlanType!.text!
                                        .equalsIgnoreCase("Addon")))
                            ? Strings.select_plan
                            : Strings.plan_group,
                        require: false),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),

                    (changePlanController.custPlanGrpId == null ||
                            (changePlanController.selectedPlanType != null &&
                                changePlanController.selectedPlanType!.text!
                                    .equalsIgnoreCase("Addon")))
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
                              value: changePlanController.selectedPlan,
                              items: changePlanController.selectPlanList
                                  ?.map((PostpaidPlanDetail value) {
                                return DropdownMenuItem<PostpaidPlanDetail>(
                                  value: value,
                                  child: Text("${value.displayName!}"),
                                );
                              }).toList(),
                              onChanged: (value) {
                                changePlanController.selectedPlan =
                                    value as PostpaidPlanDetail?;
                                changePlanController.update();
                                changePlanController.resetPlanSummary();
                                changePlanController.setPlanSummary();
                                changePlanController.getPlanDateDetailReq();
                              },
                              validator: (value) {
                                if (value == null ||
                                    changePlanController.selectedPlan == null ||
                                    changePlanController.selectedPlan?.id ==
                                        0) {
                                  return Strings.please_select_plan;
                                }
                                return null;
                              },
                            ),
                          )
                        : DropdownButtonHideUnderline(
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
                                  Strings.plan_group,
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
                              value: changePlanController.selPlanGroup,
                              items: changePlanController.planGroupList
                                  ?.map((PlanGroupDetail value) {
                                return DropdownMenuItem<PlanGroupDetail>(
                                  value: value,
                                  child: Text(value.planGroupName!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                PlanGroupDetail selValue =
                                    value as PlanGroupDetail;
                                */

                    /* if (changePlanController.selPlanGroup !=
                                    selValue) {*/

                    /*
                                changePlanController.selPlanGroup = selValue;
                                changePlanController.update();
                                openPlanGroupToPlanScreen(
                                    selValue.planGroupId!);
                                //}
                              },
                              validator: (value) {
                                if (value == null ||
                                    changePlanController.selPlanGroup == null) {
                                  return Strings.please_select_plan_group;
                                }
                                return null;
                              },
                            ),
                          ),
                    */

                    /*______________________Change Plan(changePlanController.custPlanGrpId == null)_________________________*/
                    /*

                    (changePlanController.custPlanGrpId != null &&
                            (changePlanController.selectedPlanType != null &&
                                changePlanController.selectedPlanType!.text!
                                    .equalsIgnoreCase("Change Plan")))
                        ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                        : Container(),

                    (changePlanController.custPlanGrpId != null &&
                            (changePlanController.selectedPlanType != null &&
                                changePlanController.selectedPlanType!.text!
                                    .equalsIgnoreCase("Change Plan")))
                        ? InputTitleRequire(
                            title: Strings.select_active_paln, require: false)
                        : Container(),

                    (changePlanController.custPlanGrpId != null &&
                            (changePlanController.selectedPlanType != null &&
                                changePlanController.selectedPlanType!.text!
                                    .equalsIgnoreCase("Change Plan")))
                        ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                        : Container(),

                    (changePlanController.custPlanGrpId != null &&
                            (changePlanController.selectedPlanType != null &&
                                changePlanController.selectedPlanType!.text!
                                    .equalsIgnoreCase("Change Plan")))
                        ? CoustomTextField(
                            labelText: Strings.active_paln,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                changePlanController.activePlanController,
                            borderEnableColors: AppTheme.colorIconGrey,
                            borderFocusColors: AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            textInputAction: TextInputAction.next,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING,
                                vertical: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.select_plan;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              // showCustomerActivePlan();
                            },
                            readOnly: true)
                        : Container(),

                    (changePlanController.custPlanGrpId == null ||
                            (changePlanController.selectedPlanType != null &&
                                changePlanController.selectedPlanType!.text!
                                    .equalsIgnoreCase("Renew")))
                        ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                        : Container(),

                    (changePlanController.custPlanGrpId != null &&
                            (changePlanController.selectedPlanType != null &&
                                changePlanController.selectedPlanType!.text!
                                    .equalsIgnoreCase("Renew")))
                        ? Align(
                            alignment: Alignment.centerRight,
                            child: CustomText(
                              title:
                                  "${changePlanController.selectedPlanList.length} Plan Selected",
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.normal,
                            ),
                          )
                        : Container(),

                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Renew"))
                        ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                        : Container(),

                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Renew"))
                        ? Row(
                            children: [
                              SizedBox(
                                width: 15,
                                height: 10,
                                child: Checkbox(
                                  value: changePlanController.addDirectCharge,
                                  activeColor: AppTheme.colorPrimary,
                                  onChanged: (value) {
                                    changePlanController.addDirectCharge =
                                        !changePlanController.addDirectCharge;
                                    changePlanController.update();
                                  },
                                ),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              InkWell(
                                onTap: () {
                                  openAddChargeScreen();
                                },
                                child: CustomText(
                                  title: Strings.add_direct_charge,
                                  colors: AppTheme.colorPrimary,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ],
                          )
                        : Container(),

                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Renew"))
                        ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                        : Container(),

                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Renew"))
                        ? Align(
                            alignment: Alignment.centerRight,
                            child: CustomText(
                              title:
                                  "${changePlanController.chargeDataList!.length} Added Direct Charge",
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.normal,
                            ),
                          )
                        : Container(),

                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Addon"))
                        ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                        : Container(),

                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Addon"))
                        ? InputTitleRequire(
                            title: Strings.add_on_start_date, require: false)
                        : Container(),

                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Addon"))
                        ? const SizedBox(height: Constant.VERY_SMALL_PADDING)
                        : Container(),
                    (changePlanController.selectedPlanType != null &&
                            changePlanController.selectedPlanType!.text!
                                .equalsIgnoreCase("Addon"))
                        ? CoustomTextField(
                            labelText: Strings.add_on_start_date,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                changePlanController.addOnStartDateController,
                            borderEnableColors: AppTheme.colorIconGrey,
                            borderFocusColors: AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            textInputAction: TextInputAction.next,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING,
                                vertical: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {
                              return null;
                            },
                            onTextFiledOnTap: () {
                              selectDate(
                                  context,
                                  Strings.add_on_start_date,
                                  DateTime(DateTime.now().year - 10),
                                  DateTime(DateTime.now().year + 10));
                            },
                            readOnly: true)
                        : Container(),
*/
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
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
          if(value!.isEmpty){
            return  Strings.please_enter_remarks;
          }
          return null;
        },
      ),
    );
  }

  externalRemarksView() {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(7.0),
        color: AppTheme.colorWhite,
      ),
      child: TextFormField(
        controller: changePlanController.externalRemarksController,
        maxLines: 3,
        maxLength: 250,
        style: const TextStyle(fontSize: AppTheme.medium),
        decoration: InputDecoration(
          hintText: Strings.externalRemark,
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
          return null;
        },
      ),
    );
  }

  paymentRemarksView() {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(7.0),
        color: AppTheme.colorWhite,
      ),
      child: TextFormField(
        controller: changePlanController.paymentRemarkController,
        maxLines: 3,
        maxLength: 250,
        style: const TextStyle(fontSize: AppTheme.medium),
        decoration: InputDecoration(
          hintText: Strings.payment_remarks,
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
          return null;
        },
      ),
    );
  }

  validateForm() {
    if (changePlanFormKey.currentState!.validate()) {
      if (changePlanController.selPlanGroup != null &&
          (changePlanController.selectedPlanType != null &&
              changePlanController.selectedPlanType!.text!
                  .equalsIgnoreCase("Renew")) &&
          changePlanController.selectedPlanList.isEmpty) {
        Utils.showSnackbar(
            Strings.ERROR,
            "Please select the customer group plans",
            AppTheme.colorWhite,
            AppTheme.colorRed);
        return;
      }
      if (changePlanController.selectedPlanType != null &&
          changePlanController.selectedPlanType!.text!
              .equalsIgnoreCase("Change Plan")) {
        // changePlanController.deActivePlanReq();
      } else {
        changePlanController.changePlanReq();
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  validatEForm() {
    if (changePlanFormKey.currentState!.validate()) {
      changePlanController.changePlanSubmitted = true;
      bool isOnePlanSelected = true;
      bool isAnyFieldNull = false;

      bool isAddon = changePlanController.selectedPlanType!.text!
          .equalsIgnoreCase("Addon");
      // if(changePlanController.selectPlanGroup != null )
      bool? hasIndividualSelected = changePlanController
          .selectPlanGroup?.planGroupName!
          .equalsIgnoreCase("individual");
      bool? hasGroupPlanSelected = changePlanController
          .selectPlanGroup?.planGroupName!
          .equalsIgnoreCase("groupPlan");

      if (changePlanController.selectedPlanType!.text == null ||
          changePlanController.selectedPlanType!.text == "") {
        Utils.showSnackbar(Strings.ERROR, "Please select Change Plan Type",
            AppTheme.colorWhite, AppTheme.colorRed);
        return;
      }

      if (!isAddon && changePlanController.childCustList!.isEmpty) {
        if (changePlanController.selectPlanGroup!.planGroupName!
            .equalsIgnoreCase("individual")) {

          isOnePlanSelected = hasNonNullValue(
            changePlanController.custServiceData,
            "changeFlag",
            "newPlanSelection",
          );
        } else if (changePlanController.selectPlanGroup!.planGroupName!
            .equalsIgnoreCase("groupPlan")) {
          isAnyFieldNull = hasNullValue(
              changePlanController.custServiceData, "newPlanSelection");
        }
      } else if (!isAddon &&
          (changePlanController.selectPlanGroup!.planGroupName == null ||
              changePlanController.selectPlanGroup!.planGroupName == "")) {
        for (ChildCustList item in changePlanController.childCustList!) {
          if (changePlanController.selectPlanGroup!.planGroupValue!
              .equalsIgnoreCase("individual")) {
            isOnePlanSelected = hasNonNullValue(
              item.serviceMappingData!,
              "changeFlag",
              "newPlanSelection",
            );
          } else if (changePlanController.selectPlanGroup!.planGroupValue!
              .equalsIgnoreCase("groupPlan")) {
            isAnyFieldNull =
                hasNullValue(item.serviceMappingData!, "newPlanSelection");
            if (isAnyFieldNull) {
              // errorMsg();
              Utils.showSnackbar(
                  Strings.ERROR,
                  "Please select at least one new plan.",
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
              return;
            }
          }
        }
      } else if (isAddon) {
        isOnePlanSelected = hasNonNullValue(
          changePlanController.custServiceData,
          "changeFlag",
          "newPlanSelection",
        );
        if (!isOnePlanSelected) {

          for (var item in changePlanController.childCustList!) {
            if (hasNonNullValue(
                item.serviceMappingData!, "changeFlag", "newPlanSelection")) {
              isOnePlanSelected = true;
              break;
            } else {
              isOnePlanSelected = true;
              isAnyFieldNull = false;
            }
          }
        }
      } else {
        if (changePlanController.selectPlanGroup!.planGroupValue!
            .equalsIgnoreCase("individual")) {
          isOnePlanSelected = hasNonNullValue(
            changePlanController.custServiceData,
            "changeFlag",
            "newPlanSelection",
          );
          if (!isOnePlanSelected) {
            for (var item in changePlanController.childCustList!) {
              if (hasIndividualSelected!) {
                isOnePlanSelected = hasNonNullValue(
                  item.serviceMappingData!,
                  "changeFlag",
                  "newPlanSelection",
                );
                if (isOnePlanSelected) break;
              } else if (hasGroupPlanSelected!) {
                isAnyFieldNull =
                    hasNullValue(item.serviceMappingData!, "newPlanSelection");
                break;
              }
            }
          }
        } else if (changePlanController.selectPlanGroup!.planGroupValue!
            .equalsIgnoreCase("groupPlan")) {
          isAnyFieldNull = hasNullValue(
              changePlanController.custServiceData, "newPlanSelection");
          if (isAnyFieldNull) {
            for (var item in changePlanController.childCustList!) {
              if (hasIndividualSelected!) {
                isOnePlanSelected = hasNonNullValue(
                  item.serviceMappingData!,
                  "changeFlag",
                  "newPlanSelection",
                );
                break;
              } else if (hasGroupPlanSelected!) {
                isAnyFieldNull =
                    hasNullValue(item.serviceMappingData!, "newPlanSelection");
                break;
              }
            }
          }
        }
      }


      if (!isOnePlanSelected || isAnyFieldNull) {
        Utils.showSnackbar(
            Strings.ERROR,
            "Please select at least one new plan.",
            AppTheme.colorWhite,
            AppTheme.colorRed);
        return;
      }

      print("Called API :::::::::::::");
      if (changePlanController.paymentTypeSelection == SingingCharacter.no) {
        changePlanController.prepareChangePlanPayload(null,context);
      } else {
        // changePlanController.openRecordPayment();
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  openPlanGroupToPlanScreen(int planGroupId) async {
    var result = await Get.to(SelectPlanGroup(),
        arguments: {Constant.PLAN_GROUP_ID: planGroupId});

    if (result != null) {
      List<PostpaidPlanDetail> selectedList = result;
      if (selectedList.isNotEmpty) {
        changePlanController.resetPlanSummary();
        changePlanController.selectedPlanList.clear();
        changePlanController.selectedPlanList.addAll(selectedList);
        changePlanController.setPlanSummary();
      }
    }
    changePlanController.update();
  }

  openSelectPlanGroupScreen(int planGroupId, int customerId, int serviceId,
      String? selectPlanType) async {
    var result = await Get.to(() => SelectPlanGroupList(), arguments: {
      Constant.PLAN_GROUP_ID: planGroupId,
      Constant.CUSTOMER_ID: customerId,
      Constant.SERVICE_AREA_ID: serviceId,
      Constant.SELECT_PLAN_TYPE: selectPlanType,
    });

    if (result != null) {
      List<CustomerPlanServiceDetail> selectedList = result;
      if (selectedList.isNotEmpty) {
        changePlanController.resetPlanSummary();
        changePlanController.planServiceList!.clear();
        changePlanController.planServiceList!.addAll(selectedList);
        changePlanController.setPlanSummary();
      }
    }
    changePlanController.update();
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

    if (changePlanController.planServiceList![index].custPlanStatus != null &&
        changePlanController
            .planServiceList![index].custPlanStatus!.isNotEmpty) {
      if (changePlanController.planServiceList![index].custPlanStatus!
          .equalsIgnoreCase(Strings.active)) {
        currentStatusChange = AppTheme.colorGreen;
      } else if (changePlanController.planServiceList![index].custPlanStatus!
          .equalsIgnoreCase(Strings.stop)) {
        currentStatusChange = AppTheme.colorRed;
      }
    }

    if (changePlanController.planServiceList![index].isinvoicestop != null) {
      if (changePlanController.planServiceList![index].isinvoicestop == true) {
        billingHoldColor = AppTheme.colorRed;
      } else {
        billingHoldColor = AppTheme.colorGreen;
      }
    }

    Color? statusColor, statusTextColor = AppTheme.colorLightBlack;
    if (changePlanController.planServiceList![index].custPlanStatus != null &&
        changePlanController
            .planServiceList![index].custPlanStatus!.isNotEmpty) {
      if (changePlanController.planServiceList![index].custPlanStatus!
          .equalsIgnoreCase("Active")) {
        statusColor = AppTheme.statusClosedGreen;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController.planServiceList![index].custPlanStatus!
          .equalsIgnoreCase("Rejected")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController.planServiceList![index].custPlanStatus!
          .equalsIgnoreCase("Approve")) {
        statusColor = AppTheme.statusApprove;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController.planServiceList![index].custPlanStatus!
          .equalsIgnoreCase("InActive")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController.planServiceList![index].custPlanStatus!
          .equalsIgnoreCase("Pending")) {
        statusColor = AppTheme.statusPending;
        statusTextColor = AppTheme.colorWhite;
      } else if (changePlanController.planServiceList![index].custPlanStatus!
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
                    title:
                        changePlanController.planServiceList![index].service ??
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
                        title: (changePlanController.planServiceList![index]
                                        .custPlanStatus !=
                                    null &&
                                changePlanController.planServiceList![index]
                                    .custPlanStatus!.isNotEmpty)
                            ? changePlanController
                                .planServiceList![index].custPlanStatus
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
                    changePlanController.planServiceList![index].connectionNo ??
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
                        ? "${changePlanController.planServiceList![index].validity ?? "--"}"
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
                    changePlanController.planServiceList![index].planName!
                            .toString()
                            .trim() ??
                        "--",
                    AppTheme.lable_noramal),
              ),
              Expanded(
                flex: 1,
                child: cardDataRow(
                    Strings.plan_group,
                    changePlanController
                            .planServiceList?[index].planGroupName ??
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
                child: cardDataRow(
                    Strings.start_date,
                    changePlanController.apiDateAMPMFormat.format(
                            DateTime.parse(changePlanController
                                .planServiceList![index].dbStartDate!
                                .toString())) ??
                        "--",
                    AppTheme.lable_noramal),
                flex: 1,
              ),
              Expanded(
                child: cardDataRow(
                    Strings.service_expiry_date,
                    changePlanController.customerType
                            .equalsIgnoreCase(Strings.prepaid)
                        ? changePlanController.apiDateAMPMFormat.format(
                                DateTime.parse(changePlanController
                                    .planServiceList![index].dbEndDate!
                                    .toString())) ??
                            "--"
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
                child: cardDataRow(
                    Strings.billing_end_date,
                    changePlanController.customerType
                            .equalsIgnoreCase(Strings.prepaid)
                        ? changePlanController.apiDateAMPMFormat.format(
                                DateTime.parse(changePlanController
                                    .planServiceList![index].dbExpiryDate!
                                    .toString())) ??
                            "--"
                        : "N/A",
                    AppTheme.lable_noramal),
                flex: 1,
              ),
              Expanded(
                child: cardDataRow(
                    Strings.remaining_days,
                    changePlanController.customerType
                            .equalsIgnoreCase(Strings.prepaid)
                        ? remainingDuration(changePlanController
                                .planServiceList![index].dbEndDate!) ??
                            "--"
                        : "N/A",
                    AppTheme.lable_noramal),
                flex: 1,
              ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              changePlanController
                          .planServiceList![index].isPromiseToPayTaken ==
                      false
                  ? Padding(
                      padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING,
                      ),
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.LARGE_PADDING,
                            vertical: Constant.VERY_SMALL_PADDING),
                        decoration: BoxDecoration(
                          borderRadius:
                              BorderRadius.circular(Constant.LARGE_PADDING),
                          color: AppTheme.colorDisableGray,
                        ),
                        child: CustomText(
                            title: Strings.no,
                            colors: AppTheme.colorWhite,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            maxLines: 1,
                            height: 1,
                            fontWeight: FontWeight.w500),
                      ),
                    )
                  : Padding(
                      padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING,
                      ),
                      child: InkWell(
                        onTap: () {},
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: Constant.LARGE_PADDING,
                              vertical: Constant.VERY_SMALL_PADDING),
                          decoration: BoxDecoration(
                            borderRadius:
                                BorderRadius.circular(Constant.LARGE_PADDING),
                            color: AppTheme.colorPrimaryTheme,
                          ),
                          child: CustomText(
                              title: Strings.yes,
                              colors: AppTheme.colorWhite,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.small,
                              maxLines: 1,
                              height: 1,
                              fontWeight: FontWeight.w500),
                        ),
                      ),
                    )
            ],
          ),
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
          ((changePlanController.selectedPlanType != null &&
                      changePlanController.selectedPlanType!.text!
                          .equalsIgnoreCase("Addon")) ||
                  (changePlanController.selectPlanGroup != null &&
                      changePlanController.selectPlanGroup!.planGroupValue!
                          .equalsIgnoreCase("individual")))
              ? Padding(
                  padding: const EdgeInsets.symmetric(
                      vertical: Constant.SMALL_PADDING,
                      horizontal: Constant.VERY_SMALL_PADDING),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const SizedBox(
                        width: Constant.LARGE_PADDING,
                      ),
                      SizedBox(
                        width: Constant.LARGE_PADDING,
                        height: Constant.MEDIUM_PADDING,
                        child: Checkbox(
                          value: changePlanController
                              .custServiceData[index].changeFlag,
                          activeColor: AppTheme.colorPrimary,
                          onChanged: (value) {
                            changePlanController
                                .custServiceData[index].changeFlag = value;
                            if (value == true) {
                              changePlanController.changePlanSelection(
                                  isSelectedPlan: value,
                                  data: changePlanController
                                      .custServiceData[index],
                                  index: index,
                                  isChildPlan: false,
                                  childIdx: -1);
                            } else {
                              changePlanController.newPlanSelection = null;
                              changePlanController.newPlanData[
                                      changePlanController
                                          .custServiceData[index].connectionNo]
                                  .clear();
                            }
                            changePlanController.update();
                          },
                        ),
                      ),
                    ],
                  ),
                )
              : const SizedBox.shrink(),
          ((changePlanController.selectedPlanType != null &&
                      changePlanController.selectedPlanType!.text!
                          .equalsIgnoreCase("Addon")) ||
                  (changePlanController.selectPlanGroup != null &&
                      changePlanController.selectPlanGroup!.planGroupValue!
                          .equalsIgnoreCase("individual")))
              ? const Divider()
              : const SizedBox.shrink(),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: cardDataRow(
                    Strings.connection_no,
                    changePlanController.custServiceData[index].connectionNo ??
                        "--",
                    AppTheme.lable_noramal),
                flex: 3,
              ),
              Expanded(
                child: cardDataRow(
                    Strings.service_name,
                    changePlanController.custServiceData[index].service ?? "-",
                    AppTheme.lable_noramal),
                flex: 2,
              ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 3,
                child: cardDataRow(
                    Strings.current_plan,
                    changePlanController.custServiceData[index].planName ?? "-",
                    AppTheme.lable_noramal),
              ),
              changePlanController.custServiceData[index].plangroupid != null
                  ? Expanded(
                      flex: 2,
                      child: cardDataRow(
                          Strings.plan_group,
                          changePlanController
                                  .custServiceData[index].planGroupName ??
                              "-",
                          AppTheme.lable_noramal),
                    )
                  : const SizedBox.shrink(),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 3,
                child: Padding(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      CustomText(
                          title: Strings.select_new_plan,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small + 1,
                          height: 1,
                          fontWeight: FontWeight.w500),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING),

                        changePlanController.newPlanData[changePlanController
                          .custServiceData[index].connectionNo ] ==
                          null ?
                      (changePlanController.selectedPlanType != null && ! (changePlanController.selectedPlanType!.text!
                          .equalsIgnoreCase("Addon"))) &&
                          (changePlanController.selectPlanGroup != null &&  !(changePlanController.selectPlanGroup!.planGroupValue!
                              .equalsIgnoreCase("individual"))) ?
                      DropdownButtonFormField(
                        onTap: (){
                          changePlanController.filterPlanGroup(changePlanController.custServiceData[index].service, -1);
                        },
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
                            Strings.select_plan_category,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ),
                          ),
                        ),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        disabledHint: changePlanController.custServiceData[index].custServMappingStatus!.equalsIgnoreCase("Hold") ? Text("Disabled") : null,
                        isDense: true,
                        value: changePlanController.custServiceData[index].newPlanSelection,
                        items: changePlanController.planGroupData[changePlanController.customerDetail!.id.toString()]!
                            .map((PlanMappingGroupDetail list) {
                          return DropdownMenuItem<PlanMappingGroupDetail>(
                              value: list,
                              // enabled: !list.inactive!,
                              child: CustomText(
                                title: list.plan!.displayName,
                                fontSize: AppTheme.small,
                                colors: AppTheme.title_dark,
                              )
                            // child: Text(list['displayName'].toString()),
                          );
                        }).toList(),
                        onChanged: (value) {
                          changePlanController.custServiceData[index].newPlanSelection = value;
                          changePlanController.update();
                        },
                        validator: (value) {
                          if (value == null) {
                            return Strings.please_select_plan;
                          }
                          return null;
                        },
                      )
                          : const SizedBox.shrink() :  const SizedBox.shrink(),

                      changePlanController.newPlanData[changePlanController
                                  .custServiceData[index].connectionNo] !=
                              null
                          ? DropdownButtonFormField(
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
                                  Strings.select_plan_category,
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
                              value: changePlanController.newPlanSelection,
                              items: changePlanController.newPlanData[
                                      changePlanController
                                          .custServiceData[index].connectionNo]
                                  .map<DropdownMenuItem<int>>((list) {
                                return DropdownMenuItem<int>(
                                    value: list['id'],
                                    child: CustomText(
                                      title: list['label'],
                                      fontSize: AppTheme.small,
                                      colors: AppTheme.title_dark,
                                    )
                                    // child: Text(list['displayName'].toString()),
                                    );
                              }).toList(),
                              onChanged: (value) {
                                changePlanController.custServiceData[index]
                                    .newPlanSelection = value;
                                changePlanController.newPlanSelection = value;

                                changePlanController.selectNewPlan(changePlanController.newPlanSelection,changePlanController
                                    .custServiceData[index]);
                                changePlanController.update();
                              },
                              validator: (value) {
                                if (value == null) {
                                  return Strings.please_select_plan;
                                }
                                return null;
                              },
                            )
                          : const SizedBox.shrink(),
                    ],
                  ),
                ),
              ),
              Flexible(
                flex: 1,
                child: Padding(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const SizedBox(
                        height: Constant.LARGE_PADDING,
                      ),
                      InkWell(
                        onTap: () {
                          changePlanController.modalOpenDetails(
                              changePlanController.newPlanSelection,
                              changePlanController
                                  .custServiceData[index].connectionNo!,
                              changePlanController.customerId,
                              changePlanController.selectPlanGroup != null
                                  ? changePlanController
                                      .selectPlanGroup!.planGroupValue!
                                  : "",
                              context);
                        },
                        child: Container(
                          width: Constant.BTN_HEIGHT_M,
                          height: Constant.BTN_HEIGHT_M,
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(
                                  Constant.BTN_ROUNDED_CORNER),
                              color:
                                  changePlanController.newPlanSelection != null
                                      ? AppTheme.colorPrimary
                                      : AppTheme.buttonDisableColor),
                          padding: const EdgeInsets.symmetric(
                              vertical: Constant.VERY_SMALL_PADDING,
                              horizontal: Constant.VERY_SMALL_PADDING),
                          child: Icon(Icons.visibility,
                              color: AppTheme.colorWhite),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ],
      ),
    );
  }

  showRemark() {
    if (changePlanController.planServiceList != null &&
        changePlanController.planServiceList!.isNotEmpty) {
      showDialog(
          context: context,
          barrierDismissible: true,
          builder: (BuildContext context) {
            return RemarkDialog(promisePayRemarkBtnAction: this);
          });
    } else {}
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

  openCustomerPaymentOwnerScreen(String? type) async {
    var result = await Get.to(() => CustomerPaymentOwnerList(),
        arguments: {Constant.CUSTOMER_TYPE: changePlanController.customerType});
    if (result != null) {
      StaffUserlist data = result;
      if (data != null) {
        if (type!.equalsIgnoreCase(Strings.payment_owner)) {
          changePlanController.custPaymentOwnerId = data.id!;
          changePlanController.paymentOwnerController.text = data.displayName!;
        }
        changePlanController.update();
      }
    }
  }
}

noDataFound() {
  return const NoDataFound();
}

bool hasNullValue(List<dynamic> items, String fieldName) {
  return items.any((element) => element == fieldName);
}


bool hasNonNullValue(List<dynamic> items, String fieldCheckbox, String fieldName) {
    return items.any((element) => element != fieldName);
}

String remainingDuration(String endDate) {
  final now = Moment.now();
  final endDates = DateTime.parse(endDate).toMoment();
  var remainingDays = endDates.differenceInDays(now);
  return remainingDays.toString();
}
