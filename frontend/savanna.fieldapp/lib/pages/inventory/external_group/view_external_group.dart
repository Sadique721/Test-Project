import 'package:savbill/pages/inventory/external_group/add_edit_external_grp.dart';
import 'package:savbill/pages/inventory/external_group/external_detail/external_item_management_detail.dart';
import 'package:savbill/pages/inventory/external_group/external_group_item.dart';
import 'package:savbill/pages/inventory/external_group/external_group_mapping.dart';
import 'package:savbill/pages/inventory/external_group/view_external_group_controller.dart';
import 'package:savbill/pages/inventory/inwards/change_inward_status_dialog.dart';
import 'package:savbill/pages/inventory/module/request/change_inward_status_req.dart';
import 'package:savbill/pages/inventory/module/response/external_group_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
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

class ViewExternalGroup extends StatefulWidget {
  @override
  _ViewExternalGroupState createState() => _ViewExternalGroupState();
}

class _ViewExternalGroupState extends State<ViewExternalGroup>
    implements ChangeInwardStatusBtnAction {
  final viewExternalGroupController = Get.put(ViewExternalGroupController());

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
      child: GetBuilder<ViewExternalGroupController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: viewExternalGroupController.isLoading),
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
                    title: Strings.external_item_groups,
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
                                    viewExternalGroupController
                                        .searchController,
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
                              viewExternalGroupController.applyFilter();
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
                              viewExternalGroupController.clearFilter();
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
                child: (viewExternalGroupController.externalGroupList != null &&
                        viewExternalGroupController
                            .externalGroupList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewExternalGroupController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewExternalGroupController
                                    .externalGroupList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  viewExternalGroupController
                                      .externalGroupList?.length) {
                                if (viewExternalGroupController
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
                                ExternalGroupDetail item =
                                    viewExternalGroupController
                                        .externalGroupList![index];
                                return ExternalGroupItem(
                                  index: index,
                                  item: item,
                                  onTapEdit: () {
                                    addEditExternalGroupScreen(
                                        Strings.edit, item);
                                  },
                                  onTapMacMap: () {
                                    openExternalGroupMappingScreen(
                                        Strings.edit, item);
                                  },
                                  onTapApprove: () {
                                    showChangeStatusDialog(
                                        item, Strings.approve);
                                  },
                                  onTapReject: () {
                                    showChangeStatusDialog(
                                        item, Strings.reject);
                                  },
                                  onTapMacMapView: () {
                                    openExternalGroupMappingScreen(
                                        Strings.view, item);
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
                                              viewExternalGroupController
                                                  .deleteExternalData(
                                                      item, index);
                                            },
                                            negativeBtnClick: () {
                                              Get.back();
                                            });
                                      },
                                    );
                                  },
                                  onTapExternalDetails: (){
                                    externalItemDetailsScreen(item.id,item.externalItemGroupNumber);
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
                      addEditExternalGroupScreen(Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.create_external_item,
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

  openExternalGroupMappingScreen(String from, ExternalGroupDetail? item) async {
    var result = await Get.to(ExternalGroupMappingDetail(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewExternalGroupController.clearFilter();
    }
  }

  addEditExternalGroupScreen(String from, ExternalGroupDetail? item) async {
    var result = await Get.to(AddEditExternalGrp(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewExternalGroupController.clearFilter();
    }
  }



  externalItemDetailsScreen(int? externalId,String? externalItemName) async {
    var result = await Get.to(()=> ExternalItemManagementDetail(),
        arguments: {Constant.EXTERNAL_ID: externalId,Constant.EXTERNAL_ITEM_NAME: externalItemName});
    if (result != null && result == true) {
      viewExternalGroupController.clearFilter();
    }
  }

  showChangeStatusDialog(ExternalGroupDetail detail, String from) {
    String status = "";

    if (from.equalsIgnoreCase(Strings.approve)) {
      status = Strings.approve;
    } else if (from.equalsIgnoreCase(Strings.reject)) {
      status = Strings.rejected;
    }

    ChangeInwardStatusReq request = ChangeInwardStatusReq(
        id: detail.id, approvalStatus: status, approvalRemark: "");

    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return ChangeInwardStatusDialog(
            changeInwardStatusBtnAction: this,
            changeInwardStatusReq: request,
            from: from,
            screenId: 2,
          );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.external_item_group_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void changeInwardStatusBtnAction(
      {String? identifier, ChangeInwardStatusReq? changeInwardStatusReq}) {
    Get.back();
    if (changeInwardStatusReq != null) {
      viewExternalGroupController
          .changeExternalGroupStatus(changeInwardStatusReq);
    }
  }
}
