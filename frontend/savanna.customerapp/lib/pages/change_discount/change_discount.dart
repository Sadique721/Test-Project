import 'package:savbill/pages/change_discount/change_discount_controller.dart';
import 'package:savbill/pages/change_discount/change_discount_item_view.dart';
import 'package:savbill/pages/change_discount/response/change_discount_list.dart';
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

class CustomerChangeDiscount extends StatefulWidget {
  @override
  _CustomerChangeDiscountState createState() => _CustomerChangeDiscountState();
}

class _CustomerChangeDiscountState extends State<CustomerChangeDiscount> {
  final customerDiscountController = Get.put(CustomerDiscountController());

  @override
  void initState() {
    customerDiscountController.focusNode = FocusNode();
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  void dispose() {
    customerDiscountController.focusNode.dispose();
    super.dispose();
  }


  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerDiscountController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerDiscountController.isLoading),
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
                      title: customerDiscountController.customerName,
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
              child: (customerDiscountController.discountList != null &&
                      customerDiscountController.discountList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          customerDiscountController.discountList!.length,
                      itemBuilder: (context, index) {
                        DiscountDetails item =
                            customerDiscountController.discountList![index];

                        return ChangeDiscountItemView(
                            item: item,
                            index: index,
                            customerDiscountController:
                                customerDiscountController);
                      })
                  : noDataFound(),
            ),
            customerDiscountController.discountList != null &&
                    customerDiscountController.discountList!.isNotEmpty
                ? Row(children: [
                    Expanded(
                      child: SimpleButton(
                        onTap: () {
                          customerDiscountController.updateCustomerDiscount();
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
