package com.savbill.cpm.modules.purchaseDetails.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.paymentGatewayMaster.domain.PaymentGateWay;
import com.savbill.cpm.modules.paymentGatewayMaster.dto.PaymentGatewayDTO;
import com.savbill.cpm.modules.paymentGatewayMaster.mapper.PaymentGatewayMapper;
import com.savbill.cpm.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.savbill.cpm.modules.placeOrder.domain.Order;
import com.savbill.cpm.modules.placeOrder.mapper.OrderMapper;
import com.savbill.cpm.modules.placeOrder.model.OrderDTO;
import com.savbill.cpm.modules.placeOrder.service.OrderService;
import com.savbill.cpm.modules.purchaseDetails.domain.PurchaseDetails;
import com.savbill.cpm.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.postpaid.PartnerService;

@Mapper
public abstract class PurchaseDetailsMapper implements IBaseMapper<PurchaseDetailsDTO, PurchaseDetails> {

    @Override
    @Mapping(source = "purchaseDetails.customer", target = "custid")
    @Mapping(source = "purchaseDetails.order", target = "orderid")
    @Mapping(source = "purchaseDetails.paymentGateWay", target = "pgid")
    @Mapping(source = "purchaseDetails.partner", target = "partnerid")
    @Mapping(source = "purchaseDetails.purchasedate",target = "purchaseDateString",dateFormat = "dd-MM-yyyy HH:mm a")
    @Mapping(source = "purchaseDetails.transResDate",target = "transResDateString",dateFormat = "dd-MM-yyyy HH:mm a")
    public abstract PurchaseDetailsDTO domainToDTO(PurchaseDetails purchaseDetails, CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "customer", source = "dtoData.custid")
    @Mapping(target = "order", source = "dtoData.orderid")
    @Mapping(target = "paymentGateWay", source = "dtoData.pgid")
    @Mapping(target = "partner", source = "dtoData.partnerid")
    public abstract PurchaseDetails dtoToDomain(PurchaseDetailsDTO dtoData, CycleAvoidingMappingContext context);

    @Autowired
    private CustomersService customersService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private PaymentGatewayMapper paymentGatewayMapper;
    @Autowired
    private PartnerService partnerService;

    Integer fromCustomersToId(Customers entity) {
        return entity == null ? null : entity.getId();
    }

    Customers fromCustomersIdToCustomers(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Customers entity;
        try {
            entity = customersService.get(entityId);
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer fromPartnerToId(Partner entity) {
        return entity == null ? null : entity.getId();
    }

    Partner fromPartnerIdToPartner(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Partner entity;
        try {
            entity = partnerService.get(entityId);
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Long fromOrderToId(Order entity) {
        return entity == null ? null : entity.getId();
    }

    Order fromOrderIdToOrder(Long entityId) {
        if (entityId == null) {
            return null;
        }
        Order entity;
        try {
            OrderDTO entityDTO = orderService.getEntityById(entityId, false);
            entity = orderMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Long fromPaymentGatewayToId(PaymentGateWay entity) {
        return entity == null ? null : entity.getId();
    }

    PaymentGateWay fromPaymentGatewayIdToPaymentGateway(Long entityId) {
        if (entityId == null) {
            return null;
        }
        PaymentGateWay entity;
        try {
            PaymentGatewayDTO entityDTO = paymentGatewayService.getEntityById(entityId, false);
            entity = paymentGatewayMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    void afterMapping(@MappingTarget PurchaseDetailsDTO purchaseDetailsDTO, PurchaseDetails purchaseDetails) {
        if (null != purchaseDetails.getPartner()) {
            purchaseDetailsDTO.setPartnerName(purchaseDetails.getPartner().getName());
        } else {
            purchaseDetailsDTO.setPartnerName("-");
        }
        if (null != purchaseDetails.getCustomer()) {
            purchaseDetailsDTO.setCustName(purchaseDetails.getCustomer().getFullName());
        } else {
            purchaseDetailsDTO.setCustName("-");
        }
        if (null != purchaseDetails.getPaymentGateWay()) {
            purchaseDetailsDTO.setPgName(purchaseDetails.getPaymentGateWay().getName());
        } else {
            purchaseDetailsDTO.setPgName("-");
        }
    }
}
