import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_caf/caf_service_management/caf_service_managment_controller.dart';
import 'package:savbill/pages/customer_caf/caf_service_management/list_item_caf_service_management.dart';
import 'package:savbill/pages/service_management/add_service_management_screen.dart';
import 'package:savbill/pages/service_management/list_item_service_management.dart';
import 'package:savbill/pages/service_management/service_managment_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CafServiceManagementView extends StatefulWidget {
  @override
  _ServiceManagementViewState createState() => _ServiceManagementViewState();
}

class _ServiceManagementViewState extends State<CafServiceManagementView> {
  final cafServiceManagementController = Get.put(CafServiceManagementController());
  final serviceManagementFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CafServiceManagementController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: cafServiceManagementController.isLoading),
      ]);
    });
  }

  _body() {
    String? currentCustomerName;
    if (cafServiceManagementController.customerDetail != null) {
      currentCustomerName =
          "${cafServiceManagementController.customerDetail!.title.toString().capitalizeFirst} ${cafServiceManagementController.customerDetail!.custname} ${Strings.service_detail}";
    }
    return Container(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Container(
          color: AppTheme.colorBG,
          width: MediaQuery.of(context).size.width,
          child: Container(
            color: AppTheme.colorBG,
            width: MediaQuery.of(context).size.width,
            child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  Container(
                    height: Constant.MENU_PROFILE_SIZE,
                    padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Expanded(
                          child: CustomText(
                              title: currentCustomerName,
                              colors: AppTheme.colorBlack,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium + 1,
                              fontWeight: FontWeight.w500),
                        ),
                        const SizedBox(
                          width: Constant.VERY_SMALL_PADDING,
                        ),
                        InkWell(
                          onTap: () {
                            addServiceScreen();
                          },
                          child: Container(
                            padding: const EdgeInsets.only(
                                top: Constant.SMALL_PADDING,
                                bottom: Constant.SMALL_PADDING,
                                left: Constant.SMALL_PADDING,
                                right: Constant.SMALL_PADDING),
                            // height: Constant.CARD_BOTTOM_BUTTON_H,
                            alignment: Alignment.center,
                            decoration: BoxDecoration(
                              color: AppTheme.colorPrimary,
                              borderRadius: const BorderRadius.all(
                                  Radius.circular(Constant.ROUNDED_CORNER)),
                            ),
                            child: Row(
                              // crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                const Padding(
                                  padding: EdgeInsets.only(
                                      left: Constant.VERY_SMALL_PADDING,
                                      right: Constant.VERY_SMALL_PADDING),
                                  child: Icon(
                                    size: Constant.ICON_SIZE_M,
                                    Icons.add_circle,
                                    color: Colors.white,
                                  ),
                                ),
                                CustomText(
                                  title: Strings.add_service,
                                  colors: AppTheme.colorWhite,
                                  fontSize: AppTheme.small,
                                  textAlign: TextAlign.center,
                                  fontWeight: FontWeight.normal,
                                )
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    flex: 1,
                    child: (cafServiceManagementController.customerServiceList !=
                                null &&
                            cafServiceManagementController
                                .customerServiceList!.isNotEmpty)
                        ? ListView.builder(
                            scrollDirection: Axis.vertical,
                            itemCount: cafServiceManagementController
                                .customerServiceList!.length,
                            itemBuilder: (context, index) {
                              CustomerPlanServiceDetail item =
                                  cafServiceManagementController
                                      .customerServiceList![index];
                              return ItemListCafServiceManagement(
                                  item: item,
                                  index: index,
                                  controller: cafServiceManagementController);
                            })
                        : noDataFound(),
                  ),
                ]),
          )),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.service_management, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 3.0,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 5,
          width: Constant.BTN_HEIGHT_M - 5,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE + 5,
            width: Constant.ICON_SIZE + 5,
            color: txtColor,
            fit: BoxFit.fitWidth,
          ),
        ),
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
          flex: 2,
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
      fontSize: AppTheme.verySmall,
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



  addServiceScreen() async {
    bool chkRefresh = await Get.to(AddServiceManagement(), arguments: {
      Constant.FROM: Strings.service_management,
      Constant.CUSTOMER_DETAIL: cafServiceManagementController.customerDetail,
      Constant.CUSTOMER_TYPE: cafServiceManagementController.customerType,
    });
    if (chkRefresh) {
      cafServiceManagementController.getPlanServiceData(cafServiceManagementController.customerDetail!.id!);
    }
  }
}
