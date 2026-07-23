import 'package:savbill/pages/workflow/audit_details_controller.dart';
import 'package:savbill/pages/workflow/model/cust_audit_details_res.dart';
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
  final auditDetailController = Get.put(AuditDetailController());
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
    return GetBuilder<AuditDetailController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: auditDetailController.isLoading),
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
                          "${auditDetailController.customerDetail!.title} ${auditDetailController.customerDetail!.username} ${Strings.audit_detials}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w500),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                Align(
                  alignment: Alignment.centerLeft,
                  child: CustomText(
                      title: Strings.workflow_audit,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium ,
                      fontWeight: FontWeight.w600),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                Container(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: (auditDetailController.custAuditDetailsList != null &&
                          auditDetailController.custAuditDetailsList!.isNotEmpty)
                      ? ListView.builder(
                          scrollDirection: Axis.vertical,
                          shrinkWrap: true,
                          controller: auditDetailController.controller,
                          itemCount:
                              auditDetailController.custAuditDetailsList!.length +
                                  1,
                          itemBuilder: (context, index) {
                            if (index ==
                                auditDetailController
                                    .custAuditDetailsList?.length) {
                              if (auditDetailController.isShowLoadMore) {
                                return Padding(
                                  padding: const EdgeInsets.all(
                                      Constant.SMALL_PADDING),
                                  child: Center(
                                    child: SizedBox(
                                      width: Constant.SCREEN_PADDING,
                                      height: Constant.SCREEN_PADDING,
                                      child: CircularProgressIndicator(
                                        strokeWidth: 2.5,
                                        valueColor: AlwaysStoppedAnimation<Color>(
                                            AppTheme.colorProgress),
                                        backgroundColor: AppTheme.colorProgressBg,
                                      ),
                                    ),
                                  ),
                                );
                              } else {
                                return Container();
                              }
                            } else {
                              AuditDetailList item = auditDetailController
                                  .custAuditDetailsList![index];
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
    return DynamicAppBar(Strings.audit_detials, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  auditDetailsItem({required AuditDetailList item}) {
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
              Strings.employee_name,
              (item.employeeName != null && item.employeeName!.isNotEmpty)
                  ? item.employeeName
                  : "-",
              "${Strings.audit} ${Strings.date}",
              (item.auditDate != null && item.auditDate!.isNotEmpty)
                  ? item.auditDate
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
              Strings.module,
              (item.module != null && item.module!.isNotEmpty)
                  ? item.module
                  : "-",
              Strings.operation,
              (item.operation != null && item.operation!.isNotEmpty)
                  ? item.operation
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.remarks,
              (item.remark != null && item.remark!.isNotEmpty)
                  ? item.remark
                  : "-",
              "",
              "",
            ),
          ),

          const SizedBox(
            height: Constant.MEDIUM_PADDING,
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
          flex: 2,
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
