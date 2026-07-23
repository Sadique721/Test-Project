import 'dart:developer';

import 'package:savbill/pages/inventory/inwards/add_edit_inwards.dart';
import 'package:savbill/pages/inventory/inwards/change_inward_status_dialog.dart';
import 'package:savbill/pages/inventory/inwards/inward_details/inwards_details.dart';
import 'package:savbill/pages/inventory/inwards/inward_mapping.dart';
import 'package:savbill/pages/inventory/inwards/inwards_item.dart';
import 'package:savbill/pages/inventory/inwards/view_inwards_controller.dart';
import 'package:savbill/pages/inventory/module/request/change_inward_status_req.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

class ViewInwards extends StatefulWidget {
  @override
  _ViewInwardsState createState() => _ViewInwardsState();
}

class _ViewInwardsState extends State<ViewInwards>
    implements ChangeInwardStatusBtnAction {
  final viewInwardsController = Get.put(ViewInwardsController());

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
      child: GetBuilder<ViewInwardsController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body:SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: viewInwardsController.isLoading),
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
              /*Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.inwards,
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
                                    viewInwardsController.searchController,
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
                              viewInwardsController.applyFilter();
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
                              viewInwardsController.clearFilter();
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
              ),*/

              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      CustomText(
                          title: Strings.inwards,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500),
                      InkWell(
                        onTap: () {
                          if (viewInwardsController.filterViewOpen) {
                            viewInwardsController.filterViewOpen =
                            false;
                          } else {
                            viewInwardsController.filterViewOpen =
                            true;
                          }
                          viewInwardsController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color:
                              viewInwardsController.isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ]),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              viewInwardsController.filterViewOpen
                  ? Container(
                width: MediaQuery.of(context).size.width,
                margin: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Material(
                  color: AppTheme.colorWhite,
                  elevation: 1.5,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(
                          Constant.BTN_ROUNDED_CORNER - 2)),
                  child: Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H - 2,
                              width: Constant.DROP_DOWN_ARROW_W_H - 2,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.select_search_option,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: false,
                            isDense: true,
                            value: viewInwardsController
                                .selectedInwardSearchOption,
                            items: viewInwardsController
                                .inwardSearchOptionList!
                                .map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: CustomText(title: value.text!,colors: AppTheme.title_dark,),
                              );
                            }).toList(),
                            onChanged: (value) {
                              viewInwardsController
                                  .selectedInwardSearchOption =
                              value as DropdownDetail?;
                            },
                            validator: (value) {
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.enter_search_detail,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                            viewInwardsController
                                .searchController,
                            borderEnableColors: AppTheme.colorBlack,
                            borderFocusColors: AppTheme.colorBlack,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            keyboardType: TextInputType.text,
                            maxLines: 1,
                            onTextValidator: (String? value) {},
                            onTextFiledOnTap: () {},
                            readOnly: false),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Expanded(
                                child: SimpleButton(
                                  onTap: () {
                                    viewInwardsController
                                        .applyFilter();
                                  },
                                  radius: Constant.BTN_HEIGHT_M,
                                  height: Constant.BTN_HEIGHT_M,
                                  bgColors: AppTheme.colorPrimary,
                                  child: CustomText(
                                    title: Strings.apply,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                              const SizedBox(
                                width: Constant.LARGE_PADDING,
                              ),
                              Expanded(
                                child: SimpleButton(
                                  onTap: () {
                                    viewInwardsController
                                        .clearFilter();
                                  },
                                  radius: Constant.BTN_HEIGHT_M,
                                  height: Constant.BTN_HEIGHT_M,
                                  bgColors: AppTheme.colorBlack,
                                  borderColors: AppTheme.colorBlack,
                                  child: CustomText(
                                    title: Strings.clear,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                            ]),
                      ],
                    ),
                  ),
                ),
              )
                  : Container(),
              viewInwardsController.filterViewOpen
                  ? const SizedBox(
                height: Constant.MEDIUM_PADDING,
              )
                  : Container(),

              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (viewInwardsController.inwardsList != null &&
                        viewInwardsController.inwardsList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewInwardsController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                viewInwardsController.inwardsList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  viewInwardsController.inwardsList?.length) {
                                if (viewInwardsController.isShowLoadMore) {
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
                                InwardsDetail item = viewInwardsController.inwardsList![index];
                                return InwardsItem(
                                  index: index,
                                  item: item,
                                  onTapEdit: () {
                                    if(item.approvalStatus != null &&
                                        item.approvalStatus!.isNotEmpty &&
                                        item.approvalStatus!
                                            .equalsIgnoreCase(Strings.approve)){
                                      return;
                                    }
                                    addEditInwardsScreen(Strings.edit, item);
                                  },
                                  onTapMacMap: () {
                                    openInwardMappingScreen(Strings.edit, item);
                                  },
                                  onTapMacMapView: () {
                                    openInwardMappingScreen(Strings.view, item);
                                  },
                                  onTapApprove: () {
                                    showChangeStatusDialog(
                                        item, Strings.approve);
                                  },
                                  onTapReject: () {
                                    showChangeStatusDialog(
                                        item, Strings.reject);
                                  },
                                  onTapDelete: () {
                                    if(item.approvalStatus != null &&
                                        item.approvalStatus!.isNotEmpty &&
                                        item.approvalStatus!
                                            .equalsIgnoreCase(Strings.approve)){
                                      return;
                                    }
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
                                              viewInwardsController
                                                  .deleteInwardsData(
                                                      item, index);
                                            },
                                            negativeBtnClick: () {
                                              Get.back();
                                            });
                                      },
                                    );
                                  },
                                  onTapInwardDetail :(){
                                    inwardsDetailsScreen(item.id,item.inwardNumber);
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
                      addEditInwardsScreen(Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.create_inward,
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

  showChangeStatusDialog(InwardsDetail detail, String from) {
    String status = "";

    if (from.equalsIgnoreCase(Strings.approve)) {
      status = Strings.approve;
    } else if (from.equalsIgnoreCase(Strings.reject)) {
      status = Strings.rejected;
    }

    ChangeInwardStatusReq request = ChangeInwardStatusReq(
        id: detail.id, productId: detail.productId!.id,approvalStatus: status, approvalRemark: "");

    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return ChangeInwardStatusDialog(
            changeInwardStatusBtnAction: this,
            changeInwardStatusReq: request,
            from: from,
            screenId: 1,
          );
        });
  }

  openInwardMappingScreen(String from, InwardsDetail? item) async {
    var result = await Get.to(InwardMappingDetail(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewInwardsController.clearFilter();
    }
  }

  addEditInwardsScreen(String from, InwardsDetail? item) async {
    var result = await Get.to(AddEditInward(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});
    log("addEditInwardsScreen==>$result");

    if (result != null && result == true) {
      viewInwardsController.clearFilter();
    }
  }

  inwardsDetailsScreen(int? inwardId,String? inwardNumber) async {
    var result = await Get.to(()=> InwardDetails(),
        arguments: {Constant.INWARD_ID: inwardId,Constant.INWARD_NUMBER: inwardNumber});
    log("inwardDetailsScreen==>$result");

    if (result != null && result == true) {
      viewInwardsController.clearFilter();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.inwards, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }

  @override
  void changeInwardStatusBtnAction(
      {String? identifier, ChangeInwardStatusReq? changeInwardStatusReq}) {
    Get.back();
    viewInwardsController.changeInwardStatus(changeInwardStatusReq!);
  }
}
