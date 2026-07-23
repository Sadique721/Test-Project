import 'package:savbill/pages/contact/contact.dart';
import 'package:savbill/pages/dashboard/home_tab.dart';
import 'package:savbill/pages/dashboard/model/data_list_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class MyPlanTab extends StatefulWidget {
  final HomeItemClickEvent homeItemClickEvent;

  MyPlanTab({Key? key, required this.homeItemClickEvent}) : super(key: key);

  @override
  _MyPlanTabState createState() => _MyPlanTabState();
}

class _MyPlanTabState extends State<MyPlanTab> {
  List<ItemList> _dataList = [];

  @override
  void initState() {
    super.initState();
    _dataList
        .add(ItemList(id: 0, title: Strings.customers, icon: customers));
    _dataList.add(ItemList(id: 1, title: Strings.my_ticket, icon: openTicket));
    _dataList
        .add(ItemList(id: 2, title: Strings.payment_records, icon: payments));
    _dataList.add(ItemList(id: 3, title: Strings.contact, icon: connect));
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width,
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      decoration: const BoxDecoration(
          image: DecorationImage(
              fit: BoxFit.cover,
              image: AssetImage(
                dashboardBgWhite,
              ))),
      child: Column(children: <Widget>[
        const SizedBox(height: Constant.SCREEN_PADDING),
        Expanded(
          child: ListView.builder(
            itemCount: _dataList.length,
            itemBuilder: (BuildContext context, int index) {
              ItemList data = _dataList[index];
              String? icon = data.icon;
              return Padding(
                padding: EdgeInsets.only(
                  left: Constant.VERY_SMALL_PADDING,
                  right: Constant.VERY_SMALL_PADDING,
                  top: (index == 0) ? 0 : Constant.LARGE_PADDING,
                ),
                child: InkWell(
                  onTap: () {
                    if (data.id == 0) {
                      openCustomerScreen();
                    }
                    if (data.id == 1) {
                      myTicketsScreen();
                    }
                    if (data.id == 2) {
                      openRenewScreen();
                    }
                    if (data.id == 3) {
                      openContactsScreen();
                    }
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      border: Border.all(
                          color: AppTheme.colorPrimary, // Set border color
                          width: 1.0), // Set border width
                      borderRadius: const BorderRadius.all(
                          Radius.circular(6.0)), // Set rounded corner radius
                    ),
                    child: IntrinsicHeight(
                      child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              width: Constant.SEARCH_BAR_CARD_H,
                              height: Constant.SEARCH_BAR_CARD_H,
                              padding:
                                  const EdgeInsets.all(Constant.MEDIUM_PADDING),
                              decoration: BoxDecoration(
                                color: AppTheme.colorPrimary,
                                borderRadius: const BorderRadius.only(
                                  topLeft: Radius.circular(5.0),
                                  bottomLeft: Radius.circular(5.0),
                                ),
                              ),
                              child: Image.asset(
                                icon!,
                                height: Constant.ICON_SIZE,
                                width: Constant.ICON_SIZE,
                              ),
                            ),
                            const SizedBox(width: Constant.SCREEN_PADDING),
                            Align(
                              child: CustomText(
                                  title: data.title,
                                  fontSize: AppTheme.large,
                                  fontWeight: FontWeight.w400,
                                  colors: AppTheme.colorBlack),
                            )
                          ]),
                    ),
                  ),
                ),
              );
            },
          ),
        ),
      ]),
    );
  }

  openCustomerScreen() async {
    //await Get.to(Usages());
  }

  myTicketsScreen() async {
    /*bool chkRefresh = await Get.to(CreateTicket());
    if (chkRefresh) {
      widget.homeItemClickEvent.itemClick(3);
    }*/
    widget.homeItemClickEvent.itemClick(3);
  }

  openRenewScreen() async {
    widget.homeItemClickEvent.itemClick(2);
  }

  openContactsScreen() async {
    bool chkRefresh = await Get.to(Contact());
  }
}
