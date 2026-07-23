import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AlertDialogHelper extends StatefulWidget {
  late String title;
  late String message;
  late String positiveBtnText;
  late String negativeBtnText;
  late VoidCallback positiveBtnClick;
  late VoidCallback negativeBtnClick;

  AlertDialogHelper(
      {Key? key,
      title = '',
      message = '',
      positiveBtnText = '',
      negativeBtnText = '',
      positiveBtnClick,
      negativeBtnClick})
      : super(key: key) {
    this.title = title;
    this.message = message;
    this.positiveBtnText = positiveBtnText;
    this.negativeBtnText = negativeBtnText;
    this.positiveBtnClick = positiveBtnClick;
    this.negativeBtnClick = negativeBtnClick;
  }

  @override
  State<AlertDialogHelper> createState() => _AlertDialogHelperState();
}

class _AlertDialogHelperState extends State<AlertDialogHelper> {
  @override
  Widget build(BuildContext context) {
    // set up the buttons
    List<Widget> actions = [];
    if (!widget.negativeBtnText.isNullOrEmpty()) {
      Widget negativeButton = TextButton(
        child: CustomText(
          title: widget.negativeBtnText,
          colors: AppTheme.colorPrimary,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.normal,
        ),
        onPressed: widget.negativeBtnClick,
      );

      actions.add(negativeButton);
    }
    if (!widget.positiveBtnText.isNullOrEmpty()) {
      Widget positiveButton = TextButton(
        child: CustomText(
          title: widget.positiveBtnText,
          colors: AppTheme.colorPrimary,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.normal,
        ),
        onPressed: widget.positiveBtnClick,
      );
      actions.add(positiveButton);
    }

    // return AlertDialog(
    //   title: Visibility(
    //       visible: !widget.title.isNullOrEmpty(),
    //       child: Container(
    //         color: AppTheme.colorPrimary,
    //         width: double.infinity,
    //         height: Constant.CARD_BOTTOM_BUTTON_H,
    //         // child: Text(
    //         //   widget.title,
    //         //   style: const TextStyle(
    //         //       fontSize: AppTheme.large, fontWeight: FontWeight.w600),
    //         // ),
    //      child: CustomText(
    //           title: widget.title,
    //           colors: AppTheme.colorWhite,
    //           fontSize: AppTheme.large,
    //           fontWeight: FontWeight.w600,
    //         ),
    //       )),
    //   content: Visibility(
    //     visible: !widget.message.isNullOrEmpty(),
    //     child: Text(widget.message),
    //   ),
    //   actions: actions,
    // );

    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      ),
      elevation: 0,
      clipBehavior: Clip.antiAliasWithSaveLayer,
      insetPadding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      backgroundColor: Colors.transparent,
      // child: contentBox(context),
      child: AlertDialog(
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
        content: Visibility(
          visible: !widget.message.isNullOrEmpty(),
          child: StatefulBuilder(// You need this, notice the parameters below:
              builder: (BuildContext context, StateSetter setState) {
            // setDialogState = setState;
            return Container(
              width: MediaQuery.of(context).size.width,
              color: AppTheme.colorTransparent,
              child: SingleChildScrollView(
                child: Column(
                    mainAxisSize: MainAxisSize.max,
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Container(
                        color: AppTheme.colorPrimary,
                        padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          // crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Align(
                              alignment: Alignment.centerLeft,
                              child: CustomText(
                                title: widget.title,
                                colors: AppTheme.title_dark,
                                fontSize: AppTheme.large,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                        // InkWell(
                        //   onTap: (){
                        //     Get.back();
                        //     // widget.negativeBtnClick;
                        //   },
                        //   child: Align(
                        //     alignment: Alignment.topCenter,
                        //     child: Icon(Icons.cancel,color: AppTheme.colorWhite,size: 20,)
                        //   ),
                        // ),
                          ],
                        ),
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      Padding(
                        padding: const EdgeInsets.symmetric(vertical: Constant.SMALL_PADDING,horizontal: Constant.SMALL_PADDING
                        ),
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: widget.message,
                            colors: AppTheme.colorLightBlack,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.normal,
                          ),
                        ),
                      ),
                    ]),
              ),
            );
          }),
        ),
        actions: actions,
      ),
    );
  }
}
