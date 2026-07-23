import 'package:savbill/pages/inventory/assigned_inventories/forward_inventory_controller.dart';
import 'package:savbill/pages/inventory/module/response/all_ware_house_res.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_request_list_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_new_list_res.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';
import '../../../util/strings.dart';
import '../../../widgets/dynamic_appbar.dart';
import '../../../widgets/input_textfield.dart';
import '../../../widgets/progress_bar.dart';
import '../../../widgets/title_widge.dart';

class ForwardInventoryRequest extends StatefulWidget {
  AssignedInventoryDataList? itemList;

  ForwardInventoryRequest({super.key, required this.itemList});

  @override
  _ForwardInventoryRequestState createState() =>
      _ForwardInventoryRequestState();
}

class _ForwardInventoryRequestState extends State<ForwardInventoryRequest> {
  final addEditProductFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    final forwardInventoryController =
        Get.put(ForwardInventoryController(widget.itemList));
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ForwardInventoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(forwardInventoryController),
            body: _body(forwardInventoryController),
          ),
          ProgressBar(isLoader: forwardInventoryController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(ForwardInventoryController forwardInventoryController) {
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
                      key: addEditProductFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        mainAxisSize: MainAxisSize.max,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          Stack(
                            children: [
                              Container(
                                padding: const EdgeInsets.all(10),
                                margin:
                                    const EdgeInsets.only(top: 10, bottom: 10),
                                decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(5),
                                    border: Border.all(
                                        width: 1.0,
                                        style: BorderStyle.solid,
                                        color: AppTheme.colorIconGrey)),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.inventory_request_name,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_reason,
                                        textEditingController:
                                            forwardInventoryController
                                                .requestInventoryNameController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: true),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.on_behalf_of,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_reason,
                                        textEditingController:
                                            forwardInventoryController
                                                .onBehalfOfController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: true),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.requester,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_reason,
                                        textEditingController:
                                            forwardInventoryController
                                                .requesterController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: true),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.request_to,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
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
                                          child: Text(
                                            Strings.select_requester,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: forwardInventoryController
                                            .wareHousesRes,
                                        items: forwardInventoryController
                                            .allWareHouseList
                                            .map((WareHouseDataList value) {
                                          return DropdownMenuItem<
                                              WareHouseDataList>(
                                            value: value,
                                            child: CustomText(
                                              title: value.name!,
                                              fontSize: AppTheme.medium,
                                              colors: AppTheme.colorBlack,
                                              fontWeight: FontWeight.normal,
                                            ),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          forwardInventoryController
                                                  .wareHousesRes =
                                              value as WareHouseDataList?;
                                          forwardInventoryController
                                              .forwardToReqId = value!.id;
                                          forwardInventoryController.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              forwardInventoryController
                                                      .wareHousesRes ==
                                                  null) {
                                            return Strings
                                                .please_select_ware_house;
                                          }
                                          // return null;
                                        },
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.reason, require: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_reason,
                                        textEditingController:
                                            forwardInventoryController
                                                .reasonController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: true),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.remarks, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_remarks,
                                        textEditingController:
                                            forwardInventoryController
                                                .remarkReasonController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings.enter_remarks;
                                          }
                                          return null;
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                  ],
                                ),
                              ),
                              Positioned(
                                left: 30,
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(3),
                                      color: Colors.white),
                                  child: InputTitleRequire(
                                      title: Strings.basic_details,
                                      require: false),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        validateForm(forwardInventoryController);
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.submit,
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

  validateForm(ForwardInventoryController controller) {
    if (addEditProductFormKey.currentState!.validate()) {
      controller.forwardInventoryRequestApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar(ForwardInventoryController forwardInventoryController) {
    return DynamicAppBar(
        forwardInventoryController.from.equalsIgnoreCase(Strings.add)
            ? Strings.forward_inventory_request
            : Strings.add_product,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
