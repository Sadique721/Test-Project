import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../widgets/title_widge.dart';

class DiscountReAssignWorkFlowDialog extends StatefulWidget {
  final DiscountReAssignWorkFlowAction discountReAssignWorkFlowAction;
  final List<ReassignWorkflowList> itemsOrgLst;

  const DiscountReAssignWorkFlowDialog({
    Key? key,
    required this.discountReAssignWorkFlowAction,
    required this.itemsOrgLst,
  }) : super(key: key);

  @override
  _DiscountReAssignWorkFlowState createState() => _DiscountReAssignWorkFlowState();
}

class _DiscountReAssignWorkFlowState extends State<DiscountReAssignWorkFlowDialog> {
  List<ReassignWorkflowList> itemsLst = [];
  TextEditingController controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    setState(() {
      itemsLst.addAll(widget.itemsOrgLst);
    });
  }

  @override
  Widget build(BuildContext context) {
    String title = Strings.assign_staff;
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
            backgroundColor: AppTheme.colorPrimary,
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
                    Container(
                      color: AppTheme.colorPrimary,
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.MEDIUM_PADDING,
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
                          vertical: Constant.SMALL_PADDING,
                          horizontal: Constant.SCREEN_PADDING),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: Strings.select_staff,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Container(
                      color: AppTheme.colorBlueRoundView,
                      margin: const EdgeInsets.symmetric(
                          horizontal: Constant.SMALL_PADDING),
                      padding: const EdgeInsets.symmetric(
                        vertical: Constant.SMALL_PADDING,
                      ),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          CustomText(
                            title: Strings.name,
                            colors: AppTheme.title_dark,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w500,
                          ),
                          CustomText(
                            title: Strings.username,
                            colors: AppTheme.title_dark,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w500,
                          ),
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SMALL_PADDING),
                      child: Divider(
                        height: 2,
                        color: AppTheme.dividerColor,
                        thickness: 0.5,
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Flexible(
                      child: ListView.builder(
                        shrinkWrap: true,
                        primary: false,
                        itemCount: itemsLst.length,
                        itemBuilder: (context, index) {
                          ReassignWorkflowList item = itemsLst[index];
                          return Column(
                            // mainAxisAlignment: MainAxisAlignment.spaceAround,
                            // crossAxisAlignment: CrossAxisAlignment.center,
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
                                    } else {
                                      f.selected = false;
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
                                    crossAxisAlignment:
                                    CrossAxisAlignment.center,
                                    mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
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
                                      Expanded(
                                        child: Row(
                                          mainAxisAlignment:
                                          MainAxisAlignment.spaceEvenly,
                                          crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                          children: [
                                            CustomText(
                                              title:
                                              "${item.firstname!} ${item.lastname!}",
                                              textAlign: TextAlign.start,
                                              colors: item.selected != null &&
                                                  item.selected == true
                                                  ? AppTheme.colorPrimary
                                                  : AppTheme.title_dark,
                                              fontSize: AppTheme.small + 1,
                                              fontWeight: FontWeight.w500,
                                            ),
                                            const SizedBox(
                                              width: Constant.MEDIUM_PADDING,
                                            ),
                                            CustomText(
                                              title: item.username!,
                                              textAlign: TextAlign.start,
                                              colors: item.selected != null &&
                                                  item.selected == true
                                                  ? AppTheme.colorPrimary
                                                  : AppTheme.title_dark,
                                              fontSize: AppTheme.small + 1,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ],
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                              index == (itemsLst.length - 1)
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
                      ),
                    ),
                    const SizedBox(height: Constant.VERY_SMALL_PADDING),
                    // Padding(
                    //   padding: const EdgeInsets.symmetric(
                    //       horizontal: Constant.SMALL_PADDING),
                    //   child: Divider(
                    //     height: 2,
                    //     color: AppTheme.dividerColor,
                    //     thickness: 0.5,
                    //   ),
                    // ),
                    // const SizedBox(height: Constant.VERY_SMALL_PADDING),
                    // reviewEditor(),
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
                                Strings.assign,
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
    ReassignWorkflowList? selectedItem;
    for (var element in itemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedItem = element;
      }
    }
    if (selectedItem != null) {
      widget.discountReAssignWorkFlowAction.discountReAssignWorkFlowBtnAction(
          selectedItem: selectedItem, remarkController: controller);
    } else {
      Utils.showSnackbar(Strings.INFO, "Please select at-lease one staff",
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }
  }

  reviewEditor() {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.SMALL_PADDING),
          InputTitleRequire(title: Strings.remarks, require: false),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(7.0),
              color: AppTheme.colorWhite,
            ),
            child: TextFormField(
              controller: controller,
              maxLines: 3,
              maxLength: 250,
              style: const TextStyle(fontSize: AppTheme.medium),
              decoration: InputDecoration(
                hintText: Strings.remarks,
                alignLabelWithHint: true,
                contentPadding:
                const EdgeInsets.all(Constant.TEXT_FIELD_CONTENT_PADDING),
                focusColor: Colors.transparent,
                focusedBorder: OutlineInputBorder(
                  borderRadius:
                  BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                  borderSide:
                  BorderSide(color: AppTheme.colorPrimary, width: 1.0),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius:
                  BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                  borderSide: BorderSide(
                    color: AppTheme.colorIconGrey,
                    width: 1.0,
                  ),
                ),
                border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(
                        Constant.TEXT_FIELD_CONTENT_PADDING)),
                isDense: true,
                labelStyle: TextStyle(
                  color: AppTheme.colorGrey,
                  fontSize: AppTheme.medium,
                  fontWeight: FontWeight.normal,
                  height: 1,
                  fontFamily: AppTheme.appFontName,
                  decoration: TextDecoration.none,
                ),
                counterText: "",
              ),
              keyboardType: TextInputType.multiline,
              validator: (value) {
                return null;
              },
            ),
          ),
          const SizedBox(height: Constant.SMALL_PADDING),
        ],
      ),
    );
  }
}

abstract class DiscountReAssignWorkFlowAction {
  void discountReAssignWorkFlowBtnAction({
    ReassignWorkflowList selectedItem,
    TextEditingController remarkController,
  });
}
