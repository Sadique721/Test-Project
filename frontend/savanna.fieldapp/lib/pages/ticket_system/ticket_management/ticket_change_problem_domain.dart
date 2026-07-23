import 'package:savbill/pages/ticket_system/model/response/create_ticket_active_service_res.dart';
import 'package:savbill/pages/ticket_system/model/response/get_reason_category_active_services_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_change_problem_domain_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class TicketChangeProblemDomain extends StatefulWidget {
  @override
  _TicketChangeProblemDomainState createState() =>
      _TicketChangeProblemDomainState();
}

class _TicketChangeProblemDomainState extends State<TicketChangeProblemDomain> {
  final ticketChangeProblemDomainController =
      Get.put(TicketChangeProblemDomainController());
  final ticketChangeProblemDomainFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  Widget build(BuildContext context) {
    return GetBuilder<TicketChangeProblemDomainController>(
        builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: ticketChangeProblemDomainController.isLoading),
      ]);
    });
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SCREEN_PADDING,
                        right: Constant.SCREEN_PADDING),
                    child: Form(
                      key: ticketChangeProblemDomainFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(height: Constant.SCREEN_PADDING),
                            CustomText(
                                title:
                                    "${Strings.ticket} : ${ticketChangeProblemDomainController.castTitle}",
                                colors: AppTheme.colorBlack,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium + 1,
                                fontWeight: FontWeight.w500),
                            const SizedBox(height: Constant.SCREEN_PADDING),
                            Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.service, require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.service,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: ticketChangeProblemDomainController
                                          .selectServiceListData,
                                      items: ticketChangeProblemDomainController
                                          .servicesAreaList!
                                          .map(
                                              (GetActiveServiceDataList value) {
                                        return DropdownMenuItem<
                                            GetActiveServiceDataList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.serviceName!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        ticketChangeProblemDomainController
                                                .selectServiceListData =
                                            value as GetActiveServiceDataList?;
                                        ticketChangeProblemDomainController
                                            .update();
                                        ticketChangeProblemDomainController
                                            .reasonCategoryDataList!
                                            .clear();
                                        ticketChangeProblemDomainController
                                            .selectTicketProblemDomain = null;
                                        // ticketChangeProblemDomainController
                                        //     .getSubCategory();
                                        List<int> serviceIDS = [];
                                        if (ticketChangeProblemDomainController
                                                .selectServiceListData !=
                                            null) {
                                          serviceIDS.add(
                                              ticketChangeProblemDomainController
                                                  .selectServiceListData!.id!);
                                          ticketChangeProblemDomainController
                                              .getTicketReasonCategoryByActiveServices(
                                                  serviceIDS);
                                        }
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            ticketChangeProblemDomainController
                                                    .selectServiceListData ==
                                                null) {
                                          return Strings.select_service;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.ticket_problem_domain,
                                      require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(
                                              Strings.ticket_problem_domain,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: ticketChangeProblemDomainController
                                          .selectTicketProblemDomain,
                                      items: ticketChangeProblemDomainController
                                          .reasonCategoryDataList!
                                          .map((ReasonCategoryDataList value) {
                                        return DropdownMenuItem<
                                            ReasonCategoryDataList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.categoryName!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        ticketChangeProblemDomainController
                                                .selectTicketProblemDomain =
                                            value as ReasonCategoryDataList?;
                                        ticketChangeProblemDomainController
                                            .update();
                                        if (ticketChangeProblemDomainController
                                                .selectTicketProblemDomain!
                                                .id !=
                                            null) {
                                          ticketChangeProblemDomainController
                                              .getTicketSubProblemDomainCategory(
                                                  ticketChangeProblemDomainController
                                                      .selectTicketProblemDomain!
                                                      .id);
                                        }
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            ticketChangeProblemDomainController
                                                    .selectTicketProblemDomain ==
                                                null) {
                                          return Strings
                                              .select_ticket_problem_domain;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.ticket_sub_problem_domain,
                                      require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(
                                              Strings.ticket_sub_problem_domain,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: ticketChangeProblemDomainController
                                          .selectedTicketSubProblemDomain,
                                      items: ticketChangeProblemDomainController
                                          .ticketSubProblemDomainList!
                                          .map((SubProblemDomainDetail value) {
                                        return DropdownMenuItem<
                                            SubProblemDomainDetail>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.subCategoryName!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        ticketChangeProblemDomainController
                                                .selectedTicketSubProblemDomain =
                                            value as SubProblemDomainDetail?;
                                        ticketChangeProblemDomainController
                                            .update();
                                        ticketChangeProblemDomainController
                                            .setReasonData();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            ticketChangeProblemDomainController
                                                    .selectedTicketSubProblemDomain ==
                                                null) {
                                          return Strings
                                              .select_ticket_sub_problem_domain;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.ticket_reason,
                                      require: false),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.ticket_reason,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: ticketChangeProblemDomainController
                                          .selectedReasonMapping,
                                      items: ticketChangeProblemDomainController
                                          .ticketReasonMappingList!
                                          .map(
                                              (TicketSubCategoryGroupReasonMappingList
                                                  value) {
                                        return DropdownMenuItem<
                                            TicketSubCategoryGroupReasonMappingList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.reason!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        ticketChangeProblemDomainController
                                                .selectedReasonMapping =
                                            value
                                                as TicketSubCategoryGroupReasonMappingList?;
                                        ticketChangeProblemDomainController
                                            .update();
                                      },
                                      validator: (value) {
                                        // if (value == null ||
                                        //     ticketChangeProblemDomainController
                                        //             .selectedReasonMapping ==
                                        //         null) {
                                        //   return Strings.select_ticket_reason;
                                        // }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.remarks, require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.remarks,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          ticketChangeProblemDomainController
                                              .remarksController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
                                      textColor: AppTheme.colorBlack,
                                      keyboardType: TextInputType.text,
                                      fontSize: AppTheme.small,
                                      textInputAction: TextInputAction.done,
                                      fontWeight: FontWeight.w500,
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.MEDIUM_PADDING,
                                              vertical:
                                                  Constant.MEDIUM_PADDING),
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (value!.isEmpty) {
                                          return Strings.please_select_remark;
                                        } else {
                                          return null;
                                        }
                                      },
                                      onTextFiledOnTap: () {},
                                      readOnly: false),
                                ),
                              ],
                            ),
                            const SizedBox(
                              height: Constant.EXTRA_LARGE_PADDING,
                            ),
                          ]),
                    ),
                  ),
                ),
              ),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        validateForm();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.save,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  validateForm() {
    if (ticketChangeProblemDomainFormKey.currentState!.validate()) {
      ticketChangeProblemDomainController.ticketUpdateCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.change_problem_domain,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  _backScreen() {
    Get.back();
  }
}
