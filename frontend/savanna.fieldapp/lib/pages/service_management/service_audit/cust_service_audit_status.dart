import 'package:savbill/pages/service_management/service_audit/cust_service_audit_status_controller.dart';
import 'package:savbill/pages/service_management/service_audit/response/cust_service_audit_status_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerAuditDetail extends StatefulWidget {
  @override
  _CustomerAuditDetailState createState() => _CustomerAuditDetailState();
}

class _CustomerAuditDetailState extends State<CustomerAuditDetail> {
  final auditServiceController =
      Get.put(CustomerAuditServiceStatusController());
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
    return GetBuilder<CustomerAuditServiceStatusController>(
        builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: auditServiceController.isLoading),
      ]);
    });
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SingleChildScrollView(
        child: Container(
          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
          child: Container(
            color: AppTheme.colorBG,
            width: MediaQuery.of(context).size.width,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(
                  height: Constant.SMALL_PADDING,
                ),
                Align(
                  alignment: Alignment.centerLeft,
                  child: CustomText(
                      title:
                          "${auditServiceController.customerDetail!.title} ${auditServiceController.customerDetail!.username} ${Strings.audit_detials}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w500),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                /*Align(
                  alignment: Alignment.centerLeft,
                  child: CustomText(
                      title: Strings.workflow_audit,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w600),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),*/
                Container(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: (auditServiceController.serviceAuditStatusContentList !=
                              null &&
                          auditServiceController
                              .serviceAuditStatusContentList!.isNotEmpty)
                      ? ListView.builder(
                          scrollDirection: Axis.vertical,
                          shrinkWrap: true,
                          controller: auditServiceController.controller,
                          itemCount: auditServiceController
                                  .serviceAuditStatusContentList!.length +
                              1,
                          itemBuilder: (context, index) {
                            if (index ==
                                auditServiceController
                                    .serviceAuditStatusContentList?.length) {
                              if (auditServiceController.isShowLoadMore) {
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
                              ServiceAuditStatusContent item =
                                  auditServiceController
                                      .serviceAuditStatusContentList![index];
                              return auditDetailsItem(item: item);
                            }
                          })
                      : noDataFound(),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        "${Strings.customer} ${Strings.audit_detials}",
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  auditDetailsItem({required ServiceAuditStatusContent item}) {
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
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.action_date,
              (item.serviceStopTime != null && item.serviceStopTime!.isNotEmpty)
                  ? item.serviceStopTime
                  : "-",
              Strings.action,
              (item.action != null && item.action!.isNotEmpty)
                  ? item.action
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.staff_name,
              (item.staffName != null && item.staffName!.isNotEmpty)
                  ? item.staffName
                  : "-",
              Strings.remarks,
              (item.remarks != null && item.remarks!.isNotEmpty)
                  ? item.remarks
                  : "-",
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
