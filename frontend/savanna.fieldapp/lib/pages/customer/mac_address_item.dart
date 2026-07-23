import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:flutter/material.dart';

class MacAddressItem extends StatelessWidget {
  String item;
  int index;
  final Function()? onDeleteTap;

  MacAddressItem(
      {Key? key, required this.index, required this.item, this.onDeleteTap})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: RichText(
                        maxLines: 2,
                        softWrap: true,
                        text: TextSpan(
                          text: "${Strings.mac_address} : ",
                          style: TextStyle(
                            fontWeight: FontWeight.w400,
                            fontSize: AppTheme.small,
                            color: AppTheme.title_dark,
                          ),
                          children: [
                            TextSpan(
                              text: item,
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
                    InkWell(
                      onTap: onDeleteTap!,
                      child: Icon(
                        Icons.delete,
                        size: 24,
                        color: AppTheme.colorRed,
                      ),
                    ),
                  ])),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          /* cardButtonRow(),*/
        ],
      ),
    );
  }
}
