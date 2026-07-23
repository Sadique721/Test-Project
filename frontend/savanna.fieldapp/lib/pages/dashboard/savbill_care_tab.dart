import 'package:savbill/pages/dashboard/add_followup_dialog.dart';
import 'package:savbill/pages/dashboard/savbill_caretab_controller.dart';
import 'package:savbill/pages/dashboard/case_assign.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/case_type_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/dashboard/ticket_detail.dart';
import 'package:savbill/pages/dashboard/ticket_view_list_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/list_loader.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class SavbillCareTab extends StatefulWidget {
  SavbillCareTab({Key? key}) : super(key: key);

  @override
  _SavbillCareTabState createState() => _SavbillCareTabState();
}

class _SavbillCareTabState extends State<SavbillCareTab>
    implements AddFollowUpBtnAction {
  final savbillCareTabController = Get.put(SavbillCareTabController());

  @override
  void initState() {
    super.initState();
    savbillCareTabController.page = 1;
    savbillCareTabController.update();
    savbillCareTabController.initPlatformState();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<SavbillCareTabController>(builder: (controller) {
      return Stack(children: <Widget>[
        savbillCareTabController.isLoading
            ? Padding(
                padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                child: ListView.separated(
                  itemCount: 5,
                  itemBuilder: (context, index) => const ListLoader(),
                  separatorBuilder: (context, index) =>
                      const SizedBox(height: Constant.SCREEN_PADDING),
                ),
              )
            : Container(
                width: MediaQuery.of(context).size.width,
                color: AppTheme.colorBG,
                /*   decoration: const BoxDecoration(
              image: DecorationImage(
                  fit: BoxFit.cover,
                  image: AssetImage(
                    dashboardBgYellow,
                  ))),*/
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Container(
                        padding: const EdgeInsets.only(
                            top: Constant.SCREEN_PADDING,
                            left: Constant.SCREEN_PADDING,
                            right: Constant.SCREEN_PADDING),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            CustomText(
                                title: Strings.ticket_summary,
                                colors: AppTheme.colorBlack,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium + 1,
                                fontWeight: FontWeight.w500),
                            Row(
                              children: [
                                InkWell(
                                  onTap: () {
                                    //_settingModalBottomSheet(context);
                                    if (savbillCareTabController.filterViewOpen) {
                                      savbillCareTabController.filterViewOpen =
                                          false;
                                    } else {
                                      savbillCareTabController.filterViewOpen =
                                          true;
                                    }
                                    savbillCareTabController.update();
                                  },
                                  child: Container(
                                      height: 38,
                                      margin:
                                          const EdgeInsets.only(right: 0), //12
                                      child: Icon(
                                        Icons.filter_alt_rounded,
                                        color:
                                            savbillCareTabController.isFilterApply
                                                ? AppTheme.colorPrimary
                                                : AppTheme.colorBlack,
                                        size: 32,
                                      )),
                                ),
                                /*Material(
                                  color: AppTheme.colorWhite,
                                  elevation: 2,
                                  shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(20)),
                                  child: InkWell(
                                    onTap: () {
                                      openCreateTicketScreen();
                                    },
                                    child: Container(
                                      decoration: BoxDecoration(
                                        color: AppTheme.colorPrimary,
                                        borderRadius: const BorderRadius.all(
                                            Radius.circular(20)),
                                      ),
                                      padding: const EdgeInsets.all(6),
                                      child: Icon(
                                        Icons.add,
                                        color: AppTheme.colorWhite,
                                        size: 22,
                                      ),
                                    ),
                                  ),
                                ),*/
                              ],
                            )
                          ],
                        ),
                      ),
                      savbillCareTabController.filterViewOpen
                          ? const SizedBox(
                              height: Constant.SMALL_PADDING,
                            )
                          : Container(),
                      savbillCareTabController.filterViewOpen
                          ? Container(
                              width: MediaQuery.of(context).size.width,
                              margin: const EdgeInsets.symmetric(
                                  horizontal: Constant.SCREEN_PADDING),
                              child: Material(
                                color: AppTheme.colorWhite,
                                elevation: 1.5,
                                shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.BTN_ROUNDED_CORNER - 2)),
                                child: Padding(
                                  padding: const EdgeInsets.all(
                                      Constant.SMALL_PADDING),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: InputDecoration(
                                            filled: true,
                                            contentPadding:
                                                const EdgeInsets.fromLTRB(
                                                    Constant.MEDIUM_PADDING,
                                                    0,
                                                    Constant.MEDIUM_PADDING,
                                                    0),
                                            fillColor: Colors.transparent,
                                            labelText: Strings.case_type,
                                            labelStyle: TextStyle(
                                              color: AppTheme.colorBlack,
                                              fontSize: AppTheme.medium,
                                              fontWeight: FontWeight.normal,
                                              height: 1,
                                              fontFamily: AppTheme.appFontName,
                                              decoration: TextDecoration.none,
                                            ),
                                            border: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 1.0),
                                            ),
                                            focusColor: Colors.transparent,
                                            focusedBorder: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 1.0),
                                            ),
                                          ),
                                          isExpanded: false,
                                          isDense: true,
                                          value: savbillCareTabController
                                              .selectedCaseType,
                                          items: savbillCareTabController
                                              .caseTypeList
                                              ?.map((CaseTypeDetail value) {
                                            return DropdownMenuItem<
                                                CaseTypeDetail>(
                                              value: value,
                                              child: Text(value.text!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            savbillCareTabController
                                                    .selectedCaseType =
                                                value as CaseTypeDetail?;
                                            savbillCareTabController.update();
                                          },
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: InputDecoration(
                                            filled: true,
                                            contentPadding:
                                                const EdgeInsets.fromLTRB(
                                                    Constant.MEDIUM_PADDING,
                                                    0,
                                                    Constant.MEDIUM_PADDING,
                                                    0),
                                            fillColor: Colors.transparent,
                                            labelText: Strings.case_status,
                                            labelStyle: TextStyle(
                                              color: AppTheme.colorBlack,
                                              fontSize: AppTheme.medium,
                                              fontWeight: FontWeight.normal,
                                              height: 1,
                                              fontFamily: AppTheme.appFontName,
                                              decoration: TextDecoration.none,
                                            ),
                                            border: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 1.0),
                                            ),
                                            focusColor: Colors.transparent,
                                            focusedBorder: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 1.0),
                                            ),
                                          ),
                                          isExpanded: false,
                                          isDense: true,
                                          value: savbillCareTabController
                                              .selectedCaseStatus,
                                          items: savbillCareTabController
                                              .caseStatusList
                                              ?.map((CaseStatusDetail value) {
                                            return DropdownMenuItem<
                                                CaseStatusDetail>(
                                              value: value,
                                              child: Text(value.text!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            savbillCareTabController
                                                    .selectedCaseStatus =
                                                value as CaseStatusDetail?;
                                            savbillCareTabController.update();
                                          },
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.center,
                                        children: [
                                          Expanded(
                                            child: SimpleButton(
                                              onTap: () {
                                                savbillCareTabController
                                                    .applyFilter();
                                              },
                                              radius: Constant.BTN_HEIGHT_M,
                                              height: Constant.BTN_HEIGHT_M,
                                              bgColors: AppTheme.colorPrimary,
                                              child: CustomText(
                                                title: Strings.apply,
                                                fontSize: AppTheme.medium,
                                                fontWeight: FontWeight.w500,
                                              ),
                                            ),
                                          ),
                                          const SizedBox(
                                            width: Constant.LARGE_PADDING,
                                          ),
                                          Expanded(
                                            child: SimpleButton(
                                              onTap: () {
                                                savbillCareTabController
                                                    .clearFilter();
                                                /*adoptCareTabController
                                                    .getPaymentListData();*/
                                              },
                                              radius: Constant.BTN_HEIGHT_M,
                                              height: Constant.BTN_HEIGHT_M,
                                              bgColors: AppTheme.colorBlack,
                                              borderColors: AppTheme.colorBlack,
                                              child: CustomText(
                                                title: Strings.clear,
                                                fontSize: AppTheme.medium,
                                                fontWeight: FontWeight.w500,
                                              ),
                                            ),
                                          ),
                                        ],
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            )
                          : Container(),
                      savbillCareTabController.filterViewOpen
                          ? const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            )
                          : const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                      Expanded(
                        flex: 1,
                        child: (savbillCareTabController.viewItems != null &&
                                savbillCareTabController.viewItems!.isNotEmpty)
                            ? ListView.builder(
                                controller: savbillCareTabController.controller,
                                scrollDirection: Axis.vertical,
                                itemCount:
                                    savbillCareTabController.viewItems!.length +
                                        1,
                                itemBuilder: (context, index) {
                                  if (index ==
                                      savbillCareTabController
                                          .viewItems?.length) {
                                    if (savbillCareTabController.isShowLoadMore) {
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
                                    return InkWell(
                                      onTap: () async {
                                        openTicketDetailScreen(
                                            savbillCareTabController
                                                .viewItems![index].caseId);
                                        /* ticketDetailDialog(
                                            context,
                                            adoptCareTabController
                                                .viewItems![index]);*/
                                      },
                                      child: TicketListViewItem(
                                        item: savbillCareTabController
                                            .viewItems![index],
                                        showBtn: true,
                                        // userDetail:
                                        //     savbillCareTabController.userDetail,
                                        onFollowupTap: () {
                                          showFollowUpPopup(index);
                                        },
                                        onAssignTap: () {
                                          openCaseAssignScreen(
                                              savbillCareTabController
                                                  .viewItems![index]);
                                          // showRattingPopup(index);
                                        },
                                      ),
                                    );
                                  }
                                })
                            : noDataFound(),
                      ),
                    ]),
              ),
        ProgressBar(isLoader: savbillCareTabController.isLoadingProgress)
      ]);
    });
  }

  openCaseAssignScreen(TicketDetail ticketDetail) async {
    bool chkRefresh = await Get.to(CaseAssign(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });

    if (chkRefresh) {
      savbillCareTabController.page = 1;
      savbillCareTabController.update();
      savbillCareTabController.getTicketListData();
    }
  }

  openTicketDetailScreen(int? ticketId) async {
    Get.to(TicketDetailScreen(), arguments: {
      Constant.TICKET_ID: ticketId,
    });
  }

  showFollowUpPopup(int index) {
    String title =
        "Ticket Comment (${savbillCareTabController.viewItems![index].caseNumber})";
    savbillCareTabController.remarksController.clear();
    savbillCareTabController.update();
    //caseRateDialogContent(context, caseId!);
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return AddFollowUpDialog(
              addFollowUpBtnAction: this,
              caseDetail: savbillCareTabController.viewItems![index],
              title: title);
        });
  }

  openCreateTicketScreen() async {
    /*  bool chkRefresh = await Get.to(CreateTicket());
    if (chkRefresh) {
      adoptCareTabController.getTicketListData();
    }*/
  }

  void _settingModalBottomSheet(BuildContext context) {
    showModalBottomSheet(
        isScrollControlled: true,
        context: context,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.only(
              topLeft: Radius.circular(Constant.ROUNDED_CORNER),
              topRight: Radius.circular(Constant.ROUNDED_CORNER)),
        ),
        backgroundColor: Colors.white,
        builder: (BuildContext context) {
          return StatefulBuilder(builder: (BuildContext context,
              StateSetter setModalState /*You can rename this!*/) {
            return Wrap(children: <Widget>[
              Container(
                padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                      Text(
                        Strings.advance_filter,
                        style: AppTheme.textStyle(
                            fontSize: AppTheme.extraLarge,
                            color: AppTheme.colorPrimary,
                            fontWeight: FontWeight.w500),
                      ),
                    ]),
                    const SizedBox(
                      height: Constant.LARGE_PADDING,
                    ),
                    DropdownButtonHideUnderline(
                      child: DropdownButtonFormField(
                        icon: SvgPicture.asset(
                          downArrowSvg,
                          height: Constant.DROP_DOWN_ARROW_W_H,
                          width: Constant.DROP_DOWN_ARROW_W_H,
                          color: AppTheme.colorBlack,
                          fit: BoxFit.fill,
                        ),
                        decoration: InputDecoration(
                          filled: true,
                          contentPadding: const EdgeInsets.fromLTRB(
                              Constant.MEDIUM_PADDING,
                              0,
                              Constant.MEDIUM_PADDING,
                              0),
                          fillColor: Colors.transparent,
                          labelText: Strings.case_type,
                          labelStyle: TextStyle(
                            color: AppTheme.colorBlack,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.normal,
                            height: 1,
                            fontFamily: AppTheme.appFontName,
                            decoration: TextDecoration.none,
                          ),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.DROP_DOWN_ROUNDED_CORNER),
                            borderSide: BorderSide(
                                color: AppTheme.colorBlack, width: 1.0),
                          ),
                          focusColor: Colors.transparent,
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.DROP_DOWN_ROUNDED_CORNER),
                            borderSide: BorderSide(
                                color: AppTheme.colorBlack, width: 1.0),
                          ),
                        ),
                        isExpanded: false,
                        isDense: true,
                        value: savbillCareTabController.selectedCaseType,
                        items: savbillCareTabController.caseTypeList
                            ?.map((CaseTypeDetail value) {
                          return DropdownMenuItem<CaseTypeDetail>(
                            value: value,
                            child: Text(value.text!),
                          );
                        }).toList(),
                        onChanged: (value) {
                          setModalState(() {
                            savbillCareTabController.selectedCaseType =
                                value as CaseTypeDetail?;
                          });
                        },
                      ),
                    ),
                    const SizedBox(
                      height: Constant.LARGE_PADDING,
                    ),
                    DropdownButtonHideUnderline(
                      child: DropdownButtonFormField(
                        icon: SvgPicture.asset(
                          downArrowSvg,
                          height: Constant.DROP_DOWN_ARROW_W_H,
                          width: Constant.DROP_DOWN_ARROW_W_H,
                          color: AppTheme.colorBlack,
                          fit: BoxFit.fill,
                        ),
                        decoration: InputDecoration(
                          filled: true,
                          contentPadding: const EdgeInsets.fromLTRB(
                              Constant.MEDIUM_PADDING,
                              0,
                              Constant.MEDIUM_PADDING,
                              0),
                          fillColor: Colors.transparent,
                          labelText: Strings.case_status,
                          labelStyle: TextStyle(
                            color: AppTheme.colorBlack,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.normal,
                            height: 1,
                            fontFamily: AppTheme.appFontName,
                            decoration: TextDecoration.none,
                          ),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.DROP_DOWN_ROUNDED_CORNER),
                            borderSide: BorderSide(
                                color: AppTheme.colorBlack, width: 1.0),
                          ),
                          focusColor: Colors.transparent,
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.DROP_DOWN_ROUNDED_CORNER),
                            borderSide: BorderSide(
                                color: AppTheme.colorBlack, width: 1.0),
                          ),
                        ),
                        isExpanded: false,
                        isDense: true,
                        value: savbillCareTabController.selectedCaseStatus,
                        items: savbillCareTabController.caseStatusList
                            ?.map((CaseStatusDetail value) {
                          return DropdownMenuItem<CaseStatusDetail>(
                            value: value,
                            child: Text(value.text!),
                          );
                        }).toList(),
                        onChanged: (value) {
                          setModalState(() {
                            savbillCareTabController.selectedCaseStatus =
                                value as CaseStatusDetail?;
                          });
                        },
                      ),
                    ),
                    const SizedBox(
                      height: Constant.LARGE_PADDING,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              savbillCareTabController.applyFilter();
                            },
                            radius: Constant.BTN_HEIGHT_M,
                            height: Constant.BTN_HEIGHT_M,
                            bgColors: AppTheme.colorPrimary,
                            child: CustomText(
                              title: Strings.apply,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: Constant.LARGE_PADDING,
                        ),
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              savbillCareTabController.clearFilter();
                            },
                            radius: Constant.BTN_HEIGHT_M,
                            height: Constant.BTN_HEIGHT_M,
                            bgColors: AppTheme.colorBlack,
                            borderColors: AppTheme.colorBlack,
                            child: CustomText(
                              title: Strings.clear,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ]);
          });
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  caseRateDialogContent(BuildContext context, int caseId) {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          //bool isRatLoading = false;
          return Dialog(
              shape: RoundedRectangleBorder(
                borderRadius:
                    BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
              ),
              elevation: 0,
              clipBehavior: Clip.antiAliasWithSaveLayer,
              insetPadding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              backgroundColor: Colors.transparent,
              child: Stack(
                children: [
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
                        borderRadius: BorderRadius.all(
                            Radius.circular(Constant.SMALL_PADDING))),
                    content: StatefulBuilder(
                        builder: (BuildContext context, StateSetter _setState) {
                      return Container(
                        // height: double.infinity,
                        width: MediaQuery.of(context).size.width,
                        color: AppTheme.colorWhite,
                        child: SingleChildScrollView(
                          child: Column(
                              mainAxisSize: MainAxisSize.min,
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Padding(
                                  padding: const EdgeInsets.all(
                                      Constant.SMALL_PADDING),
                                  child: Align(
                                    alignment: Alignment.centerLeft,
                                    child: CustomText(
                                      title: Strings.ticket_rating,
                                      colors: AppTheme.title_dark,
                                      fontSize: AppTheme.large,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                ),
                                const SizedBox(height: Constant.MEDIUM_PADDING),
                                Padding(
                                  padding: const EdgeInsets.only(
                                      left: Constant.SMALL_PADDING,
                                      right: Constant.SMALL_PADDING),
                                  child: Container(
                                      decoration: BoxDecoration(
                                        borderRadius:
                                            BorderRadius.circular(7.0),
                                        color: AppTheme.colorWhite,
                                      ),
                                      child: TextFormField(
                                        controller: savbillCareTabController
                                            .remarksController,
                                        maxLines: 3,
                                        maxLength: 250,
                                        style: const TextStyle(
                                            fontSize: AppTheme.medium),
                                        decoration: InputDecoration(
                                          hintText: Strings.remarks,
                                          alignLabelWithHint: true,
                                          contentPadding: const EdgeInsets.all(
                                              Constant
                                                  .TEXT_FIELD_CONTENT_PADDING),
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
                                              borderRadius:
                                                  BorderRadius.circular(Constant
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
                                      )),
                                ),
                                const SizedBox(height: Constant.MEDIUM_PADDING),
                                Align(
                                  alignment: Alignment.center,
                                  child: RatingBar.builder(
                                    minRating: 1,
                                    direction: Axis.horizontal,
                                    allowHalfRating: false,
                                    itemCount: 5,
                                    itemPadding: const EdgeInsets.symmetric(
                                        horizontal: 4.0),
                                    itemBuilder: (context, _) => Icon(
                                      Icons.star,
                                      color: AppTheme.colorPrimary,
                                    ),
                                    onRatingUpdate: (rating) {
                                      savbillCareTabController.rating = rating;
                                      savbillCareTabController.update();
                                    },
                                  ),
                                ),
                                const SizedBox(height: Constant.MEDIUM_PADDING),
                                Row(
                                  children: [
                                    Expanded(
                                      child: InkWell(
                                        onTap: () {
                                          if (savbillCareTabController
                                              .remarksController.text
                                              .isNullOrEmpty()) {
                                            Utils.showSnackbar(
                                                Strings.ERROR,
                                                Strings.please_enter_remarks,
                                                AppTheme.colorWhite,
                                                AppTheme.colorRed);
                                            return;
                                          }

                                          if (savbillCareTabController.rating ==
                                                  null ||
                                              savbillCareTabController.rating! <=
                                                  0.0) {
                                            Utils.showSnackbar(
                                                Strings.ERROR,
                                                "Please select rating.",
                                                AppTheme.colorWhite,
                                                AppTheme.colorRed);
                                            return;
                                          }
                                          Navigator.pop(context);
                                          /* adoptCareTabController
                                              .addCaseRattingApiCall(
                                                  caseId,
                                                  adoptCareTabController
                                                      .remarksController.text,
                                                  adoptCareTabController.rating!
                                                      .toInt());*/
                                        },
                                        child: Container(
                                          padding: const EdgeInsets.symmetric(
                                              vertical: Constant.LARGE_PADDING),
                                          decoration: BoxDecoration(
                                            border: Border.all(
                                              color: AppTheme.colorLightGrey,
                                              width: 1.0,
                                            ),
                                            borderRadius:
                                                const BorderRadius.only(
                                                    bottomLeft:
                                                        Radius.circular(6.0)),
                                          ),
                                          child: /*isRatLoading
                                              ? const CircularProgressIndicator()
                                              :*/
                                              CustomText(
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
                                          Navigator.pop(context);
                                        },
                                        child: Container(
                                          padding: const EdgeInsets.symmetric(
                                              vertical: Constant.LARGE_PADDING),
                                          decoration: BoxDecoration(
                                            border: Border.all(
                                              color: AppTheme.colorLightGrey,
                                              width: 1.0,
                                            ),
                                            borderRadius:
                                                const BorderRadius.only(
                                                    bottomRight:
                                                        Radius.circular(6.0)),
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
                      );
                    }),
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
              ));
        });
  }

  @override
  void followUpBtnAction(
      {String? identifier, TicketDetail? caseDetail, String? remarks}) {
    Get.back();
    savbillCareTabController.caseFollowUpApiCall(caseDetail, remarks!);
  }
}
