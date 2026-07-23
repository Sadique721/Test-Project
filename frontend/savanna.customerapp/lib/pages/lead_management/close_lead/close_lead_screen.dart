import 'package:savbill/pages/lead_management/close_lead/close_lead_controller.dart';
import 'package:savbill/pages/lead_management/model/all_rejected_reason_lead_res.dart';
import 'package:savbill/pages/lead_management/model/reassign_lead_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CloseLeadScreen extends StatefulWidget {
  @override
  _CloseLeadState createState() => _CloseLeadState();
}

class _CloseLeadState extends State<CloseLeadScreen> {
  final closeLeadController = Get.put(CloseLeadController());
  final closeLeadFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  void initState() {
    // WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    // WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CloseLeadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: closeLeadController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(CloseLeadController controller) {
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
                    key: closeLeadFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),

                        closeLeadController.pageTitle!.equalsIgnoreCase(Strings.closeLead)?
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            InputTitleRequire(
                                title: Strings.rejectedReason, require: true),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
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
                                decoration: Utils.ddlDecoration(),
                                hint: Align(
                                  alignment: Alignment.centerLeft,
                                  child: Text(
                                    Strings.selectedRejectedReasonRequired,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value: controller.selectedRejectedContentData,
                                items: controller.allRejectedReasonContentList
                                    .map((RejectedContent value) {
                                  return DropdownMenuItem<RejectedContent>(
                                    value: value,
                                    child: Text(value.name!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  controller.selectedRejectedContentData =
                                      value as RejectedContent?;
                                  closeLeadController.rejectedSubReasonDtoList
                                      .clear();
                                  closeLeadController.selectedRejectedSubReasonDto =
                                      null;
                                  closeLeadController.rejectedSubReasonDtoList =
                                      value!.rejectSubReasonDtoList!;
                                  controller.update();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      controller.selectedRejectedContentData ==
                                          null) {
                                    return Strings.rejectionReasonRequired;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ],
                        ) :
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            InputTitleRequire(
                                title: Strings.staff, require: true),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            DropdownSearch<ReassignLeadDataList>(
                              key: closeLeadController.selectStaffDropDownKey,
                              mode: Mode.form,
                              selectedItem:
                              closeLeadController.selectedReassignLeadData,
                              items: (filter, infiniteScrollProps) =>
                              closeLeadController.reassignLeadDataList!,
                              compareFn: (item1, item2) => item1.id == item2.id,
                              itemAsString: (item) => item.fullName!,
                              decoratorProps: DropDownDecoratorProps(
                                baseStyle: TextStyle(
                                    color: AppTheme.title_dark, fontSize: AppTheme.small),
                                // Change text color
                                decoration: InputDecoration(
                                  hintText: Strings.select_staff,
                                  hintStyle: AppTheme.dropdownHintStyle,
                                  labelStyle: AppTheme.dropdownHintStyle,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide:
                                    BorderSide(color: AppTheme.colorBlack, width: 0.8),
                                  ),
                                  focusColor: Colors.black,
                                  focusedBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorPrimary, width: 0.8),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                      color: AppTheme.colorBlack,
                                      width: 1.0,
                                    ),
                                  ),
                                ),
                              ),
                              popupProps: PopupProps.menu(
                                showSearchBox: true,
                                fit: FlexFit.loose,
                                constraints: BoxConstraints(),
                                menuProps: MenuProps(
                                  backgroundColor: Colors.white,
                                  borderRadius: BorderRadius.circular(
                                      Constant.DROP_DOWN_ROUNDED_CORNER),
                                ),
                                searchFieldProps: TextFieldProps(
                                  decoration: InputDecoration(
                                    hintText: Strings.pelase_select_staff,
                                    hintStyle: AppTheme.dropdownHintStyle,
                                    border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                          color: AppTheme.colorBlack, width: 0.8),
                                    ),
                                  ),
                                ),
                                listViewProps: ListViewProps(
                                  shrinkWrap: true,
                                ),
                              ),
                              onChanged: (value) {
                                controller.selectedReassignLeadData =
                                      value as ReassignLeadDataList?;
                                      controller.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                          controller.selectedReassignLeadData ==
                                              null) {
                                        return Strings.pelase_select_staff;
                                      }
                                      return null;
                              },
                            )
                          ],
                        ),

                        closeLeadController.rejectedSubReasonDtoList.isNotEmpty
                            ? Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: [
                                  const SizedBox(
                                      height: Constant.SCREEN_PADDING),
                                  InputTitleRequire(
                                      title: Strings.rejectedSubReason,
                                      require: false),
                                  const SizedBox(
                                    height: Constant.SMALL_PADDING,
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
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                        alignment: Alignment.centerLeft,
                                        child: Text(
                                          Strings.selectedRejectedSubReasonLead,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ),
                                        ),
                                      ),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: false,
                                      isDense: true,
                                      value: controller
                                          .selectedRejectedSubReasonDto,
                                      items: controller.rejectedSubReasonDtoList
                                          .map((RejectSubReasonDtoList value) {
                                        return DropdownMenuItem<
                                            RejectSubReasonDtoList>(
                                          value: value,
                                          child: Text(value.name!),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        controller
                                                .selectedRejectedSubReasonDto =
                                            value as RejectSubReasonDtoList?;
                                        controller.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.remarks, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        Container(
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(7.0),
                            // color: AppTheme.colorWhite,
                          ),
                          child: TextFormField(
                            controller: closeLeadController.remarkController,
                            maxLines: 3,
                            maxLength: 250,
                            style: const TextStyle(fontSize: AppTheme.medium),
                            decoration: InputDecoration(
                              hintText: Strings.enter_remarks,
                              alignLabelWithHint: true,
                              filled: true,
                              hoverColor: Colors.white,
                              fillColor: AppTheme.colorWhite,
                              contentPadding: const EdgeInsets.all(
                                  Constant.TEXT_FIELD_CONTENT_PADDING * 1.5),
                              focusColor: Colors.transparent,
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.BTN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorPrimary, width: 1.0),
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
                                  borderRadius: BorderRadius.circular(
                                      Constant.TEXT_FIELD_CONTENT_PADDING)),
                              isDense: true,
                              hintStyle: TextStyle(
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.normal,
                                  height: 1,
                                  color: AppTheme.colorGrey),
                              errorStyle: TextStyle(
                                color: AppTheme.colorError,
                                fontWeight: FontWeight.normal,
                                fontSize: AppTheme.large - 1,
                              ),
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
                              if (value!.isEmpty) {
                                return Strings.please_enter_remarks;
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.EXTRA_LARGE_PADDING,
                        ),
                      ],
                    ),
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
                      title: closeLeadController.pageTitle!.equalsIgnoreCase(Strings.closeLead) ? Strings.submit :Strings.assignLead,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  titleWithRequireWidget(String title) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.normal,
        ),
        CustomText(
          title: " *",
          colors: Colors.red,
          textAlign: TextAlign.start,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.w600,
        ),
      ],
    );
  }

  _appBar() {
    return DynamicAppBar(
        closeLeadController.pageTitle!.equalsIgnoreCase(Strings.closeLead)
            ? Strings.closeALead
            : Strings.assignLead,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (closeLeadFormKey.currentState!.validate()) {
      if(closeLeadController.pageTitle!.equalsIgnoreCase(Strings.closeLead)) {
        closeLeadController.saveCloseLead();
      }else if(closeLeadController.pageTitle!.equalsIgnoreCase(Strings.reassignLead)){
        closeLeadController.updateLeadReassign();
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }
}
