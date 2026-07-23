import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';

class LocationSettingsDialog extends StatefulWidget {
  final LocationBtnAction locationBtnAction;
  final bool isAppPermission;
  final String from;

  const LocationSettingsDialog(
      {Key? key,
      required this.locationBtnAction,
      required this.isAppPermission,
      required this.from})
      : super(key: key);

  @override
  _LocationSettingsState createState() => _LocationSettingsState();
}

class _LocationSettingsState extends State<LocationSettingsDialog> {
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
    String title = "";

    if (widget.from.equalsIgnoreCase(Constant.NEAR_BY_DEVICE)) {
      title = Strings.location_settings_desc_near_by_device;
    } else if (widget.from.equalsIgnoreCase(Constant.DEVICE_LOCATION)) {
      title = Strings.location_settings_desc_device_current;
    }
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
                title: widget.isAppPermission
                    ? Strings.location_settings_desc_per
                    : title,
                colors: AppTheme.colorBlack,
                fontSize: AppTheme.medium,
                fontWeight: FontWeight.w300,
                height: 1.35,
              ),
              const SizedBox(height: Constant.VERY_EXTRA_LARGE_PADDING),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        widget.locationBtnAction
                            .btnClickAction(btnIdentifier: Strings.try_again);
                      },
                      radius: Constant.BTN_ROUNDED_CORNER,
                      height: Constant.CARD_BOTTOM_BUTTON_H,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.try_again,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: Constant.LARGE_PADDING),
              InkWell(
                onTap: () {
                  widget.locationBtnAction.btnClickAction(
                      btnIdentifier: widget.isAppPermission
                          ? Strings.app_permission_settings
                          : Strings.location_settings);
                },
                child: CustomText(
                  title: widget.isAppPermission
                      ? Strings.app_permission_settings
                      : Strings.location_settings,
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

abstract class LocationBtnAction {
  void btnClickAction({String btnIdentifier});
}
