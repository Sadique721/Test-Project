package com.savbill.integrationsystem.etims.controller;

import com.savbill.integrationsystem.core.dto.KRAGenericResponseDTO;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.etims.DTO.*;
import com.savbill.integrationsystem.etims.service.KRAETimsService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/SavbillIntegrationSystem/etims")
public class ApiController {

    @Autowired
    private KRAETimsService etimsService;

    @PostMapping("/addCustomerV2")
    public KRAGenericResponseDTO addCustomer(@RequestBody ETimsCustomerDTO dto, HttpServletRequest req) {

        KRAGenericResponseDTO response = new KRAGenericResponseDTO();
        MDC.put("type", "ETIMS_ADD_CUSTOMER");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {
            if (dto == null || dto.getCustomerNo() == null || dto.getCustomerTin() == null) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("CustomerNo and CustomerTIN are required!");
                return response;
            }
            response = etimsService.processEtimsAddCustomer(dto);
            if (response == null) {
                response = new KRAGenericResponseDTO();
                response.setResponseCode(APIConstants.NULL_VALUE);
                response.setResponseMessage("No response from eTIMS!");
                response.setData(null);
            }
            return response;
        } catch (Exception ex) {
            response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing eTIMS customer");
            response.setData(ex.getMessage());
            return response;
        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/addItemsListV2")
    public KRAGenericResponseDTO addItemsList(@RequestBody List<ETimsItemDTO> items, HttpServletRequest req) {

        KRAGenericResponseDTO response = new KRAGenericResponseDTO();
        MDC.put("type", "ETIMS_ADD_ITEMS");
        MDC.put("requestFrom", req.getHeader("requestFrom"));
        try {
            if (items == null || items.isEmpty()) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Item list cannot be empty!");
                return response;
            }
            for (ETimsItemDTO item : items) {
                if (item.getItemCode() == null || item.getItemName() == null) {
                    response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setResponseMessage("ItemCode and ItemName are required!");
                    return response;
                }
            }
            response = etimsService.processEtimsAddItemsList(items);
            if (response == null) {
                response = new KRAGenericResponseDTO();
                response.setResponseCode(APIConstants.NULL_VALUE);
                response.setResponseMessage("No response from eTIMS!");
                response.setData(null);
            }
            return response;
        } catch (Exception ex) {
            response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing eTIMS Add Items");
            response.setData(ex.getMessage());
            return response;
        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/updateItemV2")
    public KRAGenericResponseDTO updateItemsList(@RequestBody List<ETimsItemDTO> itemList,
                                          HttpServletRequest req) {

        KRAGenericResponseDTO response = new KRAGenericResponseDTO();

        MDC.put("type", "ETIMS_UPDATE_ITEMS");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {

            //  Validation
            if (itemList == null || itemList.isEmpty()) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Item list cannot be empty!");
                return response;
            }

            List<Object> resultList = new ArrayList<>();

            for (ETimsItemDTO item : itemList) {

                if (item.getItemCode() == null) {
                    response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setResponseMessage("ItemCode is required!");
                    return response;
                }

                //  Call SINGLE API per item
                KRAGenericResponseDTO singleResponse =
                        etimsService.processEtimsUpdateItem(item);

                resultList.add(singleResponse.getData());
            }

            //  Final response
            response.setResponseCode(APIConstants.SUCCESS);
            response.setResponseMessage("All items processed successfully");
            response.setData(resultList);

            return response;

        } catch (Exception ex) {

            response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing eTIMS Update Items");
            response.setData(ex.getMessage());

            return response;

        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/addSaleV2")
    public KRAGenericResponseDTO addSale(@RequestBody ETimsSaleDTO sale,
                                  HttpServletRequest req) {

        KRAGenericResponseDTO response = new KRAGenericResponseDTO();

        MDC.put("type", "ETIMS_ADD_SALE");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {

            //  Basic Validation
            if (sale == null || sale.getCustomerTin() == null || sale.getSaleItemList() == null) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("CustomerTIN and Sale Items are required!");
                return response;
            }

            if (sale.getSaleItemList().isEmpty()) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Sale item list cannot be empty!");
                return response;
            }

            //  Service Call
            response = etimsService.processEtimsAddSale(sale);

            //  Null Safety
            if (response == null) {
                response = new KRAGenericResponseDTO();
                response.setResponseCode(APIConstants.NULL_VALUE);
                response.setResponseMessage("No response from eTIMS!");
                response.setData(null);
            }

            return response;

        } catch (Exception ex) {

            response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing eTIMS Add Sale");
            response.setData(ex.getMessage());

            return response;

        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/addSaleCreditNoteV2")
    public KRAGenericResponseDTO addCreditNote(@RequestBody ETimsCreditNoteDTO creditNote,
                                        HttpServletRequest req) {

        KRAGenericResponseDTO response = new KRAGenericResponseDTO();

        MDC.put("type", "ETIMS_ADD_CREDIT_NOTE");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {
            if (creditNote == null || creditNote.getOrgInvoiceNo() == null || creditNote.getCreditNoteItemsList() == null) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("OrgInvoiceNo and Items are required!");
                return response;
            }
            if (creditNote.getCreditNoteItemsList().isEmpty()) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Credit note item list cannot be empty!");
                return response;
            }

            //  Service Call
            response = etimsService.processEtimsAddCreditNote(creditNote);

            //  Null Safety
            if (response == null) {
                response = new KRAGenericResponseDTO();
                response.setResponseCode(APIConstants.NULL_VALUE);
                response.setResponseMessage("No response from eTIMS!");
                response.setData(null);
            }

            return response;

        } catch (Exception ex) {

            response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing Credit Note");
            response.setData(ex.getMessage());

            return response;

        } finally {
            MDC.clear();
        }
    }
    @GetMapping("/getSalesByTraderInvoiceNoV2")
    public KRAGenericResponseDTO getSalesByTraderInvoiceNo(@RequestBody ETimsGetSalesDTO salesDTO,
            HttpServletRequest req) {

        KRAGenericResponseDTO response = new KRAGenericResponseDTO();

        MDC.put("type", "ETIMS_GET_SALE");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {

            //  Validation
            if (salesDTO.getTraderInvoiceNo() == null || salesDTO.getTraderInvoiceNo().trim().isEmpty()) {
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("TraderInvoiceNo is required!");
                return response;
            }

            //  Service Call
            response = etimsService.processGetSalesByTraderInvoiceNo(salesDTO);

            //  Null Safety
            if (response == null) {
                response = new KRAGenericResponseDTO();
                response.setResponseCode(APIConstants.NULL_VALUE);
                response.setResponseMessage("No response from eTIMS!");
                response.setData(null);
            }

            return response;

        } catch (Exception ex) {

            response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while fetching eTIMS Sales Data");
            response.setData(ex.getMessage());

            return response;

        } finally {
            MDC.clear();
        }
    }

    /**
     * API to add customer in KRA, in batch
     * @param dtoList
     * @param req
     * @return
     */
    @PostMapping("/batch/addCustomerV2")
    public List<KRAGenericResponseDTO> addCustomer(@RequestBody List<ETimsCustomerDTO> dtoList, HttpServletRequest req) {

        MDC.put("type", "ETIMS_ADD_CUSTOMER");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {
            if (dtoList == null || dtoList.isEmpty()) {
                KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Customer list is required!");
                return Collections.singletonList(response);
            }


            return etimsService.processEtimsAddCustomer(dtoList);

        } catch (Exception ex) {
            KRAGenericResponseDTO response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing eTIMS customer");
            response.setData(ex.getMessage());
            return Collections.singletonList(response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * API to add plan in KRA, in batch
     * @param items
     * @param req
     * @return
     */
    @PostMapping("/batch/addItemsListV2")
    public List<KRAGenericResponseDTO> addItemsListBatch(@RequestBody List<ETimsItemDTO> items, HttpServletRequest req) {

        MDC.put("type", "ETIMS_ADD_ITEMS");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {
            if (items == null || items.isEmpty()) {
                KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Item list cannot be empty!");
                return Collections.singletonList(response);
            }

            return etimsService.processEtimsAddItemsListBatch(items);

        } catch (Exception ex) {
            KRAGenericResponseDTO response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing eTIMS Add Items");
            response.setData(ex.getMessage());
            return Collections.singletonList(response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * API to add invoice in KRA, in batch
     * @param saleList
     * @param req
     * @return
     */
    @PostMapping("/batch/addSaleV2")
    public List<KRAGenericResponseDTO> addSaleBatch(@RequestBody List<ETimsSaleDTO> saleList, HttpServletRequest req) {

        MDC.put("type", "ETIMS_ADD_SALE");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {

            if (saleList == null || saleList.isEmpty()) {
                KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Sale list is required!");
                return Collections.singletonList(response);
            }

            return etimsService.processEtimsAddSale(saleList);

        } catch (Exception ex) {

            KRAGenericResponseDTO response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing eTIMS Add Sale");
            response.setData(ex.getMessage());

            return Collections.singletonList(response);

        } finally {
            MDC.clear();
        }
    }

    /**
     * API to add Credit note in KRA, in batch
     * @param creditNoteList
     * @param req
     * @return
     */
    @PostMapping("/batch/addSaleCreditNoteV2")
    public List<KRAGenericResponseDTO> addCreditNoteBatch(@RequestBody List<ETimsCreditNoteDTO> creditNoteList, HttpServletRequest req) {

        MDC.put("type", "ETIMS_ADD_CREDIT_NOTE");
        MDC.put("requestFrom", req.getHeader("requestFrom"));

        try {
            if (creditNoteList == null || creditNoteList.isEmpty()) {
                KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setResponseMessage("Credit note list is required!");
                return Collections.singletonList(response);
            }

            return etimsService.processEtimsAddCreditNote(creditNoteList);

        } catch (Exception ex) {

            KRAGenericResponseDTO response = new KRAGenericResponseDTO();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing Credit Note");
            response.setData(ex.getMessage());

            return Collections.singletonList(response);

        } finally {
            MDC.clear();
        }
    }
}