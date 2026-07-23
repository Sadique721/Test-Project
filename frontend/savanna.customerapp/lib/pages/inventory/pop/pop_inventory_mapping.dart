import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/pages/inventory/pop/pop_inventory_mapping_controller.dart';
import 'package:savbill/pages/inventory/pop/pop_inventory_mapping_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PopInventoryMapping extends StatefulWidget {
  @override
  _PopInventoryMappingState createState() => _PopInventoryMappingState();
}

class _PopInventoryMappingState extends State<PopInventoryMapping> {
  final popInventoryMappingController =
      Get.put(PopInventoryMappingController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: popInventoryMappingController.changeData);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<PopInventoryMappingController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: popInventoryMappingController.isLoading),
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
            IntrinsicHeight(
              child: Row(
                children: [
                  Expanded(
                    flex: 3,
                    child: CustomText(
                      title: Strings.mac_address,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                      maxLines: 2,
                    ),
                  ),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  Expanded(
                    flex: 3,
                    child: CustomText(
                      title: Strings.serial_no,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                      maxLines: 2,
                    ),
                  ),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  Expanded(
                    flex: 2,
                    child: CustomText(
                      title: Strings.status,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                      maxLines: 2,
                    ),
                  ),
                  Expanded(
                    flex: 1,
                    child: CustomText(
                      title: "",
                      colors: AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall,
                      fontWeight: FontWeight.w400,
                      maxLines: 2,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
            Expanded(
                child: (popInventoryMappingController.popInOutWardMACMapping !=
                            null &&
                        popInventoryMappingController
                            .popInOutWardMACMapping!.isNotEmpty)
                    ? ListView.builder(
                        itemCount: popInventoryMappingController
                            .popInOutWardMACMapping!.length,
                        itemBuilder: (BuildContext context, int index) {
                          InOutWardMACMapping item =
                              popInventoryMappingController
                                  .popInOutWardMACMapping![index];
                          return Container(
                            margin: const EdgeInsets.only(
                                top: Constant.VERY_SMALL_PADDING - 2),
                            child: PopInventoryMapItem(
                              item: item,
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
                                          popInventoryMappingController
                                              .deleteMappingData(item, index);
                                        },
                                        negativeBtnClick: () {
                                          Get.back();
                                        });
                                  },
                                );
                              },
                            ),
                          );
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
    return DynamicAppBar(Strings.pop_management, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
