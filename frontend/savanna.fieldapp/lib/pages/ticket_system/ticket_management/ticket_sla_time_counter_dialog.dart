import 'dart:developer';

import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_countdown_timer/countdown_timer_controller.dart';
import 'package:flutter_countdown_timer/current_remaining_time.dart';
import 'package:flutter_countdown_timer/flutter_countdown_timer.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:intl/intl.dart';

class TicketSLATimeCounterDialog extends StatefulWidget {
  final TicketDetail ticketDetail;

  const TicketSLATimeCounterDialog({
    Key? key,
    required this.ticketDetail,
  }) : super(key: key);

  @override
  _TicketSLATimeCounterDialogState createState() =>
      _TicketSLATimeCounterDialogState();
}

class _TicketSLATimeCounterDialogState
    extends State<TicketSLATimeCounterDialog> {
  late StateSetter setDialogState;
  CountdownTimerController? timeController;

  DateTime? crateDateTime;

  int? slaEndTime;

  @override
  void initState() {
    super.initState();
    crateDateTime = DateFormat(Constant.DATE_NEW_TIME_FORMAT)
        .parse(widget.ticketDetail.createdate!);

    var slaUnit = widget.ticketDetail.caseSlaUnit;

    var SLAData = widget.ticketDetail.caseSlaTime;

    if (slaUnit != null) {
      if (slaUnit.equalsIgnoreCase("Min")) {
        slaEndTime = crateDateTime!.millisecondsSinceEpoch +
            (int.parse(SLAData.toString()) * 60000);
        // 1 minute = 60000;
        log("Min=>$slaEndTime");
      } else if (slaUnit.equalsIgnoreCase("Hour")) {
        slaEndTime = crateDateTime!.millisecondsSinceEpoch +
            ((int.parse(SLAData.toString()) * (60 * 60000)));
        // 1 hours = 3.6e+6
        log("Hour=>$slaEndTime");
      } else if (slaUnit.equalsIgnoreCase("Day")) {
        slaEndTime = crateDateTime!.millisecondsSinceEpoch +
            (int.parse(SLAData.toString()) * (24 * (60 * 60000)));
        // 1 days = 8.64e+7
        log("Day=>$slaEndTime");
      }
    }
    timeController =
        CountdownTimerController(endTime: slaEndTime ?? 0, onEnd: onEnd);
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
    String title = "${Strings.sla} ${Strings.counter}";

    return Stack(children: [
      AlertDialog(
        insetPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING * 2,
        ),
        contentPadding: const EdgeInsets.only(
          top: 0,
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
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Container(
                      color: AppTheme.colorPrimary,
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
                          top: Constant.SMALL_PADDING,
                          bottom: Constant.SMALL_PADDING,
                          left: Constant.SMALL_PADDING,
                          right: Constant.SMALL_PADDING),
                      child: CountdownTimer(
                        controller: timeController,
                        endTime: slaEndTime ?? 0,
                        onEnd: onEnd,
                        widgetBuilder: (_, CurrentRemainingTime? time) {
                          if (time == null || time.isNullOrEmpty()) {
                            return valueWidget("00:00:00:00");
                          }
                          return valueWidget(
                              "${time.days ?? 00}:${time.hours ?? 00}:${time.min ?? 00}:${time.sec ?? 00}");
                        },
                      ), /*CustomText(
                        title: "00:00:00:00",
                        textAlign: TextAlign.center,
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.extraLarge,
                        fontWeight: FontWeight.w700,
                      ),*/
                    ),
                    const SizedBox(
                      height: Constant.SMALL_PADDING,
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

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.extraLarge,
      fontWeight: FontWeight.w500,
      maxLines: 1,
    );
  }

  void onEnd() {
    timeController!.disposeTimer();
  }
}
