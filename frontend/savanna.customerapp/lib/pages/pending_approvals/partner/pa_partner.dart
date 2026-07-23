import 'package:savbill/pages/pending_approvals/model/request/partner_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/ap_partner_res.dart';
import 'package:savbill/pages/pending_approvals/partner/pa_partner_controller.dart';
import 'package:savbill/pages/pending_approvals/partner/pa_partner_item.dart';
import 'package:savbill/pages/pending_approvals/partner/pa_partner_status_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PendingApprovalPartner extends StatefulWidget {
  @override
  _PendingApprovalPartnerState createState() => _PendingApprovalPartnerState();
}

class _PendingApprovalPartnerState extends State<PendingApprovalPartner>
    implements PAPartnerStatusBtnAction {
  final paPartnerController = Get.put(PAPartnerController());

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
      child: GetBuilder<PAPartnerController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paPartnerController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.partner_details,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (paPartnerController.partnerList != null &&
                        paPartnerController.partnerList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: paPartnerController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                paPartnerController.partnerList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  paPartnerController.partnerList?.length) {
                                if (paPartnerController.isShowLoadMore) {
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
                                APPartner item =
                                    paPartnerController.partnerList![index];
                                return PendingApprovalPartnerItem(
                                  item: item,
                                  onTapApprove: () {
                                    showChangeStatusDialog(
                                        item, Strings.approve);
                                  },
                                  onTapReject: () {
                                    showChangeStatusDialog(
                                        item, Strings.reject);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  showChangeStatusDialog(APPartner detail, String from) {
    String status = "";

    if (from.equalsIgnoreCase(Strings.approve)) {
      status = Strings.approved;
    } else if (from.equalsIgnoreCase(Strings.reject)) {
      status = Strings.rejected;
    }

    PartnerApproveRejectReq request = PartnerApproveRejectReq(
        nextStaffId: "",
        partnerPaymentId: detail.id,
        flag: status,
        remark: "",
        staffId: paPartnerController.userDetail!.userId.toString());

    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return PAPartnerStatusDialog(
              paPartnerStatusBtnAction: this,
              partnerApproveRejectReq: request,
              from: from);
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.partner_payment, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  @override
  void paPartnerStatusBtnAction(
      {String? identifier, PartnerApproveRejectReq? request}) {
    Get.back();
    paPartnerController.approveRejectPartner(request!);
  }
}
