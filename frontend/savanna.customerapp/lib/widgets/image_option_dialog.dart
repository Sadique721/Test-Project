import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ImageOptionDialog extends StatefulWidget {
  final ImageOptionBtnAction imageOptionBtnAction;
  final bool? showFileSelect, showCameraSelect, showGallerySelect;

  const ImageOptionDialog(
      {Key? key,
      required this.imageOptionBtnAction,
      this.showFileSelect = false,
      this.showCameraSelect = true,
      this.showGallerySelect = true})
      : super(key: key);

  @override
  _ImageOptionState createState() => _ImageOptionState();
}

class _ImageOptionState extends State<ImageOptionDialog> {
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
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              CustomText(
                title: Strings.add_photos,
                textAlign: TextAlign.start,
                colors: AppTheme.colorBlack,
                fontSize: AppTheme.medium,
                fontWeight: FontWeight.w600,
                height: 1.35,
              ),
              const SizedBox(height: Constant.LARGE_PADDING),
              widget.showCameraSelect == true
                  ? InkWell(
                      onTap: () {
                        widget.imageOptionBtnAction.imageOptionSelection(
                            btnIdentifier: Strings.take_photo);
                      },
                      child: CustomText(
                        title: Strings.take_photo,
                        textAlign: TextAlign.start,
                        colors: AppTheme.colorBlack,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    )
                  : Container(),
              widget.showCameraSelect == true
                  ? const SizedBox(height: Constant.LARGE_PADDING)
                  : Container(),
              widget.showGallerySelect == true
                  ? InkWell(
                      onTap: () {
                        widget.imageOptionBtnAction.imageOptionSelection(
                            btnIdentifier: Strings.choose_from_gallery);
                      },
                      child: CustomText(
                        title: Strings.choose_from_gallery,
                        textAlign: TextAlign.start,
                        colors: AppTheme.colorBlack,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    )
                  : Container(),
              widget.showGallerySelect == true
                  ? const SizedBox(height: Constant.LARGE_PADDING)
                  : Container(),
              widget.showFileSelect == true
                  ? InkWell(
                      onTap: () {
                        widget.imageOptionBtnAction.imageOptionSelection(
                            btnIdentifier: Strings.pdf_or_xl);
                      },
                      child: CustomText(
                        title: Strings.pdf_or_xl,
                        textAlign: TextAlign.start,
                        colors: AppTheme.colorBlack,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    )
                  : Container(),
              widget.showFileSelect == true
                  ? const SizedBox(height: Constant.LARGE_PADDING)
                  : Container(),
              const SizedBox(height: Constant.VERY_SMALL_PADDING),
              InkWell(
                onTap: () {
                  Get.back();
                },
                child: CustomText(
                  title: Strings.cancel,
                  colors: AppTheme.colorRed,
                  fontSize: AppTheme.medium,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ]),
      ),
    );
  }
}

abstract class ImageOptionBtnAction {
  void imageOptionSelection({String btnIdentifier});
}
