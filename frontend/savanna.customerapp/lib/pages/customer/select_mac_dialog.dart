import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class MacAddressDialog extends StatefulWidget {
  final MacAddressAction macAddressAction;
  final List<InwardMacMapDetail> macAddressLst;

  const MacAddressDialog({
    Key? key,
    required this.macAddressAction,
    required this.macAddressLst,
  }) : super(key: key);

  @override
  _MacAddressState createState() => _MacAddressState();
}

class _MacAddressState extends State<MacAddressDialog> {
  List<InwardMacMapDetail> selectItemsLst = [];

  @override
  void initState() {
    super.initState();
    setState(() {
      selectItemsLst.addAll(widget.macAddressLst);
    });
  }

  @override
  Widget build(BuildContext context) {
    String title = Strings.mac_address;
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
                      itemCount: selectItemsLst.length,
                      itemBuilder: (context, index) {
                        InwardMacMapDetail item = selectItemsLst[index];
                        return Column(
                          children: [
                            InkWell(
                              onTap: () {
                                selectItemsLst.forEach((f) {
                                  if (f.id == item.id) {
                                    f.selected = !f.selected!;
                                  }else{
                                    f.selected =false;
                                  }
                                });
                                setState(() {
                                  selectItemsLst = selectItemsLst;
                                });
                              },
                              child: Padding(
                                padding: const EdgeInsets.symmetric(
                                    vertical: Constant.SMALL_PADDING + 1,
                                    horizontal: Constant.MEDIUM_PADDING),
                                child: Row(
                                  children: [
                                    item.selected == true
                                        ? Icon(
                                            Icons.check_circle,
                                            color: AppTheme.colorPrimary,
                                            size: Constant.ICON_SIZE,
                                          )
                                        : Icon(
                                            Icons.radio_button_off,
                                            color: AppTheme.lable_noramal,
                                            size: Constant.ICON_SIZE,
                                          ),
                                    const SizedBox(
                                      width: Constant.SMALL_PADDING,
                                    ),
                                    CustomText(
                                      title:
                                          "${item.serialNumber!}-${item.macAddress!}",
                                      textAlign: TextAlign.start,
                                      colors: item.selected == true
                                          ? AppTheme.colorPrimary
                                          : AppTheme.lable_noramal,
                                      fontSize: AppTheme.small + 1,
                                      fontWeight: item.selected == true
                                          ? FontWeight.w500
                                          : FontWeight.w700,
                                    ),
                                  ],
                                ),
                              ),
                            ),
                            index == (selectItemsLst.length - 1)
                                ? Container()
                                : Padding(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal:
                                            Constant.SCREEN_PADDING - 5),
                                    child: Divider(
                                      height: 5,
                                      color: AppTheme.lable_noramal,
                                      thickness: 0.1,
                                    ),
                                  ),
                          ],
                        );
                      },
                    )),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      children: [
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              validateSelection();
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorLightGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomLeft: Radius.circular(
                                        Constant.MEDIUM_PADDING)),
                              ),
                              child: Text(
                                Strings.select,
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: AppTheme.medium + 1,
                                  color: AppTheme.colorPositive,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              Get.back();
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorLightGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomRight: Radius.circular(
                                        Constant.MEDIUM_PADDING)),
                              ),
                              child: Text(
                                Strings.cancel,
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: AppTheme.medium + 1,
                                  color: AppTheme.colorNagative,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
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

  validateSelection() {
    List<InwardMacMapDetail> selectedItem = [];
    for (var element in selectItemsLst) {
      if (element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      widget.macAddressAction.macAddressBtnAction(selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.ERROR, Strings.select_at_list_one_item,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }
}

abstract class MacAddressAction {
  void macAddressBtnAction({List<InwardMacMapDetail> selectedItem});
}
