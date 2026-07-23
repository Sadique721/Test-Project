import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/add_condition_controller.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/add_condition_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class AddCondition extends StatefulWidget {
  @override
  _AddConditionState createState() => _AddConditionState();
}

class _AddConditionState extends State<AddCondition> {
  final addConditionController = Get.put(AddConditionController());

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
      child: GetBuilder<AddConditionController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addConditionController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return Stack(children: [
      GestureDetector(
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
                const SizedBox(
                  height: Constant.SCREEN_PADDING,
                ),
                Expanded(
                  child: SingleChildScrollView(
                    child: (addConditionController.tatQueryFieldMappingList !=
                                null &&
                            addConditionController
                                .tatQueryFieldMappingList!.isNotEmpty)
                        ? ListView.builder(
                            physics: const NeverScrollableScrollPhysics(),
                            shrinkWrap: true,
                            itemCount: addConditionController
                                .tatQueryFieldMappingList!.length,
                            itemBuilder: (BuildContext context, int index) {
                              TatQueryFieldMappingList item =
                                  addConditionController
                                      .tatQueryFieldMappingList![index];
                              return Container(
                                margin: EdgeInsets.only(
                                  top: index == 0 ? 0 : Constant.SMALL_PADDING,
                                  left: Constant.SCREEN_PADDING,
                                  right: Constant.SCREEN_PADDING,
                                ),
                                child: AddConditionItemView(
                                    item: item,
                                    index: index,
                                    addConditionController:
                                        addConditionController),
                              );
                            })
                        : Container(),
                  ),
                ),
              ]),
        ),
      ),
      Positioned(
          child: Align(
        alignment: FractionalOffset.bottomCenter,
        child: Row(
          children: [
            _buttonView(Strings.add),
            const SizedBox(
              height: Constant.BOTTOM_BTN_HEIGHT,
              width: 1.5,
            ),
            _buttonView(Strings.save)
          ],
        ),
      )),
    ]);
  }

  _buttonView(String btnName) {
    return Expanded(
      child: SimpleButton(
        onTap: () {
          addConditionController.addConditionItem(btnName);
        },
        radius: 0,
        height: Constant.BOTTOM_BTN_HEIGHT,
        bgColors: AppTheme.colorPrimary,
        borderColors: AppTheme.colorPrimary,
        child: CustomText(
          title: btnName,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.w400,
        ),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.add_condition, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
