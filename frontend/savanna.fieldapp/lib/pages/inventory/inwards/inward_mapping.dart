import 'dart:developer';

import 'package:savbill/pages/inventory/inwards/inward_mapping_controller.dart';
import 'package:savbill/pages/inventory/inwards/inward_mapping_item.dart';
import 'package:savbill/pages/inventory/inwards/inward_mapping_item_new.dart';
import 'package:savbill/pages/inventory/module/request/save_manual_mac_serial_req.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class InwardMappingDetail extends StatefulWidget {
  @override
  _InwardMappingDetailState createState() => _InwardMappingDetailState();
}

class _InwardMappingDetailState extends State<InwardMappingDetail> {
  final viewInwardMappingController = Get.put(ViewInwardMappingController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: viewInwardMappingController.changeData);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ViewInwardMappingController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: viewInwardMappingController.isLoading),
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
        padding: const EdgeInsets.only(
            left: Constant.SCREEN_PADDING, right: Constant.SCREEN_PADDING),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            const SizedBox(height: Constant.MEDIUM_PADDING),
            viewInwardMappingController.from.equalsIgnoreCase(Strings.edit)
                ? SizedBox.shrink()
                : Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Expanded(
                            flex: 2,
                            child: Padding(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: Constant.SCREEN_PADDING),
                              child: CoustomTextField(
                                  labelText: Strings.search_your_text_here,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      viewInwardMappingController
                                          .searchController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.SMALL_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onChanged: (value) {
                                    viewInwardMappingController
                                        .searchData(value);
                                  },
                                  onTextValidator: (String? value) {
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ),
                          Expanded(
                              flex: 1,
                              child: Padding(
                                padding: const EdgeInsets.symmetric(
                                    horizontal: Constant.SCREEN_PADDING),
                                child: SimpleButton(
                                  onTap: () {
                                    viewInwardMappingController.clearData();
                                  },
                                  radius: 8,
                                  height: Constant.APPBAR_ITEM_H,
                                  bgColors: AppTheme.colorBlack,
                                  borderColors: AppTheme.colorPrimary,
                                  child: CustomText(
                                    title: Strings.clear,
                                    colors: AppTheme.colorWhite,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w400,
                                  ),
                                ),
                              ))
                        ],
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                    ],
                  ),
            viewInwardMappingController.from.equalsIgnoreCase(Strings.view)
                ? Container()
                : Form(
                    key: viewInwardMappingController.addInwardMapFormKey,
                    autovalidateMode:
                        viewInwardMappingController.autoValidateMode,
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING
                          ),
                          viewInwardMappingController.showMacAddress
                              ? Row(
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                  children: [
                                      Flexible(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.mac_address,
                                            require: true),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Flexible(
                                        flex: 2,
                                        child: CoustomTextField(
                                            labelText: Strings.mac_address,
                                            hintColor: AppTheme.colorIconGrey,
                                            textEditingController:
                                                viewInwardMappingController
                                                    .macAddController,
                                            borderEnableColors:
                                                AppTheme.colorIconGrey,
                                            borderFocusColors:
                                                AppTheme.colorIconGrey,
                                            textColor: AppTheme.colorBlack,
                                            keyboardType: TextInputType.text,
                                            fontSize: AppTheme.small,
                                            textInputAction:
                                                TextInputAction.next,
                                            fontWeight: FontWeight.w500,
                                            contentPadding:
                                                const EdgeInsets.symmetric(
                                                    horizontal:
                                                        Constant.MEDIUM_PADDING,
                                                    vertical: Constant
                                                        .MEDIUM_PADDING),
                                            borderCorner:
                                                Constant.BTN_ROUNDED_CORNER,
                                            onTextValidator: (String? value) {
                                              if (value!.isEmpty) {
                                                return Strings
                                                    .please_enter_mac_address;
                                              }
                                              return null;
                                            },
                                            onTextFiledOnTap: () {},
                                            readOnly: false),
                                      ),
                                    ])
                              : Container(),
                          viewInwardMappingController.showMacAddress
                              ? const SizedBox(height: Constant.MEDIUM_PADDING)
                              : Container(),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.serial_no, require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.serial_no,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          viewInwardMappingController
                                              .serialNoController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
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
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (value!.isEmpty) {
                                          return Strings.please_enter_serial_no;
                                        }
                                        return null;
                                      },
                                      onTextFiledOnTap: () {},
                                      readOnly: false),
                                ),
                              ]),
                          const SizedBox(height: Constant.LARGE_PADDING),
                          Align(
                            alignment: Alignment.centerRight,
                            child: InkWell(
                              onTap: () {
                                validateForm();
                              },
                              child: CustomText(
                                title: Strings.plus_add_mapping,
                                colors: AppTheme.colorPrimary,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                        ]),
                  ),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            IntrinsicHeight(
              child: Row(
                children: [
                  viewInwardMappingController.from
                          .equalsIgnoreCase(Strings.edit)
                      ? Container()
                      : Expanded(
                          child: CustomText(
                            title: Strings.item_id_new,
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.center,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            maxLines: 2,
                          ),
                        ),
                  viewInwardMappingController.showMacAddress
                      ? Expanded(
                          flex: 1,
                          child: CustomText(
                            title: Strings.mac_address_new,
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.center,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            maxLines: 2,
                          ),
                        )
                      : const SizedBox.shrink(),
                  Expanded(
                    flex: 1,
                    child: CustomText(
                      title: Strings.serial_no_new,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.center,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                      maxLines: 2,
                    ),
                  ),
                  viewInwardMappingController.from
                          .equalsIgnoreCase(Strings.edit)
                      ? Container()
                      : Expanded(
                          child: CustomText(
                            title: Strings.item_type_new,
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.center,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            maxLines: 2,
                          ),
                        ),
                  Expanded(
                    child: CustomText(
                      title: Strings.action,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.center,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                      maxLines: 2,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
            viewInwardMappingController.from.equalsIgnoreCase(Strings.edit)
                ? Container()
                : Expanded(
                    child: (viewInwardMappingController.inwardMacMapList !=
                                null &&
                            viewInwardMappingController
                                .inwardMacMapList!.isNotEmpty)
                        ? ListView.builder(
                            controller: viewInwardMappingController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewInwardMappingController
                                .inwardMacMapList!.length + 1,
                            itemBuilder: (BuildContext context, int index) {
                              if (index ==
                                  viewInwardMappingController.inwardMacMapList?.length) {
                                if (viewInwardMappingController.isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                          AlwaysStoppedAnimation<Color>(
                                              AppTheme.colorProgress),
                                          backgroundColor:
                                          AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                InwardMacSerialDataList item =
                                viewInwardMappingController
                                    .inwardMacMapList![index];

                                return Container(
                                  margin: const EdgeInsets.only(
                                      top: Constant.VERY_SMALL_PADDING - 2),
                                  child: InwardMapItem(
                                    item: item,
                                    showMacAddress: viewInwardMappingController
                                        .showMacAddress,
                                    from: viewInwardMappingController.from,
                                    onTapDelete: () {
                                      showDialog(
                                        context: context,
                                        builder: (BuildContext context) {
                                          return AlertDialogHelper(
                                              title: Strings.app_name,
                                              message: Strings.msg_delete,
                                              positiveBtnText: Strings.ok,
                                              negativeBtnText: Strings.cancel,
                                              positiveBtnClick: () {
                                                Get.back();
                                                // viewInwardMappingController
                                                //     .updateManualMacSerialCall(
                                                //         item, index);
                                                viewInwardMappingController
                                                    .deleteInwardsMapData(
                                                    item, index);
                                              },
                                              negativeBtnClick: () {
                                                Get.back();
                                              });
                                        },
                                      );
                                    },
                                  ),
                                );
                              }
                            })
                        : noDataFound(),
                  ),
            viewInwardMappingController.from.equalsIgnoreCase(Strings.view)
                ? Container()
                : Expanded(
                    child: (viewInwardMappingController.macSerialListReq !=
                                null &&
                            viewInwardMappingController
                                .macSerialListReq!.isNotEmpty)
                        ? ListView.builder(
                            controller: viewInwardMappingController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewInwardMappingController
                                .macSerialListReq.length,
                            itemBuilder: (BuildContext context, int index) {
                              if (index ==
                                  viewInwardMappingController
                                      .macSerialListReq?.length) {
                                if (viewInwardMappingController
                                    .isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                                  AppTheme.colorProgress),
                                          backgroundColor:
                                              AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                MacSerialListDTOList item1 =
                                    viewInwardMappingController
                                        .macSerialListReq[index];

                                return Container(
                                  margin: const EdgeInsets.only(
                                      top: Constant.VERY_SMALL_PADDING - 2),
                                  child: InwardMappingItemNew(
                                    item: item1,
                                    showMacAddress: viewInwardMappingController
                                        .showMacAddress,
                                    from: viewInwardMappingController.from,
                                    onTapDelete: () {
                                      showDialog(
                                        context: context,
                                        builder: (BuildContext context) {
                                          return AlertDialogHelper(
                                              title: Strings.app_name,
                                              message: Strings.msg_delete,
                                              positiveBtnText: Strings.ok,
                                              negativeBtnText: Strings.cancel,
                                              positiveBtnClick: () {
                                                Get.back();
                                                viewInwardMappingController
                                                    .macSerialListReq
                                                    .removeAt(index);
                                              },
                                              negativeBtnClick: () {
                                                Get.back();
                                              });
                                        },
                                      );
                                    },
                                  ),
                                );
                              }
                            })
                        : noDataFound(),
                  ),
            viewInwardMappingController.from.equalsIgnoreCase(Strings.view)
                ? SizedBox()
                : Center(
                    child: SimpleButton(
                        child: CustomText(title: Strings.submit),
                        onTap: () {
                          viewInwardMappingController.saveManualMacSerialCall();
                        }),
                  ),
          ],
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.inward_mac_mapping, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (viewInwardMappingController.addInwardMapFormKey.currentState!
        .validate()) {
      if (viewInwardMappingController.inwardsDetail != null) {
        int qty = viewInwardMappingController.inwardsDetail!.inTransitQty!;
        int itemSize = 0;
        if (viewInwardMappingController.inwardMacMapList != null) {
          itemSize = viewInwardMappingController.inwardMacMapList!.length;
        }
        if (itemSize >= qty) {
          Utils.showSnackbar(Strings.INFO, "Total entered Mac/Serial is greater then inward In transit qty.",
              AppTheme.colorWhite, AppTheme.colorBlueRView);
          return;
        }
      }

      viewInwardMappingController.macSerialListReq.add(MacSerialListDTOList(
          macAddress: viewInwardMappingController.macAddController.text,
          serialNumber: viewInwardMappingController.serialNoController.text));

      viewInwardMappingController.update();
      viewInwardMappingController.macAddController.clear();
      viewInwardMappingController.serialNoController.clear();
    } else {
      viewInwardMappingController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      viewInwardMappingController.update();
    }
  }
}
