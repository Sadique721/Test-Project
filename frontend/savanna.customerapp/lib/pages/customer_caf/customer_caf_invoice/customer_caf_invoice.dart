import 'package:savbill/pages/customer_caf/customer_caf_invoice/customer_caf_invoice_controller.dart';
import 'package:savbill/pages/customer_caf/customer_caf_invoice/customer_caf_invoice_item_view.dart';
import 'package:savbill/pages/customer_caf/response/customer_caf_invoice_details_res.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/pages/customer_invoice/add_remark_invoice.dart';
import 'package:savbill/pages/customer_invoice/invoice_payment_details.dart';
import 'package:savbill/pages/customer_invoice/pdf_viewer_page.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/permisstion_deny_dialog.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:permission_handler/permission_handler.dart';

class CustomerCafInvoiceDetail extends StatefulWidget {
  @override
  _CustomerInvoiceDetailState createState() => _CustomerInvoiceDetailState();
}

class _CustomerInvoiceDetailState extends State<CustomerCafInvoiceDetail>
    implements PermissionDenyBtnAction, InvoiceAddRemarkBtnAction {
  final customerCafInvoiceController = Get.put(CustomerCafInvoiceController());
  GetStorage getStorage = GetStorage();

  @override
  void initState() {
    WidgetsFlutterBinding.ensureInitialized();
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerCafInvoiceController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerCafInvoiceController.isLoading),
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
              padding: const EdgeInsets.only(
                  top: Constant.SCREEN_PADDING,
                  left: Constant.SCREEN_PADDING,
                  right: Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                      child: CustomText(
                          title: customerCafInvoiceController.customerName,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  InkWell(
                    onTap: () {
                      if (customerCafInvoiceController.filterViewOpen) {
                        customerCafInvoiceController.filterViewOpen = false;
                      } else {
                        customerCafInvoiceController.filterViewOpen = true;
                      }
                      customerCafInvoiceController.update();
                    },
                    child: Container(
                        height: 38,
                        margin: const EdgeInsets.only(right: 12), //
                        child: Icon(
                          Icons.filter_alt_rounded,
                          color: customerCafInvoiceController.isFilterApply
                              ? AppTheme.colorPrimary
                              : AppTheme.colorBlack,
                          size: 32,
                        )),
                  ),
                ],
              ),
            ),
            /*const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),*/

            customerCafInvoiceController.filterViewOpen
                ? Container(
                    width: MediaQuery.of(context).size.width,
                    margin: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING),
                    child: Material(
                      color: AppTheme.colorWhite,
                      elevation: 1.5,
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              Constant.BTN_ROUNDED_CORNER - 2)),
                      child: Padding(
                        padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              CoustomTextField(
                                  labelText: Strings.document_no,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      customerCafInvoiceController
                                          .documentNoController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {},
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              CoustomTextField(
                                  labelText: Strings.from_date,
                                  suffixIcon: Padding(
                                    padding: const EdgeInsetsDirectional.all(
                                        Constant.MEDIUM_PADDING),
                                    child: SvgPicture.asset(
                                      calendarSvg,
                                      color: AppTheme.colorBlack,
                                      width: Constant.ICON_SIZE_S,
                                      height: Constant.ICON_SIZE_S,
                                      // myIcon is a 48px-wide widget.
                                    ),
                                  ),
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      customerCafInvoiceController
                                          .invoiceFormDateController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {},
                                  onTextFiledOnTap: () {
                                    selectDate(
                                        context,
                                        Strings.from_date,
                                        DateTime(DateTime.now().year - 10),
                                        DateTime(DateTime.now().year + 10));
                                  },
                                  readOnly: true),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              CoustomTextField(
                                  labelText: Strings.to_date,
                                  suffixIcon: Padding(
                                    padding: const EdgeInsetsDirectional.all(
                                        Constant.MEDIUM_PADDING),
                                    child: SvgPicture.asset(
                                      calendarSvg,
                                      color: AppTheme.colorBlack,
                                      width: Constant.ICON_SIZE_S,
                                      height: Constant.ICON_SIZE_S,
                                      // myIcon is a 48px-wide widget.
                                    ),
                                  ),
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      customerCafInvoiceController
                                          .invoiceToDateController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {},
                                  onTextFiledOnTap: () {
                                    selectDate(
                                        context,
                                        Strings.to_date,
                                        DateTime(DateTime.now().year - 10),
                                        DateTime(DateTime.now().year + 10));
                                  },
                                  readOnly: true),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Expanded(
                                    child: SimpleButton(
                                      onTap: () {
                                        customerCafInvoiceController
                                            .applyFilter();
                                      },
                                      radius: Constant.BTN_HEIGHT_M,
                                      height: Constant.BTN_HEIGHT_M,
                                      bgColors: AppTheme.colorPrimary,
                                      child: CustomText(
                                        title: Strings.apply,
                                        fontSize: AppTheme.medium,
                                        fontWeight: FontWeight.w500,
                                      ),
                                    ),
                                  ),
                                  const SizedBox(
                                    width: Constant.LARGE_PADDING,
                                  ),
                                  Expanded(
                                    child: SimpleButton(
                                      onTap: () {
                                        customerCafInvoiceController
                                            .clearFilter();
                                      },
                                      radius: Constant.BTN_HEIGHT_M,
                                      height: Constant.BTN_HEIGHT_M,
                                      bgColors: AppTheme.colorBlack,
                                      borderColors: AppTheme.colorBlack,
                                      child: CustomText(
                                        title: Strings.clear,
                                        fontSize: AppTheme.medium,
                                        fontWeight: FontWeight.w500,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ]),
                      ),
                    ),
                  )
                : Container(),
            customerCafInvoiceController.filterViewOpen
                ? const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  )
                : Container(),
            Expanded(
              flex: 1,
              child: (customerCafInvoiceController.invoiceList != null &&
                      customerCafInvoiceController.invoiceList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          customerCafInvoiceController.invoiceList!.length,
                      itemBuilder: (context, index) {
                        if (index ==
                            customerCafInvoiceController.invoiceList?.length) {
                          if (customerCafInvoiceController.isShowLoadMore) {
                            return Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
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
                          Invoicesearchlist item =
                              customerCafInvoiceController.invoiceList![index];
                          return CustomerCafInvoiceViewItem(
                            item: item,
                            index: index,
                            controller: customerCafInvoiceController,
                            customerDetail: customerCafInvoiceController.customerDetail,
                            onTapDocumentGenerate: () async {
                              customerCafInvoiceController
                                  .generatePdfInvoiceAPI(item.id);
                            },
                            onTapDownload: () {
                              customerCafInvoiceController.documentId =
                                  item.id.toString();
                              customerCafInvoiceController.fileDownloading(
                                  Strings.invoice,
                                  "${UrlConstants.trialInvoiceDownloadUrl}${customerCafInvoiceController.documentId}",
                                  item.customerName);
                              // openPdfViewScreen(Strings.invoice,
                              //     "${UrlConstants.trialInvoiceDownloadUrl}${customerCafInvoiceController.documentId}",item.customerName);
                            },
                            onTapOpenTicketInvoice: () {
                              if (item.adjustedAmount! >= item.totalamount!) {
                                Utils.showSnackbar(
                                    Strings.INFO,
                                    "Total payment is already adjusted",
                                    AppTheme.colorWhite,
                                    AppTheme.colorBlueRView);
                              } else {
                                openInvoicePaymentList(
                                  item.id,
                                );
                              }
                            },
                            onTapInvoice: () {
                              customerCafInvoiceController.invoiceId = item.id;
                              addRemarkInvoiceDialog(context, "Void Invoice");
                            },
                            onTapPrintInvoice: () async {
                              customerCafInvoiceController.documentId =
                                  item.id.toString();
                              openPdfViewScreen(
                                  "${Strings.view} ${Strings.invoice}",
                                  "${UrlConstants.trial_invoice_receipt_url}${customerCafInvoiceController.documentId}",
                                  item.customerName);
                            },
                            onTapCancelRegenerate: () {
                              customerCafInvoiceController.invoiceId = item.id;
                              addRemarkInvoiceDialog(
                                  context, "Cancel and Regenerate Invoice");
                            },
                            onTapReprintInvoice: () async {
                              // customerCafInvoiceController.permissionReady =
                              // await customerCafInvoiceController.checkPermission();
                              // if (customerCafInvoiceController.permissionReady) {
                              //   await customerCafInvoiceController.prepareSaveDir();
                              //   print("Downloading");
                              //   try {
                              //     String fileName = customerCafInvoiceController
                              //         .convertCurrentDateTimeToString();
                              //     String token = "";
                              //     if (getStorage.hasData(Constant.USER_TOKEN)) {
                              //       token = await getStorage.read(Constant.USER_TOKEN);
                              //     }
                              //     Map<String, String> headers = {
                              //       'Content-type': 'application/json; charset=UTF-8',
                              //       'Accept': 'application/json',
                              //       'Authorization': 'Bearer $token'
                              //     };
                              //     final response = await http.get(Uri.parse(UrlConstants.trial_invoice_receipt_url+item.docnumber.toString()), headers: headers);
                              //     await Dio().download(
                              //         response.toString(),
                              //         "${customerCafInvoiceController.localPath}/$fileName.pdf",
                              //         onReceiveProgress: (rec, total) {
                              //       // print("Downloading " + ((rec / total) * 100).toStringAsFixed(0) + "%");
                              //       showApiResponsePopup();
                              //     });
                              //     print("Download Completed.");
                              //   } catch (e) {
                              //     print("Download Failed.\n\n" + e.toString());
                              //   }
                              // }



                              customerCafInvoiceController.documentId =
                                  item.id.toString();
                              customerCafInvoiceController.fileDownloading(
                                  Strings.invoice,
                                  "${UrlConstants.trialInvoiceDownloadUrl}${customerCafInvoiceController.documentId}",
                                  item.customerName);


                              // customerCafInvoiceController.documentId =
                              //     item.id.toString();
                              // openPdfViewScreen(
                              //     "${Strings.view} ${Strings.invoice}",
                              //     "${UrlConstants.trial_invoice_receipt_url}${customerCafInvoiceController.documentId}",
                              //     item.customerName);
                            },
                            onTapInvoicePayment: (){
                              customerCafInvoiceController.openPaymentGateways(plan: item,context: context);
                            },
                          );
                        }
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
    return DynamicAppBar(Strings.invoice_detail, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.from_date) {
      if (customerCafInvoiceController.selectedInvoiceFromDate != null) {
        selectedDate = customerCafInvoiceController.selectedInvoiceFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.to_date) {
      if (customerCafInvoiceController.selectedInvoiceToDate != null) {
        selectedDate = customerCafInvoiceController.selectedInvoiceToDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate!,
      firstDate: firstDate,
      lastDate: lastDate,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      builder: (BuildContext? context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            primaryColor: AppTheme.colorPrimary,
            colorScheme: ColorScheme.light(primary: AppTheme.colorPrimary),
            buttonTheme:
                const ButtonThemeData(textTheme: ButtonTextTheme.primary),
          ),
          child: child!,
        );
      },
    );
    if (picked != null && picked != selectedDate) {
      if (identity == Strings.from_date) {
        customerCafInvoiceController.selectedInvoiceFromDate = picked;
        customerCafInvoiceController.invoiceFormDateController.text =
            customerCafInvoiceController.dateFormat.format(picked);
        customerCafInvoiceController.billFrom =
            customerCafInvoiceController.apiDateFormat.format(picked);
      }
      if (identity == Strings.to_date) {
        customerCafInvoiceController.selectedInvoiceToDate = picked;
        customerCafInvoiceController.invoiceToDateController.text =
            customerCafInvoiceController.dateFormat.format(picked);
        customerCafInvoiceController.billTo =
            customerCafInvoiceController.apiDateFormat.format(picked);
      }
      customerCafInvoiceController.update();
    }
  }

  showApiResponsePopup() {
    showDialog(
      context: Get.context!,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.app_name,
            message: "Invoice Download Successfully.",
            positiveBtnText: Strings.ok,
            positiveBtnClick: () {
              Get.back();
              Get.back();
            },
            negativeBtnClick: () {
              Get.back();
            });
      },
    );
  }

  @override
  void btnClickAction({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.app_permission_settings)) {
      customerCafInvoiceController.setBtnClickEvent(true);
      openAppSettings();
    }
  }

  openInvoicePaymentList(int? invoiceId) async {
    Get.to(InvoicePaymentDetails(), arguments: {
      Constant.CUSTOMER_ID: customerCafInvoiceController.customerId,
      Constant.CUSTOMER_NAME: customerCafInvoiceController.customerName,
      Constant.INVOICE_ID: invoiceId
    });
  }

  addRemarkInvoiceDialog(BuildContext context, String? pageName) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return AddRemarkInvoiceDialog(
              pageName: pageName, invoiceAddRemarkBtnAction: this);
        });
  }

  @override
  void invoicePaymentDetails(
      {String? identifier, TextEditingController? remarkController}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.remarks)) {
      customerCafInvoiceController.getVoidInvoiceListAPI(
          customerCafInvoiceController.invoiceId, remarkController!);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.submit)) {
      customerCafInvoiceController.cancelAndRegenerateAPI(
          customerCafInvoiceController.invoiceId, remarkController!);
    }
  }

  checkPermissionAndDownload() async {
    final status = await Permission.storage.request();
    if (status.isGranted) {
      customerCafInvoiceController.downloadFile();
    } else {
      permissionDenyDialog();
    }
    // customerCafInvoiceController.downloadFile();
  }

  void permissionDenyDialog() async {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return PermissionDenyDialog(
              permissionDenyBtnAction: this,
              titleMsg: Strings.file_storage_permission_denied_msg);
        });
  }

  openPdfViewScreen(
      String? pageTitle, String? networkPathUrl, String? customerName) async {
    var result = await Get.to(PdfViewerPage(
        title: pageTitle ?? " ",
        filePathUrl: networkPathUrl ?? "",
        customerName: customerName));
    if (result != null) {}
  }
}
