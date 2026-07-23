import 'package:savbill/pages/inventory/module/response/view_outward_list_res.dart';
import 'package:savbill/pages/inventory/outwards/add_edit_outwards.dart';
import 'package:savbill/pages/inventory/outwards/out_ward_details_screen.dart';
import 'package:savbill/pages/inventory/outwards/out_ward_new_mapping.dart';
import 'package:savbill/pages/inventory/outwards/outward_mapping.dart';
import 'package:savbill/pages/inventory/outwards/outwards_item.dart';
import 'package:savbill/pages/inventory/outwards/view_outwards_controller.dart';
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

class ViewOutwards extends StatefulWidget {
  @override
  _ViewOutwardsState createState() => _ViewOutwardsState();
}

class _ViewOutwardsState extends State<ViewOutwards> {
  final viewOutwardsController = Get.put(ViewOutwardsController());
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();
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
      child: GetBuilder<ViewOutwardsController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewOutwardsController.isLoading),
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
                    title: Strings.outwards,
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
                                    viewOutwardsController.searchController,
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
                              viewOutwardsController.applyFilter();
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
                              viewOutwardsController.clearFilter();
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
                          title: Strings.outwards,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500),
                      InkWell(
                        onTap: () {
                          if (viewOutwardsController.filterViewOpen) {
                            viewOutwardsController.filterViewOpen =
                            false;
                          } else {
                            viewOutwardsController.filterViewOpen =
                            true;
                          }
                          viewOutwardsController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color:
                              viewOutwardsController.isFilterApply
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
              viewOutwardsController.filterViewOpen
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
                            value: viewOutwardsController
                                .selectedOutwardSearchOption,
                            items: viewOutwardsController
                                .outwardSearchOptionList!
                                .map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              viewOutwardsController
                                  .selectedOutwardSearchOption =
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
                            viewOutwardsController
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
                                    viewOutwardsController
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
                                    viewOutwardsController
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
              viewOutwardsController.filterViewOpen
                  ? const SizedBox(
                height: Constant.MEDIUM_PADDING,
              )
                  : Container(),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (viewOutwardsController.outwardsList != null &&
                        viewOutwardsController.outwardsList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewOutwardsController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                viewOutwardsController.outwardsList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index == viewOutwardsController.outwardsList?.length) {
                                if (viewOutwardsController.isShowLoadMore) {
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
                                OutwardDetail item =
                                    viewOutwardsController.outwardsList![index];
                                return OutwardsItem(
                                  index: index,
                                  item: item,
                                  onTapEdit: () {
                                    if(item.approvalStatus != null &&
                                        item.approvalStatus!.isNotEmpty &&
                                        item.approvalStatus!
                                            .equalsIgnoreCase(Strings.approve)){
                                      return;
                                    }
                                    addEditOutwardsScreen(Strings.edit, item);
                                  },
                                  onTapMacMap: () {
                                    openOutwardMappingScreen(item);
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
                                              viewOutwardsController
                                                  .deleteOutwardsData(
                                                      item, index);
                                            },
                                            negativeBtnClick: () {
                                              Get.back();
                                            });
                                      },
                                    );
                                  },
                                  onTapMacMapView: (){
                                    openOutwardMappingScreenView(item);
                                  },
                                  onTapOutWardDetail: (){
                                    openOutwardDetailsScreenView(item.id, item.outwardNumber);
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
                      addEditOutwardsScreen(Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.create_outward,
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



  openOutwardMappingScreenView(OutwardDetail? item) async {
    var result = await Get.to(OutwardNewMapping(),
        arguments: {Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewOutwardsController.clearFilter();
    }
  }

  openOutwardDetailsScreenView(int? outwardsId, String? outwardsNumber) async {
    var result = await Get.to(OutWardDetails(),
        arguments: {Constant.OUTWARDS_ID: outwardsId, Constant.OUTWARDS_NUMBER: outwardsNumber});

    if (result != null && result == true) {
      viewOutwardsController.clearFilter();
    }
  }


  openOutwardMappingScreen(OutwardDetail? item) async {
    var result = await Get.to(OutwardMappingDetail(),
        arguments: {Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewOutwardsController.clearFilter();
    }
  }

  addEditOutwardsScreen(String from, OutwardDetail? item) async {
    var result = await Get.to(AddEditOutwards(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewOutwardsController.clearFilter();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.outwards, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }
}
