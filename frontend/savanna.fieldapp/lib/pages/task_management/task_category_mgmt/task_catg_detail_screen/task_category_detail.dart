import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/task_catg_detail_screen/task_category_details_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';


class TaskCategoryDetailScreen extends StatefulWidget {
  const TaskCategoryDetailScreen({super.key});

  @override
  State<TaskCategoryDetailScreen> createState() => _TaskCategoryDetailScreenState();
}

class _TaskCategoryDetailScreenState extends State<TaskCategoryDetailScreen> {
  final taskCategoryDetailsController = Get.put(TaskCategoryDetailsController());

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
      child: GetBuilder<TaskCategoryDetailsController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: taskCategoryDetailsController.isLoading),
        ]);
      }), /**/
    );
  }
  _body() {
    return GestureDetector(
        onTap: () {
          FocusScope.of(context).requestFocus(FocusNode());
        },
        child: Container(
            width: MediaQuery.of(context).size.width,
            height: MediaQuery.of(context).size.height,
            margin: const EdgeInsets.only(
              top: Constant.SMALL_PADDING,
            ),
            color: AppTheme.colorBG,
            child: SingleChildScrollView(
              physics: const ScrollPhysics(),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      margin: const EdgeInsets.only(
                          top: Constant.SMALL_PADDING,
                          left: Constant.SCREEN_PADDING),
                      child: CustomText(
                        title: "${Strings.task_category} ${Strings.details}",
                        fontSize: AppTheme.medium,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    basicDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    tatMappingDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                  ]),
            ))
    );
  }
  basicDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.basic_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.name,
                          taskCategoryDetailsController.taskCategoryMgmtDataList?.categoryName ?? "-",
                          Strings.service,
                          taskCategoryDetailsController.taskCategoryMgmtDataList?.status ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }
  tatMappingDetailView(){
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.customer_quota_details),
          maintainState: true,
          shape: const Border(),

          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.tAT_mapping_detail,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            (taskCategoryDetailsController.taskCategoryMgmtDataList !=
                null)
                ? Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            )
                : Container(),
            (taskCategoryDetailsController.taskCategoryMgmtDataList !=
                null)
                ? ListView.builder(
                physics: const NeverScrollableScrollPhysics(),
                scrollDirection: Axis.vertical,
                shrinkWrap: true,
                itemCount: taskCategoryDetailsController
                    .taskCategoryMgmtDataList!.caseCategoryTatMappingList?.length,
                itemBuilder: (context, ii) {
                  CaseCategoryTatMappingList? items = taskCategoryDetailsController.taskCategoryMgmtDataList!.caseCategoryTatMappingList![ii] ;

                  int? lstLength = taskCategoryDetailsController
                      .caseCategoryTatMappingList?.length;

                  String tatForTask = "";
                  if (items?.ticketTatMatrix != null &&
                      items?.ticketTatMatrix!.name != null &&
                      items.ticketTatMatrix!.name!.isNotEmpty) {
                    tatForTask = items.ticketTatMatrix!.name!;
                  }

                  return Padding(
                    padding: EdgeInsets.only(
                        top: (ii == 0)
                            ? Constant.SMALL_PADDING
                            : Constant.EXPANTABLE_ITEM_MARGIN,
                        left: Constant.EXPANTABLE_ITEM_MARGIN,
                        right: Constant.EXPANTABLE_ITEM_MARGIN,
                        bottom: (ii == (lstLength! - 1))
                            ? Constant.EXPANTABLE_ITEM_MARGIN
                            : 0),
                    child: InkWell(
                      onTap: () async {},
                      child: Container(
                        decoration: BoxDecoration(
                          color: AppTheme.expantableItemBg,
                          border:
                          Border.all(color: AppTheme.expantableItemBg),
                          borderRadius: const BorderRadius.all(
                            Radius.circular(3),
                          ),
                        ),
                        child: Padding(
                          padding:
                          const EdgeInsets.all(Constant.SMALL_PADDING),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              basicDetailItem(
                                  Strings.order,
                                  items.orderid != null
                                      ? items.orderid!.toString()
                                      : "-",
                                  Strings.tat_for_task,
                                  tatForTask, null,
                                  false,
                                  false),
                              const SizedBox(
                                  height: Constant.SMALL_PADDING),
                            ],
                          ),
                        ),
                      ),
                    ),
                  );
                })
                : Container(),
          ],
        ),
      ),
    );
  }

  basicDetailItem(String title1, String? value1, String title2, String? value2,
      Function()? onTap1, bool? isLink1, bool? isLink2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              InkWell(
                child: valueWidget(value1, isLink1!),
                onTap: onTap1,
              ),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2, isLink2!),
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
      maxLines: 2,
    );
  }

  valueWidget(String? value, bool isLinkable) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      decoration: isLinkable ? TextDecoration.underline : TextDecoration.none,
      maxLines: 2,
    );
  }
  _appBar() {
    return DynamicAppBar(
        "${Strings.category} ${Strings.details}",
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
