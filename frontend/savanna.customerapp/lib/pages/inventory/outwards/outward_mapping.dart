import 'dart:developer';
import 'package:savbill/pages/inventory/outwards/outward_mapping_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../module/response/inward_mac_serial_item_res.dart';
import 'outward_mapping_item.dart';

class OutwardMappingDetail extends StatefulWidget {
  @override
  _OutwardMappingDetailState createState() => _OutwardMappingDetailState();
}

class _OutwardMappingDetailState extends State<OutwardMappingDetail> {
  final viewOutwardMappingController = Get.put(ViewOutwardMappingController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: viewOutwardMappingController.changeData);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ViewOutwardMappingController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewOutwardMappingController.isLoading),
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
            const SizedBox(
              height: Constant.SCREEN_PADDING + Constant.SMALL_PADDING,
            ),
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
                            viewOutwardMappingController.searchController,
                        borderEnableColors: AppTheme.colorIconGrey,
                        borderFocusColors: AppTheme.colorIconGrey,
                        textColor: AppTheme.colorBlack,
                        keyboardType: TextInputType.text,
                        fontSize: AppTheme.small,
                        textInputAction: TextInputAction.next,
                        fontWeight: FontWeight.w500,
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: Constant.MEDIUM_PADDING,
                            vertical: Constant.MEDIUM_PADDING),
                        borderCorner: Constant.BTN_ROUNDED_CORNER,
                        onChanged: (value) {
                          viewOutwardMappingController.searchData(value);
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
                          viewOutwardMappingController.clearData();
                        },
                        radius: 5,
                        height: Constant.APPBAR_ITEM_H+5,
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
            Row(
              children: [
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: "${Strings.required_qty} : ${viewOutwardMappingController.outwardsDetail!.inTransitQty}",
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small+1,
                    fontWeight: FontWeight.w600,
                    maxLines: 2,
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: "${Strings.already_selected_qty} : ${viewOutwardMappingController.outwardsDetail!.selectedItems}",
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500,
                    maxLines: 2,
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: "${Strings.current_selected_qty} : ${"0"}",
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500,
                    maxLines: 2,
                  ),
                ),
              ],
            ),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Row(
              children: [
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: Strings.item_id_new,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500,
                    maxLines: 2,
                  ),
                ),
                Expanded(
                  flex: 2,
                  child: CustomText(
                    title: Strings.mac_address_new,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500,
                    maxLines: 2,
                  ),
                ),
                Expanded(
                  flex: 2,
                  child: CustomText(
                    title: Strings.serial_no_new,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500,
                    maxLines: 2,
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: Strings.item_type_new,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500,
                    maxLines: 2,
                  ),
                ),
              ],
            ),
            const SizedBox(height: Constant.SMALL_PADDING),

            Expanded(
                child: (viewOutwardMappingController.inwardMacMapList != null &&
                        viewOutwardMappingController
                            .inwardMacMapList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewOutwardMappingController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewOutwardMappingController
                                .inwardMacMapList!.length + 1,
                            itemBuilder: (BuildContext context, int index) {
                              if (index ==
                                  viewOutwardMappingController
                                      .inwardMacMapList?.length) {
                                if (viewOutwardMappingController.isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor: AlwaysStoppedAnimation<Color>(
                                              AppTheme.colorProgress),
                                          backgroundColor: AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              }
                              else {
                                InwardMacSerialDataList item =
                                viewOutwardMappingController
                                    .inwardMacMapList![index];
                                item.macAddressValue = item.macAddress;
                                item.serialNumberValue = item.serialNumber;
                                return Container(
                                  margin: const EdgeInsets.only(
                                      top: Constant.VERY_SMALL_PADDING - 2),
                                  child: InkWell(
                                    onTap: () {
                                      if (item.outwardId == null) {
                                        if (item.selected != null &&
                                            item.selected!) {
                                          item.selected = false;
                                        } else {
                                          item.selected = true;
                                        }
                                        viewOutwardMappingController.update();
                                      }
                                    },
                                    child: OutwardMapItem(
                                      item: item,
                                      controller: viewOutwardMappingController,
                                      onSelectChanged: (value) {
                                        if (item.selected != null &&
                                            item.selected!) {
                                          item.selected = false;
                                        } else {
                                          item.selected = true;
                                        }
                                        viewOutwardMappingController.update();
                                      },
                                    ),
                                  ),
                                );
                              }


                            }),
                      )
                    : Container()),
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
                    title: Strings.submit,
                    fontSize: AppTheme.medium,
                    fontWeight: FontWeight.w400,
                  ),
                ))
              ],
            )
          ],
        ),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.outward_mac_mapping, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    viewOutwardMappingController.addOutwardMacMapApiCall();
  }
}
