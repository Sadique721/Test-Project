import 'package:savbill/pages/customer/model/response/plan_group_mapping_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class PlanGroupMappingList extends StatelessWidget {
  PlanGroupMappingDetail item;
  int index;
  final Function()? onDeleteTap;

// type ==1 show discount and trial flag, else show offer price and new price
  PlanGroupMappingList(
      {Key? key, required this.index, required this.item, this.onDeleteTap})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    // String validity = "${item.planDetail!.validity}-${item.planDetail!.unitsOfValidity!}";
    return Card(
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          cardItem(Strings.service, '${item.service}', Strings.plan_name,
              '${item.plan!.displayName}'),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          cardItem(Strings.price,
              '${item.plan!.offerprice}',Strings.new_price, '${item.newofferprice}'),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          cardItem(Strings.validity, '${item.plan!.validity} ${item.plan!.unitsOfValidity}','',''),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ],
      ),
    );
  }

  cardItem(String lbl1, String val1, String lbl2, String val2) {
    return Padding(
      padding:  const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
      child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: RichText(
                maxLines: 2,
                softWrap: true,
                text: TextSpan(
                  text: lbl1.isNotEmpty ? "${lbl1} : " : "",
                  style: TextStyle(
                    fontWeight: FontWeight.w400,
                    fontSize: AppTheme.small,
                    color: AppTheme.title_dark,
                  ),
                  children: [
                    TextSpan(
                      text: val1,
                      style: TextStyle(
                        fontSize: AppTheme.small,
                        fontWeight: FontWeight.normal,
                        color: AppTheme.lable_noramal,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            Expanded(
              child: RichText(
                textAlign: TextAlign.end,
                maxLines: 2,
                softWrap: true,
                text: TextSpan(
                  text: lbl2.isNotEmpty ? "${lbl2} : " : "",
                  style: TextStyle(
                    fontWeight: FontWeight.w400,
                    fontSize: AppTheme.small,
                    color: AppTheme.title_dark,
                  ),
                  children: [
                    TextSpan(
                      text: val2,
                      style: TextStyle(
                        fontSize: AppTheme.small,
                        fontWeight: FontWeight.normal,
                        color: AppTheme.lable_noramal,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ]),
    );
  }

  cardButtonRow() {
    return Row(children: <Widget>[
      cardButtonView(Strings.delete, Constant.BTN_ROUNDED_CORNER,
          Constant.BTN_ROUNDED_CORNER, AppTheme.colorRed, onDeleteTap!),
    ]);
  }

  cardButtonView(String btnName, double leftBottom, double rightBottom,
      Color txtColor, Function() onTap) {
    return Expanded(
      child: InkWell(
        onTap: onTap,
        child: Container(
          height: Constant.CARD_BOTTOM_BUTTON_H - 10,
          alignment: Alignment.center,
          decoration: BoxDecoration(
              color: AppTheme.colorCardWhiteBtn,
              borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(leftBottom),
                  bottomRight: Radius.circular(rightBottom))),
          child: CustomText(
            title: btnName,
            colors: txtColor,
            textAlign: TextAlign.center,
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    );
  }
}