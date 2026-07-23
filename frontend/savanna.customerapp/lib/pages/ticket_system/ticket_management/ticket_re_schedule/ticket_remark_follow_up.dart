import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/model/get_ticket_follow_up_remark_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/ticket_remark_followup_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class TicketRemarkFollowUp extends StatefulWidget {
  @override
  _TicketRemarkFollowUpState createState() => _TicketRemarkFollowUpState();
}

class _TicketRemarkFollowUpState extends State<TicketRemarkFollowUp> {
  final remarkFollowUpController = Get.put(TicketRemarkFollowUpController());

  final remarkFollowUpFormKey = GlobalKey<FormState>();
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
    return GetBuilder<TicketRemarkFollowUpController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: remarkFollowUpController.isLoading),
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
        child: SingleChildScrollView(
          child: Container(
            color: AppTheme.colorBG,
            width: MediaQuery.of(context).size.width,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                reviewEditor(),
                Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    InkWell(
                      onTap: (){
                        if (remarkFollowUpController.remarkController.text.isNullOrEmpty()) {
                          Utils.showSnackbar(
                              Strings.ERROR,
                              Strings.please_enter_remarks,
                              AppTheme.colorWhite,
                              AppTheme.colorRed);
                          return;
                        } else {
                          remarkFollowUpController.addRemarkFollowUp(remarkFollowUpController.remarkController.text);
                        }
                      },
                      child: Container(
                        padding: const EdgeInsets.only(
                            top: Constant.SMALL_PADDING,
                            bottom: Constant.SMALL_PADDING,
                            left: Constant.MEDIUM_PADDING,
                            right: Constant.MEDIUM_PADDING),
                        // height: Constant.CARD_BOTTOM_BUTTON_H,
                        alignment: Alignment.center,
                        decoration: BoxDecoration(
                          color: AppTheme.colorPrimary,
                          borderRadius: const BorderRadius.all(
                              Radius.circular(Constant.ROUNDED_CORNER - 10)),
                        ),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const Padding(
                              padding: EdgeInsets.only(
                                  left: Constant.VERY_SMALL_PADDING,
                                  right: Constant.VERY_SMALL_PADDING),
                              child: Icon(
                                size: Constant.ICON_SIZE_M,
                                Icons.check_circle,
                                color: Colors.white,
                              ),
                            ),
                            CustomText(
                              title: Strings.save,
                              colors: AppTheme.colorWhite,
                              fontSize: AppTheme.small - 1,
                              textAlign: TextAlign.center,
                              fontWeight: FontWeight.normal,
                            )
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                Align(
                  alignment: Alignment.centerLeft,
                  child: CustomText(
                      title: Strings.remarks_list,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w600),
                ),
                Container(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: (remarkFollowUpController.remarkFollowUpDataList !=
                      null &&
                      remarkFollowUpController
                          .remarkFollowUpDataList!.isNotEmpty)
                      ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      shrinkWrap: true,
                      itemCount: remarkFollowUpController
                          .remarkFollowUpDataList!.length,
                      itemBuilder: (context, index) {
                        if (index ==
                            remarkFollowUpController
                                .remarkFollowUpDataList?.length) {
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
                          TicketFollowUpRemarkDataList item =
                          remarkFollowUpController
                              .remarkFollowUpDataList![index];
                          return followUpItem(item: item);
                        }
                      })
                      : SizedBox(
                    child: noDataFound(),
                    height: MediaQuery.of(context).size.height * 0.7,
                  ),
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
        "${Strings.remarks} ${Strings.followup}",
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  reviewEditor() {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.SMALL_PADDING),
          InputTitleRequire(title: Strings.remarks, require: true),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(7.0),
              color: AppTheme.colorWhite,
            ),
            child: TextFormField(
              controller: remarkFollowUpController.remarkController,
              maxLines: 2,
              maxLength: 250,
              style: const TextStyle(fontSize: AppTheme.medium),
              decoration: InputDecoration(
                hintText: Strings.remarks,
                alignLabelWithHint: true,
                contentPadding:
                const EdgeInsets.all(Constant.TEXT_FIELD_CONTENT_PADDING),
                focusColor: Colors.transparent,
                focusedBorder: OutlineInputBorder(
                  borderRadius:
                  BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                  borderSide:
                  BorderSide(color: AppTheme.colorPrimary, width: 1.0),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius:
                  BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                  borderSide: BorderSide(
                    color: AppTheme.colorIconGrey,
                    width: 1.0,
                  ),
                ),
                border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(
                        Constant.TEXT_FIELD_CONTENT_PADDING)),
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
          const SizedBox(height: Constant.SMALL_PADDING),
        ],
      ),
    );
  }

  followUpItem({required TicketFollowUpRemarkDataList item}) {
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
          Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING,
                  vertical: Constant.SMALL_PADDING),
              child: basicDetailItem(Strings.remarks, item.remark ?? "",
                  Strings.created_date, actionDate)),
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