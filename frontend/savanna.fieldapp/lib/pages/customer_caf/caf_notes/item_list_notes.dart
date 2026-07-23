import 'package:savbill/pages/customer_caf/response/cust_caf_notes_res.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:flutter/material.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
class ItemListNotes extends StatefulWidget {
  final CafNoteContent item;
  final int index;
  final Function()? onTap;
  ItemListNotes({Key? key, required this.index, required this.item, this.onTap})
      : super(key: key);
  @override
  State<ItemListNotes> createState() => _ItemListNotesControllerState();
}
class _ItemListNotesControllerState extends State<ItemListNotes> {
  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.symmetric(
        vertical: Constant.SMALL_PADDING,
        horizontal: Constant.SMALL_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.MEDIUM_PADDING,
        ),
        child: Column(
          children: [
            basicDetailItem(
              Strings.id,
              widget.item.id.toString(),
              Strings.created_date_time,
              Utils.changeDateFormat(widget.item.createdOn, "dd-MM-yyyy HH:mm"),
            ),
            SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.create_by,),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget( widget.item.createdByName, AppTheme.title_dark),
                    ],
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.created_staff_team,),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      InkWell(onTap: widget.onTap,child: valueWidget(widget.item.createdByName, AppTheme.colorPrimary),),
                    ],
                  ),
                ),
              ],
            ),
            SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.notes,),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),

                      CustomText(
                        title: (widget.item.notes.isNotEmpty)
                            ? widget.item.notes
                            : "-",
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small+1,
                        fontWeight: FontWeight.normal,
                      ),
                    ],
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}
basicDetailItem(String title1, String? value1, String title2, String? value2) {
  return Row(
    mainAxisSize: MainAxisSize.max,
    crossAxisAlignment: CrossAxisAlignment.center,
    children: [
      Expanded(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            titleWidget(title1),
            const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
            valueWidget(value1, AppTheme.title_dark),
          ],
        ),
      ),
      Expanded(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            titleWidget(title2),
            const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
            valueWidget(value2, AppTheme.title_dark),
          ],
        ),
      ),
    ],
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
valueWidget(String? value, Color txtColors) {
  return CustomText(
    title: value!.isNotEmpty ? value : "",
    colors: txtColors,
    textAlign: TextAlign.start,
    fontSize: AppTheme.small + 1,
    fontWeight: FontWeight.normal,
    maxLines: 2,
  );
}