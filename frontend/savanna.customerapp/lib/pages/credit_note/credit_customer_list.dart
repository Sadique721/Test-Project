import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../theme/app_theme.dart';
import '../../util/constant.dart';
import '../../util/strings.dart';
import '../../widgets/dynamic_appbar.dart';
import '../../widgets/no_data_found.dart';
import '../../widgets/progress_bar.dart';
import 'credit_customer_controller.dart';
import 'customer_view_item.dart';

class CreditCustomerList extends StatefulWidget {
  @override
  _CreditCustomerListState createState() => _CreditCustomerListState();
}

class _CreditCustomerListState extends State<CreditCustomerList> {
  final creditCustomerController = Get.put(CreditCustomerController());

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
      child: GetBuilder<CreditCustomerController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: creditCustomerController.isLoading),
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
              child: (creditCustomerController.parentCustomerList != null &&
                  creditCustomerController.parentCustomerList!.isNotEmpty)
                  ? Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: ListView.builder(
                    controller: creditCustomerController.controller,
                    scrollDirection: Axis.vertical,
                    itemCount: creditCustomerController
                        .parentCustomerList!.length +
                        1,
                    itemBuilder: (context, index) {
                      if (index ==
                          creditCustomerController
                              .parentCustomerList?.length) {
                        if (creditCustomerController.isShowLoadMore) {
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
                                result: creditCustomerController
                                    .parentCustomerList![index]);
                          },
                          child: CustomerViewItem(
                            index: index,
                            item: creditCustomerController
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
    return DynamicAppBar(Strings.customer_list, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}