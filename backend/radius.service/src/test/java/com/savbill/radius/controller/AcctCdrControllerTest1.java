package com.savbill.radius.controller;
import brave.Tracer;
import com.savbill.radius.dto.CDRSearchDTO;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.services.AcctCdrService;
import com.savbill.radius.services.ExcelExportService;
import com.savbill.radius.utils.RadiusConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class AcctCdrControllerTest1 {
    @InjectMocks
    AcctCdrController acctCdrController;
    @Mock
    AcctCdrService acctCdrService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Mock
    ExcelExportService excelExportService;
    @Mock
    Tracer tracer;


    @Test
    public void findAllAcctCdrsTest(){
        PaginationDTO paginationDTO=new PaginationDTO();
        AcctCdr acctCdr=getacctCdr();
        List<AcctCdr>acctCdrList=new ArrayList<>();
        acctCdrList.add(acctCdr);
        Page<AcctCdr> page =new PageImpl<>(acctCdrList);
        Tracer tracer = Mockito.mock(Tracer.class);
        Map<String, Object> response = new HashMap<>();
        response.put("acctCdr",page);
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
        Mockito.when(acctCdrService.findAllAcctCdr(1,paginationDTO,httpServletRequest)).thenReturn(page);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=acctCdrController.findAllAcctCdrs(paginationDTO,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findAcctCdrByUserNameTest(){
       CDRSearchDTO paginationDTO=new CDRSearchDTO();
       paginationDTO.setUserName("NK");
       paginationDTO.setFramedIpAddress("121212L");
        Tracer tracer = Mockito.mock(Tracer.class);
        paginationDTO.setSize(2);
        paginationDTO.setPage(1);
        AcctCdr acctCdr=getacctCdr();
        List<AcctCdr>acctCdrList=new ArrayList<>();
        acctCdrList.add(acctCdr);
        Page<AcctCdr> page =new PageImpl<>(acctCdrList);

        Map<String, Object> response = new HashMap<>();
        response.put("acctCdr",page);
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
        Mockito.when(acctCdrService.findAcctCrdUsingFilter(paginationDTO, 1)).thenReturn(page);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=acctCdrController.findAcctCdrByUserName(paginationDTO,1, httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
        @Test
        public void deleteAcctCdrTest(){
            Map<String, Object> response = new HashMap<>();
            Tracer tracer = Mockito.mock(Tracer.class);
            response.put("message","AcctCdr has been deleted successfully.");
            ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=acctCdrController.deleteAcctCdr(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        public void getCdrDetailTest(){
             AcctCdr acctCdr=getacctCdr();
            Map<String, Object> response = new HashMap<>();
           // Tracer tracer = Tracing.newBuilder().build().tracer();
//            TraceContext ctx = TraceContext.newBuilder().traceId(10L).spanId(10L).build();
//            Span span = tracer.toSpan(ctx);
//            tracer.withSpanInScope(span);
            response.put("cdrDetail",acctCdr);
            ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
            Mockito.when(acctCdrService.findAcctCdrById(1L,1)).thenReturn(acctCdr);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=acctCdrController.getCdrDetail(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
           @Test
         //  @Ignore
         public void exportExcelTest(){
               CDRSearchDTO paginationDTO=new CDRSearchDTO();
               paginationDTO.setFromDate("2018-02-17");
               paginationDTO.setToDate("2019-02-17");
               AcctCdr acctCdr=getacctCdr();
               List<AcctCdr>acctCdrList=new ArrayList<>();
//               acctCdrList.add(acctCdr);
               Page<AcctCdr> page =new PageImpl<>(acctCdrList);
               DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
               String currentDateTime = dateFormatter.format(new Date());
               String headerKey = "Content-Disposition";
               String headerValue = "attachment; filename=CDRUsers" + currentDateTime + ".xlsx";
               httpServletResponse.setContentType(headerKey);
               httpServletResponse.setContentType(headerValue);
               Map<String, Object> response = new HashMap<>();
               response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
                ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
               Mockito.when(acctCdrService.findAcctCrdUsingFilter(paginationDTO, 1)).thenReturn(page);
               Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
               ResponseEntity<Map<String, Object>>output=acctCdrController.exportExcel("NK","121212L","2018-02-17","2019-02-17",1,httpServletResponse,httpServletRequest);
               assertNotNull(output);
               assertEquals(output.getStatusCode().value(),200);

         }
    AcctCdr getacctCdr(){
        AcctCdr acctCdr=new AcctCdr();
        acctCdr.setCdrId(1L);
        Tracer tracer = Mockito.mock(Tracer.class);
        acctCdr.setAcctInputGigawords("GGGGG");
        acctCdr.setAcctInputPackets("1111");
        acctCdr.setUserName("RadiusSet");
        acctCdr.setFramedIpAddress("121212L");
        acctCdr.setUserPassword("1212");
        acctCdr.setAcctClass("First");
        acctCdr.setAcctAuthentic("valid");
        acctCdr.setAcctDelayTime("11");
        acctCdr.setAcctInputOctets("11");
        acctCdr.setAcctInterimInterval("11");
        acctCdr.setAcctLinkCount("11");
        return acctCdr;
    }






}
