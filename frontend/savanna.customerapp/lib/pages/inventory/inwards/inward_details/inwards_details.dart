import 'package:savbill/pages/inventory/inwards/inward_details/inwards_details_controller.dart';
import 'package:savbill/pages/inventory/module/response/inwards_details_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class InwardDetails extends StatefulWidget {
  @override
  _InwardDetailsState createState() => _InwardDetailsState();
}

class _InwardDetailsState extends State<InwardDetails> {
  final inwardsDetailsController = Get.put(InwardsDetailsController());
  final inwardsDetailsFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<InwardsDetailsController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: inwardsDetailsController.isLoading),
        ]);
      }), /**/
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
          width: MediaQuery.of(context).size.width,
          height: MediaQuery.of(context).size.height,
          margin: const EdgeInsets.only(
            top: Constant.SMALL_PADDING,
          ),
          color: AppTheme.colorBG,
          child: SingleChildScrollView(
            physics: const ScrollPhysics(),
            child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    margin: const EdgeInsets.only(
                        top: Constant.SMALL_PADDING,
                        left: Constant.SCREEN_PADDING),
                    child: CustomText(
                      title: "${inwardsDetailsController.inwardNumber} ${Strings.inwards}",
                      fontSize: AppTheme.medium,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  ),
                  basicDetailView(),
                  const SizedBox(
                    height: Constant.VERY_SMALL_PADDING,
                  ),
                  itemDetailView(),
                  const SizedBox(
                    height: Constant.VERY_SMALL_PADDING,
                  ),

                ]),
          ))
    );
  }

  basicDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.basic_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.inward_name,
                          inwardsDetailsController.inwardsDetailData?.productId?.name ?? "-",
                          Strings.qty,
                          inwardsDetailsController.inwardsDetailData?.qty.toString() ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.transit_qty,
                          inwardsDetailsController.inwardsDetailData?.inTransitQty.toString() ?? "-",
                          Strings.status,
                          inwardsDetailsController.inwardsDetailData?.status ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.destination_type,
                          inwardsDetailsController.inwardsDetailData?.destinationType ?? "-",
                          Strings.destination,
                          inwardsDetailsController.inwardsDetailData?.destination ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.category_type,
                          inwardsDetailsController.inwardsDetailData?.categoryType ?? "-",
                          Strings.source,
                          inwardsDetailsController.inwardsDetailData?.source ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.source_type,
                          inwardsDetailsController.inwardsDetailData?.sourceType ?? "-",
                          Strings.inward_date,
                          inwardsDetailsController.inwardsDetailData?.inwardDateTime ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.warranty_start_date,
                          inwardsDetailsController.inwardsDetailData?.startDateTime ?? "-",
                          Strings.warranty_end_date,
                          inwardsDetailsController.inwardsDetailData?.expiryDateTime ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.description,
                          inwardsDetailsController.inwardsDetailData?.description ?? "-",
                          "",
                          "",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }


  itemDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.item_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.item_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            (inwardsDetailsController.inwardsDetailData != null && inwardsDetailsController.inwardsDetailData!.itemList != null && inwardsDetailsController.inwardsDetailData!.itemList!.isNotEmpty)
                ? ListView.builder(
                physics: const NeverScrollableScrollPhysics(),
                scrollDirection: Axis.vertical,
                shrinkWrap: true,
                itemCount: inwardsDetailsController.inwardsDetailData!.itemList!.length,
                itemBuilder: (context, ii) {
                  ItemList? items = inwardsDetailsController.inwardsDetailData!.itemList![ii];
                  int? lstLength = inwardsDetailsController.inwardsDetailData!.itemList!.length;
                  return Padding(
                    padding: EdgeInsets.only(
                        top: (ii == 0)
                            ? Constant.SMALL_PADDING
                            : Constant.EXPANTABLE_ITEM_MARGIN,
                        left: Constant.EXPANTABLE_ITEM_MARGIN,
                        right: Constant.EXPANTABLE_ITEM_MARGIN,
                        bottom: (ii == (lstLength - 1))
                            ? Constant.EXPANTABLE_ITEM_MARGIN
                            : 0),
                    child: Container(
                      decoration: BoxDecoration(
                        color: AppTheme.expantableItemBg,
                        border:
                        Border.all(color: AppTheme.expantableItemBg),
                        borderRadius: const BorderRadius.all(
                          Radius.circular(3),
                        ),
                      ),
                      child: Padding(
                        padding:
                        const EdgeInsets.all(Constant.SMALL_PADDING),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            basicDetailItem(
                                Strings.asset_id,
                                items.assetId ?? "-",
                                Strings.mac_address,
                                items.macAddress ?? "-",
                                null,
                                false,
                                false),
                            const SizedBox(height: Constant.SMALL_PADDING),
                            basicDetailItem(
                                Strings.serial_no,
                                items.serialNumber ?? "-",
                                Strings.warranty_status,
                                items.oemWarrantyStatus ?? "-",
                                null,
                                false,
                                false),
                            const SizedBox(height: Constant.SMALL_PADDING),
                            basicDetailItem(
                                Strings.warranty_period,
                                items.oemWarrantyRemainingDays ?? "-",
                                "",
                               "",
                                null,
                                false,
                                false),
                            const SizedBox(height: Constant.SMALL_PADDING),
                          ],
                        ),
                      ),
                    ),
                  );
                })
                : Container(),
          ],
        ),
      ),
    );
  }

  basicDetailItem(String title1, String? value1, String title2, String? value2,
      Function()? onTap1, bool? isLink1, bool? isLink2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              InkWell(
                child: valueWidget(value1, isLink1!),
                onTap: onTap1,
              ),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2, isLink2!),
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

  valueWidget(String? value, bool isLinkable) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      decoration: isLinkable ? TextDecoration.underline : TextDecoration.none,
      maxLines: 2,
    );
  }

  _appBar() {
    return DynamicAppBar(
        Strings.inwards_details,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

}