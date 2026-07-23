import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class PlanDetailsDialog extends StatefulWidget {
  dynamic planDetails;
  final double? planDiscount;
  final bool? displayPlanDetails;

  PlanDetailsDialog({
    required this.planDetails,
    required this.planDiscount,
    required this.displayPlanDetails,
  });

  @override
  State<PlanDetailsDialog> createState() => _PlanDetailsDialogState();
}

class _PlanDetailsDialogState extends State<PlanDetailsDialog> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    DateFormat dateFormat =
        DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
    return contentBox(context);
  }

  contentBox(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: widget.displayPlanDetails!
          ? Stack(
              children: [
                AlertDialog(
                  insetPadding: const EdgeInsets.only(
                    top: Constant.SCREEN_PADDING * 2,
                  ),
                  contentPadding: const EdgeInsets.all(
                   0
                  ),
                  clipBehavior: Clip.antiAliasWithSaveLayer,
                  backgroundColor: AppTheme.colorPrimary,
                  shape: const RoundedRectangleBorder(
                      borderRadius: BorderRadius.all(
                          Radius.circular(Constant.SMALL_PADDING))),
                  content: Container(
                    width: MediaQuery.of(context).size.width,
                    color: AppTheme.colorWhite,
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Container(
                          color: AppTheme.colorPrimary,
                          padding: const EdgeInsets.symmetric(
                              vertical: Constant.MEDIUM_PADDING,
                              horizontal: Constant.SCREEN_PADDING),
                          child: Align(
                            alignment: Alignment.centerLeft,
                            child: CustomText(
                              title: Strings.plan_detail,
                              colors: AppTheme.title_dark,
                              fontSize: AppTheme.large,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        if (widget.planDetails != null) ...[
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              Flexible(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "Quota Type :",
                                  value: widget.planDetails['quotatype'],
                                ),
                              ),
                              Flexible(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "Data Quota :",
                                  value: widget.planDetails['quota'] != null
                                      ? '${widget.planDetails['quota']}-${widget.planDetails['quotaUnit']}'
                                      : '-',
                                ),
                              ),
                            ],
                          ),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              Flexible(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "Price (incl. Tax):",
                                  value: widget.planDetails['category'] ==
                                          'Business Promotion'
                                      ? widget.planDetails['newOfferprice']
                                          .toStringAsFixed(2)
                                      : widget.planDetails['offerprice']
                                          .toStringAsFixed(2),
                                ),
                              ),
                              Flexible(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "Discount (%) :",
                                  value:
                                      widget.planDiscount!.toStringAsFixed(2),
                                ),
                              ),
                            ],
                          ),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              Flexible(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "Time Quota :",
                                  value: widget.planDetails['quotatime'] != null
                                      ? '${widget.planDetails['quotatime']}-${widget.planDetails['quotaunittime']}'
                                      : '-',

                                ),
                              ),
                              Flexible(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "Validity :",
                                  value: widget.planDetails['validity'] != null ? "${widget.planDetails['validity']} ${widget.planDetails['unitsOfValidity']}" : "-",
                                ),
                              ),
                            ],
                          ),
                          Row(
                            children: [
                              Expanded(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "Start Date :",
                                  value: formatDate(DateTime.parse(
                                    widget.planDetails['startDate'],
                                  )),
                                ),
                              ),
                              Expanded(
                                flex: 1,
                                child: _buildDataGroup(
                                  label: "End Date :",
                                  value: formatDate(DateTime.parse(
                                      widget.planDetails['endDate'])),
                                ),
                              ),
                            ],
                          ),
                        ],
                        const SizedBox(height: Constant.SMALL_PADDING),
                        Align(
                          alignment: Alignment.center,
                          child: InkWell(
                            onTap: (){
                              Get.back();
                              },
                            child: Container(
                              alignment: Alignment.center,
                              width: Constant.REMARKS_VIEW_HEIGHT,
                              height:  Constant.APPBAR_ITEM_H - 5,
                              padding: const EdgeInsets.only(
                                  top: Constant.VERY_SMALL_PADDING+2,
                                  bottom: Constant.VERY_SMALL_PADDING+2),
                              decoration:  BoxDecoration(
                                color: AppTheme.colorRed,
                                borderRadius: const BorderRadius.all(
                                    Radius.circular(
                                        Constant.MEDIUM_PADDING)),
                              ),
                              child: CustomText(
                                title: Strings.close,
                                fontSize: AppTheme.small,
                                fontWeight: FontWeight.bold,
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(height: Constant.SMALL_PADDING),
                      ],
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
              ],
            )
          : const SizedBox.shrink(),
    );
  }

  Widget _buildDataGroup({required String label, required String value}) {
    return Padding(
      padding: const EdgeInsets.symmetric(
          vertical: Constant.VERY_SMALL_PADDING,
          horizontal: Constant.VERY_SMALL_PADDING+2),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Flexible(
            flex: 1,
            child: CustomText(
              title: label,
              fontWeight: FontWeight.w300,
              fontSize: AppTheme.small-1,
              colors: AppTheme.lable_noramal,
            ),
          ),
          const SizedBox(width: Constant.VERY_SMALL_PADDING),
          Flexible(
              flex: 1,
              child: CustomText(
                title: value,
                colors: AppTheme.lable_noramal,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w400,
              )),
        ],
      ),
    );
  }

  String formatDate(DateTime date) {
    return date != null
        ? '${date.year}-${date.month}-${date.day} ${date.hour}:${date.minute}:${date.second}'
        : '-';
  }
}
