import 'package:savbill/pages/customer/location_list_controller.dart';
import 'package:savbill/pages/customer/location_list_item.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LocationList extends StatefulWidget {
  @override
  _LocationListState createState() => _LocationListState();
}

class _LocationListState extends State<LocationList> {
  final locationListController = Get.put(LocationListController());

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
      child: GetBuilder<LocationListController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: locationListController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SizedBox(
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
              child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                        child: CoustomTextField(
                            labelText: Strings.search_your_text_here,
                            textEditingController:
                                locationListController.searchController,
                            keyboardType: TextInputType.text,
                            borderEnableColors: AppTheme.colorPrimary,
                            textInputAction: TextInputAction.done,
                            onChanged: (value) {},
                            onTextValidator: (String? value) {
                              return null;
                            },
                            onSubmitted: (String? value) {
                              locationListController.getLocationData();
                            },
                            prefixIcon: Icon(
                              Icons.search,
                              color: AppTheme.colorPrimary,
                            ),
                            borderCorner: Constant.BTN_ROUNDED_CORNER_M,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: false)),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    Row(children: [
                      Material(
                        color: AppTheme.colorWhite,
                        elevation: 2,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(6)),
                        child: InkWell(
                          onTap: () {
                            locationListController.getLocationData();
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.statusClosedGreen,
                              borderRadius:
                                  const BorderRadius.all(Radius.circular(6)),
                            ),
                            padding: const EdgeInsets.all(5),
                            child: Icon(
                              Icons.search,
                              color: AppTheme.colorWhite,
                              size: 22,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      Material(
                        color: AppTheme.colorWhite,
                        elevation: 2,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(6)),
                        child: InkWell(
                          onTap: () {
                            locationListController.searchController.clear();
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.colorRed,
                              borderRadius:
                                  const BorderRadius.all(Radius.circular(6)),
                            ),
                            padding: const EdgeInsets.all(5),
                            child: Icon(
                              Icons.close,
                              color: AppTheme.colorWhite,
                              size: 22,
                            ),
                          ),
                        ),
                      ),
                    ]),
                  ]),
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),
            Expanded(
              flex: 1,
              child: (locationListController.locationList != null &&
                      locationListController.locationList!.isNotEmpty)
                  ? Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: ListView.builder(
                          scrollDirection: Axis.vertical,
                          itemCount:
                              locationListController.locationList!.length,
                          itemBuilder: (context, index) {
                            LocationDetail data =
                                locationListController.locationList![index];
                            return InkWell(
                              onTap: () async {
                                Get.back(result: data);
                              },
                              child: LocationViewItem(
                                index: index,
                                item: data,
                              ),
                            );
                          }),
                    )
                  : noDataFound(),
            ),
          ],
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.location, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }
}
