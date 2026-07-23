import 'dart:developer';

import 'package:savbill/pages/inventory/module/response/view_pop_list_res.dart';
import 'package:savbill/pages/inventory/pop/pop_details/pop_details.dart';
import 'package:savbill/pages/inventory/pop/pop_item.dart';
import 'package:savbill/pages/inventory/pop/view_pop_controller.dart';
import 'package:savbill/pages/inventory/pop/view_pop_inventory.dart';
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

class ViewPopList extends StatefulWidget {
  @override
  _ViewPopListState createState() => _ViewPopListState();
}

class _ViewPopListState extends State<ViewPopList> {
  final viewPopController = Get.put(ViewPopController());

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
      child: GetBuilder<ViewPopController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: viewPopController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.pop_management,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Flexible(
                        child: Card(
                          margin: const EdgeInsets.all(0),
                          elevation: 0.5,
                          child: Container(
                            height: 50,
                            padding: const EdgeInsets.symmetric(
                                horizontal:
                                    Constant.SEARCH_BAR_CARD_PADDING - 2,
                                vertical: Constant.SEARCH_BAR_CARD_PADDING - 4),
                            child: CoustomTextField(
                                labelText: Strings.search_your_text_here,
                                textEditingController:
                                    viewPopController.searchController,
                                keyboardType: TextInputType.text,
                                borderEnableColors: AppTheme.colorPrimary,
                                textInputAction: TextInputAction.done,
                                onChanged: (value) {},
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                prefixIcon: Icon(
                                  Icons.search,
                                  color: AppTheme.colorPrimary,
                                ),
                                borderCorner: Constant.BTN_ROUNDED_CORNER_M,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: false),
                          ),
                        ),
                      ),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      Row(children: [
                        Material(
                          color: AppTheme.colorWhite,
                          elevation: 2,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(6)),
                          child: InkWell(
                            onTap: () {
                              viewPopController.applyFilter();
                            },
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.statusClosedGreen,
                                borderRadius:
                                    const BorderRadius.all(Radius.circular(6)),
                              ),
                              padding: const EdgeInsets.all(5),
                              child: Icon(
                                Icons.check,
                                color: AppTheme.colorWhite,
                                size: 22,
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        Material(
                          color: AppTheme.colorWhite,
                          elevation: 2,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(6)),
                          child: InkWell(
                            onTap: () {
                              viewPopController.clearFilter();
                            },
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.colorRed,
                                borderRadius:
                                    const BorderRadius.all(Radius.circular(6)),
                              ),
                              padding: const EdgeInsets.all(5),
                              child: Icon(
                                Icons.close,
                                color: AppTheme.colorWhite,
                                size: 22,
                              ),
                            ),
                          ),
                        ),
                      ]),
                    ]),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (viewPopController.popList != null &&
                        viewPopController.popList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewPopController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewPopController.popList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index == viewPopController.popList?.length) {
                                if (viewPopController.isShowLoadMore) {
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
                                PopDetail item =
                                    viewPopController.popList![index];
                                return PopItem(
                                  index: index,
                                  item: item,
                                  onTapEdit: () {
                                    // addEditPopScreen(Strings.edit, item);
                                    viewPopController.viewPopDetail(item.id!);
                                  },
                                  onTapInventoryMap: () async {
                                    var result = Get.to(ViewPopInventory(),
                                        arguments: {
                                          Constant.ID: item.id!,
                                          Constant.CUST_USERNAME: item.name
                                        });
                                    if (result != null && result == true) {
                                      viewPopController.clearFilter();
                                    }
                                  },
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
                                              viewPopController.deletePopData(
                                                  item, index);
                                            },
                                            negativeBtnClick: () {
                                              Get.back();
                                            });
                                      },
                                    );
                                  },
                                  onTapPopDetails: (){
                                    popDetailsScreen(item.id,item.name);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
              Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                    onTap: () {
                      viewPopController.addEditPopScreen(Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.create_pop,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ))
                ],
              )
            ]),
      ),
    );
  }

  popDetailsScreen(int? popId,String? popName) async {
    var result = await Get.to(()=> PopDetails(),
        arguments: {Constant.POP_ID: popId,Constant.POP_NAME: popName});

    if (result != null && result == true) {
      viewPopController.clearFilter();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.pop_management, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
