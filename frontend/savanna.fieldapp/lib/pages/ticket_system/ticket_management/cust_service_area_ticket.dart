import 'package:savbill/pages/ticket_system/model/response/create_ticket_active_service_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_get_serial_number_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustServiceAreaDialog extends StatefulWidget {
  final CustServiceAreaAction serviceAreaAction;
  final List<GetActiveServiceDataList> itemsOrgLst;
  final List<SerialNumberDataList> serialItemsOrgLst;
  final String fromFor;

  const CustServiceAreaDialog({
    Key? key,
    required this.serviceAreaAction,
    required this.itemsOrgLst,
    required this.serialItemsOrgLst,
    required this.fromFor,
  }) : super(key: key);

  @override
  _ServiceAreaState createState() => _ServiceAreaState();
}

class _ServiceAreaState extends State<CustServiceAreaDialog> {
  List<GetActiveServiceDataList> itemsLst = [];
  List<SerialNumberDataList> serialItemsLst = [];

  @override
  void initState() {
    super.initState();
    setState(() {
      if (widget.fromFor.equalsIgnoreCase(Strings.service)) {
        itemsLst.addAll(widget.itemsOrgLst);
      } else if (widget.fromFor.equalsIgnoreCase(Strings.serial_no)) {
        serialItemsLst.addAll(widget.serialItemsOrgLst);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    String title = "";
    if (widget.fromFor.equalsIgnoreCase(Strings.service)) {
      title = "${Strings.select} ${Strings.service}";
    } else if (widget.fromFor.equalsIgnoreCase(Strings.serial_no)) {
      title = "${Strings.select} ${Strings.serial_no}";
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
                        child: widget.fromFor.equalsIgnoreCase(Strings.service)
                            ? itemsLst.isNotEmpty
                                ? ListView.builder(
                                    shrinkWrap: true,
                                    primary: false,
                                    itemCount: itemsLst.length,
                                    itemBuilder: (context, index) {
                                      GetActiveServiceDataList item =
                                          itemsLst[index];
                                      return Column(
                                        children: [
                                          InkWell(
                                            onTap: () {
                                              for (var f in itemsLst) {
                                                if (f.id == item.id!) {
                                                  if (f.selected == null) {
                                                    f.selected = true;
                                                  } else {
                                                    f.selected = !f.selected!;
                                                  }
                                                  break;
                                                }
                                              }
                                              setState(() {
                                                itemsLst = itemsLst;
                                              });
                                            },
                                            child: Padding(
                                              padding:
                                                  const EdgeInsets.symmetric(
                                                      vertical: Constant
                                                              .SMALL_PADDING +
                                                          1,
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING),
                                              child: Row(
                                                children: [
                                                  item.selected == true
                                                      ? Icon(
                                                          Icons.check,
                                                          color: AppTheme
                                                              .colorPrimary,
                                                          size: Constant
                                                              .ICON_SIZE_M,
                                                        )
                                                      : const Icon(
                                                          Icons.check,
                                                          color: Colors.white,
                                                          size: Constant
                                                              .ICON_SIZE_M,
                                                        ),
                                                  const SizedBox(
                                                    width:
                                                        Constant.SMALL_PADDING,
                                                  ),
                                                  CustomText(
                                                    title: item.serviceName,
                                                    textAlign: TextAlign.start,
                                                    colors: item.selected !=
                                                                null &&
                                                            item.selected ==
                                                                true
                                                        ? AppTheme.colorPrimary
                                                        : AppTheme
                                                            .lable_noramal,
                                                    fontSize:
                                                        AppTheme.small + 1,
                                                    fontWeight:
                                                        item.selected != null &&
                                                                item.selected ==
                                                                    true
                                                            ? FontWeight.w500
                                                            : FontWeight.w700,
                                                  ),
                                                ],
                                              ),
                                            ),
                                          ),
                                          index == (itemsLst.length - 1)
                                              ? Container()
                                              : Padding(
                                                  padding: const EdgeInsets
                                                          .symmetric(
                                                      horizontal: Constant
                                                              .SCREEN_PADDING -
                                                          5),
                                                  child: Divider(
                                                    height: 5,
                                                    color:
                                                        AppTheme.dividerColor,
                                                    thickness: 0.5,
                                                  ),
                                                ),
                                        ],
                                      );
                                    },
                                  )
                                : const NoDataFound(
                                    height: Constant.REMARKS_VIEW_HEIGHT,
                                  )
                            : serialItemsLst.isNotEmpty
                                ? ListView.builder(
                                    shrinkWrap: true,
                                    primary: false,
                                    itemCount: serialItemsLst.length,
                                    itemBuilder: (context, index) {
                                      SerialNumberDataList item =
                                          serialItemsLst[index];
                                      return Column(
                                        children: [
                                          InkWell(
                                            onTap: () {
                                              for (var f in serialItemsLst) {
                                                if (f.id == item.id!) {
                                                  if (f.selected == null) {
                                                    f.selected = true;
                                                  } else {
                                                    f.selected = !f.selected!;
                                                  }
                                                  break;
                                                }
                                              }
                                              setState(() {
                                                serialItemsLst = serialItemsLst;
                                              });
                                            },
                                            child: Padding(
                                              padding:
                                                  const EdgeInsets.symmetric(
                                                      vertical: Constant
                                                              .SMALL_PADDING +
                                                          1,
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING),
                                              child: Row(
                                                children: [
                                                  item.selected == true
                                                      ? Icon(
                                                          Icons.check,
                                                          color: AppTheme
                                                              .colorPrimary,
                                                          size: Constant
                                                              .ICON_SIZE_M,
                                                        )
                                                      : const Icon(
                                                          Icons.check,
                                                          color: Colors.white,
                                                          size: Constant
                                                              .ICON_SIZE_M,
                                                        ),
                                                  const SizedBox(
                                                    width:
                                                        Constant.SMALL_PADDING,
                                                  ),
                                                  CustomText(
                                                    title: item.serialNumber,
                                                    textAlign: TextAlign.start,
                                                    colors: item.selected !=
                                                                null &&
                                                            item.selected ==
                                                                true
                                                        ? AppTheme.colorPrimary
                                                        : AppTheme
                                                            .lable_noramal,
                                                    fontSize:
                                                        AppTheme.small + 1,
                                                    fontWeight:
                                                        item.selected != null &&
                                                                item.selected ==
                                                                    true
                                                            ? FontWeight.w500
                                                            : FontWeight.w700,
                                                  ),
                                                ],
                                              ),
                                            ),
                                          ),
                                          index == (serialItemsLst.length - 1)
                                              ? Container()
                                              : Padding(
                                                  padding: const EdgeInsets
                                                          .symmetric(
                                                      horizontal: Constant
                                                              .SCREEN_PADDING -
                                                          5),
                                                  child: Divider(
                                                    height: 5,
                                                    color:
                                                        AppTheme.dividerColor,
                                                    thickness: 0.5,
                                                  ),
                                                ),
                                        ],
                                      );
                                    },
                                  )
                                : const NoDataFound(
                                    height: Constant.REMARKS_VIEW_HEIGHT,
                                  )),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      children: [
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              if (widget.fromFor
                                  .equalsIgnoreCase(Strings.service)) {
                                validateSelection();
                              } else if (widget.fromFor
                                  .equalsIgnoreCase(Strings.serial_no)) {
                                validateSerialNoSelection();
                              }
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomLeft: Radius.circular(
                                        Constant.SMALL_PADDING)),
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
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomRight: Radius.circular(
                                        Constant.SMALL_PADDING)),
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
    List<GetActiveServiceDataList> selectedItem = [];
    for (var element in itemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      widget.serviceAreaAction.serviceAreaBtnAction(
          identifier: widget.fromFor, selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.ERROR, "Please select at-lease one item",
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

  validateSerialNoSelection() {
    List<SerialNumberDataList> selectedSerialItem = [];
    for (var element in serialItemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedSerialItem.add(element);
      }
    }
    if (selectedSerialItem.isNotEmpty) {
      widget.serviceAreaAction.serialNoBtnAction(
          identifier: widget.fromFor, selectedSerialItem: selectedSerialItem);
    } else {
      Utils.showSnackbar(Strings.ERROR, "Please select at-lease one item",
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }
}

abstract class CustServiceAreaAction {
  void serviceAreaBtnAction(
      {String identifier, List<GetActiveServiceDataList> selectedItem});

  void serialNoBtnAction(
      {String identifier, List<SerialNumberDataList> selectedSerialItem});
}
