import 'package:savbill/pages/inventory/module/response/get_all_team_based_inventory_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ServiceAreaSelectionDialog extends StatefulWidget {
  final ServiceAreaSelectionAction serviceAreaSelectionAction;
  final List<StaffServiceAreaDetail>? itemsOrgLst;
  final List<ProblemDomainDetail>? parentCategoryList;
  final String fromFor;

  const ServiceAreaSelectionDialog({
    Key? key,
    required this.serviceAreaSelectionAction,
    this.itemsOrgLst,
    this.parentCategoryList,
    required this.fromFor,
  }) : super(key: key);

  @override
  _ServiceAreaSelectionState createState() => _ServiceAreaSelectionState();
}

class _ServiceAreaSelectionState extends State<ServiceAreaSelectionDialog> {
  List<StaffServiceAreaDetail>? itemsLst = [];
  List<ProblemDomainDetail>? itemsParentList = [];

  @override
  void initState() {
    super.initState();
    setState(() {
      if(widget.itemsOrgLst!.isNotEmpty) {
        itemsLst!.addAll(widget.itemsOrgLst!);
      }else if(widget.parentCategoryList!.isNotEmpty) {
        itemsParentList!.addAll(widget.parentCategoryList!);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    String title = "";
    if (widget.fromFor.equalsIgnoreCase(Strings.service_area)) {
      title = "${Strings.select} ${Strings.service_area}";
    } else if (widget.fromFor.equalsIgnoreCase(Strings.parent_service_area)) {
      title = "${Strings.select} ${Strings.parent_service_area}";
    }else if (widget.fromFor.equalsIgnoreCase(Strings.parent_category)) {
      title = "${Strings.select} ${Strings.parent_category}";
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
                              StaffServiceAreaDetail item = itemsLst![index];
                              return Column(
                                children: [
                                  InkWell(
                                    onTap: () {
                                      for (var f in itemsLst!) {
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
                                      padding: const EdgeInsets.symmetric(
                                          vertical: Constant.SMALL_PADDING + 1,
                                          horizontal: Constant.MEDIUM_PADDING),
                                      child: Row(
                                        children: [
                                          item.selected == true
                                              ? Icon(
                                                  Icons.check,
                                                  color: AppTheme.colorPrimary,
                                                  size: Constant.ICON_SIZE_M,
                                                )
                                              : const Icon(
                                                  Icons.check,
                                                  color: Colors.white,
                                                  size: Constant.ICON_SIZE_M,
                                                ),
                                          const SizedBox(
                                            width: Constant.SMALL_PADDING,
                                          ),
                                          CustomText(
                                            title: item.name,
                                            textAlign: TextAlign.start,
                                            colors: item.selected != null &&
                                                    item.selected == true
                                                ? AppTheme.colorPrimary
                                                : AppTheme.lable_noramal,
                                            fontSize: AppTheme.small + 1,
                                            fontWeight: item.selected != null &&
                                                    item.selected == true
                                                ? FontWeight.w500
                                                : FontWeight.w700,
                                          ),
                                        ],
                                      ),
                                    ),
                                  ),
                                  index == (itemsLst!.length - 1)
                                      ? Container()
                                      : Padding(
                                          padding: const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.SCREEN_PADDING - 5),
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
    List<StaffServiceAreaDetail> selectedItem = [];
    for (var element in itemsLst!) {
      if (element.selected != null && element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      widget.serviceAreaSelectionAction.serviceAreaSelectionBtnAction(
          identifier: widget.fromFor, selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.INFO, "Please select at-lease one item",
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }
  }
}

abstract class ServiceAreaSelectionAction {
  void serviceAreaSelectionBtnAction(
      {String identifier, List<StaffServiceAreaDetail> selectedItem});
}
