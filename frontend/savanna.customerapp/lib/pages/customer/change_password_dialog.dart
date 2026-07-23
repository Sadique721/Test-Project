import 'package:savbill/pages/customer/model/request/change_customer_pwd_req.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ChangePasswordDialog extends StatefulWidget {
  final ChangePasswordBtnAction changePasswordBtnAction;
  final ChangeCustomerPasswordReq changeCustomerPasswordReq;

  const ChangePasswordDialog(
      {Key? key,
      required this.changePasswordBtnAction,
      required this.changeCustomerPasswordReq})
      : super(key: key);

  @override
  _ChangePasswordDialogState createState() => _ChangePasswordDialogState();
}

class _ChangePasswordDialogState extends State<ChangePasswordDialog> {
  TextEditingController oldPwdController = TextEditingController();
  TextEditingController newPwdController = TextEditingController();

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
      child: contentBox(context, Strings.change_password),
    );
  }

  contentBox(BuildContext context, String title) {
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
        content: Container(
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
                 /* const SizedBox(height: Constant.MEDIUM_PADDING),
                  Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SMALL_PADDING,
                        right: Constant.SMALL_PADDING),
                    child: InputTitleRequire(
                        title: Strings.old_password, require: true),
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
                      child: TextFormField(
                        controller: oldPwdController,
                        maxLines: 1,
                        maxLength: 30,
                        style: const TextStyle(fontSize: AppTheme.medium),
                        decoration: InputDecoration(
                          hintText: Strings.old_password,
                          alignLabelWithHint: true,
                          contentPadding: const EdgeInsets.all(
                              Constant.TEXT_FIELD_CONTENT_PADDING),
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
                          return null;
                        },
                      ),
                    ),
                  ),*/
                  const SizedBox(height: Constant.MEDIUM_PADDING),
                  Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SMALL_PADDING,
                        right: Constant.SMALL_PADDING),
                    child: InputTitleRequire(
                        title: Strings.new_password, require: true),
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
                        child: TextFormField(
                          controller: newPwdController,
                          maxLines: 1,
                          maxLength: 30,
                          style: const TextStyle(fontSize: AppTheme.medium),
                          decoration: InputDecoration(
                            hintText: Strings.new_password,
                            alignLabelWithHint: true,
                            contentPadding: const EdgeInsets.all(
                                Constant.TEXT_FIELD_CONTENT_PADDING),
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
                            return null;
                          },
                        )),
                  ),
                  const SizedBox(height: Constant.MEDIUM_PADDING * 2),
                  Row(
                    children: [
                      Expanded(
                        child: InkWell(
                          onTap: () {
                            // if (oldPwdController.text.isNullOrEmpty()) {
                            //   Utils.showSnackbar(
                            //       Strings.ERROR,
                            //       Strings.please_enter_old_password,
                            //       AppTheme.colorWhite,
                            //       AppTheme.colorRed);
                            //   return;
                            // }

                            if (newPwdController.text.isNullOrEmpty()) {
                              Utils.showSnackbar(
                                  Strings.ERROR,
                                  Strings.please_enter_new_password,
                                  AppTheme.colorWhite,
                                  AppTheme.colorRed);
                              return;
                            }
                            widget.changeCustomerPasswordReq.password =
                                oldPwdController.text;
                            widget.changeCustomerPasswordReq.newpassword =
                                newPwdController.text;
                            widget.changeCustomerPasswordReq.selfcarepwd =
                                newPwdController.text;
                            widget.changePasswordBtnAction
                                .changePasswordBtnAction(
                              identifier: Strings.submit,
                              changeCustomerPasswordReq:
                                  widget.changeCustomerPasswordReq,
                            );
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
                            oldPwdController.clear();
                            newPwdController.clear();
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
                              title: Strings.clear,
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
        ),
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

abstract class ChangePasswordBtnAction {
  void changePasswordBtnAction(
      {String identifier, ChangeCustomerPasswordReq changeCustomerPasswordReq});
}
