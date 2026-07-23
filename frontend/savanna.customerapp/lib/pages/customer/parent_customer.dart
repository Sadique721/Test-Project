import 'package:savbill/pages/customer/parent_customer_controller.dart';
import 'package:savbill/pages/customer/parent_customer_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ParentCustomerList extends StatefulWidget {
  @override
  _ParentCustomerListState createState() => _ParentCustomerListState();
}

class _ParentCustomerListState extends State<ParentCustomerList> {
  final parentCustomerController = Get.put(ParentCustomerController());

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
      child: GetBuilder<ParentCustomerController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: parentCustomerController.isLoading),
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
              child: (parentCustomerController.parentCustomerList != null &&
                      parentCustomerController.parentCustomerList!.isNotEmpty)
                  ? Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: ListView.builder(
                          controller: parentCustomerController.controller,
                          scrollDirection: Axis.vertical,
                          itemCount: parentCustomerController
                                  .parentCustomerList!.length +
                              1,
                          itemBuilder: (context, index) {
                            if (index ==
                                parentCustomerController
                                    .parentCustomerList?.length) {
                              if (parentCustomerController.isShowLoadMore) {
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
                                      result: parentCustomerController
                                          .parentCustomerList![index]);
                                },
                                child: ParentCustomerViewItem(
                                  index: index,
                                  item: parentCustomerController
                                      .parentCustomerList![index],
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
    return DynamicAppBar(Strings.select_a_customer, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
