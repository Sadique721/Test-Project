package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MvnoResponse {
    List<MvnoDTO> mvnos;
    List<ServiceAreaDTO> serviceAreas;
}
