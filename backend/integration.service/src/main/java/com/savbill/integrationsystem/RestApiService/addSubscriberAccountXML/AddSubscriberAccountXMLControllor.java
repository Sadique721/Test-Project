package com.savbill.integrationsystem.RestApiService.addSubscriberAccountXML;

import com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML.AddSubscriberAccountXMLSoapService;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.soap.SOAPException;
import java.io.IOException;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class AddSubscriberAccountXMLControllor {
    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ChangeServService changeServService;
    @Value("${defaultplan}")
    String plan;

    @Value("${servicearea.name}")
    Long serviceArea;
    @Autowired
    private AddSubscriberAccountXMLSoapService addSubscriberAccountXMLSoapService;

    @PostMapping("/addSubscriberAccountXML")
    public GenericDataDTO addSubscriberAccountXML(@RequestBody AddSubscriberAccountXMLDTO request) {
        try {
            return addSubscriberAccountXMLSoapService.addSubscriberAccount(request);
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
