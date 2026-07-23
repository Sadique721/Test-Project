import 'package:savbill/pages/inventory/external_group/enternal_owner_list_controller.dart';
import 'package:savbill/pages/inventory/external_group/owner_view_item_list.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ExternalOwnerList extends StatefulWidget {
  @override
  _ParentCustomerListState createState() => _ParentCustomerListState();
}

class _ParentCustomerListState extends State<ExternalOwnerList> {
  final externalOwnerItemController = Get.put(ExternalOwnerItemController());

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
      child: GetBuilder<ExternalOwnerItemController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: externalOwnerItemController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SizedBox(
        width: MediaQuery.of(context).size.width,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SCREEN_PADDING,
            ),
            Expanded(
              flex: 1,
              child: ((externalOwnerItemController.externalOwnerDataList != null &&
                  externalOwnerItemController.externalOwnerDataList!.isNotEmpty) ||
                  (externalOwnerItemController.externalPartnerDataList != null &&
                      externalOwnerItemController.externalPartnerDataList!.isNotEmpty))
                  ? Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: ListView.builder(
                    controller: externalOwnerItemController.controller,
                    scrollDirection: Axis.vertical,
                    itemCount: externalOwnerItemController.type!.equalsIgnoreCase(Strings.customer_owned) ? externalOwnerItemController
                        .externalOwnerDataList!.length + 1 :externalOwnerItemController
                        .externalPartnerDataList!.length + 1,
                    itemBuilder: (context, index) {
                      if (index == (externalOwnerItemController.type!.equalsIgnoreCase(Strings.customer_owned) ? externalOwnerItemController.externalOwnerDataList?.length : externalOwnerItemController.externalPartnerDataList?.length)) {
                        if (externalOwnerItemController.isShowLoadMore) {
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
                        return InkWell(
                          onTap: () async {

                            Get.back(
                                result:  externalOwnerItemController.type!.equalsIgnoreCase(Strings.customer_owned) ? externalOwnerItemController
                                    .externalOwnerDataList![index] : externalOwnerItemController
                                    .externalPartnerDataList![index]);
                          },
                          child: OwnerListViewItem(
                            index: index,
                            item: (externalOwnerItemController.externalOwnerDataList!.isNotEmpty)  ? externalOwnerItemController
                                .externalOwnerDataList![index] : null,
                            itemList: (externalOwnerItemController.externalPartnerDataList!.isNotEmpty) ? externalOwnerItemController
                                .externalPartnerDataList![index]  : null,
                          ),
                        );
                      }
                    }),
              )
                  : noDataFound(),
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
    return DynamicAppBar(Strings.customer_list, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}