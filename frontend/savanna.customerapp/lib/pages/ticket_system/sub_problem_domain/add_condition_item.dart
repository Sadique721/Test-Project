import 'package:savbill/pages/ticket_system/model/response/condition_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/add_condition_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class AddConditionItemView extends StatelessWidget {
  TatQueryFieldMappingList item;
  int index;
  AddConditionController addConditionController;

  AddConditionItemView(
      {Key? key,
      required this.index,
      required this.item,
      required this.addConditionController})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    CustomText(
                      title: "${Strings.field} ",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall,
                      fontWeight: FontWeight.w700,
                      maxLines: 1,
                    ),
                    const SizedBox(
                      width: Constant.VERY_SMALL_PADDING - 1,
                    ),
                    Container(
                      width: 100,
                      height: Constant.APPBAR_ITEM_H - 10,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(7.0),
                        color: AppTheme.colorWhite,
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButtonFormField(
                          icon: SvgPicture.asset(
                            downArrowSvg,
                            height: Constant.DROP_DOWN_ARROW_W_H,
                            width: Constant.DROP_DOWN_ARROW_W_H,
                            color: AppTheme.colorBlack,
                            fit: BoxFit.fill,
                          ),
                          decoration: Utils.ddlDecoration(),
                          hint: Align(
                            alignment: Alignment.centerLeft,
                            child: CustomText(
                              title: Strings.field,
                              fontSize: AppTheme.small,
                              colors: AppTheme.title_dark,
                            ),
                          ),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: item.selectedField,
                          items: addConditionController.fieldList
                              ?.map((ConditionDetail value) {
                            return DropdownMenuItem<ConditionDetail>(
                              value: value,
                              child: CustomText(
                               title:  value.text!,
                                fontSize: AppTheme.small,
                                colors: AppTheme.title_dark,
                              ),
                            );
                          }).toList(),
                          onChanged: (value) {
                            addConditionController
                                .tatQueryFieldMappingList![index]
                                .selectedField = value as ConditionDetail?;
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    )
                  ],
                ),
                Row(
                  children: [
                    CustomText(
                      title: Strings.operator,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall,
                      fontWeight: FontWeight.w700,
                      maxLines: 1,
                    ),
                    const SizedBox(
                      width: Constant.VERY_SMALL_PADDING - 1,
                    ),
                    Container(
                      width: 100,
                      height: Constant.APPBAR_ITEM_H - 10,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(7.0),
                        color: AppTheme.colorWhite,
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButtonFormField(
                          icon: SvgPicture.asset(
                            downArrowSvg,
                            height: Constant.DROP_DOWN_ARROW_W_H,
                            width: Constant.DROP_DOWN_ARROW_W_H,
                            color: AppTheme.colorBlack,
                            fit: BoxFit.fill,
                          ),
                          decoration: Utils.ddlDecoration(),
                          hint: Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              Strings.operator,
                              style: TextStyle(
                                fontSize: AppTheme.small,
                                color: AppTheme.colorIconGrey,
                                fontFamily: AppTheme.appFontName,
                              ),
                            ),
                          ),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: item.selectedOperator,
                          items: addConditionController.operatorList
                              ?.map((String value) {
                            return DropdownMenuItem<String>(
                              value: value,
                              child: CustomText(
                                title: value,
                                fontSize: AppTheme.small,
                                colors: AppTheme.title_dark,
                              ),
                            );
                          }).toList(),
                          onChanged: (value) {
                            addConditionController
                                .tatQueryFieldMappingList![index]
                                .selectedOperator = value as String?;
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    CustomText(
                      title: Strings.value,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall,
                      fontWeight: FontWeight.w700,
                      maxLines: 1,
                    ),
                    const SizedBox(width: Constant.VERY_SMALL_PADDING - 1),
                    Container(
                      width: 100,
                      height: Constant.APPBAR_ITEM_H - 10,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(7.0),
                        color: AppTheme.colorWhite,
                      ),
                      child: TextFormField(
                          key: Key(item.uId.toString()),
                          initialValue: (item.queryValue != null &&
                                  item.queryValue!.isNotEmpty)
                              ? item.queryValue
                              : "",
                          textAlign: TextAlign.start,
                          textAlignVertical: TextAlignVertical.center,
                          style: TextStyle(
                            color: AppTheme.title_dark,
                            fontSize: AppTheme.small,
                            height: 1,
                            fontFamily: AppTheme.appFontName,
                            decoration: TextDecoration.none,
                          ),
                          decoration: InputDecoration(
                              counterText: "",
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.BTN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorPrimary, width: 1.0),
                              ),
                              focusColor: Colors.transparent,
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.BTN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorLightBlack, width: 1.0),
                              ),
                              enabledBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.BTN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                  color: AppTheme.colorBlack,
                                  width: 1.0,
                                ),
                              ),
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING),
                              hintText: "Enter Value",
                              hintStyle: TextStyle(
                                fontSize: AppTheme.small,
                                color: AppTheme.colorDisableGray
                              ),
                              alignLabelWithHint: true,
                              fillColor: AppTheme.colorWhite,
                              hoverColor: AppTheme.colorWhite),
                          textInputAction: TextInputAction.done,
                          keyboardType: TextInputType.text,
                          maxLines: 1,
                          onChanged: (value) {
                            if (value.isEmpty) {
                              addConditionController
                                  .tatQueryFieldMappingList![index]
                                  .queryValue = "";
                            } else {
                              addConditionController
                                  .tatQueryFieldMappingList![index]
                                  .queryValue = value;
                            }
                            addConditionController.update();
                          }),
                    )
                  ],
                ),
                Row(
                  children: [
                    CustomText(
                      title: Strings.condition,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall,
                      fontWeight: FontWeight.w700,
                      maxLines: 1,
                    ),
                    const SizedBox(
                      width: Constant.VERY_SMALL_PADDING - 1,
                    ),
                    Container(
                      width: 100,
                      height: Constant.APPBAR_ITEM_H - 10,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(7.0),
                        color: AppTheme.colorWhite,
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButtonFormField(
                          icon: SvgPicture.asset(
                            downArrowSvg,
                            height: Constant.DROP_DOWN_ARROW_W_H,
                            width: Constant.DROP_DOWN_ARROW_W_H,
                            color: AppTheme.colorBlack,
                            fit: BoxFit.fill,
                          ),
                          decoration: Utils.ddlDecoration(),
                          hint: Align(
                            alignment: Alignment.centerLeft,
                            child: CustomText(
                              title: Strings.condition,
                              fontSize: AppTheme.small,
                              colors: AppTheme.title_dark,
                            ),
                          ),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: false,
                          isDense: true,
                          value: item.selectedCondition,
                          items: addConditionController.conditionList
                              ?.map((String value) {
                            return DropdownMenuItem<String>(
                              value: value,
                              child: Text(
                                value,
                                style:
                                    const TextStyle(fontSize: AppTheme.small),
                              ),
                            );
                          }).toList(),
                          onChanged: (value) {
                            addConditionController
                                .tatQueryFieldMappingList![index]
                                .selectedCondition = value as String?;
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              ],
            ),
          ],
        ),
      ),
    );
  }

  detailItem(String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
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
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
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
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }
}
