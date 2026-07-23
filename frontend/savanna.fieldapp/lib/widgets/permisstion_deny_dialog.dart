import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';

class PermissionDenyDialog extends StatefulWidget {
  final PermissionDenyBtnAction permissionDenyBtnAction;
  final String titleMsg;

  const PermissionDenyDialog(
      {Key? key, required this.permissionDenyBtnAction, required this.titleMsg})
      : super(key: key);

  @override
  _PermissionDenyState createState() => _PermissionDenyState();
}

class _PermissionDenyState extends State<PermissionDenyDialog> {
  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      ),
      elevation: 0,
      backgroundColor: Colors.transparent,
      child: contentBox(context),
    );
  }

  contentBox(context) {
    return Container(
      decoration: BoxDecoration(
        shape: BoxShape.rectangle,
        color: AppTheme.colorWhite,
        borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      ),
      child: Padding(
        padding: const EdgeInsets.all(
          Constant.LARGE_PADDING,
        ),
        child: Column(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const SizedBox(height: Constant.MEDIUM_PADDING),
              CustomText(
                title: widget.titleMsg,
                colors: AppTheme.colorBlack,
                fontSize: AppTheme.medium,
                fontWeight: FontWeight.w300,
                height: 1.35,
              ),
              const SizedBox(height: Constant.VERY_EXTRA_LARGE_PADDING),
              SimpleButton(
                onTap: () {
                  widget.permissionDenyBtnAction.btnClickAction(
                      btnIdentifier: Strings.app_permission_settings);
                },
                bgColors: AppTheme.colorPrimary,
                borderColors: AppTheme.colorPrimary,
                child: CustomText(
                  title: Strings.app_permission_settings,
                  fontSize: AppTheme.medium,
                  fontWeight: FontWeight.w400,
                ),
              ),
              const SizedBox(height: Constant.LARGE_PADDING),
              InkWell(
                onTap: () {
                  widget.permissionDenyBtnAction
                      .btnClickAction(btnIdentifier: Strings.cancel);
                },
                child: CustomText(
                  title: Strings.cancel,
                  colors: AppTheme.colorPrimary,
                  fontSize: AppTheme.medium,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: Constant.SMALL_PADDING),
            ]),
      ),
    );
  }
}

abstract class PermissionDenyBtnAction {
  void btnClickAction({String btnIdentifier});
}

