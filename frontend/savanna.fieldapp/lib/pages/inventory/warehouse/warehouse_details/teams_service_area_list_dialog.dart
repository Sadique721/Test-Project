import 'dart:convert';
import 'dart:developer';

import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class TeamServiceAreaListDialog extends StatefulWidget {
  final List<dynamic>? itemList;
  final String fromFor;

  const TeamServiceAreaListDialog({
    Key? key,
    this.itemList,
    required this.fromFor,
  }) : super(key: key);

  @override
  _TeamServiceAreaListState createState() => _TeamServiceAreaListState();
}

class _TeamServiceAreaListState extends State<TeamServiceAreaListDialog> {
  List<dynamic>? itemsLst = [];

  @override
  void initState() {
    super.initState();
    setState(() {
      itemsLst!.addAll(widget.itemList!);
    });
  }

  @override
  Widget build(BuildContext context) {
    String title = "";
    if (widget.fromFor.equalsIgnoreCase(Strings.teams)) {
      title = "${Strings.teams} List";
    } else if (widget.fromFor.equalsIgnoreCase(Strings.parent_service_area)) {
      title = "${Strings.parent_service_area} List";
    }else if (widget.fromFor.equalsIgnoreCase(Strings.service_area_name)) {
      title = "${Strings.service_area_name} List";
    }
    return contentBox(context, title);
  }

  contentBox(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: Stack(
        children: [
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
                          horizontal: Constant.SCREEN_PADDING),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: title,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.large,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING - 5),
                      child: Divider(
                        height: 5,
                        color: AppTheme.dividerColor,
                        thickness: 1,
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Flexible(
                        child: ListView.builder(
                          shrinkWrap: true,
                          primary: false,
                          itemCount: itemsLst!.length,
                          itemBuilder: (context, index) {
                            var item = itemsLst![index];
                            return Column(
                              children: [
                                Padding(
                                  padding: const EdgeInsets.symmetric(
                                      vertical: Constant.SMALL_PADDING + 1,
                                      horizontal: Constant.MEDIUM_PADDING),
                                  child: Row(
                                    children: [
                                      CustomText(
                                        title: "${widget.fromFor}:",
                                        colors: AppTheme.colorPrimary,
                                        textAlign: TextAlign.start,
                                        fontSize: AppTheme.small + 1,
                                        fontWeight: FontWeight.w400,
                                        decoration: TextDecoration.none,
                                        maxLines: 1,
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      CustomText(
                                        title:item.name,
                                        textAlign: TextAlign.start,
                                        colors: AppTheme.lable_noramal,
                                        fontSize: AppTheme.small + 1,
                                        fontWeight: FontWeight.w700,
                                      ),
                                    ],
                                  ),
                                ),
                                index == (itemsLst!.length - 1)
                                    ? Container()
                                    :  Padding(
                                  padding: const EdgeInsets.symmetric(
                                      horizontal:
                                      Constant.SCREEN_PADDING - 5),
                                  child: Divider(
                                    height: 1,
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
        ],
      ),
    );
  }

}