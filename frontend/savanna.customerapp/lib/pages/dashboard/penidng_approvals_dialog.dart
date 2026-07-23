import 'package:savbill/pages/dashboard/model/data_list_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PendingApprovalsDialog extends StatefulWidget {
  final PendingApprovalsItemAction pendingApprovalsItemAction;

  const PendingApprovalsDialog({
    Key? key,
    required this.pendingApprovalsItemAction,
  }) : super(key: key);

  @override
  _PendingApprovalState createState() => _PendingApprovalState();
}

class _PendingApprovalState extends State<PendingApprovalsDialog> {
  List<ItemList>? item = [];

  @override
  void initState() {
    super.initState();
    setState(() {
      item!.add(ItemList(id: 1, title: Strings.customer_pending_approvals));
      item!.add(ItemList(
          id: 2, title: Strings.customer_termination_pending_approvals));
      item!.add(ItemList(id: 3, title: Strings.plans_pending_approvals));
      item!.add(ItemList(id: 4, title: Strings.plans_group_pending_approvals));
      item!.add(ItemList(id: 5, title: Strings.payment_pending_approvals));
      item!.add(ItemList(id: 6, title: Strings.ticket_pending_approvals));
      item!.add(
          ItemList(id: 7, title: Strings.change_discount_pending_approvals));
      item!.add(ItemList(id: 8, title: Strings.invoices_pending_approvals));
      item!.add(ItemList(id: 9, title: Strings.partner_payment));
      item!.add(ItemList(id: 10, title: Strings.open_lead));
      item!.add(ItemList(id: 11, title: Strings.customer_document));
      item!.add(ItemList(id: 12, title: Strings.special_plan_approval));
      item!.add(ItemList(id: 13, title: Strings.inventory_pending_approval));
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

  contentBox(BuildContext context) {
    return Stack(children: [
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
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Align(
                  alignment: Alignment.topCenter,
                  child:  Container(
                    color: AppTheme.colorPrimary,
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING,
                        vertical: Constant.MEDIUM_PADDING),
                    child: Align(
                      alignment: Alignment.centerLeft,
                      child: CustomText(
                        title: Strings.pending_approvals,
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.large,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ),


                const SizedBox(height: Constant.LARGE_PADDING),
                Expanded(
                    child: SingleChildScrollView(
                  child: ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: item!.length,
                    itemBuilder: (context, index) {
                      ItemList detail = item![index];
                      return InkWell(
                        onTap: () {
                          widget.pendingApprovalsItemAction
                              .pendingApprovalsItemAction(item: detail);
                        },
                        child: Container(
                          margin: const EdgeInsets.symmetric(
                              vertical: Constant.VERY_SMALL_PADDING,
                              horizontal: Constant.SMALL_PADDING),
                          decoration: BoxDecoration(
                            color: AppTheme.expantableItemBg,
                            border: Border.all(color: AppTheme.colorLightGrey),
                            borderRadius: const BorderRadius.all(
                              Radius.circular(4),
                            ),
                          ),
                          child: Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.LARGE_PADDING - 2,
                                horizontal: Constant.MEDIUM_PADDING),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Expanded(
                                  child: CustomText(
                                    title: detail.title,
                                    textAlign: TextAlign.start,
                                    colors: AppTheme.title_dark,
                                    fontSize: AppTheme.small + 2,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                                Icon(
                                  Icons.arrow_forward_ios_rounded,
                                  size: 16,
                                  color: AppTheme.statusClosedGreen,
                                )
                              ],
                            ),
                          ),
                        ),
                      );
                    },
                  ),
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

abstract class PendingApprovalsItemAction {
  void pendingApprovalsItemAction({ItemList item});
}
