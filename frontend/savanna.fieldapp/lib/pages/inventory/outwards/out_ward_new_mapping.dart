import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
import 'package:savbill/pages/inventory/outwards/out_ward_new_mapping_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'outward_new_mapping_item.dart';

class OutwardNewMapping extends StatefulWidget {
  @override
  _OutwardNewMappingState createState() => _OutwardNewMappingState();
}

class _OutwardNewMappingState extends State<OutwardNewMapping> {
  final outWardMappingController = Get.put(OutwardMappingController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: outWardMappingController.changeData);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<OutwardMappingController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: controller.isLoading),
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
                        outWardMappingController.searchController,
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
                          outWardMappingController.searchData(value);
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
                          outWardMappingController.clearData();
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

            (outWardMappingController.inwardMacMapList != null &&
                    outWardMappingController.inwardMacMapList!.isNotEmpty)
                ? IntrinsicHeight(
                    child: Row(
                      children: [
                        outWardMappingController.showMacAddress
                            ? Expanded(
                                child: CustomText(
                                  title: Strings.item_id_new,
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.center,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  maxLines: 2,
                                ),
                              )
                            : Container(),
                        outWardMappingController.showMacAddress
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
                            : Container(),
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
                        outWardMappingController.showMacAddress
                            ? Expanded(
                                child: CustomText(
                                  title: Strings.item_type_new,
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.center,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  maxLines: 2,
                                ),
                              )
                            : Container(),

                        // Expanded(
                        //    child: CustomText(
                        //      title: Strings.action,
                        //      colors: AppTheme.title_dark,
                        //      textAlign: TextAlign.center,
                        //      fontSize: AppTheme.small,
                        //      fontWeight: FontWeight.w500,
                        //      maxLines: 2,
                        //    ),
                        //  ),
                      ],
                    ),
                  )
                : const SizedBox.shrink(),
            const SizedBox(height: Constant.SMALL_PADDING),
            Expanded(
                child: (outWardMappingController.inwardMacMapList != null &&
                        outWardMappingController.inwardMacMapList!.isNotEmpty)
                    ? ListView.builder(
                        controller: outWardMappingController.controller,
                        scrollDirection: Axis.vertical,
                        itemCount:
                            outWardMappingController.inwardMacMapList!.length + 1,
                        itemBuilder: (BuildContext context, int index) {
                          if (index ==
                              outWardMappingController
                                  .inwardMacMapList?.length) {
                            if (outWardMappingController.isShowLoadMore) {
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
                          }else{
                            InwardMacSerialDataList item =
                            outWardMappingController.inwardMacMapList![index];
                            return Container(
                              margin: const EdgeInsets.only(
                                  top: Constant.VERY_SMALL_PADDING - 2),
                              child: OutwardMapItem(
                                item: item,
                                showMacAddress:
                                outWardMappingController.showMacAddress,
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
                                            // outWardMappingController
                                            //     .deleteInwardsMapData(
                                            //     item, index);
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
                    : noDataFound()),
          ],
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.outward_mac_mapping, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

}
