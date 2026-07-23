import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class AllInventoriesItem extends StatelessWidget {
  InventoryListDetail item;
  ValueChanged<bool?>? onSelectChanged;

  AllInventoriesItem({
    Key? key,
    required this.item,
    this.onSelectChanged,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color statusColor = AppTheme.colorBlueRView;

    if (item.itemStatus != null &&
        item.itemStatus!.equalsIgnoreCase("Returned")|| item.itemStatus != null &&
        item.itemStatus!.equalsIgnoreCase("Maintenance")) {
      statusColor = AppTheme.colorGrey;
    } else if (item.itemStatus!.equalsIgnoreCase("UnAllocated")) {
      statusColor = AppTheme.colorBlueRView;
    } else if (item.itemStatus!.equalsIgnoreCase("Allocated")) {
      statusColor = AppTheme.colorGreen;
    }else if (item.itemStatus!.equalsIgnoreCase("Defective")) {
      statusColor = AppTheme.colorRed;
    }else if(item.itemStatus!.equalsIgnoreCase("Staff Allocated")){
      statusColor = AppTheme.colorGreen;
    }


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
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    Center(
                      child: SizedBox(
                        width: 15,
                        height: 15,
                        child: Checkbox(
                          value: item.selected ?? false,
                          activeColor: AppTheme.colorPrimary,
                          onChanged: onSelectChanged,
                        ),
                      ),
                    ),
                   const SizedBox(width: Constant.SMALL_PADDING,),
                   CustomText(
                            title: item.serialNumber ?? "",
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            maxLines: 2,
                            height: 1,
                            fontWeight: FontWeight.w500),
                  ],
                ),

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
                            Constant.LARGE_PADDING),color: statusColor),
                    child: CustomText(
                        title:  item.itemStatus,
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
          ),
          Divider(
            color: AppTheme.title_dark,
            thickness: 0.5,
            height: Constant.MEDIUM_PADDING,
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.type,
                item.condition != null && item.condition!.isNotEmpty
                    ? item.condition!
                    : "-",
                Strings.warranty,
                item.warranty != null && item.warranty!.isNotEmpty
                    ? "${item.warranty!} (${item.warrantyPeriod!})"
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.ownership_status,
                item.ownershipType != null && item.ownershipType!.isNotEmpty
                    ? item.ownershipType
                    : "-",
                Strings.owner_type,
                item.ownerType != null && item.ownerType != null
                    ? item.ownerType!
                    : ""),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.owner_name,
                item.ownerName != null && item.ownerName != null
                    ? item.ownerName!.toString()
                    : "-",
                Strings.download,
                "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
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
              valueWidget(value1),
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
              valueWidget(value2),
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
      fontSize: AppTheme.verySmall+1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
