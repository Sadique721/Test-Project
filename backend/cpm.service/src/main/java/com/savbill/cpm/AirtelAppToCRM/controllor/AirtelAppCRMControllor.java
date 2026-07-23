package com.savbill.cpm.AirtelAppToCRM.controllor;

import com.savbill.cpm.AirtelAppToCRM.dto.AirtelAppToCRMDTO;
import com.savbill.cpm.AirtelAppToCRM.dto.TransactionStatusDTO;
import com.savbill.cpm.AirtelAppToCRM.service.AirtelAppToCRMService;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.common.ClientService;
import com.savbill.cpm.repository.common.ClientServiceRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.spring.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class AirtelAppCRMControllor {

    private final String MODULES = "[AirtelAppCRMControllor ]";
    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    AirtelAppToCRMService customersService;
    @Autowired
    private CustomersRepository customersRepository;


    @PostMapping("/getcustomersByAccNumber")
    public List<AirtelAppToCRMDTO> getCustomersByAcc(@RequestBody AirtelAppToCRMDTO airtelAppToCRMDTO) {
//        LoggedInUser loggedInUser = getLoggedInUser();
        return customersService.getCustomersByAccountNumber(airtelAppToCRMDTO, getLoggedInUser().getMvnoId());
    }

    @PostMapping("/getCustDetailsByAcctNum")
    public List<AirtelAppToCRMDTO> getCustDetailsByAcctNum(@RequestBody AirtelAppToCRMDTO airtelAppToCRMDTO) {
//        LoggedInUser loggedInUser = getLoggedInUser();
        return customersService.getCustomersByAccountNumber(airtelAppToCRMDTO, airtelAppToCRMDTO.getMvnoId());
    }

    @PostMapping("/getCustomerByOnlyAccountNumber")
    public List<AirtelAppToCRMDTO> getCustomerByOnlyAccountNumber(@RequestBody String accountNumber) {
        return customersService.getCustomersByOnlyAccountNumber(accountNumber);
    }

    @PostMapping("/getcustomersbillFetch")
    public AirtelAppToCRMDTO getcustomersbillFetch(@RequestBody AirtelAppToCRMDTO airtelAppToCRMDTO) {
        return customersService.AirtelAppToCRMServiceBillFetch(airtelAppToCRMDTO);
    }

    @GetMapping("/getmobilenumber/{custid}")
    public String getMobilenumber(@PathVariable("custid") String id) {
        return customersService.getmobilenumber(id);
    }


    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULES + e.getStackTrace(), e);
        }
        return loggedInUser;
    }
    @GetMapping("/getcustomersByAccountNo/{accountNo}")
    public ResponseEntity<List<AirtelAppToCRMDTO>> getCustomersByAccount(@PathVariable("accountNo") String accountNo, HttpServletRequest request) {
        List<Object[]> results = customersRepository.findCustomersByAccountNumber(accountNo, getLoggedInUser().getMvnoId());
        ClientService clientService = clientServiceRepository.findByNameAndMvnoId("MOBILE_NUMBER",  getLoggedInUser().getMvnoId());
        String mobileNumber = clientService != null ? clientService.getValue() : null;
        List<AirtelAppToCRMDTO> airtelAppToCRMDTOS = results.stream()
                .map(obj -> {
                    return new AirtelAppToCRMDTO(
                            (String) obj[0], // customerMsisdn
                            (String) obj[1], // username
                            (String) obj[2], // password
                            (String) obj[3], // accountNo
                            String.valueOf(obj[4]), // walletBal
                            (String) obj[5], // firstName
                            (String) obj[6], // lastName
                            (String) obj[7], // status
                            obj[8] != null ? Integer.parseInt(String.valueOf(obj[8])) : 0,  // custId
                            obj[9] != null ? Integer.parseInt(String.valueOf(obj[9])) : 0,  // mvnoId
                            obj[10] != null ? Integer.parseInt(String.valueOf(obj[10])) : 0, // buId
                            mobileNumber , // Now setting mobile number dynamically
                            (String) obj[11] //custtype
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(airtelAppToCRMDTOS);
    }

    @GetMapping("/getCustomerByOnlyAccountNumber/{accountNumber}")
    public ResponseEntity<List<AirtelAppToCRMDTO>> getCustomersByOnlyAccountNumber(@PathVariable("accountNumber") String accountNumber, HttpServletRequest request) {
        List<Object[]> results = customersRepository.findCustomersByOnlyAccountNumber(accountNumber);
        List<AirtelAppToCRMDTO> airtelAppToCRMDTOS = results.stream()
                .map(obj -> {
                    ClientService clientService = obj[9] != null ? clientServiceRepository
                            .findByNameAndMvnoId("MOBILE_NUMBER", Integer.parseInt(String.valueOf(obj[9])))
                            : null;
                    String mobileNumber = clientService != null ? clientService.getValue() : null;
                    return new AirtelAppToCRMDTO(
                            (String) obj[0], // customerMsisdn
                            (String) obj[1], // username
                            (String) obj[2], // password
                            (String) obj[3], // accountNo
                            String.valueOf(obj[4]), // walletBal
                            (String) obj[5], // firstName
                            (String) obj[6], // lastName
                            (String) obj[7], // status
                            obj[8] != null ? Integer.parseInt(String.valueOf(obj[8])) : 0,  // custId
                            obj[9] != null ? Integer.parseInt(String.valueOf(obj[9])) : 0,  // mvnoId
                            obj[10] != null ? Integer.parseInt(String.valueOf(obj[10])) : 0, // buId
                            mobileNumber,
                            (String) obj[11] //custtype
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(airtelAppToCRMDTOS);
    }

@GetMapping("/CustomerByID/{custId}")
public ResponseEntity<List<TransactionStatusDTO>> getCustomersByID(@PathVariable("custId") String custId, HttpServletRequest request) {
    List<Object[]> results = customersRepository.findCustomersById(Integer.valueOf(custId));
    List<TransactionStatusDTO> transactionstatusDTOList = results.stream()
            .map(obj -> new TransactionStatusDTO(
                    (String) obj[0], // accountNo
                    (String) obj[1], // mobile
                    (String) obj[2], // Name
                    (String) obj[3] // email
            ))
            .collect(Collectors.toList());
          return ResponseEntity.status(HttpStatus.OK).body(transactionstatusDTOList);
    }
}
