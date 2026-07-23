import 'package:savbill/pages/customer_charge/add_charge.dart';
import 'package:savbill/pages/customer_charge/charge_management_controller.dart';
import 'package:savbill/pages/customer_charge/charge_plan_item.dart';
import 'package:savbill/pages/customer_charge/response/customer_charge_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ChargeManagement extends StatefulWidget {
  @override
  _ChargeManagementState createState() => _ChargeManagementState();
}

class _ChargeManagementState extends State<ChargeManagement> {
  final chargeManagementController = Get.put(ChargeManagementController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<ChargeManagementController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: chargeManagementController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                      child: CustomText(
                      title: chargeManagementController.customerName,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  Material(
                    color: AppTheme.colorWhite,
                    elevation: 2,
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20)),
                    child: InkWell(
                      onTap: () {
                        openAddChargeScreen();
                      },
                      child: Container(
                        decoration: BoxDecoration(
                          color: AppTheme.colorPrimary,
                          borderRadius:
                              const BorderRadius.all(Radius.circular(20)),
                        ),
                        padding: const EdgeInsets.all(6),
                        child: Icon(
                          Icons.add,
                          color: AppTheme.colorWhite,
                          size: 22,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 1,
              child: (chargeManagementController.chargeOverrideList != null &&
                      chargeManagementController.chargeOverrideList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          chargeManagementController.chargeOverrideList!.length,
                      itemBuilder: (context, index,) {
                        CustChargeOverrideDetail item = chargeManagementController.chargeOverrideList![index];
                        return ChargePlanItem(item: item, index: index,controller:chargeManagementController);
                      })
                  : noDataFound(),
            ),
          ]),
    );
  }

  openAddChargeScreen() async {
    var result = await Get.to(AddCharge(), arguments: {
      Constant.CUSTOMER_DETAIL: chargeManagementController.customerDetail,
      Constant.CUSTOMER_PLAN_GRP_ID: chargeManagementController.custPlanGrpId,
      Constant.FROM: Strings.create_charge,
      Constant.CUSTOMER_PLAN_MAP:
          (chargeManagementController.planMappingList != null &&
                  chargeManagementController.planMappingList!.isNotEmpty)
              ? chargeManagementController.planMappingList
              : []
    });

    if (result != null && result == true) {
      chargeManagementController.getCustomerChargeDetail();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.charge_management, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
