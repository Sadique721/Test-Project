import 'package:savbill/pages/customer/cust_notes/customer_notes_detail_controller.dart';
import 'package:savbill/pages/customer_caf/caf_notes/caf_staff_details/caf_staff_details_screen.dart';
import 'package:savbill/pages/customer_caf/caf_notes/item_list_notes.dart';
import 'package:savbill/pages/customer_caf/response/cust_caf_notes_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
class CustomerNotesDetail extends StatefulWidget {
  @override
  _CafNotesDetailState createState() => _CafNotesDetailState();
}
class _CafNotesDetailState extends State<CustomerNotesDetail> {
  final customerNotesDetailController = Get.put(CustomerNotesDetailController());
  @override
  void initState() {
    super.initState();
  }
  _backScreen() {
    Get.back();
  }
  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerNotesDetailController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerNotesDetailController.isLoading),
      ]);
    });
  }
  _body() {
    String? currentCustomerName;
    if (customerNotesDetailController.customerDetail != null) {
      currentCustomerName =
      "${customerNotesDetailController.customerDetail?.title?.capitalizeFirst ??""} ${customerNotesDetailController.customerDetail?.custname ??""} ${Strings.caf_notes}";
    }
    return Container(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.all(Constant.SMALL_PADDING),
              child: CustomText(
                  title: currentCustomerName,
                  colors: AppTheme.colorBlack,
                  fontSize: AppTheme.medium + 1,
                  fontWeight: FontWeight.w500),
            ),
            customerNotesDetailController.customerNotesDetails!.isNotEmpty  ? Expanded(
              flex: 1,
              child: ListView.builder(
                padding: EdgeInsets.zero,
                scrollDirection: Axis.vertical,
                itemCount: customerNotesDetailController.customerNotesDetails!.length,
                itemBuilder: (context, index) {
                  CafNoteContent item = customerNotesDetailController.customerNotesDetails![index];
                  return ItemListNotes(
                    item: item,
                    index: index,
                    onTap: (){
                      Get.to(() => CafStaffDetails(), arguments: {
                        Constant.CUSTOMER_DETAIL: customerNotesDetailController.customerDetail,
                      });
                    },
                  );
                },
              ),
            ) : Expanded(child: NoDataFound()),
          ]),
    );
  }
  noDataFound() {
    return const NoDataFound();
  }
  _appBar() {
    return DynamicAppBar(Strings.cust_notes, "", AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
