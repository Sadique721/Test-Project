import 'package:savbill/pages/dashboard/model/response/workflow_audit_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class WorkflowAuditItem extends StatelessWidget {
  WorkflowAuditDetail item;
  final Function()? onTapStaffDetail;

  WorkflowAuditItem({
    Key? key,
    required this.item,
    required this.onTapStaffDetail,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String actionDate = "";
    if (item.actionDateTime != null && item.actionDateTime!.isNotEmpty) {
      DateTime date =
          DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.actionDateTime!);
      actionDate =
          DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
              .format(date);
    }
    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                    child: CustomText(
                        title: item.entityName != null &&
                                item.entityName!.isNotEmpty
                            ? item.entityName
                            : "",
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500)),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    item.action != null && item.action!.isNotEmpty
                        ? CustomText(
                            title: item.action,
                            colors: AppTheme.colorPrimary,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            maxLines: 1,
                            height: 1,
                            fontWeight: FontWeight.w500)
                        : Container(),
                    item.action != null && item.action!.isNotEmpty
                        ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                        : Container(),
                    actionDate.isNotEmpty
                        ? CustomText(
                            title: actionDate,
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.verySmall,
                            maxLines: 1,
                            height: 1,
                            fontWeight: FontWeight.w500)
                        : Container(),
                  ],
                )
              ],
            ),
          ),
          Divider(
            color: AppTheme.title_dark,
            thickness: 0.5,
            height: Constant.MEDIUM_PADDING,
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  // flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.staff_name),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      InkWell(
                        onTap: onTapStaffDetail,
                        child: CustomText(
                          title: (item.actionByName != null &&
                                  item.actionByName!.isNotEmpty)
                              ? item.actionByName
                              : "-",
                          colors: AppTheme.colorPrimary,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.normal,
                          maxLines: 2,
                        ),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  // flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.remarks),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(
                          (item.remark != null && item.remark!.isNotEmpty)
                              ? item.remark
                              : "-"),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
          // flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          // flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
