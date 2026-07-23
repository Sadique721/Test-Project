import 'package:savbill/pages/change_discount/response/change_discount_list.dart';
import 'package:savbill/pages/customer_caf/customer_caf_change_discount/caf_change_discount_controller.dart';
import 'package:savbill/pages/customer_caf/customer_caf_change_discount/caf_change_discount_item_view.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerCafChangeDiscount extends StatefulWidget {
  @override
  _CustomerChangeDiscountState createState() => _CustomerChangeDiscountState();
}

class _CustomerChangeDiscountState extends State<CustomerCafChangeDiscount> {
  final customerCafDiscountController = Get.put(CustomerCafDiscountController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerCafDiscountController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerCafDiscountController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                 Expanded(child:  CustomText(
                      title: customerCafDiscountController.customerName,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 1,
              child: (customerCafDiscountController.discountList != null &&
                      customerCafDiscountController.discountList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          customerCafDiscountController.discountList!.length,
                      itemBuilder: (context, index) {
                        DiscountDetails item =
                            customerCafDiscountController.discountList![index];

                        return CafChangeDiscountItemView(
                            item: item,
                            index: index,
                            customerCafDiscountController:
                                customerCafDiscountController);
                      })
                  : noDataFound(),
            ),
            customerCafDiscountController.discountList != null &&
                    customerCafDiscountController.discountList!.isNotEmpty
                ? Row(children: [
                    Expanded(
                      child: SimpleButton(
                        onTap: () {
                          customerCafDiscountController.updateCustomerDiscount();
                        },
                        radius: 0,
                        height: Constant.BOTTOM_BTN_HEIGHT,
                        bgColors: AppTheme.colorPrimary,
                        borderColors: AppTheme.colorPrimary,
                        child: CustomText(
                          title: Strings.update_discount,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w400,
                        ),
                      ),
                    ),
                  ])
                : Container(),
          ]),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.change_discount, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
