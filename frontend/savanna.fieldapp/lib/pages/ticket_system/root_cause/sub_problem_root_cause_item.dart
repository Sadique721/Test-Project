
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';
import '../../../util/resources.dart';
import '../../../widgets/coustom_text.dart';
import '../model/response/root_cause_list_res.dart';
import '../model/response/root_cause_sub_problem_res.dart';

class SubProblemRootCauseItem extends StatelessWidget {
  ResoSubCategoryMappingList item;
  bool isShowDelete;
  final Function()? onTapDelete;


  SubProblemRootCauseItem({
    Key? key,
    required this.item,
    required this.isShowDelete,
    this.onTapDelete,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {

    return Container(
      margin:  const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          IntrinsicHeight(
            child: Row(
              children: [
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: CustomText(
                      // title: item.subcateId,
                      title: item.subCateName,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                      maxLines: 2,
                    ),
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                ),
                isShowDelete
                    ? VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                )
                    : Container(),
                isShowDelete
                    ? const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                )
                    : Container(),
                isShowDelete
                    ? Center(
                  child: buttonView(deleteSvg, AppTheme.custDeleteLight,
                      AppTheme.custDeleteDark, onTapDelete!),
                )
                    : Container(),
                isShowDelete
                    ? const SizedBox(
                  width: Constant.MEDIUM_PADDING + 2,
                )
                    : Container(),
              ],
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function() onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 1.5,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 10,
          width: Constant.BTN_HEIGHT_M - 10,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE,
            width: Constant.ICON_SIZE,
            color: txtColor,
            fit: BoxFit.fill,
          ),
        ),
      ),
    );
  }
}