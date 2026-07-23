import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ChangeSubscribeTrialPlanDialog extends StatefulWidget {
  final ChangeSubScribePlanBtnAction changeSubscribePlanBtnAction;
  final CustPlanDataList trialPlanData;

  TextEditingController remarksController = TextEditingController();

  // TrialPlanData? trialPlanData;
  final List<DropdownDetail>? billingStartList = [];
  DropdownDetail? selectedBillingStart;
  bool? isRemarkFlag = false;

  ChangeSubscribeTrialPlanDialog(
      {Key? key,
      required this.changeSubscribePlanBtnAction,
      required this.trialPlanData})
      : super(key: key);

  @override
  _ChangeSubscribeTrialPlanDialogState createState() =>
      _ChangeSubscribeTrialPlanDialogState();
}

class _ChangeSubscribeTrialPlanDialogState
    extends State<ChangeSubscribeTrialPlanDialog> {
  @override
  void initState() {
    super.initState();

    widget.billingStartList!.add(DropdownDetail(
        id: Strings.from_today,
        text: Strings.from_today,
        type: Strings.billing_start));
    widget.billingStartList!.add(DropdownDetail(
        id: Strings.including_trial_period,
        text: Strings.including_trial_period,
        type: Strings.billing_start));
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      ),
      elevation: 0,
      clipBehavior: Clip.antiAliasWithSaveLayer,
      insetPadding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      backgroundColor: Colors.transparent,
      child: contentBox(context),
    );
  }

  contentBox(BuildContext context) {
    return Stack(children: [
      AlertDialog(
        insetPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING * 2,
        ),
        contentPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING,
        ),
        clipBehavior: Clip.antiAliasWithSaveLayer,
        backgroundColor: AppTheme.colorWhite,
        shape: const RoundedRectangleBorder(
            borderRadius:
                BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
        content: Container(
          width: MediaQuery.of(context).size.width,
          color: AppTheme.colorWhite,
          child: SingleChildScrollView(
            child: Column(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                    child: Align(
                      alignment: Alignment.centerLeft,
                      child: CustomText(
                        title: Strings.subscriber_trial_plan,
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.large,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  const SizedBox(height: Constant.SMALL_PADDING),
                  Padding(
                      padding: const EdgeInsets.only(
                          left: Constant.SMALL_PADDING,
                          right: Constant.SMALL_PADDING),
                      child:
                          titleWithRequireWidget(Strings.billing_start, true)),
                  Padding(
                    padding: const EdgeInsets.only(
                        top: Constant.SMALL_PADDING,
                        left: Constant.SMALL_PADDING,
                        right: Constant.SMALL_PADDING),
                    child: Container(
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(7.0),
                        color: AppTheme.colorWhite,
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButtonFormField(
                          icon: SvgPicture.asset(
                            downArrowSvg,
                            height: Constant.DROP_DOWN_ARROW_W_H,
                            width: Constant.DROP_DOWN_ARROW_W_H,
                            color: AppTheme.colorGrey,
                            fit: BoxFit.fill,
                          ),
                          decoration: InputDecoration(
                              filled: true,
                              contentPadding: const EdgeInsets.fromLTRB(
                                  Constant.LARGE_PADDING,
                                  0,
                                  Constant.LARGE_PADDING,
                                  0),
                              fillColor: AppTheme.colorWhite,
                              hintText: Strings.select_date,
                              hintStyle: AppTheme.dropdownHintStyle,
                              labelStyle: AppTheme.dropdownLabelStyle,
                              errorStyle: AppTheme.dropdownErrorStyle,
                              alignLabelWithHint: true,
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.DROP_DOWN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorGrey, width: 0.8),
                              ),
                              focusColor: Colors.transparent,
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.DROP_DOWN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorBlack, width: 0.8),
                              ),
                              errorMaxLines: 2),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: false,
                          isDense: true,
                          value: widget.selectedBillingStart,
                          items: widget.billingStartList!
                              .map((DropdownDetail value) {
                            return DropdownMenuItem<DropdownDetail>(
                              value: value,
                              child: Text(value.text!),
                            );
                          }).toList(),
                          onChanged: (value) {
                            widget.selectedBillingStart =
                                value as DropdownDetail?;
                            setState(() {
                              if (widget.selectedBillingStart!.text!
                                  .equalsIgnoreCase(Strings.from_today)) {
                                widget.isRemarkFlag = true;
                              } else {
                                widget.isRemarkFlag = false;
                              }
                            });
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ),
                  widget.isRemarkFlag == true
                      ? Column(
                          children: [
                            const SizedBox(height: Constant.MEDIUM_PADDING * 1),
                            Padding(
                                padding: const EdgeInsets.only(
                                    left: Constant.SMALL_PADDING,
                                    right: Constant.SMALL_PADDING),
                                child: titleWithRequireWidget(
                                    Strings.remarks, false)),
                            Padding(
                              padding: const EdgeInsets.only(
                                  top: Constant.SMALL_PADDING,
                                  left: Constant.SMALL_PADDING,
                                  right: Constant.SMALL_PADDING),
                              child: Container(
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(7.0),
                                  color: AppTheme.colorWhite,
                                ),
                                child: TextFormField(
                                  controller: widget.remarksController,
                                  maxLines: 3,
                                  maxLength: 250,
                                  style: const TextStyle(
                                      fontSize: AppTheme.medium),
                                  decoration: InputDecoration(
                                    hintText: Strings.enter_remarks,
                                    alignLabelWithHint: true,
                                    contentPadding: const EdgeInsets.all(
                                        Constant.TEXT_FIELD_CONTENT_PADDING),
                                    focusColor: Colors.transparent,
                                    focusedBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.BTN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                          color: AppTheme.colorPrimary,
                                          width: 1.0),
                                    ),
                                    enabledBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.BTN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                        color: AppTheme.colorIconGrey,
                                        width: 1.0,
                                      ),
                                    ),
                                    border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant
                                                .TEXT_FIELD_CONTENT_PADDING)),
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
                              ),
                            ),
                          ],
                        )
                      : Container(),
                  const SizedBox(height: Constant.MEDIUM_PADDING * 2),
                  Row(
                    children: [
                      Expanded(
                        child: InkWell(
                          onTap: () {
                            if (widget.selectedBillingStart == null) {
                              Utils.showSnackbar(
                                  Strings.ERROR,
                                  Strings.please_select_billing_date,
                                  AppTheme.colorWhite,
                                  AppTheme.colorRed);
                              return;
                            }
                            widget.changeSubscribePlanBtnAction
                                .subscriberTrialBtnAction(
                                    identifier: Strings.submit,
                                    item: widget.trialPlanData,
                            remarkController: widget.remarksController.text,
                            selectBillingData: widget.selectedBillingStart);
                          },
                          child: Container(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.LARGE_PADDING),
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.colorLightGrey,
                                width: 1.0,
                              ),
                              borderRadius: const BorderRadius.only(
                                  bottomLeft: Radius.circular(6.0)),
                            ),
                            child: CustomText(
                              title: Strings.submit,
                              colors: AppTheme.colorPositive,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
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
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.LARGE_PADDING),
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.colorLightGrey,
                                width: 1.0,
                              ),
                              borderRadius: const BorderRadius.only(
                                  bottomRight: Radius.circular(6.0)),
                            ),
                            child: CustomText(
                              title: Strings.cancel,
                              colors: AppTheme.colorNagative,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ]),
          ),
        ),
      ),
      Positioned(
        child: GestureDetector(
          onTap: () {
            Get.back();
          },
          child: Align(
            alignment: Alignment.topRight,
            child: Icon(Icons.close, color: AppTheme.colorWhite),
          ),
        ),
      ),
    ]);
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

abstract class ChangeSubScribePlanBtnAction {
  void subscriberTrialBtnAction({String identifier, CustPlanDataList? item,DropdownDetail? selectBillingData,String? remarkController});
}
