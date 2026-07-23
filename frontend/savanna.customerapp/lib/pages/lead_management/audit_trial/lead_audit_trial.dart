import 'package:savbill/pages/lead_management/audit_trial/lead_audit_trial_controller.dart';
import 'package:savbill/pages/lead_management/model/lead_audit_trial_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class LeadAuditScreen extends StatefulWidget {
  @override
  _LeadAuditScreenState createState() => _LeadAuditScreenState();
}

class _LeadAuditScreenState extends State<LeadAuditScreen> {
  final leadAuditTrialController = Get.put(LeadAuditTrialController());

  final shiftLocationFormKey = GlobalKey<FormState>();
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
    return GetBuilder<LeadAuditTrialController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: leadAuditTrialController.isLoading),
      ]);
    });
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        padding: const EdgeInsets.all(Constant.SMALL_PADDING),
        child: Container(
          color: AppTheme.colorBG,
          width: MediaQuery.of(context).size.width,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Align(
                alignment: Alignment.centerLeft,
                child: CustomText(
                    title: Strings.audit_trial_list,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: Constant.SMALL_PADDING,),
              Expanded(
                flex: 1,
                child: Padding(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: (leadAuditTrialController.leadAuditTrialList !=
                      null &&
                      leadAuditTrialController
                          .leadAuditTrialList!.isNotEmpty)
                      ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      controller: leadAuditTrialController.controller,
                      itemCount: leadAuditTrialController
                          .leadAuditTrialList!.length +
                          1,
                      itemBuilder: (context, index) {
                        if (index ==
                            leadAuditTrialController
                                .leadAuditTrialList?.length) {
                          if (leadAuditTrialController.isShowLoadMore) {
                            return Padding(
                              padding: const EdgeInsets.all(
                                  Constant.SMALL_PADDING),
                              child: Center(
                                child: SizedBox(
                                  width: Constant.SCREEN_PADDING,
                                  height: Constant.SCREEN_PADDING,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2.5,
                                    valueColor:
                                    AlwaysStoppedAnimation<Color>(
                                        AppTheme.colorProgress),
                                    backgroundColor:
                                    AppTheme.colorProgressBg,
                                  ),
                                ),
                              ),
                            );
                          } else {
                            return Container();
                          }
                        } else {
                          LeadAuditList item =
                          leadAuditTrialController
                              .leadAuditTrialList![index];
                          return leadAuditTrialItem(item: item);
                        }
                      })
                      : SizedBox(
                    child: noDataFound(),
                    height: MediaQuery.of(context).size.height * 0.7,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.audit_trial, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }


  leadAuditTrialItem({required LeadAuditList item}) {
    String actionDate = "";
    if (item.createdOn != null && item.createdOn!.isNotEmpty) {
      DateTime date =
      DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.createdOn!);
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
                        title: item.staffName != null &&
                            item.staffName!.isNotEmpty
                            ? item.staffName
                            : "",
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500)),
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
            child: basicDetailItem(
                Strings.action,
                (item.auditName != null && item.auditName!.isNotEmpty)
                    ? item.auditName
                    : "-",
                Strings.created_on,
                actionDate ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),

          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.log,
                (item.name != null && item.name!.isNotEmpty)
                    ? item.name
                    : "-",
                "",
                ""),
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
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        Expanded(
          flex: 3,
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
          flex: 2,
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