import 'package:savbill/pages/customer_caf/customer_caf_invoice/customer_caf_invoice_controller.dart';
import 'package:savbill/pages/customer_caf/response/customer_caf_invoice_details_res.dart';
import 'package:savbill/pages/customer_invoice/response/payment_config_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:country_picker/country_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

class CustomerCAFMobileNumberDialog extends StatefulWidget {
  final String title;
  final TextEditingController mobileController;
  Invoicesearchlist? plan;
  ActivePaymentConfig? selectedItem;


  CustomerCAFMobileNumberDialog({
    Key? key,
    required this.title,
    required this.mobileController,
    required this.plan,
    required this.selectedItem,
  }) : super(key: key);

  @override
  State<CustomerCAFMobileNumberDialog> createState() => _CustomDialogForInvoicePaymentState();
}

class _CustomDialogForInvoicePaymentState extends State<CustomerCAFMobileNumberDialog> {
  final customerInvoiceController = Get.find<CustomerCafInvoiceController>();
  final _formKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  String countryCode = Strings.defaultCountryCode;

  @override
  Widget build(BuildContext context) {
    countryCode = customerInvoiceController.customerDetail!.countryCode!;
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.SMALL_PADDING),
      ),
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              decoration: BoxDecoration(
                color: AppTheme.colorPrimary,
                borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(Constant.SMALL_PADDING),
                  topRight: Radius.circular(Constant.SMALL_PADDING),
                ),
              ),
              padding: const EdgeInsets.all(Constant.SMALL_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  CustomText(
                    title: widget.title,
                    fontSize: AppTheme.large,
                    fontWeight: FontWeight.bold,
                  ),
                  InkWell(
                    onTap: (){
                      Get.back();
                    },
                    child: Icon(Icons.close, color: AppTheme.colorWhite),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding:
                    const EdgeInsets.only(bottom: Constant.MEDIUM_PADDING),
                    child: CustomText(
                      title: Strings.mobile_number,
                      colors: AppTheme.colorBlack,
                    ),
                  ),
                  CoustomTextField(
                    labelText: Strings.enter_mobile_no,
                    hintColor: AppTheme.colorIconGrey,
                    inputFormatters: [
                      FilteringTextInputFormatter.deny(
                        RegExp(r'^0+'), // Prevents leading zeros
                      ),
                    ],
                    textEditingController: widget.mobileController,
                    maxLength: 9,
                    prefixIcon: InkWell(
                      onTap: _showCountryCodeDialog,
                      child: Container(
                        decoration: BoxDecoration(
                          color: AppTheme.colorWhite,
                          border: Border(
                            right: BorderSide(
                                width: 0.5, color: AppTheme.colorIconGrey),
                          ),
                        ),
                        margin: const EdgeInsets.only(
                            left: 2.0, right: 8, top: 2, bottom: 2),
                        width: 50.0,
                        child: Center(
                          child: CustomText(
                            title: countryCode,
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ),
                    ),
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    keyboardType: TextInputType.number,
                    fontSize: AppTheme.small,
                    textInputAction: TextInputAction.done,
                    fontWeight: FontWeight.w500,
                    contentPadding: const EdgeInsets.symmetric(
                        horizontal: Constant.MEDIUM_PADDING,
                        vertical: Constant.MEDIUM_PADDING),
                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                    onTextValidator: (String? value) {
                      if (value == null || value.isEmpty) {
                        return Strings.enter_mobile_no;
                      } else if (value.length != 9) {
                        return Strings.mobile_number_must_be_ten_digit;
                      }
                      return null;
                    },
                    readOnly: false,
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
                    onTap: (){
                      if(_formKey.currentState!.validate()){
                        Get.back();
                        if(widget.selectedItem!.paymentConfigName!.equalsIgnoreCase("AIRTEL")){
                          customerInvoiceController.airtelPayApiCall(plan: widget.plan, selectedData: widget.selectedItem, context: Get.context,mobileNumber: widget.mobileController.text);
                        }else if(widget.selectedItem!.paymentConfigName!.equalsIgnoreCase("MoMo Pay")){
                          customerInvoiceController.momoPayRequestApiCall(plan: widget.plan, selectedData: widget.selectedItem, context: Get.context,mobileNumber: widget.mobileController.text,countryCode: countryCode);
                        }
                      }else{
                        autoValidateMode = AutovalidateMode.onUserInteraction;
                      }
                    },
                    radius: Constant.BTN_HEIGHT_M,
                    height: Constant.BTN_HEIGHT_M,
                    borderColors: AppTheme.colorPrimary,
                    bgColors: AppTheme.colorPrimary,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
                      child: CustomText(
                        title: Strings.buy,
                        fontSize: AppTheme.medium,
                      ),
                    ),
                  ),

                  SizedBox(width: Constant.MEDIUM_PADDING),

                  SimpleButton(
                    onTap: (){
                      Get.back();
                    },
                    radius: Constant.BTN_HEIGHT_M,
                    height: Constant.BTN_HEIGHT_M,
                    bgColors: AppTheme.colorDisableGray,
                    borderColors: AppTheme.colorWhite,
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

  void _showCountryCodeDialog() async {
    showCountryPicker(
      context: context,
      showPhoneCode: true,
      onSelect: (Country country) {
        setState(() {
          countryCode = "+${country.phoneCode}";
        });
      },
      countryListTheme: CountryListThemeData(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(Constant.BTN_ROUNDED_CORNER_M),
          topRight: Radius.circular(Constant.BTN_ROUNDED_CORNER_M),
        ),
        inputDecoration: InputDecoration(
          hintText: Strings.search,
          prefixIcon: Icon(
            Icons.search,
            color: AppTheme.colorIconGrey,
          ),
          border: OutlineInputBorder(
            borderSide: BorderSide(
              color: AppTheme.colorIconGrey,
            ),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(
              color: AppTheme.colorIconGrey,
              width: 1.0,
            ),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(color: AppTheme.colorIconGrey, width: 1.0),
          ),
        ),
      ),
    );
  }
}