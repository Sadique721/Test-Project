import 'package:savbill/pages/customer/model/response/nearby_devices_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class NearbyDeviceDialog extends StatefulWidget {
  List<NearByDeviceDetail> detailsInfo;
  String title;

  NearbyDeviceDialog({
    Key? key,
    required this.title,
    required this.detailsInfo,
  }) : super(key: key);

  @override
  _NearbyDeviceDialogState createState() => _NearbyDeviceDialogState();
}

class _NearbyDeviceDialogState extends State<NearbyDeviceDialog> {
  List<NearByDeviceDetail> deviceItemsLst = [];

  @override
  void initState() {
    setState(() {
      deviceItemsLst.addAll(widget.detailsInfo);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      ),
      elevation: 0,
      clipBehavior: Clip.antiAliasWithSaveLayer,
      insetPadding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      backgroundColor: Colors.transparent,
      child: contentBox(context),
    );
  }

  contentBox(context) {
    return Stack(children: [
      AlertDialog(
        insetPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING * 2,
        ),
        contentPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING,
        ),
        clipBehavior: Clip.antiAliasWithSaveLayer,
        backgroundColor: AppTheme.colorWhite,
        shape: const RoundedRectangleBorder(
            borderRadius:
                BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
        content: Container(
          width: MediaQuery.of(context).size.width,
          color: AppTheme.colorWhite,
          child: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.MEDIUM_PADDING),
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: CustomText(
                      title: widget.title,
                      colors: AppTheme.colorPrimary,
                      fontSize: AppTheme.large,
                      fontWeight: FontWeight.w600,
                      maxLines: 1,
                    ),
                  ),
                ),
                const SizedBox(height: Constant.MEDIUM_PADDING),
                Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.MEDIUM_PADDING),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Expanded(
                        flex: 2,
                        child: CustomText(
                          title: Strings.name,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      Expanded(
                        flex: 3,
                        child: CustomText(
                          title: "${Strings.lat}-${Strings.long}",
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      Expanded(
                        flex: 2,
                        child: CustomText(
                          title: Strings.distance_km,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      Expanded(
                        flex: 3,
                        child: CustomText(
                          title: Strings.address,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: Constant.SMALL_PADDING),
                Flexible(
                    child: ListView.builder(
                  shrinkWrap: true,
                  primary: false,
                  itemCount: deviceItemsLst.length,
                  itemBuilder: (context, index) {
                    NearByDeviceDetail item = deviceItemsLst[index];
                    return Column(
                      children: [
                        InkWell(
                          onTap: () {},
                          child: Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.SMALL_PADDING + 1,
                                horizontal: Constant.MEDIUM_PADDING),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Expanded(
                                  flex: 2,
                                  child: CustomText(
                                    title: item.name ?? "-",
                                    colors: AppTheme.lable_noramal,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w400,
                                  ),
                                ),
                                Expanded(
                                  flex: 3,
                                  child: CustomText(
                                    title:
                                        "${item.latitude ?? "-"}\n${item.longitude ?? "-"}",
                                    colors: AppTheme.lable_noramal,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w400,
                                  ),
                                ),
                                Expanded(
                                  flex: 2,
                                  child: CustomText(
                                    title: item.distance ?? "-",
                                    colors: AppTheme.lable_noramal,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w400,
                                  ),
                                ),
                                Expanded(
                                  flex: 3,
                                  child: CustomText(
                                    title: item.address ?? "-",
                                    colors: AppTheme.lable_noramal,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w400,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                        ((deviceItemsLst.length - 1) == index)
                            ? Container()
                            : Padding(
                                padding: const EdgeInsets.symmetric(
                                    horizontal: Constant.SCREEN_PADDING - 5),
                                child: Divider(
                                  height: 5,
                                  color: AppTheme.dividerColor,
                                  thickness: 0.5,
                                ),
                              ),
                      ],
                    );
                  },
                )),
                const SizedBox(height: Constant.SMALL_PADDING),
              ]),
        ),
      ),
      Positioned(
        child: GestureDetector(
          onTap: () {
            Get.back();
          },
          child: Align(
            alignment: Alignment.topRight,
            child: Icon(Icons.close, color: AppTheme.colorWhite),
          ),
        ),
      ),
    ]);
  }
}
