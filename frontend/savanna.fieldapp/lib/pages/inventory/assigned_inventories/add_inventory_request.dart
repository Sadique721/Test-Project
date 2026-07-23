import 'package:savbill/pages/inventory/assigned_inventories/raised_inventory_product_item.dart';
import 'package:savbill/pages/inventory/module/response/add_product_inventory_request.dart';
import 'package:savbill/pages/inventory/module/response/all_active_product_categories_by_res.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_active_products_by_product_category_res.dart';
import 'package:savbill/pages/inventory/module/response/on_behalf_of_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_new_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';
import '../../../util/strings.dart';
import '../../../widgets/dynamic_appbar.dart';
import '../../../widgets/input_textfield.dart';
import '../../../widgets/progress_bar.dart';
import '../../../widgets/title_widge.dart';
import 'add_inventory_controller.dart';

class AddInventoryRequest extends StatefulWidget {
  @override
  _AddEditProductState createState() => _AddEditProductState();
}

class _AddEditProductState extends State<AddInventoryRequest> {
  final addInventoryController = Get.put(AddInventoryController());
  final addEditProductFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
      child: GetBuilder<AddInventoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: addInventoryController.isLoading),
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
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SCREEN_PADDING,
                        right: Constant.SCREEN_PADDING),
                    child: Form(
                      key: addEditProductFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                              height: Constant.SCREEN_PADDING
                          ),
                          Stack(
                            children: [
                              Container(
                                padding:const EdgeInsets.all(10),
                                margin: const EdgeInsets.only(top: 10,bottom: 10),
                                decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(5),
                                    border: Border.all(
                                        width: 1.0,
                                        style: BorderStyle.solid,
                                        color: AppTheme.colorIconGrey
                                    )
                                ),
                                child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment:MainAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.on_behalf_of, require: true),

                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),


                                    DropdownButtonHideUnderline(
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
                                            Strings.select_onbehalf_of,
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
                                        value: addInventoryController.selectedOnBehalfValue,
                                        // items: addInventoryController.chargeList
                                        //     ?.map((ProductChargeDetail value) {
                                        items: <String>['Warehouse', 'Pop', 'ServiceArea', 'StaffUser']
                                            .map<DropdownMenuItem<String>>((String value) {
                                          // return DropdownMenuItem<ProductChargeDetail>(
                                          return DropdownMenuItem<String>(
                                            value: value,
                                            // child: Text(value.name!),
                                            child: Text(value,style:TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorBlack,
                                              fontFamily: AppTheme.appFontName,
                                            ),),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addInventoryController.selectedOnBehalfValue = value as String;
                                          addInventoryController.onBehalfOfList!.clear();
                                          addInventoryController.getOnBehalfOfList(addInventoryController.selectedOnBehalfValue!);
                                          addInventoryController.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addInventoryController.selectedOnBehalfValue == null) {
                                  return Strings.please_select_charge;
                                }
                                          // return null;
                                        },
                                      ),
                                    ),

                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.requester, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),

                                    IgnorePointer(
                                        ignoring:/*addInventoryController.onBehalfData != null ? true : */false,
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
                                              Strings.select_requester,
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
                                          value: addInventoryController.onBehalfData,
                                          items: addInventoryController.onBehalfOfList
                                              ?.map((OnBehalfDataList value) {
                                            return DropdownMenuItem<OnBehalfDataList>(
                                              value: value,
                                              child: Text(value.name!,style:TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorBlack,
                                                fontFamily: AppTheme.appFontName,
                                              ),),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            addInventoryController.onBehalfData = value as OnBehalfDataList?;
                                            addInventoryController.allWareHouseList!.clear();
                                            addInventoryController.selectedWareHouseData = null;
                                            addInventoryController.getAllWareHouses();
                                            addInventoryController.update();
                                          },
                                          validator: (value) {
                                           if (value == null || addInventoryController.onBehalfData == null) {
                                              return Strings.please_select_category;
                                            }
                                            // return null;
                                          },
                                        ),
                                      ),
                                    ),

                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.request_to, require: true),

                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),

                                    DropdownButtonHideUnderline(
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
                                            Strings.select_requester,
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
                                        value: addInventoryController.selectedWareHouseData,
                                        items: addInventoryController.allWareHouseList
                                            ?.map((WareHouseDataList value) {
                                          return DropdownMenuItem<WareHouseDataList>(
                                            value: value,
                                            child: Text(value.name!,style:TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorBlack,
                                              fontFamily: AppTheme.appFontName,
                                            ),),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addInventoryController.selectedWareHouseData = value as WareHouseDataList;
                                          addInventoryController.update();
                                        },
                                        validator: (value) {
                                          if (value == null || addInventoryController.selectedWareHouseData == null) {
                                            return Strings.please_select_ware_house;
                                          }
                                          // return null;
                                        },
                                      ),
                                    ),

                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.reason, require: true),

                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_reason,
                                        textEditingController: addInventoryController.reasonController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if(value!.isEmpty){
                                            return Strings.enter_reason;
                                          }
                                          return null;
                                        },
                                        borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding: const EdgeInsets.symmetric(
                                            horizontal: Constant.LARGE_PADDING),
                                        readOnly: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                  ],
                              ),),
                              Positioned(
                                left: 30,
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: BoxDecoration(borderRadius: BorderRadius.circular(3),color: Colors.white),
                                  child: InputTitleRequire(
                                      title: Strings.basic_details, require: false),
                                ),
                              ),
                            ],
                          ),


                          const SizedBox(
                              height: Constant.SCREEN_PADDING
                          ),


                          Stack(
                            children: [
                              Container(
                                padding:const EdgeInsets.all(10),
                                margin: const EdgeInsets.only(top: 10,bottom: 10),
                                decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(5),
                                    border: Border.all(
                                        width: 1.0,
                                        style: BorderStyle.solid,
                                        color: AppTheme.colorIconGrey
                                    )
                                ),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment:MainAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.product_category, require: true),

                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),

                                    // DropdownButtonHideUnderline(
                                    //   child: DropdownButtonFormField(
                                    //     icon: SvgPicture.asset(
                                    //       downArrowSvg,
                                    //       height: Constant.DROP_DOWN_ARROW_W_H,
                                    //       width: Constant.DROP_DOWN_ARROW_W_H,
                                    //       color: AppTheme.colorBlack,
                                    //       fit: BoxFit.fill,
                                    //     ),
                                    //     decoration: Utils.ddlDecoration(),
                                    //     hint: Align(
                                    //       alignment: Alignment.centerLeft,
                                    //       child: Text(
                                    //         Strings.select_product_category,
                                    //         style: TextStyle(
                                    //           fontSize: AppTheme.medium,
                                    //           color: AppTheme.colorIconGrey,
                                    //           fontFamily: AppTheme.appFontName,
                                    //         ),
                                    //       ),
                                    //     ),
                                    //     style: AppTheme.dropdownTextStyle,
                                    //     isExpanded: false,
                                    //     isDense: true,
                                    //     value: addInventoryController.productCategoriesData,
                                    //     items: addInventoryController.productCategoriesList
                                    //         ?.map((ProductCategoriesList value) {
                                    //       return DropdownMenuItem<ProductCategoriesList>(
                                    //         value: value,
                                    //         child: Text(value.name!,style:TextStyle(
                                    //           fontSize: AppTheme.medium,
                                    //           color: AppTheme.colorBlack,
                                    //           fontFamily: AppTheme.appFontName,
                                    //         ),),
                                    //       );
                                    //     }).toList(),
                                    //     onChanged: (value) {
                                    //       addInventoryController.productCategoriesData = value as ProductCategoriesList?;
                                    //       addInventoryController.allCategoryProductIdCategoryList!.clear();
                                    //       addInventoryController.getCategoryByProductId(value!.id!);
                                    //       addInventoryController.update();
                                    //     },
                                    //     validator: (value) {
                                    //       // if (value == null || addInventoryController.productCategoriesData == null) {
                                    //       //   return Strings.please_select_product_category;
                                    //       // }
                                    //       return null;
                                    //     },
                                    //   ),
                                    // ),
                                    DropdownButtonHideUnderline(
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
                                            Strings.select_product_category,
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
                                        value: addInventoryController.selectedProductCategory,
                                        items: addInventoryController.productCategoryList
                                            ?.map((CategoryDetail value) {
                                          return DropdownMenuItem<CategoryDetail>(
                                            value: value,
                                            child: Text(value.name!,style:TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorBlack,
                                              fontFamily: AppTheme.appFontName,
                                            ),),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addInventoryController.selectedProductCategory = value as CategoryDetail?;
                                          addInventoryController.allCategoryProductIdCategoryList!.clear();
                                          addInventoryController.getCategoryByProductId(value!.id!);
                                          addInventoryController.update();
                                        },
                                        validator: (value) {
                                          // if (value == null || addInventoryController.productCategoriesData == null) {
                                          //   return Strings.please_select_product_category;
                                          // }
                                          return null;
                                        },
                                      ),
                                    ),

                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.product, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),

                                    DropdownButtonHideUnderline(
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
                                            Strings.select_product,
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
                                        value: addInventoryController.allActiveProductsByProductData,
                                        items: addInventoryController.allCategoryProductIdCategoryList
                                            ?.map((AllActiveProductsByProductData value) {
                                          return DropdownMenuItem<AllActiveProductsByProductData>(
                                            value: value,
                                            child: Text(value.name!,style:TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorBlack,
                                              fontFamily: AppTheme.appFontName,
                                            ),),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addInventoryController.allActiveProductsByProductData = value as AllActiveProductsByProductData;
                                          // addInventoryController.getAllActiveProductsByProductCategoryRes = value as GetAllActiveProductsByProductCategoryRes?;
                                          addInventoryController.update();
                                        },
                                        validator: (value) {
                                          // if (value == null || addInventoryController.allActiveProductsByProductData == null) {
                                          //   return Strings.please_select_product;
                                          // }
                                          return null;
                                        },
                                      ),
                                    ),

                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.type, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),

                                    DropdownButtonHideUnderline(
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
                                            Strings.select_type,
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
                                        value: addInventoryController.selectedProductType,
                                        items: addInventoryController.productType
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!,style:TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorBlack,
                                              fontFamily: AppTheme.appFontName,
                                            ),),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addInventoryController.selectedProductType = value as DropdownDetail? ;
                                          addInventoryController.update();
                                        },
                                        validator: (value) {
                                          // if (value == null || addInventoryController.selectedProductType == null) {
                                          //   return Strings.please_select_product_type;
                                          // }
                                          return null;
                                        },
                                      ),
                                    ),

                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.qty, require: true),


                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_quantity,
                                        textEditingController: addInventoryController.qtyProductController,
                                        keyboardType: TextInputType.number,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding: const EdgeInsets.symmetric(
                                            horizontal: Constant.LARGE_PADDING),
                                        readOnly: false),

                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    Align(
                                        alignment: Alignment.centerRight,
                                        child: InkWell(
                                          onTap: () {
                                            if (addInventoryController.qtyProductController.text.isEmpty ||
                                                (addInventoryController.selectedProductCategory == null ||
                                                    addInventoryController.selectedProductCategory!.isNullOrEmpty()) ||
                                                (addInventoryController.allActiveProductsByProductData == null ||
                                                    addInventoryController.getAllActiveProductsByProductCategoryRes!.isNullOrEmpty())) {
                                              Utils.showSnackbar(
                                                  Strings.ERROR,
                                                  "Please add product task_catg_detail_screen",
                                                  AppTheme.colorWhite,
                                                  AppTheme.colorRed);
                                              return;
                                            }
                                            int? qty = int.parse(addInventoryController.qtyProductController.text);
                                            addInventoryController
                                                .addProductDetails!
                                                .add(RequestInvenotryProductMappings(
                                                productCategoryId: addInventoryController.selectedProductCategory!.id,
                                                productCategoryName: addInventoryController.selectedProductCategory!.name,
                                                productId : addInventoryController.allActiveProductsByProductData!.id,
                                                productName : addInventoryController.allActiveProductsByProductData!.name,
                                                itemType : addInventoryController.selectedProductType!.text,
                                                quantity : qty
                                               ));

                                            // addInventoryController.productCategoriesList!.clear();
                                            // addInventoryController.allCategoryProductIdCategoryList!.clear();
                                            // addInventoryController.productType.clear();
                                            addInventoryController.selectedProductCategory = null;
                                            addInventoryController.allActiveProductsByProductData = null;
                                            addInventoryController.selectedProductType = null;
                                            addInventoryController.qtyProductController.clear();
                                            addInventoryController.update();
                                          },
                                          child: CustomText(
                                            title: "+ Add",
                                            colors: AppTheme.colorPrimary,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.medium,
                                            fontWeight: FontWeight.w600,
                                          ),
                                        )),

                                    const SizedBox(
                                        height: Constant.SMALL_PADDING),
                                    (addInventoryController
                                            .addProductDetails!.isNotEmpty)
                                        ? ListView.builder(
                                        physics:
                                        const NeverScrollableScrollPhysics(),
                                        shrinkWrap: true,
                                        itemCount:
                                        addInventoryController
                                            .addProductDetails!.length,
                                        itemBuilder: (BuildContext context,
                                            int index) {
                                          RequestInvenotryProductMappings item =
                                          addInventoryController
                                              .addProductDetails![
                                          index];
                                          return Container(
                                            margin: EdgeInsets.only(
                                                top: index == 0
                                                    ? 0
                                                    : Constant
                                                    .VERY_SMALL_PADDING),
                                            child: RaisedInventoryProductItem(
                                                item: item,
                                                isShowDelete: true,
                                                onTapDelete: () {
                                                  addInventoryController
                                                      .addProductDetails!
                                                      .removeAt(index);
                                                  addInventoryController
                                                      .update();
                                                }),
                                          );
                                        })
                                        : Container(),


                                  ],

                                ),),
                              Positioned(
                                left: 30,
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: BoxDecoration(borderRadius: BorderRadius.circular(3),color: Colors.white),
                                  child: InputTitleRequire(
                                      title: Strings.product_details, require: false),
                                ),
                              ),
                            ],
                          ),

                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        if(addInventoryController.addProductDetails!.isNotEmpty) {
                          validateForm();
                        }else{
                          Utils.showSnackbar(Strings.INFO, Strings.please_add_product_details, AppTheme.colorWhite, AppTheme.colorBlueRView);
                        }
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.submit,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  validateForm() {
    if (addEditProductFormKey.currentState!.validate()) {
      addInventoryController.saveRequestInventoryApi();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addInventoryController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_product
            : Strings.add_product,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}