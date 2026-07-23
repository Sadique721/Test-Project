import 'package:savbill/pages/login/login_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final loginController = Get.put(LoginController());
  final loginFormKey = GlobalKey<FormState>();

  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: GetBuilder<LoginController>(builder: (controller) {
        return Stack(children: <Widget>[
          Container(
            color: AppTheme.colorPrimary,
            width: MediaQuery.of(context).size.width,
            height: MediaQuery.of(context).size.height,
            // child: Image.asset(
            //   loginBg,
            //   color: AppTheme.colorWhite,
            //   fit: BoxFit.cover,
            // ),
          ),
          Scaffold(
            backgroundColor: AppTheme.colorTransparent,
            body: SafeArea(
              child: _body(),
            ),
            appBar: NoAppBar(),
          ),
          ProgressBar(isLoader: loginController.isLoading)
        ]);
      }),
    );
  }

  _body() {
    double statusBarHeight = MediaQuery.of(context).padding.top;
    return SingleChildScrollView(
      child: Container(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height - statusBarHeight,
        padding: const EdgeInsets.symmetric(
          horizontal: Constant.SCREEN_PADDING + Constant.VERY_SMALL_PADDING,
        ),
        child: Column(children: [
          Expanded(
            child: SingleChildScrollView(
              child: Form(
                key: loginFormKey,
                autovalidateMode: autoValidateMode,
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Container(
                        alignment: Alignment.topCenter,
                        width: MediaQuery.of(context).size.width,
                        padding: const EdgeInsets.only(
                          top: Constant.VERY_EXTRA_LARGE_PADDING * 1.5,
                        ),
                        child: Image.asset(
                          savbillLogo,
                          // color: AppTheme.colorWhite,
                          width: MediaQuery.of(context).size.width -
                              (Constant.VERY_EXTRA_LARGE_PADDING * 4),
                          fit: BoxFit.contain,
                        ),
                      ),
                      const SizedBox(
                        height: Constant.VERY_EXTRA_LARGE_PADDING + 10,
                      ),
                      CoustomTextField(
                          labelText: Strings.username,
                          textEditingController:
                              loginController.emailController,
                          keyboardType: TextInputType.text,
                          borderEnableColors: AppTheme.colorPrimary,
                          textInputAction: TextInputAction.next,
                          onTextValidator: (String? value) {
                            if (value!.isEmpty) {
                              return Strings.please_enter_username;
                            }

                            return null;
                          },
                          borderCorner: Constant.INPUT_ROUNDED_CORNER,
                          contentPadding: const EdgeInsets.symmetric(
                              horizontal: Constant.LARGE_PADDING),
                          readOnly: false),
                      const SizedBox(
                        height: Constant.EXTRA_LARGE_PADDING,
                      ),
                      CoustomTextField(
                          labelText: Strings.password,
                          textEditingController:
                              loginController.passwordController,
                          keyboardType: TextInputType.text,
                          borderEnableColors: AppTheme.colorPrimary,
                          textInputAction: TextInputAction.done,
                          onTextValidator: (String? value) {
                            if (value!.isEmpty) {
                              return Strings.please_enter_password;
                            }
                            return null;
                          },
                          suffixIcon: IconButton(
                            onPressed: (){
                              loginController.isVisiblePassword= !loginController.isVisiblePassword!;
                              loginController.update();
                            },
                            icon: Icon(loginController.isVisiblePassword!
                                ? Icons.visibility
                                : Icons.visibility_off,color: AppTheme.colorPrimaryTheme,),
                          ),
                          borderCorner: Constant.INPUT_ROUNDED_CORNER,
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: Constant.LARGE_PADDING - 2,
                              horizontal: Constant.LARGE_PADDING),
                          readOnly: false,
                          obscureText: loginController.isVisiblePassword!),
                      const SizedBox(height: Constant.EXTRA_LARGE_PADDING),
                      // Align(
                      //   alignment: Alignment.topRight,
                      //   child: InkWell(
                      //     onTap: () {
                      //       Utils.showSnackbar(
                      //           Strings.SUCCESS,
                      //           Strings.under_development,
                      //           AppTheme.colorWhite,
                      //           AppTheme.colorGreen);
                      //     },
                      //     child: CustomText(
                      //       title: Strings.forgot_password,
                      //       colors: AppTheme.colorBlack,
                      //       fontSize: AppTheme.medium,
                      //       fontWeight: FontWeight.w500,
                      //     ),
                      //   ),
                      // ),
                      const SizedBox(
                        height: Constant.EXTRA_LARGE_PADDING,
                      ),
                      SimpleButton(
                        onTap: () {
                          validateForm();
                        },
                        radius: Constant.BTN_ROUNDED_CORNER,
                        height: Constant.BTN_HEIGHT,
                        bgColors: AppTheme.colorBlack,
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: Constant.BTN_LR_PADDING),
                        child: CustomText(
                          title: Strings.login.toUpperCase(),
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      const SizedBox(
                        height: Constant.EXTRA_LARGE_PADDING,
                      ),
                      // Row(
                      //   mainAxisAlignment: MainAxisAlignment.center,
                      //   crossAxisAlignment: CrossAxisAlignment.center,
                      //   children: [
                      //     const SizedBox(width: Constant.SMALL_PADDING),
                      //     CustomText(
                      //       title: Strings.new_member,
                      //       fontSize: AppTheme.medium,
                      //       fontWeight: FontWeight.w500,
                      //     ),
                      //     const SizedBox(width: Constant.VERY_SMALL_PADDING),
                      //     InkWell(
                      //       onTap: () {
                      //         Utils.showSnackbar(
                      //             Strings.SUCCESS,
                      //             Strings.under_development,
                      //             AppTheme.colorWhite,
                      //             AppTheme.colorGreen);
                      //       },
                      //       child: CustomText(
                      //         title: Strings.register_here,
                      //         fontSize: AppTheme.medium,
                      //         fontWeight: FontWeight.w500,
                      //         colors: AppTheme.colorBlack,
                      //       ),
                      //     )
                      //   ],
                      // ),
                    ]),
              ),
            ),
          ),
          CustomText(title: "${Strings.version} : ${loginController.buildVersion}"),
          SizedBox(height: Constant.MEDIUM_PADDING,)
        ]),
      ),
    );
  }

  validateForm() {
    if (loginFormKey.currentState!.validate()) {
      loginController.loginGeneratedApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }
}
