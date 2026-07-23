import 'package:savbill/pages/change_plan/request/change_plan_group_screen.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

class SelectPlanGroupDialog extends StatefulWidget {
  final SelectPlanAction selectPlanAction;
  final List<PlanMappingDetail> planLst;


  const SelectPlanGroupDialog({
    Key? key,
    required this.selectPlanAction,
    required this.planLst,
  }) : super(key: key);

  @override
  _SelectPlanGroupState createState() => _SelectPlanGroupState();
}

class _SelectPlanGroupState extends State<SelectPlanGroupDialog> {
  List<PlanMappingDetail> itemsLst = [];
  List<ChangePlanGroupScreen> changePlanGroup = [];
  ChangePlanGroupScreen? selectPlanGroup;


  @override
  void initState() {
    super.initState();

    setState(() {
      itemsLst.addAll(widget.planLst);
      changePlanGroup.add(ChangePlanGroupScreen(
          planGroupName: Strings.individual, groupId: 1));
      changePlanGroup.add(ChangePlanGroupScreen(
          planGroupName: Strings.plan_group, groupId: 2));
    });
  }

  @override
  Widget build(BuildContext context) {


    String title = Strings.select_plan;
    return contentBox(context, title);
  }

  contentBox(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: Stack(
        children: [
          AlertDialog(
            insetPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING * 2,
            ),
            contentPadding: const EdgeInsets.only(
              top: 0,
            ),
            clipBehavior: Clip.antiAliasWithSaveLayer,
            backgroundColor: AppTheme.colorWhite,
            shape: const RoundedRectangleBorder(
                borderRadius:
                    BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
            content: Container(
              width: MediaQuery.of(context).size.width,
              color: AppTheme.colorWhite,
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  // mainAxisAlignment: MainAxisAlignment.start,
                  // crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      width: double.infinity,
                      height: Constant.TABBAR_HEIGHT,
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      decoration: const BoxDecoration(
                        color: AppTheme.colorAccentTheme,
                        borderRadius: BorderRadius.only(
                            topLeft: Radius.circular(10),
                            topRight: Radius.circular(10)),
                      ),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: title,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.large,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Flexible(
                        child: ListView.builder(
                      shrinkWrap: true,
                      primary: false,
                      itemCount: itemsLst.length,
                      itemBuilder: (context, index) {
                        PlanMappingDetail item = itemsLst[index];
                        return planItemList(item, index);
                      },
                    )),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      children: [
                        /*Expanded(
                          child: InkWell(
                            onTap: () {
                              validateSelection();
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomLeft: Radius.circular(
                                        Constant.SMALL_PADDING)),
                              ),
                              child: Text(
                                Strings.select,
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: AppTheme.medium + 1,
                                  color: AppTheme.colorPositive,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),*/
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              Get.back();
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomRight: Radius.circular(
                                        Constant.SMALL_PADDING)),
                              ),
                              child: Text(
                                Strings.cancel,
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: AppTheme.medium + 1,
                                  color: AppTheme.colorNagative,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ]),
            ),
          ),
          Positioned(
            child: GestureDetector(
              onTap: () {
                Get.back();
              },
              child: Align(
                alignment: Alignment.topRight,
                child: Icon(Icons.close, color: AppTheme.colorWhite),
              ),
            ),
          ),
        ],
      ),
    );
  }

  planItemList(PlanMappingDetail item, int index) {
    return Container(
      padding: const EdgeInsets.all(5.0),
      child: Card(
        elevation: 5,
        color: AppTheme.colorWhite,
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                cardDataRow(
                    Strings.nick_name, "Nick Name", AppTheme.lable_noramal),
                cardDataRow(Strings.connection_no, "connection no",
                    AppTheme.lable_noramal),
                cardDataRow(
                    Strings.current_plan, "plan", AppTheme.lable_noramal),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                    flex: 1,
                    child: cardDataRow(Strings.nick_name, "nick name",
                        AppTheme.lable_noramal)),
                Expanded(
                    flex: 2,
                    child: Padding(
                      padding: const EdgeInsets.only(right: Constant.TEXT_FIELD_CONTENT_PADDING),
                      child: IgnorePointer(
                        ignoring: false,
                        child: DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.SPACE_BW_RADIO_BTN,
                              width: Constant.SPACE_BW_RADIO_BTN,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.plan_type,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: selectPlanGroup,
                            items: changePlanGroup
                                .map((ChangePlanGroupScreen value) {
                              return DropdownMenuItem<
                                  ChangePlanGroupScreen>(
                                value: value,
                                child: Text(value.planGroupName!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              selectPlanGroup =
                              value as ChangePlanGroupScreen;
                              setState(() {
                              });
                            },
                            validator: (value) {
                              if (value == null) {
                                return Strings.please_select_plan_screen;
                              }
                              return null;
                            },
                          ),
                        ),
                      ),
                    )),
              ],
            ),
            const SizedBox(height: Constant.SCREEN_PADDING,)
          ],
        ),
      ),
    );
  }

  cardDataRow(String label, String value, Color? textColor) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomText(
              title: label,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small + 1,
              height: 1,
              fontWeight: FontWeight.w500),
          const SizedBox(height: Constant.VERY_SMALL_PADDING),
          CustomText(
              title: value.isNotEmpty ? value : "-",
              colors: textColor ?? AppTheme.lable_noramal,
              textAlign: TextAlign.end,
              fontSize: AppTheme.small,
              maxLines: 2,
              height: 1,
              fontWeight: FontWeight.w400)
        ],
      ),
    );
  }
}

abstract class SelectPlanAction {
  void selectPlanGroupBtnAction({PlanMappingDetail selectedItem});
}
