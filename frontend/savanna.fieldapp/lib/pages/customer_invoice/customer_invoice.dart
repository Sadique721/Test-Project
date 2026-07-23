import 'dart:io';
import 'dart:typed_data';
import 'package:savbill/util/utils.dart';
import 'package:savbill/pages/customer_invoice/add_remark_invoice.dart';
import 'package:savbill/pages/customer_invoice/customer_invoice_controller.dart';
import 'package:savbill/pages/customer_invoice/customer_invoice_item_view.dart';
import 'package:savbill/pages/customer_invoice/invoice_payment_details.dart';
import 'package:savbill/pages/customer_invoice/pdf_viewer_page.dart';
import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
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
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import '../../webservices/url_constants.dart';

class CustomerInvoiceDetail extends StatefulWidget {
  @override
  _CustomerInvoiceDetailState createState() => _CustomerInvoiceDetailState();
}

class _CustomerInvoiceDetailState extends State<CustomerInvoiceDetail>
    implements PermissionDenyBtnAction, InvoiceAddRemarkBtnAction {
  final customerInvoiceController = Get.put(CustomerInvoiceController());
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
    return GetBuilder<CustomerInvoiceController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          resizeToAvoidBottomInset: true,
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerInvoiceController.isLoading),
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
                          title: customerInvoiceController.customerName,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  InkWell(
                    onTap: () {
                      if (customerInvoiceController.filterViewOpen) {
                        customerInvoiceController.filterViewOpen = false;
                      } else {
                        customerInvoiceController.filterViewOpen = true;
                      }
                      customerInvoiceController.update();
                    },
                    child: Container(
                        height: 38,
                        margin: const EdgeInsets.only(right: 12), //
                        child: Icon(
                          Icons.filter_alt_rounded,
                          color: customerInvoiceController.isFilterApply
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

            customerInvoiceController.filterViewOpen
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
                                      customerInvoiceController
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
                                      customerInvoiceController
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
                                      customerInvoiceController
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
                                        customerInvoiceController.applyFilter();
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
                                        customerInvoiceController.clearFilter();
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
            customerInvoiceController.filterViewOpen
                ? const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  )
                : Container(),
            Expanded(
              flex: 1,
              child: (customerInvoiceController.invoiceList != null &&
                      customerInvoiceController.invoiceList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount: customerInvoiceController.invoiceList!.length,
                      itemBuilder: (context, index) {
                        if (index == customerInvoiceController.invoiceList?.length) {
                          if (customerInvoiceController.isShowLoadMore) {
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
                          InvoiceDetail item =
                              customerInvoiceController.invoiceList![index];
                          return CustomerInvoiceViewItem(
                            item: item,
                            index: index,
                            controller: customerInvoiceController,
                            customerDetail:
                                customerInvoiceController.customerDetail,
                            onTapDocumentGenerate: () async {
                              customerInvoiceController
                                  .generatePdfInvoiceAPI(item.id);
                            },
                            onTapDownload: () {
                              customerInvoiceController.documentId =
                                  item.id.toString();
                              customerInvoiceController.fileDownloading(Strings.invoice,
                                  "${UrlConstants.invoiceDownloadUrl}${customerInvoiceController.documentId}",item.customerName);
                            },
                            onTapPrintInvoice: () async {
                              customerInvoiceController.documentId =
                                  item.id.toString();
                              openPdfViewScreen(
                                  "${Strings.view} ${Strings.invoice}",
                                  "${UrlConstants.invoiceDownloadUrl}${customerInvoiceController.documentId}",item.customerName);
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
                              customerInvoiceController.invoiceId = item.id;
                              addRemarkInvoiceDialog(context, "Void Invoice");
                            },

                            onTapCancelRegenerate: () {
                              customerInvoiceController.invoiceId = item.id;
                              addRemarkInvoiceDialog(
                                  context, "Cancel and Regenerate Invoice");
                            },
                            onTapInvoicePayment: () {
                              customerInvoiceController.openPaymentGateways(
                                  plan: item, context: context);
                              customerInvoiceController.setBtnClickEvent(false);
                              // customerInvoiceController.customerInvoicePaymentLinkCall(item.custid,false);
                            },
                            onTapTra: () {
                              customerInvoiceController
                                  .invoiceTraButtonCallApi(customerInvoiceController.debitdocid);
                            },
                            onTapReprintInvoice: () async {
                              /*customerInvoiceController.permissionReady =
                              await customerInvoiceController.checkPermission();
                              if (customerInvoiceController.permissionReady) {
                                await customerInvoiceController.prepareSaveDir();
                                print("Downloading");
                                try {
                                  String fileName = customerInvoiceController
                                      .convertCurrentDateTimeToString();
                                  String token = "";
                                  if (hasData(Constant.USER_TOKEN)) {
                                    token = await getStorage.read(Constant.USER_TOKEN);
                                  }
                                  Map<String, String> headers = {
                                    'Content-type': 'application/json; charset=UTF-8',
                                    'Accept': 'application/json',
                                    'Authorization': 'Bearer $token'
                                  };
                                  final response = await http.get(Uri.parse(UrlConstants.invoice_receipt_url+item.docnumber.toString()), headers: headers);
                                  await Dio().download(
                                      response.toString(),
                                      "${customerInvoiceController.localPath}/$fileName.pdf",
                                      onReceiveProgress: (rec, total) {
                                    // print("Downloading " + ((rec / total) * 100).toStringAsFixed(0) + "%");
                                    showApiResponsePopup();
                                  });
                                  print("Download Completed.");
                                } catch (e) {
                                  print("Download Failed.\n\n" + e.toString());
                                }
                              }*/

                              customerInvoiceController.documentId =
                                  item.id.toString();
                              customerInvoiceController.fileDownloading(Strings.invoice,
                                  "${UrlConstants.invoiceDownloadUrl}${customerInvoiceController.documentId}",item.customerName);
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
      if (customerInvoiceController.selectedInvoiceFromDate != null) {
        selectedDate = customerInvoiceController.selectedInvoiceFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.to_date) {
      if (customerInvoiceController.selectedInvoiceToDate != null) {
        selectedDate = customerInvoiceController.selectedInvoiceToDate;
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
        customerInvoiceController.selectedInvoiceFromDate = picked;
        customerInvoiceController.invoiceFormDateController.text =
            customerInvoiceController.dateFormat.format(picked);
        customerInvoiceController.billFrom =
            customerInvoiceController.apiDateFormat.format(picked);
      }
      if (identity == Strings.to_date) {
        customerInvoiceController.selectedInvoiceToDate = picked;
        customerInvoiceController.invoiceToDateController.text =
            customerInvoiceController.dateFormat.format(picked);
        customerInvoiceController.billTo =
            customerInvoiceController.apiDateFormat.format(picked);
      }
      customerInvoiceController.update();
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
      customerInvoiceController.setBtnClickEvent(true);
      openAppSettings();
    }
  }

  openInvoicePaymentList(int? invoiceId) async {
    Get.to(InvoicePaymentDetails(), arguments: {
      Constant.CUSTOMER_ID: customerInvoiceController.customerId,
      Constant.CUSTOMER_NAME: customerInvoiceController.customerName,
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
      customerInvoiceController.getVoidInvoiceListAPI(
          customerInvoiceController.invoiceId, remarkController!);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.submit)) {
      customerInvoiceController.cancelAndRegenerateAPI(
          customerInvoiceController.invoiceId, remarkController!);
    }
  }

  checkPermissionAndDownload() async {
    final status = await Permission.storage.request();
    if (status.isGranted) {
      customerInvoiceController.downloadFile();
    } else {
      permissionDenyDialog();
    }
    // customerInvoiceController.downloadFile();
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

  openPdfViewScreen(String? pageTitle, String? networkPathUrl,String? customerName) async {
    var result = await Get.to(PdfViewerPage(
        title: pageTitle ?? " ", filePathUrl: networkPathUrl ?? "",customerName:customerName ?? ""));
    if (result != null) {}
  }
}


Future<String> saveFile(Uint8List data, String filename) async {
  final directory = await getApplicationDocumentsDirectory();
  final file = File('${directory.path}/$filename');
  await file.writeAsBytes(data);
  return file.path;
}