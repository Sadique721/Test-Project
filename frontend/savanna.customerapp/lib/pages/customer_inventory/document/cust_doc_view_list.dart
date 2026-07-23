import 'package:savbill/pages/customer_inventory/document/cust_doc_view_list_controller.dart';
import 'package:savbill/pages/customer_inventory/document/cust_inventory_doc_item_view.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';

class CustDocViewList extends StatefulWidget {
  @override
  _CustomerListState createState() => _CustomerListState();
}

class _CustomerListState extends State<CustDocViewList> {
  final custDocViewListController = Get.put(CustDocViewListController());
  final GlobalKey<ScaffoldState> _customerListKey = GlobalKey();
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;


  @override
  void initState() {
    super.initState();
  }

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
      child: GetBuilder<CustDocViewListController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: custDocViewListController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.view_document,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (custDocViewListController.customerInventoryDataList != null &&
                        custDocViewListController.customerInventoryDataList!.fileDetails.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            scrollDirection: Axis.vertical,
                            itemCount:
                                custDocViewListController.customerInventoryDataList!.fileDetails!.length +
                                    1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  custDocViewListController
                                      .customerInventoryDataList!.fileDetails?.length) {
                                if (custDocViewListController.isShowLoadMore) {
                                  // final fileData = custDocViewListController.customerInventoryDataList!.fileDetails[index];
                                  // final filename = fileData['filename'].toString();
                                  // final uniqueName = fileData['uniquename'].toString();
                                  // final id = widget.inventoryFileData!.id;
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                                  AppTheme.colorProgress),
                                          backgroundColor:
                                              AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                return CustInventoryDocItemView(
                                  index: index,
                                  item: custDocViewListController.customerInventoryDataList!.fileDetails[index],
                                  onTapView: (){

                                    custDocViewListController.showInventoryDocData(custDocViewListController.customerInventoryDataList!.fileDetails[index]['filename'],custDocViewListController.customerInventoryDataList!.fileDetails[index]['uniquename'],UrlConstants.cust_inventory_download_doc);
                                  },
                                  onTapDownload: (){
                                    custDocViewListController.inventoryDocumentDownload(custDocViewListController.customerInventoryDataList!.fileDetails[index]['uniquename'],UrlConstants.cust_inventory_download_doc,custDocViewListController.customerInventoryDataList!.customerName);
                                  },
                                  onTapDelete: (){
                                    custDocViewListController.inventoryDocumentDelete(custDocViewListController.customerInventoryDataList!.fileDetails[index]['filename'],custDocViewListController.customerInventoryDataList!.fileDetails[index]['uniquename']);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.view_document, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }
}
