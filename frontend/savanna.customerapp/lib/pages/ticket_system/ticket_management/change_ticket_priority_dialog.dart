import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ChangeTicketPriorityDialog extends StatefulWidget {
  final TicketDetail? ticketDetail;
  final List<TicketPriority> priorityList;
  final TicketPriorityBtnAction ticketPriorityBtnAction;

  const ChangeTicketPriorityDialog({
    Key? key,
    required this.ticketDetail,
    required this.priorityList,
    required this.ticketPriorityBtnAction,
  }) : super(key: key);

  @override
  _TicketPriorityDialogDialogState createState() =>
      _TicketPriorityDialogDialogState();
}

class _TicketPriorityDialogDialogState
    extends State<ChangeTicketPriorityDialog> {
  TicketPriority? selectedPriority;
  late StateSetter setDialogState;

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
    String title =
        "${widget.ticketDetail?.caseTitle!} ${Strings.change_priority}";

    return Stack(children: [
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
            borderRadius:
                BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
        content: StatefulBuilder(// You need this, notice the parameters below:
            builder: (BuildContext context, StateSetter setState) {
          setDialogState = setState;
          return Container(
            width: MediaQuery.of(context).size.width,
            color: AppTheme.colorWhite,
            child: SingleChildScrollView(
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: title,
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
                      child: InputTitleRequire(
                          title: Strings.remarks, require: true),
                    ),
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
                    Padding(
                      padding: const EdgeInsets.only(
                          left: Constant.SMALL_PADDING,
                          right: Constant.SMALL_PADDING),
                      child: Container(
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(7.0),
                          color: AppTheme.colorWhite,
                        ),
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
                                child: Text(Strings.priority,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ))),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: selectedPriority,
                            items:
                                widget.priorityList.map((TicketPriority value) {
                              return DropdownMenuItem<TicketPriority>(
                                value: value,
                                child: Align(
                                  alignment: Alignment.centerLeft,
                                  child: CustomText(
                                    title: value.text!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              );
                            }).toList(),
                            onChanged: (value) {
                              setDialogState(() {
                                selectedPriority = value as TicketPriority?;
                              });
                            },
                            validator: (value) {
                              return null;
                            },
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.MEDIUM_PADDING * 2),
                    Row(
                      children: [
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              if (selectedPriority == null) {
                                Utils.showSnackbar(
                                    Strings.ERROR,
                                    Strings.select_priority,
                                    AppTheme.colorWhite,
                                    AppTheme.colorRed);
                                return;
                              }
                              widget.ticketPriorityBtnAction
                                  .ticketPriorityBtnAction(
                                      priority: selectedPriority!,
                                      ticketDetail: widget.ticketDetail);
                            },
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                  vertical: Constant.LARGE_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorLightGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomLeft: Radius.circular(6.0)),
                              ),
                              child: CustomText(
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
                              Get.back();
                            },
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                  vertical: Constant.LARGE_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorLightGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomRight: Radius.circular(6.0)),
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
    ]);
  }
}

abstract class TicketPriorityBtnAction {
  void ticketPriorityBtnAction(
      {TicketPriority priority, TicketDetail? ticketDetail});
}
