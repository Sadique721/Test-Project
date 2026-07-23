import 'package:savbill/pages/inventory/category/add_edit_category.dart';
import 'package:savbill/pages/inventory/category/category_item.dart';
import 'package:savbill/pages/inventory/category/details/pro_cat_manag_detail.dart';
import 'package:savbill/pages/inventory/category/view_category_controller.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

class ViewCategory extends StatefulWidget {
  @override
  _ViewCategoryState createState() => _ViewCategoryState();
}

class _ViewCategoryState extends State<ViewCategory> {
  final viewCategoryController = Get.put(ViewCategoryController());

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
      child: GetBuilder<ViewCategoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewCategoryController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              // Padding(
              //   padding: const EdgeInsets.symmetric(
              //       horizontal: Constant.SCREEN_PADDING),
              //   child: CustomText(
              //       title: Strings.product_category,
              //       colors: AppTheme.colorBlack,
              //       textAlign: TextAlign.start,
              //       fontSize: AppTheme.medium + 1,
              //       fontWeight: FontWeight.w500),
              // ),
              // const SizedBox(
              //   height: Constant.SMALL_PADDING,
              // ),
              // Container(
              //   color: AppTheme.colorWhite,
              //   padding: const EdgeInsets.symmetric(
              //       horizontal: Constant.SCREEN_PADDING),
              //   child: Column(
              //       crossAxisAlignment: CrossAxisAlignment.center,
              //       mainAxisAlignment: MainAxisAlignment.spaceBetween,
              //       children: [
              //         Card(
              //           margin: const EdgeInsets.all(0),
              //           elevation: 0.5,
              //           shadowColor: AppTheme.colorTransparent,
              //           child: Container(
              //             // height: 50,
              //             padding: const EdgeInsets.symmetric(
              //                 horizontal: Constant.SEARCH_BAR_CARD_PADDING - 2,
              //                 vertical: Constant.SEARCH_BAR_CARD_PADDING - 4),
              //             child: Column(
              //               children: [
              //                 DropdownButtonHideUnderline(
              //                   child: DropdownButtonFormField(
              //                     icon: SvgPicture.asset(
              //                       downArrowSvg,
              //                       height: Constant.DROP_DOWN_ARROW_W_H - 2,
              //                       width: Constant.DROP_DOWN_ARROW_W_H - 2,
              //                       color: AppTheme.colorBlack,
              //                       fit: BoxFit.fill,
              //                     ),
              //                     decoration: Utils.ddlDecoration(),
              //                     hint: Align(
              //                       alignment: Alignment.centerLeft,
              //                       child: Text(
              //                         Strings.select_search_option,
              //                         style: TextStyle(
              //                           fontSize: AppTheme.small,
              //                           color: AppTheme.colorIconGrey,
              //                           fontFamily: AppTheme.appFontName,
              //                         ),
              //                       ),
              //                     ),
              //                     style: AppTheme.dropdownTextStyle,
              //                     isExpanded: false,
              //                     isDense: true,
              //                     value: viewCategoryController
              //                         .selectedCategorySearchOption,
              //                     items: viewCategoryController
              //                         .categorySearchOptionList!
              //                         .map((DropdownDetail value) {
              //                       return DropdownMenuItem<DropdownDetail>(
              //                         value: value,
              //                         child: Text(value.text!),
              //                       );
              //                     }).toList(),
              //                     onChanged: (value) {
              //                       viewCategoryController
              //                               .selectedCategorySearchOption =
              //                           value as DropdownDetail?;
              //                     },
              //                     validator: (value) {
              //                       return null;
              //                     },
              //                   ),
              //                 ),
              //                 const SizedBox(
              //                   height: Constant.SMALL_PADDING,
              //                 ),
              //                 CoustomTextField(
              //                     labelText: Strings.search_your_text_here,
              //                     textEditingController:
              //                     viewCategoryController.searchController,
              //                     keyboardType: TextInputType.text,
              //                     borderEnableColors: AppTheme.colorPrimary,
              //                     textInputAction: TextInputAction.done,
              //                     onChanged: (value) {},
              //                     onTextValidator: (String? value) {
              //                       return null;
              //                     },
              //                     // prefixIcon: Icon(
              //                     //   Icons.search,
              //                     //   color: AppTheme.colorPrimary,
              //                     // ),
              //                     borderCorner: Constant.BTN_ROUNDED_CORNER,
              //                     contentPadding: const EdgeInsets.symmetric(
              //                         horizontal: Constant.LARGE_PADDING),
              //                     readOnly: false),
              //               ],
              //             ),
              //           ),
              //         ),
              //         const SizedBox(
              //           height: Constant.SMALL_PADDING,
              //         ),
              //         Row(
              //             mainAxisAlignment: MainAxisAlignment.end,
              //             children: [
              //               Material(
              //                 color: AppTheme.colorWhite,
              //                 elevation: 2,
              //                 shape: RoundedRectangleBorder(
              //                     borderRadius: BorderRadius.circular(6)),
              //                 child: InkWell(
              //                   onTap: () {
              //                     viewCategoryController.applyFilter();
              //                   },
              //                   child: Container(
              //                     decoration: BoxDecoration(
              //                       color: AppTheme.statusClosedGreen,
              //                       borderRadius: const BorderRadius.all(
              //                           Radius.circular(6)),
              //                     ),
              //                     padding: const EdgeInsets.all(5),
              //                     child: Icon(
              //                       Icons.check,
              //                       color: AppTheme.colorWhite,
              //                       size: 22,
              //                     ),
              //                   ),
              //                 ),
              //               ),
              //               const SizedBox(
              //                 width: Constant.SMALL_PADDING,
              //               ),
              //               Material(
              //                 color: AppTheme.colorWhite,
              //                 elevation: 2,
              //                 shape: RoundedRectangleBorder(
              //                     borderRadius: BorderRadius.circular(6)),
              //                 child: InkWell(
              //                   onTap: () {
              //                     viewCategoryController.clearFilter();
              //                   },
              //                   child: Container(
              //                     decoration: BoxDecoration(
              //                       color: AppTheme.colorRed,
              //                       borderRadius: const BorderRadius.all(
              //                           Radius.circular(6)),
              //                     ),
              //                     padding: const EdgeInsets.all(5),
              //                     child: Icon(
              //                       Icons.close,
              //                       color: AppTheme.colorWhite,
              //                       size: 22,
              //                     ),
              //                   ),
              //                 ),
              //               ),
              //             ]),
              //         const SizedBox(
              //           height: Constant.VERY_SMALL_PADDING,
              //         ),
              //       ]),
              // ),

              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      CustomText(
                          title: Strings.product_category,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500),
                      InkWell(
                        onTap: () {
                          if (viewCategoryController.filterViewOpen) {
                            viewCategoryController.filterViewOpen =
                            false;
                          } else {
                            viewCategoryController.filterViewOpen =
                            true;
                          }
                          viewCategoryController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color:
                              viewCategoryController.isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ]),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              viewCategoryController.filterViewOpen
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
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H - 2,
                              width: Constant.DROP_DOWN_ARROW_W_H - 2,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.select_search_option,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: false,
                            isDense: true,
                            value: viewCategoryController
                                .selectedCategorySearchOption,
                            items: viewCategoryController
                                .categorySearchOptionList!
                                .map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              viewCategoryController
                                  .selectedCategorySearchOption =
                              value as DropdownDetail?;
                            },
                            validator: (value) {
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.enter_search_detail,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                            viewCategoryController
                                .searchController,
                            borderEnableColors: AppTheme.colorBlack,
                            borderFocusColors: AppTheme.colorBlack,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            keyboardType: TextInputType.text,
                            maxLines: 1,
                            onTextValidator: (String? value) {},
                            onTextFiledOnTap: () {},
                            readOnly: false),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Expanded(
                                child: SimpleButton(
                                  onTap: () {
                                    viewCategoryController
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
                                    viewCategoryController
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
                            ]),
                      ],
                    ),
                  ),
                ),
              )
                  : Container(),
              viewCategoryController.filterViewOpen
                  ? const SizedBox(
                height: Constant.MEDIUM_PADDING,
              )
                  : Container(),
              

              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (viewCategoryController.categoryList != null &&
                        viewCategoryController.categoryList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewCategoryController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                viewCategoryController.categoryList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  viewCategoryController.categoryList?.length) {
                                if (viewCategoryController.isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                                  AppTheme.colorProgress),
                                          backgroundColor:
                                              AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                CategoryDetail item =
                                    viewCategoryController.categoryList![index];
                                return CategoryItem(
                                  index: index,
                                  item: item,
                                  onTapEdit: () {
                                    addEditCategoryScreen(Strings.edit, item);
                                  },
                                  onTapDelete: () {
                                    showDialog(
                                      context: context,
                                      builder: (BuildContext context) {
                                        return AlertDialogHelper(
                                            title: Strings.app_name,
                                            message: Strings.msg_delete,
                                            positiveBtnText: Strings.ok,
                                            negativeBtnText: Strings.cancel,
                                            positiveBtnClick: () {
                                              Get.back();
                                              viewCategoryController
                                                  .deleteCategoryData(
                                                      item, index);
                                            },
                                            negativeBtnClick: () {
                                              Get.back();
                                            });
                                      },
                                    );
                                  },
                                  onTapCategoryDetails: (){
                                    categoryDetailsScreen(productId: item.id,productName: item.name);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
              Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                    onTap: () {
                      addEditCategoryScreen(Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.add_product_category,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ))
                ],
              )
            ]),
      ),
    );
  }

  addEditCategoryScreen(String from, CategoryDetail? item) async {
    var result = await Get.to(AddEditCategory(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewCategoryController.clearFilter();
    }
  }

  categoryDetailsScreen({int? productId, String? productName}) async {
    var result = await Get.to(()=> ProductCatManagementDetails(),
        arguments: {Constant.PRODUCT_ID: productId,Constant.PRODUCT_NAME: productName});

    if (result != null && result == true) {
      viewCategoryController.clearFilter();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.product_category_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
