import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/util/constant.dart';
import 'package:get/get.dart';

class SubProblemDomainDetailController extends GetxController {
  bool isLoading = false;
  SubProblemDomainDetail? subProblemDomainDetail;
  List<TicketSubCategoryGroupReasonMappingList>? subCategoryGroupReason = [];
  List<TicketSubCategoryTatMappingList>? ticketSubCategoryTatMappingList = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.SPD_DETAIL] != null) {
        subProblemDomainDetail = arguments[Constant.SPD_DETAIL];
      }
    }
    if (subProblemDomainDetail != null) {
      if (subProblemDomainDetail!.ticketSubCategoryGroupReasonMappingList !=
              null &&
          subProblemDomainDetail!
              .ticketSubCategoryGroupReasonMappingList!.isNotEmpty) {
        subCategoryGroupReason!.addAll(
            subProblemDomainDetail!.ticketSubCategoryGroupReasonMappingList!);
      }

      if (subProblemDomainDetail!.ticketSubCategoryTatMappingList != null &&
          subProblemDomainDetail!.ticketSubCategoryTatMappingList!.isNotEmpty) {
        ticketSubCategoryTatMappingList!
            .addAll(subProblemDomainDetail!.ticketSubCategoryTatMappingList!);
      }
    }
    update();
  }
}
