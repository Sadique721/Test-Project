import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';

import '../../../dashboard/model/response/show_tat_details_res.dart';

class AddEditTatTicketReq {
  String? name;
  String? status;
  num? slaTimep1;
  num? slaTimep2;
  num? slaTime3;
  String? sunitp1;
  String? sunitp2;
  String? sunitp3;
  num? rtime;
  String? runit;
  int? id;
  List<TatMatrixMappings>? tatMatrixMappings;

  AddEditTatTicketReq(
      {this.name,
        this.status,
        this.slaTimep1,
        this.slaTimep2,
        this.slaTime3,
        this.sunitp1,
        this.sunitp2,
        this.sunitp3,
        this.rtime,
        this.runit,
        this.id,
        this.tatMatrixMappings});

  AddEditTatTicketReq.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    status = json['status'];
    slaTimep1 = json['slaTimep1'];
    slaTimep2 = json['slaTimep2'];
    slaTime3 = json['slaTime3'];
    sunitp1 = json['sunitp1'];
    sunitp2 = json['sunitp2'];
    sunitp3 = json['sunitp3'];
    rtime = json['rtime'];
    runit = json['runit'];
    id = json['id'];
    if (json['tatMatrixMappings'] != null) {
      tatMatrixMappings = <TatMatrixMappings>[];
      json['tatMatrixMappings'].forEach((v) {
        tatMatrixMappings!.add(new TatMatrixMappings.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['status'] = this.status;
    data['slaTimep1'] = this.slaTimep1;
    data['slaTimep2'] = this.slaTimep2;
    data['slaTime3'] = this.slaTime3;
    data['sunitp1'] = this.sunitp1;
    data['sunitp2'] = this.sunitp2;
    data['sunitp3'] = this.sunitp3;
    data['rtime'] = this.rtime;
    data['runit'] = this.runit;
    data['id'] = this.id;
    if (this.tatMatrixMappings != null) {
      data['tatMatrixMappings'] =
          this.tatMatrixMappings!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}