import 'package:savbill/pages/inventory/bulk_consumption/bulk_consumption_mapping_controller.dart';
import 'package:savbill/pages/inventory/bulk_consumption/bulk_consumption_mapping_item.dart';
import 'package:savbill/pages/inventory/module/response/bulk_consumption_mapping_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class BulkConsumptionMapping extends StatefulWidget {
  @override
  _BulkConsumptionMappingState createState() => _BulkConsumptionMappingState();
}

class _BulkConsumptionMappingState extends State<BulkConsumptionMapping> {
  final viewBulkConsumptionMappingController =
      Get.put(ViewBulkConsumptionMappingController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: viewBulkConsumptionMappingController.changeData);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ViewBulkConsumptionMappingController>(
          builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewBulkConsumptionMappingController.isLoading),
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
            viewBulkConsumptionMappingController.from
                    .equalsIgnoreCase(Strings.view)
                ? Container()
                : Form(
                    key: viewBulkConsumptionMappingController
                        .addBulkConsumptionMapFormKey,
                    autovalidateMode:
                        viewBulkConsumptionMappingController.autoValidateMode,
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          viewBulkConsumptionMappingController.showMacAddress
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
                                                viewBulkConsumptionMappingController
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
                          viewBulkConsumptionMappingController.showMacAddress
                              ? const SizedBox(height: Constant.SMALL_PADDING)
                              : Container(),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
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
                                          viewBulkConsumptionMappingController
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
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Align(
                            alignment: Alignment.centerRight,
                            child: InkWell(
                              onTap: () {
                                validateForm();
                              },
                              child: CustomText(
                                title: "+ Add Mapping",
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
                  viewBulkConsumptionMappingController.showMacAddress
                      ? Expanded(
                          flex: 1,
                          child: CustomText(
                            title: Strings.mac_address,
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            maxLines: 2,
                          ),
                        )
                      : Container(),
                  viewBulkConsumptionMappingController.showMacAddress
                      ? const SizedBox(
                          width: Constant.VERY_SMALL_PADDING,
                        )
                      : Container(),
                  Expanded(
                    flex: 1,
                    child: CustomText(
                      title: Strings.serial_no,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                      maxLines: 2,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
            Expanded(
                child: (viewBulkConsumptionMappingController
                                .bulkConsumptionMacMapList !=
                            null &&
                        viewBulkConsumptionMappingController
                            .bulkConsumptionMacMapList!.isNotEmpty)
                    ? ListView.builder(
                        itemCount: viewBulkConsumptionMappingController
                            .bulkConsumptionMacMapList!.length,
                        itemBuilder: (BuildContext context, int index) {
                          BulkConsumptionMappingDetail item =
                              viewBulkConsumptionMappingController
                                  .bulkConsumptionMacMapList![index];
                          return Container(
                            margin: const EdgeInsets.only(
                                top: Constant.VERY_SMALL_PADDING - 2),
                            child: BulkConsumptionMapItem(
                              item: item,
                              showMacAddress:
                                  viewBulkConsumptionMappingController
                                      .showMacAddress,
                              from: viewBulkConsumptionMappingController.from,
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
    return DynamicAppBar(
        Strings.bulk_consumption_mac_mapping,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (viewBulkConsumptionMappingController
        .addBulkConsumptionMapFormKey.currentState!
        .validate()) {
      /* if (viewBulkConsumptionMappingController.externalGroupDetail != null) {
        int qty = viewBulkConsumptionMappingController.externalGroupDetail!.qty!;
        int itemSize = 0;
        if (viewBulkConsumptionMappingController.externalGrpMacMapList != null) {
          itemSize =
              viewBulkConsumptionMappingController.externalGrpMacMapList!.length;
        }
        if (itemSize >= qty) {
          Utils.showSnackbar(
              Strings.ERROR,
              "No more external group mapping available.",
              AppTheme.colorWhite,
              AppTheme.colorRed);
          return;
        }
      }
      viewBulkConsumptionMappingController.addExternalGrpMacMappingData();*/
    } else {
      viewBulkConsumptionMappingController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      viewBulkConsumptionMappingController.update();
    }
  }
}
