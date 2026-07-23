import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';

import 'input_textfield.dart';

class CustomEditNoteDialog extends StatefulWidget {
  final String title;
  final TextEditingController controller;
  final Function()? onSave;
  final Function()? onCancel;

  const CustomEditNoteDialog({
    Key? key,
    required this.title,
    required this.controller,
    required this.onSave,
    required this.onCancel,
  }) : super(key: key);

  @override
  State<CustomEditNoteDialog> createState() => _CustomEditDialogState();
}

class _CustomEditDialogState extends State<CustomEditNoteDialog> {
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.SMALL_PADDING),
      ),
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header
            Container(
              decoration: BoxDecoration(
                color: AppTheme.colorPrimary, // Change color as needed
                borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(Constant.SMALL_PADDING),
                  topRight: Radius.circular(Constant.SMALL_PADDING),
                ),
              ),
              padding: const EdgeInsets.all(Constant.SMALL_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING-2),
                    child: CustomText(
                      title:widget.title,
                      fontSize: AppTheme.large,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                  InkWell(
                    onTap: widget.onCancel,
                    child: Icon(Icons.close, color: AppTheme.colorWhite),
                  ),
                ],
              ),
            ),

            // Input Field
            Padding(
              padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.only(bottom: Constant.MEDIUM_PADDING),
                    child: CustomText(
                      title: widget.title,
                      colors: AppTheme.colorBlack,
                    ),
                  ),
                  CoustomTextField(
                    labelText: widget.title,
                    textEditingController: widget.controller,
                    textInputAction: TextInputAction.done,
                    borderEnableColors: AppTheme.colorGrey,
                    borderFocusColors: AppTheme.colorBlueRView,
                    fillColor: AppTheme.colorWhite,
                    textColor: AppTheme.colorBlack,
                    hintColor: AppTheme.colorGrey,
                    onTextValidator: (value) {
                      if (value == null || value.isEmpty) {
                        return Strings.addNotesRequired;
                      }
                      return null;
                    },
                  ),
                ],
              ),
            ),

            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.MEDIUM_PADDING,
                  vertical: Constant.SMALL_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [

                  SimpleButton(
                    onTap: widget.onSave!,
                    radius: Constant.BTN_HEIGHT_M,
                    height: Constant.BTN_HEIGHT_M,
                    borderColors: AppTheme.colorGreen,
                    bgColors: AppTheme.colorGreen,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
                      child: CustomText(
                        title: Strings.save,
                        fontSize: AppTheme.medium,
                      ),
                    ),
                  ),

                  SizedBox(width: Constant.MEDIUM_PADDING),

                  SimpleButton(
                    onTap: widget.onCancel!,
                    radius: Constant.BTN_HEIGHT_M,
                    height: Constant.BTN_HEIGHT_M,
                    bgColors: AppTheme.colorDisableGray,
                    borderColors: AppTheme.colorDisableGray,
                    child: CustomText(
                      title: Strings.cancel,
                      fontSize: AppTheme.medium,
                    ),
                  ),


                ],
              ),
            ),

            SizedBox(height: Constant.SMALL_PADDING),
          ],
        ),
      ),
    );
  }
}
