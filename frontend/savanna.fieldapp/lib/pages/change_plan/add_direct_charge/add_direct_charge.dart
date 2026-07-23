import 'package:savbill/pages/change_plan/add_direct_charge/add_direct_charge_controller.dart';
import 'package:savbill/pages/change_plan/add_direct_charge/add_direct_charge_list_item.dart';
import 'package:savbill/pages/customer_charge/request/create_cust_charge_req.dart';
import 'package:savbill/pages/shift_location/response/charge_by_type_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AddDirectCharge extends StatefulWidget {
  @override
  _AddDirectChargeState createState() => _AddDirectChargeState();
}

class _AddDirectChargeState extends State<AddDirectCharge> {
  final addDirectChargeController = Get.put(AddDirectChargeController());
  final addDirectChargeFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<AddDirectChargeController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: addDirectChargeController.isLoading),
        ]);
      }), /**/
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SCREEN_PADDING,
                        right: Constant.SCREEN_PADDING),
                    child: Form(
                      key: addDirectChargeFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                              height: Constant.SCREEN_PADDING +
                                  Constant.SMALL_PADDING,
                            ),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.charge,
                                            require: true),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.actual_price,
                                            require: true),
                                      ),
                                    ]),
                                const SizedBox(
                                  height: Constant.VERY_SMALL_PADDING,
                                ),
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: DropdownButtonHideUnderline(
                                          child: DropdownButtonFormField(
                                            icon: SvgPicture.asset(
                                              downArrowSvg,
                                              height:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              width:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              color: AppTheme.colorBlack,
                                              fit: BoxFit.fill,
                                            ),
                                            decoration: Utils.ddlDecoration(),
                                            hint: Align(
                                                alignment: Alignment.centerLeft,
                                                child: Text(Strings.charge,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme
                                                          .colorIconGrey,
                                                      fontFamily:
                                                          AppTheme.appFontName,
                                                    ))),
                                            style: AppTheme.dropdownTextStyle,
                                            isExpanded: true,
                                            isDense: true,
                                            value: addDirectChargeController
                                                .selectedChargeList,
                                            items: addDirectChargeController
                                                .chargeList!
                                                .map((Chargelist value) {
                                              return DropdownMenuItem<
                                                  Chargelist>(
                                                value: value,
                                                child: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: CustomText(
                                                    title: value.name!,
                                                    colors: AppTheme.colorBlack,
                                                    textAlign: TextAlign.start,
                                                    fontSize: AppTheme.small,
                                                    fontWeight: FontWeight.w500,
                                                  ), //Text(value.desig!),
                                                ),
                                              );
                                            }).toList(),
                                            onChanged: (value) {
                                              addDirectChargeController
                                                      .selectedChargeList =
                                                  value as Chargelist?;
                                              addDirectChargeController
                                                      .actualPriceController
                                                      .text =
                                                  addDirectChargeController
                                                      .selectedChargeList!.price
                                                      .toString();
                                              addDirectChargeController
                                                  .selectCharge(
                                                      addDirectChargeController
                                                          .selectedChargeList!
                                                          .id);

                                              addDirectChargeController
                                                  .update();
                                            },
                                            validator: (value) {
                                              // need to add validation
                                              return null;
                                            },
                                          ),
                                        ),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.actual_price,
                                              hintColor: AppTheme.colorIconGrey,
                                              fillColor:
                                                  AppTheme.colorLightGrey,
                                              textEditingController:
                                                  addDirectChargeController
                                                      .actualPriceController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType:
                                                  TextInputType.number,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.next,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING,
                                                      vertical: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator: (String? value) {
                                                return null;
                                              },
                                              onTextFiledOnTap: () {},
                                              readOnly: true)),
                                    ]),
                              ],
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                InputTitleRequire(
                                    title: Strings.plan, require: true),
                                const SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                DropdownButtonHideUnderline(
                                  child: DropdownButtonFormField(
                                    icon: SvgPicture.asset(
                                      downArrowSvg,
                                      height: Constant.DROP_DOWN_ARROW_W_H,
                                      width: Constant.DROP_DOWN_ARROW_W_H,
                                      color: AppTheme.colorBlack,
                                      fit: BoxFit.fill,
                                    ),
                                    decoration: Utils.ddlDecoration(),
                                    hint: Align(
                                        alignment: Alignment.centerLeft,
                                        child: Text(Strings.plan,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addDirectChargeController
                                        .selectPlansForCharge,
                                    items: addDirectChargeController
                                        .plansForCharge
                                        .map((Map<String, dynamic> value) {
                                      return DropdownMenuItem<
                                          Map<String, dynamic>>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: "${value['planName']}",
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (dynamic value) {
                                      addDirectChargeController
                                          .selectPlansForCharge = value;
                                      addDirectChargeController.update();
                                    },
                                    validator: (value) {
                                      // need to add validation
                                      return null;
                                    },
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Column(
                              children: [
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.new_price,
                                            require: false),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.discount,
                                            require: false),
                                      ),
                                    ]),
                                const SizedBox(
                                  height: Constant.VERY_SMALL_PADDING,
                                ),
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Flexible(
                                          flex: 2,
                                          child: CoustomTextField(
                                              labelText: Strings.new_price,
                                              hintColor: AppTheme.colorIconGrey,
                                              textEditingController:
                                                  addDirectChargeController
                                                      .newPriceController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType:
                                                  TextInputType.number,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.next,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING,
                                                      vertical: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings
                                                      .please_enter_new_price;
                                                }
                                                return null;
                                              },
                                              onTextFiledOnTap: () {},
                                              readOnly: false)),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Flexible(
                                          flex: 2,
                                          child: CoustomTextField(
                                              labelText: Strings.enter_discount,
                                              hintColor: AppTheme.colorIconGrey,
                                              fillColor:
                                                  AppTheme.colorLightGrey,
                                              textEditingController:
                                                  addDirectChargeController
                                                      .discountController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType:
                                                  TextInputType.number,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.next,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING,
                                                      vertical: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator: (String? value) {
                                                return null;
                                              },
                                              onTextFiledOnTap: () {},
                                              readOnly: true)),
                                    ]),
                              ],
                            ),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Align(
                              alignment: Alignment.centerRight,
                              child: InkWell(
                                onTap: () {
                                  String newPrice = addDirectChargeController
                                      .newPriceController.text;
                                  if (addDirectChargeController
                                              .selectedChargeList ==
                                          null ||
                                      addDirectChargeController
                                              .selectPlansForCharge ==
                                          null ||
                                      newPrice.isEmpty) {
                                    Utils.showSnackbar(
                                        Strings.ERROR,
                                        "Please fill-up data!",
                                        AppTheme.colorWhite,
                                        AppTheme.colorRed);
                                    return;
                                  }
                                  double priceNew = double.parse(newPrice);
                                  double price = double.parse(
                                      addDirectChargeController
                                          .selectedChargeList!.price!
                                          .toString());
                                  if (price > priceNew) {
                                    Utils.showSnackbar(
                                        Strings.ERROR,
                                        Strings
                                            .new_price_must_not_actual_charge_price,
                                        AppTheme.colorWhite,
                                        AppTheme.colorRed);
                                    return;
                                  }

                                  addDirectChargeController.custChargeDetails!
                                      .add(CustChargeDetailsPojoList(
                                    type: "One-time",
                                    chargeid: addDirectChargeController
                                        .selectedChargeList!.id,
                                    validity: addDirectChargeController
                                        .selectPlansForCharge['validity'],
                                    price: double.tryParse(
                                        addDirectChargeController
                                            .newPriceController.text
                                            .toString()),
                                    actualprice: double.parse(
                                        addDirectChargeController
                                            .actualPriceController.text
                                            .toString()),
                                    chargeDate: addDirectChargeController
                                        .apiDateFormat
                                        .format(DateTime.now()),
                                    planid: addDirectChargeController
                                        .selectPlansForCharge!['planId'],
                                    planName: addDirectChargeController
                                        .selectPlansForCharge!['planName'],
                                    unitsOfValidity: addDirectChargeController
                                            .selectPlansForCharge![
                                        'unitsOfValidity'],
                                    billingCycle: addDirectChargeController
                                                    .selectPlansForCharge![
                                                'type'] ==
                                            "Recurring"
                                        ? 1
                                        : "",
                                    discount: double.tryParse(
                                        addDirectChargeController
                                            .discountController.text
                                            .toString()),
                                    connectionNo: addDirectChargeController
                                        .selectPlansForCharge!['connection_no'],
                                    chargeName: addDirectChargeController
                                        .selectedChargeList!.name,
                                      custId:addDirectChargeController
                                          .customerDetail!.id

                                  ));

                                  addDirectChargeController.selectedChargeList =
                                      null;
                                  addDirectChargeController
                                      .selectPlansForCharge = null;
                                  addDirectChargeController
                                      .actualPriceController
                                      .clear();
                                  addDirectChargeController.staticIpController
                                      .clear();
                                  addDirectChargeController.discountController
                                      .clear();
                                  addDirectChargeController.newPriceController
                                      .clear();
                                  addDirectChargeController.update();
                                },
                                child: CustomText(
                                  title: "+ Add Charge",
                                  colors: AppTheme.colorPrimary,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                            const SizedBox(height: Constant.SMALL_PADDING),
                            (addDirectChargeController.custChargeDetails !=
                                        null &&
                                    addDirectChargeController
                                        .custChargeDetails!.isNotEmpty)
                                ? ListView.builder(
                                    physics:
                                        const NeverScrollableScrollPhysics(),
                                    shrinkWrap: true,
                                    itemCount: addDirectChargeController
                                        .custChargeDetails!.length,
                                    itemBuilder:
                                        (BuildContext context, int index) {
                                      CustChargeDetailsPojoList item =
                                          addDirectChargeController
                                              .custChargeDetails![index];
                                      return Container(
                                          margin: const EdgeInsets.only(
                                              top: Constant.VERY_SMALL_PADDING),
                                          child: AddDirectChargeListItem(
                                              item: item,
                                              index: index,
                                              onDeleteTap: () {
                                                showDialog(
                                                  context: context,
                                                  builder:
                                                      (BuildContext context) {
                                                    return AlertDialogHelper(
                                                        title: Strings.app_name,
                                                        message:
                                                            Strings.msg_delete,
                                                        positiveBtnText:
                                                            Strings.ok,
                                                        negativeBtnText:
                                                            Strings.cancel,
                                                        positiveBtnClick: () {
                                                          Get.back();
                                                          addDirectChargeController
                                                              .custChargeDetails!
                                                              .remove(item);
                                                          addDirectChargeController
                                                              .update();
                                                        },
                                                        negativeBtnClick: () {
                                                          Get.back();
                                                        });
                                                  },
                                                );
                                              }));
                                    })
                                : Container(),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            addDirectChargeController.staticIPAddress == true
                                ? Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      InputTitleRequire(
                                          title: Strings.static_ip,
                                          require: true),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings.enter_static_ip,
                                          hintColor: AppTheme.colorIconGrey,
                                          textEditingController:
                                              addDirectChargeController
                                                  .staticIpController,
                                          borderEnableColors:
                                              AppTheme.colorIconGrey,
                                          borderFocusColors:
                                              AppTheme.colorIconGrey,
                                          textColor: AppTheme.colorBlack,
                                          keyboardType: TextInputType.text,
                                          fontSize: AppTheme.small,
                                          textInputAction: TextInputAction.next,
                                          fontWeight: FontWeight.w500,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.MEDIUM_PADDING,
                                                  vertical:
                                                      Constant.MEDIUM_PADDING),
                                          borderCorner:
                                              Constant.BTN_ROUNDED_CORNER,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings.enter_static_ip;
                                            }
                                            return null;
                                          },
                                          onTextFiledOnTap: () {},
                                          readOnly: false),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                    ],
                                  )
                                : const SizedBox.shrink(),
                          ]),
                    ),
                  ),
                ),
              ),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        validateForm();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.save,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(
        addDirectChargeController.title,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (addDirectChargeController.custChargeDetails != null &&
        addDirectChargeController.custChargeDetails!.isNotEmpty) {
      Get.back(result: addDirectChargeController.custChargeDetails);
    } else {
      Utils.showSnackbar(
          Strings.INFO,
          "Please add at-lease one charge detail item!",
          AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }
  }
}
