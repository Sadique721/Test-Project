import 'package:savbill/pages/connection_history/response/connection_history_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class ConnectionHistoryViewItem extends StatelessWidget {
  Content item;
  int index;

  ConnectionHistoryViewItem({Key? key, required this.index, required this.item})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String uploadTime = "-",
        downloadTime = "-",
        createDate = "",
        modifiedDate = "";
    if (item.acctInputOctets != null && item.acctInputOctets!.isNotEmpty) {
      uploadTime =
          (double.parse(item.acctInputOctets!) / 1048576).toStringAsFixed(2);
    }else{
      uploadTime="0.00";
    }

    if (item.acctOutputOctets != null && item.acctOutputOctets!.isNotEmpty) {
      downloadTime =
          (double.parse(item.acctOutputOctets!) / 1048576).toStringAsFixed(2);
    }else{
      downloadTime="0.00";
    }

    if (item.createDate != null && item.createDate!.isNotEmpty) {
      DateTime date =
          DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.createDate!,true).toLocal();
      createDate = DateFormat(Constant.DATE_TIME_FORMAT).format(date);
    }

    if (item.lastModificationDate != null &&
        item.lastModificationDate!.isNotEmpty) {
      DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
          .parse(item.lastModificationDate!,true).toLocal();
      modifiedDate = DateFormat(Constant.DATE_TIME_FORMAT).format(date);
    }

    return Card(
      margin: const EdgeInsets.symmetric(
        vertical: Constant.MEDIUM_PADDING,
        horizontal: Constant.SCREEN_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                CustomText(
                    title: item.userName ?? "",
                    colors: AppTheme.colorPrimary,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500),
                const SizedBox(
                  height: 2,
                ),
                CustomText(
                    title: item.acctSessionTime ?? "",
                    colors: AppTheme.colorPrimary,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w500),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Divider(
              color: AppTheme.title_dark,
              height: 1,
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            detailItem(Strings.nas_ip, item.nasIpAddress ?? "-",
                Strings.frame_ip, item.framedIpAddress ?? "-"),
            const SizedBox(height: Constant.SMALL_PADDING),
            detailItem(Strings.upload_mb, uploadTime, Strings.download_mb,
                downloadTime),
            const SizedBox(height: Constant.SMALL_PADDING),
            detailItem(Strings.create_date, createDate, Strings.modified_date,
                modifiedDate),
            const SizedBox(height: Constant.SMALL_PADDING),
          ],
        ),
      ),
    );
  }

  detailItem(String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
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
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
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
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }
}
