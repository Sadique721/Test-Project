import 'package:savbill/pages/customer_caf/caf_notes/caf_notes_detail_controller.dart';
import 'package:savbill/pages/customer_caf/caf_notes/caf_staff_details/caf_staff_details_screen.dart';
import 'package:savbill/pages/customer_caf/caf_notes/item_list_notes.dart';
import 'package:savbill/pages/customer_caf/response/cust_caf_notes_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../widgets/progress_bar.dart';
class CafNotesDetail extends StatefulWidget {
  @override
  _CafNotesDetailState createState() => _CafNotesDetailState();
}
class _CafNotesDetailState extends State<CafNotesDetail> {
  final cafNotesDetailController = Get.put(CafNotesDetailController());
  @override
  void initState() {
    super.initState();
  }
  _backScreen() {
    Get.back();
  }
  @override
  Widget build(BuildContext context) {
    return GetBuilder<CafNotesDetailController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: cafNotesDetailController.isLoading),
      ]);
    });
  }
  _body() {
    String? currentCustomerName;
    if (cafNotesDetailController.customerDetail != null) {
      currentCustomerName =
      "${cafNotesDetailController.customerDetail?.title?.capitalizeFirst ??""} ${cafNotesDetailController.customerDetail?.custname ??""} ${Strings.caf_notes}";
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
            cafNotesDetailController.customerNotesDetails!.isNotEmpty  ? Expanded(
              flex: 1,
              child: ListView.builder(
                padding: EdgeInsets.zero,
                scrollDirection: Axis.vertical,
                itemCount: cafNotesDetailController.customerNotesDetails!.length,
                itemBuilder: (context, index) {
                  CafNoteContent item = cafNotesDetailController.customerNotesDetails![index];
                  return ItemListNotes(
                    item: item,
                    index: index,
                    onTap: (){
                      Get.to(() => CafStaffDetails(), arguments: {
                        Constant.CUSTOMER_DETAIL: cafNotesDetailController.customerDetail,
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
    return DynamicAppBar(Strings.caf_notes, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
