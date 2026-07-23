import 'package:savbill/pages/customer_ticket/customer_ticket_controller.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/dashboard/ticket_detail.dart';
import 'package:savbill/pages/dashboard/ticket_view_list_item.dart';
import 'package:savbill/pages/ticket/customer_create_ticket.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerTicketDetail extends StatefulWidget {
  @override
  _CustomerTicketState createState() => _CustomerTicketState();
}

class _CustomerTicketState extends State<CustomerTicketDetail> {
  final customerTicketController = Get.put(CustomerTicketController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerTicketController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerTicketController.isLoading),
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
                          title: customerTicketController.customerName,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  InkWell(
                    onTap: (){
                      addEditTicketScreen(Strings.add,null);
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
                            title: Strings.add_ticket,
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
              child: (customerTicketController.tickerDetailList != null &&
                      customerTicketController.tickerDetailList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          customerTicketController.tickerDetailList?.length,
                      itemBuilder: (context, index) {
                        TicketDetail item =
                            customerTicketController.tickerDetailList![index];
                        return InkWell(
                          onTap: () async {
                            // openTicketDetailScreen(item.caseId);
                          },
                          child: TicketListViewItem(
                            item: item,
                            //userDetail: customerTicketController.userDetail,
                            showBtn: false,
                            onAssignTap: () {},
                            onFollowupTap: () {},
                          ),
                        );
                      })
                  : noDataFound(),
            ),
          ]),
    );
  }

  openTicketDetailScreen(int? ticketId) async {
    Get.to(TicketDetailScreen(), arguments: {
      Constant.TICKET_ID: ticketId,
    });
  }



  noDataFound() {
    return const NoDataFound();
  }


  addEditTicketScreen(String from, TicketDetail? item) async {
    var result = await Get.to(CustomerCreateTicket(),
        arguments: {
      Constant.FROM: from,
      Constant.TICKET_DETAIL: item,
      Constant.CUSTOMER_DETAIL: customerTicketController.customerDetail});

    if (result != null && result == true) {
      customerTicketController.getCustomerTicketDetail();
      // clearFilter();
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.ticket_detail, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
