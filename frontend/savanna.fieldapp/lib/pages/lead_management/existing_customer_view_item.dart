import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class ExistingCustomerViewItem extends StatelessWidget {
  CustomerDetail item;
  int index;

  List colorArr = [
    AppTheme.colorGreenRView,
    AppTheme.colorRedRView,
    AppTheme.colorBlueRView,
    AppTheme.colorYellowRView
  ];

  ExistingCustomerViewItem({
    Key? key,
    required this.index,
    required this.item,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.only(
        top: index == 0 ? 0 : Constant.MEDIUM_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: double.infinity,
            height: 2,
            decoration: BoxDecoration(
              color: colorArr[index % colorArr.length],
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(Constant.BTN_ROUNDED_CORNER),
                topRight: Radius.circular(Constant.BTN_ROUNDED_CORNER),
              ),
            ),
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Padding(
              padding: const EdgeInsets.only(left: Constant.MEDIUM_PADDING,right: Constant.MEDIUM_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  CustomText(
                      title: "${item.name}",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      height: 1,
                      fontWeight: FontWeight.w500),
                  CustomText(
                      title: "${item.serviceArea}",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      height: 1,
                      fontWeight: FontWeight.w500),
                ],
              )),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Padding(
              padding: const EdgeInsets.only(left: Constant.MEDIUM_PADDING,right: Constant.MEDIUM_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  CustomText(
                      title: "${item.username}",
                      colors: AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      height: 1,
                      fontWeight: FontWeight.w500),
                  CustomText(
                      title: "${item.mobile}",
                      colors: AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      height: 1,
                      fontWeight: FontWeight.w500),
                ],
              )),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }
}