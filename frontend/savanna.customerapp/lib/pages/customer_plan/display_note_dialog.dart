import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class DisplayNoteDialog extends StatefulWidget {
  // final SubscriberTrialBtnAction subscriberTrialBtnAction;
  // final CustPlanDataList? item;
  List<CustPlanDataList>? itemList;
  final String from;

  DisplayNoteDialog(
      {Key? key,
      // required this.subscriberTrialBtnAction,
      required this.from,
      required this.itemList})
      : super(key: key);

  @override
  _DisplayNoteDialogstate createState() => _DisplayNoteDialogstate();
}

class _DisplayNoteDialogstate extends State<DisplayNoteDialog> {
  @override
  void initState() {
    super.initState();
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
    String title = "";
    if (widget.from.equalsIgnoreCase(Strings.plan)) {
      title = "${Strings.audit} ";
    }

    return Stack(children: [
      AlertDialog(
        insetPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING * 2,
        ),
        contentPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING,
        ),
        clipBehavior: Clip.antiAliasWithSaveLayer,
        backgroundColor: AppTheme.colorPrimary,
        shape: const RoundedRectangleBorder(
            borderRadius:
                BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
        content: Container(
          width: MediaQuery.of(context).size.width,
          color: AppTheme.colorWhite,
          child: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  color: AppTheme.colorAccent,
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SMALL_PADDING,
                      vertical: Constant.SMALL_PADDING),
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: CustomText(
                      title: title,
                      colors: AppTheme.title_dark,
                      fontSize: AppTheme.large,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
                Divider(
                  height: 1,
                  color: AppTheme.dividerColor,
                  thickness: 1,
                ),
                const SizedBox(height: Constant.SMALL_PADDING),
                ListView.builder(
                  shrinkWrap: true,
                  primary: false,
                  itemCount: widget.itemList!.length,
                  itemBuilder: (context, index) {
                    CustPlanDataList item = widget.itemList![index];
                    String? event, remarks;
                    if (item.extendValidityremarks != null) {
                      event = "Extend Validity";
                    } else if (item.promiseToPayRemarks != null) {
                      event = "Promise To Pay";
                    } else if (item.promiseToPayRemarks == null &&
                        item.extendValidityremarks == null) {
                      event = "-";
                    }

                    if (item.extendValidityremarks != null) {
                      remarks = item.extendValidityremarks;
                    } else if (item.promiseToPayRemarks != null) {
                      remarks = item.promiseToPayRemarks;
                    }
                    return Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Padding(
                          padding: const EdgeInsets.symmetric(
                              vertical: Constant.SMALL_PADDING + 1,
                              horizontal: Constant.MEDIUM_PADDING),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              const SizedBox(
                                height: Constant.VERY_SMALL_PADDING,
                              ),
                              basicDetailItem(Strings.event, event,
                                  Strings.connection_no, item.connectionNo!),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              basicDetailItem(
                                Strings.date,
                                item.createdate!,
                                Strings.staff,
                                item.createbyname ?? "",
                              ),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              basicDetailItem(
                                  Strings.remarks,
                                  remarks ?? "",
                                  Strings.modified_by,
                                  item.lastModifiedByName ?? ""),
                              const SizedBox(
                                height: Constant.VERY_SMALL_PADDING,
                              ),
                            ],
                          ),
                        ),
                        index == (widget.itemList!.length - 1)
                            ? Container()
                            : Padding(
                                padding: const EdgeInsets.symmetric(
                                    horizontal: Constant.SCREEN_PADDING - 5),
                                child: Divider(
                                  height: 5,
                                  color: AppTheme.dividerColor,
                                  thickness: 0.5,
                                ),
                              ),
                      ],
                    );
                  },
                ),
              ]),
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

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        Expanded(
          flex: 1,
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
          flex: 1,
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
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }
}

// abstract class SubscriberTrialBtnAction {
//   void subscriberBtnAction(
//       {String? identifier,
//         String? extendsDays,
//         CustPlanDataList? item});
// }
