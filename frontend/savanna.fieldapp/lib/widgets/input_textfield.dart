import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class CoustomTextField extends StatelessWidget {
  final String? labelText;
  final Widget? prefixIcon;
  final TextEditingController? textEditingController;
  final Function(String)? onChanged;
  final FormFieldValidator<String>? onTextValidator;
  final bool? obscureText;
  final int? maxLines;
  final int? minLines;
  final Function()? onTextFiledOnTap;
  final bool readOnly;
  final FocusNode? focusNode;
  final TextInputType? keyboardType;
  final TextInputAction? textInputAction;
  final int? maxLength;
  final double borderCorner;
  final Color? borderEnableColors;
  final Color? borderFocusColors;
  final Color? fillColor;
  final Color? cursorColor;
  final Color? textColor;
  final Color? hintColor;
  final TextAlign? textAlign;
  final EdgeInsetsGeometry? contentPadding;
  final double? fontSize;
  final FontWeight? fontWeight;
  final Widget? suffixIcon;
  final List<TextInputFormatter>? inputFormatters;
  final Function(String)? onSubmitted;
  final String? initialValue;
  final AutovalidateMode? autovalidateMode;
  final bool? isEnable;


  CoustomTextField({
    this.autovalidateMode,
    this.labelText,
    this.prefixIcon,
    this.suffixIcon,
    this.textEditingController,
    this.onChanged,
    this.obscureText,
    this.maxLines,
    this.minLines,
    this.onTextFiledOnTap,
    required this.onTextValidator,
    this.readOnly = false,
    this.focusNode,
    this.keyboardType,
    this.textInputAction,
    this.borderEnableColors,
    this.borderFocusColors,
    this.fillColor,
    this.cursorColor,
    this.textColor,
    this.textAlign,
    this.contentPadding,
    this.fontSize,
    this.fontWeight,
    this.maxLength,
    this.inputFormatters,
    this.hintColor,
    this.borderCorner = Constant.BTN_ROUNDED_CORNER,
    this.onSubmitted,
    this.initialValue,
    this.isEnable = true
  }) : super();

  @override
  Widget build(BuildContext context) {
    return MediaQuery(
      data: MediaQuery.of(context).copyWith(textScaleFactor: 1),
      child: TextFormField(
        autovalidateMode: autovalidateMode,
        cursorColor: cursorColor ?? AppTheme.colorPrimary,
        textAlign: textAlign ?? TextAlign.start,
        focusNode: focusNode,
        initialValue: initialValue,
        enabled: isEnable,
        textAlignVertical: TextAlignVertical.center,
        inputFormatters: (inputFormatters != null) ? inputFormatters : null,
        decoration: InputDecoration(
            contentPadding: contentPadding ?? null,
            prefixIcon: prefixIcon ?? null,
            suffixIcon: suffixIcon ?? null,
            filled: true,
            hoverColor: Colors.white,
            fillColor: fillColor ?? AppTheme.colorWhite,
            counterText: "",
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(borderCorner),
              borderSide: BorderSide(color: AppTheme.colorPrimary, width: 1.0),
            ),
            focusColor: Colors.transparent,
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(borderCorner),
              borderSide: BorderSide(
                  color: borderFocusColors ?? AppTheme.colorPrimary,
                  width: 1.0),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(borderCorner),
              borderSide: BorderSide(
                color: borderEnableColors ?? AppTheme.colorIconGrey,
                width: 1.0,
              ),
            ),
            //labelText: labelText,
            hintText: labelText,
            hintStyle: TextStyle(
                fontSize: AppTheme.small,
                fontWeight: FontWeight.normal,
                height: 1,
                color: hintColor ?? AppTheme.colorBlack),
            alignLabelWithHint: true,
            labelStyle: TextStyle(
              color: AppTheme.colorBlack,
              fontSize: AppTheme.medium,
              fontWeight: FontWeight.normal,
              height: 1,
              fontFamily: AppTheme.appFontName,
              decoration: TextDecoration.none,
            ),
            errorStyle: TextStyle(
              color: AppTheme.colorError,
              fontWeight: FontWeight.normal,
              fontSize: AppTheme.errorSize,
            ),
            errorMaxLines: 3),
        contextMenuBuilder:
            (BuildContext context, EditableTextState editableTextState) {
          return AdaptiveTextSelectionToolbar.editable(
            anchors: editableTextState.contextMenuAnchors,
            clipboardStatus: ClipboardStatus.notPasteable,
            // to apply the normal behavior when click on copy (copy in clipboard close toolbar)
            // use an empty function `() {}` to hide this option from the toolbar
            onCopy: () => editableTextState
                .copySelection(SelectionChangedCause.toolbar),
            // to apply the normal behavior when click on cut
            onCut: () => editableTextState
                .cutSelection(SelectionChangedCause.toolbar),
            onPaste: () {
              // HERE will be called when the paste button is clicked in the toolbar
              // apply your own logic here

              // to apply the normal behavior when click on paste (add in input and close toolbar)
              editableTextState.pasteText(SelectionChangedCause.tap);
            },
            onLookUp: (){},
             onSearchWeb: (){},
            onLiveTextInput: (){},
            onShare: (){},
            // to apply the normal behavior when click on select all
            onSelectAll: () =>
                editableTextState.selectAll(SelectionChangedCause.toolbar),
          );
        },
        autofocus: true,
        style: TextStyle(
          color: readOnly
              ? textColor ?? AppTheme.colorBlack
              : textColor ?? AppTheme.colorBlack,
          fontSize: fontSize ?? AppTheme.medium,
          fontWeight: fontWeight ?? FontWeight.w300,
          height: 1,
          fontFamily: AppTheme.appFontName,
          decoration: TextDecoration.none,
        ),
        keyboardType: keyboardType,
        readOnly: readOnly,
        obscureText: obscureText ?? false,
        maxLines: maxLines ?? 1 ,
        minLines: minLines ?? 1,
        maxLength: maxLength,
        onChanged: onChanged,
        onTap: onTextFiledOnTap,

        controller: textEditingController,
        validator: onTextValidator,
        textInputAction: textInputAction,
        onFieldSubmitted: onSubmitted,
      ),
    );
  }
}
