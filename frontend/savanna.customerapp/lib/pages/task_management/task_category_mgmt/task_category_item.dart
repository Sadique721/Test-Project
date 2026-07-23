import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class TaskCategoryItem extends StatelessWidget {

  TaskCategoryMgmtDataList item;
  int index;
  final Function()? onTapEdit;
  final Function()? onTapDelete;
  final Function()? onTapDetails;

  TaskCategoryItem({
    super.key,
    required this.index,
    required this.item,
    this.onTapEdit,
    this.onTapDelete,
    this.onTapDetails,
  });


  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              // mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Padding(padding: const EdgeInsets.symmetric(vertical: Constant.VERY_SMALL_PADDING),child: titleWidget(Strings.name),),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      InkWell(onTap: onTapDetails,child: valueWidget((item.categoryName != null && item.categoryName!.isNotEmpty)
                          ? item.categoryName!
                          : "-",AppTheme.colorPrimary),)

                    ],
                  ),
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Padding(padding: const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING,vertical: Constant.VERY_SMALL_PADDING),child: titleWidget(Strings.status),),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING,vertical: Constant.VERY_SMALL_PADDING),
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal:
                            Constant.SMALL_PADDING,
                            vertical: Constant
                                .VERY_SMALL_PADDING),
                        decoration: BoxDecoration(
                          borderRadius:
                          BorderRadius.circular(
                              Constant.LARGE_PADDING),color: (item.status != null &&
                            item.status!.isNotEmpty &&
                            item.status!.equalsIgnoreCase(Strings.active))
                            ? AppTheme.statusClosedGreen
                            : AppTheme.statusReject,),
                        child: CustomText(
                            title: (item.status != null &&
                                item.status!.isNotEmpty &&
                                item.status!.equalsIgnoreCase(Strings.active))
                                ? Strings.active
                                : Strings.in_active,
                            colors: AppTheme.colorWhite,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            maxLines: 2,
                            height: 1,
                            fontWeight: FontWeight.w500),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          /*const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.status,
                (item.status != null && item.status!.isNotEmpty)
                    ? "${item.status}"
                    : "-",
                "",
                ""),
          ),*/
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              buttonView(editSvg, AppTheme.custEditLight, AppTheme.custEditDark,
                  onTapEdit!),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(deleteSvg, AppTheme.custDeleteLight,
                  AppTheme.custDeleteDark, onTapDelete!),
            ]),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value,Color? textColor) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: textColor ?? AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
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
