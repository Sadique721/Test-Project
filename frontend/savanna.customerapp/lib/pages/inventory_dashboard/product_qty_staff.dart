import 'package:savbill/pages/inventory_dashboard/model/get_product_qty_staff_res.dart';
import 'package:savbill/pages/inventory_dashboard/product_qty_staff_controller.dart';
import 'package:savbill/pages/inventory_dashboard/product_qty_staff_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class GetProductQtyStaff extends StatefulWidget {
  @override
  _GetProductQtyStaffState createState() =>
      _GetProductQtyStaffState();
}

class _GetProductQtyStaffState extends State<GetProductQtyStaff> {
  final productQtyStaffController = Get.put(GetProductQtyStaffController());

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
      child:
      GetBuilder<GetProductQtyStaffController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: productQtyStaffController.isLoading),
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
                    title: Strings.customer_detail,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (productQtyStaffController.productQtyStaffList !=
                    null &&
                    productQtyStaffController
                        .productQtyStaffList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller:
                      productQtyStaffController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount: productQtyStaffController
                          .productQtyStaffList!.length +
                          1,
                      itemBuilder: (context, index) {
                        if (index ==
                            productQtyStaffController
                                .productQtyStaffList?.length) {
                          if (productQtyStaffController
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
                          ProductQtyStaffDataList item = productQtyStaffController.productQtyStaffList![index];
                          return ProductQtyStaffItem(
                            item: item,
                            controller: productQtyStaffController,
                          );
                        }
                      }),
                )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        productQtyStaffController.inventoryType!.equalsIgnoreCase(Strings.staff) ? Strings.product_qty_staff: Strings.product_qty_warehouse,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

}