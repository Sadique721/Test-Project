import 'package:savbill/pages/customer_change_status/change_status_item_view.dart';
import 'package:savbill/pages/customer_change_status/change_status_list_controller.dart';
import 'package:savbill/pages/customer_change_status/response/customer_change_status_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerChangeStatus extends StatefulWidget {
  @override
  _CustomerChangeStatusState createState() => _CustomerChangeStatusState();
}

class _CustomerChangeStatusState extends State<CustomerChangeStatus> {
  final changeStatusListController = Get.put(ChangeStatusListController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<ChangeStatusListController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: changeStatusListController.isLoading),
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
                Expanded(child:   CustomText(
                      title: "${changeStatusListController.customerName} ${Strings.status_list}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 1,
              child: (changeStatusListController.changeStatusDetail != null &&
                      changeStatusListController.changeStatusDetail!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          changeStatusListController.changeStatusDetail!.length,
                      itemBuilder: (context, index) {
                        ChangeStatusDetail item = changeStatusListController
                            .changeStatusDetail![index];
                        return ChangeStatusItemView(item: item, index: index,controller: changeStatusListController);
                      })
                  : noDataFound(),
            ),
          ]),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.change_status, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
