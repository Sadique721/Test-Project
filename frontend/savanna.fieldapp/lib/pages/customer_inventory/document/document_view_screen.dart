
import 'package:savbill/pages/customer_inventory/document/view_document_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ViewDocumentScreen extends StatefulWidget {
  @override
  _ViewDocumentState createState() => _ViewDocumentState();
}

class _ViewDocumentState extends State<ViewDocumentScreen>
    with WidgetsBindingObserver, TickerProviderStateMixin {
  final viewDocumentController = Get.put(ViewDocumentController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: viewDocumentController.isChangeData);
  }

  @override
  void initState() {

    super.initState();
    WidgetsBinding.instance.addObserver(this);
    viewDocumentController.tabController =
        TabController(length: viewDocumentController.tabs.length, vsync: this);
    for (var i = 0; i < viewDocumentController.tabs.length; i++) {
      viewDocumentController.sectionControllers
          .add(TextEditingController(text: viewDocumentController.tabs[i]));
      viewDocumentController.latitudeControllers.add(TextEditingController());
      viewDocumentController.longitudeControllers.add(TextEditingController());
      viewDocumentController.selectedOpticalRanges.add(null);
      viewDocumentController.selectedFiles.add([]);
    }

    viewDocumentController.myTabs = <Tab>[
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.fat_optical_power_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.fat_inside_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.fat_outside_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.onu_optical_power_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.optical_power_range,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.installation_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.speed_test_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.smart_gadget,
          textAlign: TextAlign.center,
        ),
      )
    ];
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (viewDocumentController.checkBtnClickEvent) {
          viewDocumentController.setBtnClickEvent(false);
          // checkCameraPermission();
        }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ViewDocumentController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(context),
          ),
          ProgressBar(isLoader: viewDocumentController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(BuildContext context) {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            TabBar(
              isScrollable: true,
              labelPadding: EdgeInsets.symmetric(horizontal: 10.0),
              controller: viewDocumentController.tabController,
              tabAlignment: TabAlignment.start,
              unselectedLabelColor: AppTheme.title_dark.withOpacity(0.8),
              indicator: UnderlineTabIndicator(
                borderSide: BorderSide(
                    width: Constant.TAB_INDICATOR_H,
                    color: AppTheme.colorPrimary),
              ),
              labelColor: AppTheme.title_dark,
              labelStyle: const TextStyle(
                  fontSize: AppTheme.large, fontWeight: FontWeight.w600),
              unselectedLabelStyle: const TextStyle(
                  fontSize: AppTheme.medium, fontWeight: FontWeight.w500),
              tabs: viewDocumentController.myTabs,
            ),
            SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Expanded(
              child: TabBarView(
                controller: viewDocumentController.tabController,
                children: List.generate(viewDocumentController.tabs.length,
                    (tabIndex) {
                  final sections = viewDocumentController.documentList!
                      .where((e) =>
                          e.sectionName ==
                          viewDocumentController.tabs[tabIndex])
                      .toList();

                  if (sections.isEmpty) {
                    return Center(
                        child: CustomText(
                      title: "No files available for this tab.",
                      colors: AppTheme.title_dark,
                      fontSize: AppTheme.small,
                    ));
                  }

                  final section = sections.first;
                  if (viewDocumentController.tabs[tabIndex] ==
                      "Optical Power Range") {
                    return Padding(
                      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                      child: CustomText(
                        title:
                            "Optical Power Range: ${section.fileDetails![0].opticalRange}",
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.small,
                      ),
                    );
                  }
                  final fileDetails = section.fileDetails ?? [];

                  if (fileDetails.isEmpty) {
                    return Center(
                        child: CustomText(
                      title: "No files available for this section.",
                      colors: AppTheme.title_dark,
                      fontSize: AppTheme.small,
                    ));
                  }

                  // return ListView.builder(
                  //   itemCount: fileDetails.length,
                  //   itemBuilder: (context, index) {
                  //     final file = fileDetails[index];
                  //     return ListTile(
                  //       title: Text(file.fileName ?? ""),
                  //       subtitle: Text("Lat: ${file.latitude ??""}, Long: ${file.longitude ?? ""}"),
                  //       trailing: Row(
                  //         mainAxisSize: MainAxisSize.min,
                  //         children: [
                  //           IconButton(
                  //             icon: Icon(Icons.download),
                  //             onPressed: (){},
                  //             // onPressed: () => viewDocumentController.downloadDoc(file.fileName, file, tab),
                  //           ),
                  //           IconButton(
                  //             icon: Icon(Icons.remove_red_eye),
                  //             onPressed: (){},
                  //             // onPressed: () => viewDocumentController.viewDoc(file.fileName, file, tab),
                  //           ),
                  //           IconButton(
                  //             icon: Icon(Icons.delete),
                  //             onPressed: (){},
                  //             // onPressed: () => viewDocumentController.deleteDoc(file.fileName, file, tab),
                  //           ),
                  //         ],
                  //       ),
                  //     );
                  //   },
                  // );

                  return Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING),
                    child: ListView.builder(
                        scrollDirection: Axis.vertical,
                        itemCount: fileDetails.length,
                        itemBuilder: (context, index) {
                          final file = fileDetails[index];
                          if (index == fileDetails.length) {
                            if (viewDocumentController.isLoading) {
                              return Padding(
                                padding: const EdgeInsets.all(
                                    Constant.SMALL_PADDING),
                                child: Center(
                                  child: SizedBox(
                                    width: Constant.SCREEN_PADDING,
                                    height: Constant.SCREEN_PADDING,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2.5,
                                      valueColor: AlwaysStoppedAnimation<Color>(
                                          AppTheme.colorProgress),
                                      backgroundColor: AppTheme.colorProgressBg,
                                    ),
                                  ),
                                ),
                              );
                            } else {
                              return Container();
                            }
                          } else {
                            return Container(
                              margin: const EdgeInsets.only(
                                bottom: Constant.MEDIUM_PADDING,
                              ),
                              child: Material(
                                color: AppTheme.colorWhite,
                                elevation: 0.5,
                                shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.BTN_ROUNDED_CORNER)),
                                child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      Padding(
                                        padding: const EdgeInsets.symmetric(
                                            horizontal: Constant.SMALL_PADDING),
                                        child: Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.start,
                                          crossAxisAlignment:
                                              CrossAxisAlignment.end,
                                          children: [
                                            Expanded(
                                              flex: 2,
                                              child: Column(
                                                mainAxisAlignment:
                                                    MainAxisAlignment.start,
                                                crossAxisAlignment:
                                                    CrossAxisAlignment.start,
                                                children: [
                                                  titleWidget(Strings.filename),
                                                  const SizedBox(
                                                      height: Constant
                                                              .VERY_SMALL_PADDING -
                                                          1),
                                                  CustomText(
                                                    title: file.fileName ?? "",
                                                    colors:
                                                        AppTheme.colorPrimary,
                                                    textAlign: TextAlign.start,
                                                    decoration: TextDecoration
                                                        .underline,
                                                    fontSize:
                                                        AppTheme.small + 1,
                                                    fontWeight:
                                                        FontWeight.normal,
                                                    maxLines: 2,
                                                  ),
                                                ],
                                              ),
                                            ),
                                            // Expanded(
                                            //   flex: 1,
                                            //   child: Column(
                                            //     mainAxisAlignment: MainAxisAlignment.start,
                                            //     crossAxisAlignment: CrossAxisAlignment.start,
                                            //     children: [
                                            //       titleWidget(
                                            //         Strings.latitude,
                                            //       ),
                                            //       const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                                            //       CustomText(
                                            //         title: (widget.item.currentAssigneeName != null &&
                                            //             widget.item.currentAssigneeName!.isNotEmpty)
                                            //             ? "${widget.item.currentAssigneeName}"
                                            //             : "-",
                                            //         colors: AppTheme.colorPrimary,
                                            //         textAlign: TextAlign.start,
                                            //         decoration: TextDecoration.underline,
                                            //         fontSize: AppTheme.small + 1,
                                            //         fontWeight: FontWeight.normal,
                                            //
                                            //         maxLines: 1,
                                            //       )
                                            //     ],
                                            //   ),
                                            // )
                                          ],
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      Padding(
                                        padding: const EdgeInsets.symmetric(
                                            horizontal: Constant.SMALL_PADDING),
                                        child: basicDetailItem(
                                          Strings.latitude,
                                          file.latitude ?? "",
                                          Strings.longitude,
                                          file.longitude ?? "",
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      Align(
                                        alignment: FractionalOffset.topRight,
                                        child: SingleChildScrollView(
                                          scrollDirection: Axis.horizontal,
                                          child: Padding(
                                            padding: const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.SMALL_PADDING,
                                                vertical: 1),
                                            child: Column(
                                              mainAxisAlignment:
                                                  MainAxisAlignment.end,
                                              crossAxisAlignment:
                                                  CrossAxisAlignment.end,
                                              children: [
                                                Row(
                                                    mainAxisAlignment:
                                                        MainAxisAlignment.end,
                                                    children: [
                                                      buttonView(
                                                          fileDownloadSvg,
                                                          AppTheme
                                                              .custUploadFileLight,
                                                          AppTheme
                                                              .custUploadFileDark,
                                                          () {
                                                            // viewDocumentController.showInventoryDocData(file.uniqueName!,section.sectionName!,UrlConstants.cust_inventory_download_doc);
                                                            if(file.uniqueName != null && section.sectionName != null) {
                                                              viewDocumentController
                                                                  .inventoryDocumentDownload(
                                                                  file
                                                                      .uniqueName!,
                                                                  section
                                                                      .sectionName!,
                                                                  UrlConstants
                                                                      .cust_inventory_download_doc);
                                                            }else{
                                                              Utils.showSnackbar(Strings.INFO, Strings.no_data_found, AppTheme.colorWhite,
                                                                  AppTheme.colorBlueRView);
                                                            }
                                                          }),
                                                      const SizedBox(
                                                        width: Constant
                                                            .SMALL_PADDING,
                                                      ),
                                                      buttonView(
                                                          eyePasswordSvg,
                                                          AppTheme
                                                              .custNearLocationLight,
                                                          AppTheme
                                                              .custNearLocationDark,
                                                          () {
                                                            if(file.uniqueName != null && section.sectionName != null) {
                                                              viewDocumentController
                                                                  .downloadFile(
                                                                  "${UrlConstants
                                                                      .cust_inventory_download_doc}${viewDocumentController
                                                                      .inventoryId}/${file
                                                                      .uniqueName}/${section
                                                                      .sectionName}/",
                                                                  file);
                                                            }
                                                      }),

                                                      const SizedBox(
                                                        width: Constant
                                                            .SMALL_PADDING,
                                                      ),
                                                      buttonView(
                                                          deleteSvg,
                                                          AppTheme
                                                              .custDeleteLight,
                                                          AppTheme
                                                              .custDeleteDark,
                                                              () {
                                                                showDialog(
                                                                  context: context,
                                                                  builder:
                                                                      (BuildContext context) {
                                                                    return AlertDialogHelper(
                                                                        title: Strings.app_name,
                                                                        message:
                                                                        Strings.msg_delete,
                                                                        positiveBtnText:
                                                                        Strings.ok,
                                                                        negativeBtnText:
                                                                        Strings.cancel,
                                                                        positiveBtnClick: () {
                                                                          Get.back();
                                                                          viewDocumentController
                                                                              .inventoryDocumentDelete(file.fileName,file.uniqueName,section.sectionName);
                                                                          viewDocumentController
                                                                              .update();
                                                                        },
                                                                        negativeBtnClick: () {
                                                                          Get.back();
                                                                        });
                                                                  },
                                                                );

                                                          }),
                                                      const SizedBox(
                                                        width: Constant
                                                            .SMALL_PADDING,
                                                      ),
                                                    ]),
                                                SizedBox(
                                                  height:
                                                      Constant.SMALL_PADDING,
                                                ),
                                              ],
                                            ),
                                          ),
                                        ),
                                      ),
                                    ]),
                              ),
                            );
                          }
                        }),
                  );
                }).toList(),
              ),
            ),
            SizedBox(height: Constant.SMALL_PADDING),
          ],
        ),
      ),
    );
  }

  // fileViewWidget() {
  //   return viewDocumentController.fileDetail != null
  //       ? FileGridItem(
  //     fileDetail: viewDocumentController.fileDetail!,
  //     onTapItem: () {},
  //     bottomAction: fileItemAction(),
  //   )
  //       : Container();
  // }

  titleWithRequireWidget(String title, bool require) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.normal,
        ),
        require
            ? CustomText(
                title: " *",
                colors: Colors.red,
                textAlign: TextAlign.start,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              )
            : Container(),
      ],
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 1.5,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 10,
          width: Constant.BTN_HEIGHT_M - 10,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE,
            width: Constant.ICON_SIZE,
            color: txtColor,
            fit: BoxFit.fill,
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
          flex: 1,
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
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 1,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.view_document, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
