package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.DeviceDriver;
import com.savbill.radius.helper.DeviceDriverDTO;
import com.savbill.radius.repository.CoaDMProfileRepository;
import com.savbill.radius.repository.DeviceDriverRepository;
import com.savbill.radius.services.*;
import com.savbill.radius.services.*;
import com.savbill.radius.spring.SpringContext;
import com.savbill.radius.utils.CommonConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeviceDriverServiceImpl implements DeviceDriverService {

    @Autowired
    private DeviceDriverRepository deviceDriverRepository;

    @Autowired
    private CoaDMProfileRepository coaDMProfileRepository;

    @Autowired
    private CoaDMProfileServiceImpl coaDMProfileService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LiveUserService liveUserService;

    @Autowired
    private LiveUserService liverUserService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CoaDMProfileAttributeService coaDMProfileAttributeService;

    DirContext connection;

    private static final Logger log = LoggerFactory.getLogger(DeviceDriverServiceImpl.class);

    @Override
    public List<DeviceDriver> findAll(Integer mvnoId) {
        try {
            QDeviceDriver qDeviceDriver = QDeviceDriver.deviceDriver;
            BooleanExpression exp = qDeviceDriver.isNotNull();
            exp = exp.and(qDeviceDriver.isDelete.eq(false));
            if (mvnoId != null && mvnoId == 1)
                return deviceDriverRepository.findAll();
            else {
                exp = exp.and(qDeviceDriver.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                return (List<DeviceDriver>) deviceDriverRepository.findAll(exp);
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DeviceDriver findById(Long id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid device driver id.");
            } else {
                QDeviceDriver qDeviceDriver = QDeviceDriver.deviceDriver;
                BooleanExpression boolExp = qDeviceDriver.isNotNull();
                boolExp = boolExp.and(qDeviceDriver.deviceDriverId.eq(id));
                if (mvnoId == null || mvnoId != 1)
                    boolExp = boolExp.and(qDeviceDriver.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1).and(qDeviceDriver.deviceDriverId.eq(id)));
                Optional<DeviceDriver> deviceDriverOptional = deviceDriverRepository.findOne(boolExp);

                if (deviceDriverOptional.isPresent()) {
                    return deviceDriverOptional.get();
                } else {
                    throw new IllegalArgumentException(
                            "No record found with device id : '" + id + "'. Please enter valid device id");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }





    @Override
    public DeviceDriver add(DeviceDriverDTO deviceDriverDTO, Integer mvnoId) {
        try {
            DeviceDriver deviceDriver = validateDeviceDriverData(deviceDriverDTO, mvnoId, false);
            deviceDriver.setCreatedBy(CommonConstants.USER_ADMIN);
            deviceDriver.setCreatedOn(LocalDateTime.now());
            return deviceDriverRepository.save(deviceDriver);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DeviceDriver update(DeviceDriverDTO deviceDriverDTO, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        try {
            Optional<DeviceDriver> optional = deviceDriverRepository.findById(deviceDriverDTO.getDeviceDriverId());
            if(deviceDriverDTO.getUserName() != null && deviceDriverDTO.getUserName() != ""){
                optional.get().setUserName(deviceDriverDTO.getUserName());
            }
            if(deviceDriverDTO.getName() != null && deviceDriverDTO.getName() != ""){
                optional.get().setName(deviceDriverDTO.getName());
            }
            if(deviceDriverDTO.getAddress() != null && deviceDriverDTO.getAddress() != ""){
                optional.get().setAddress(deviceDriverDTO.getAddress());
            }
            if(deviceDriverDTO.getPassword() != null && deviceDriverDTO.getPassword() != ""){
                optional.get().setPassword(deviceDriverDTO.getPassword());
            }
            if(deviceDriverDTO.getUserDn() != null && deviceDriverDTO.getUserDn() != ""){
                optional.get().setUserDn(deviceDriverDTO.getUserDn());
            }
            if(deviceDriverDTO.getUserNameAttribute() != null && deviceDriverDTO.getUserNameAttribute() != ""){
                optional.get().setUserNameAttribute(deviceDriverDTO.getUserNameAttribute());
            }
            if(deviceDriverDTO.getPasswordAttribute() != null && deviceDriverDTO.getPasswordAttribute() != ""){
                optional.get().setPasswordAttribute(deviceDriverDTO.getPasswordAttribute());
            }
            optional.get().setMvnoId(mvnoId);
//            log.info("Device Driver updated succefully, updated values");
            return deviceDriverRepository.save(optional.get());
        } catch (Throwable e) {
//            log.error("Error while updating device driver: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public void delete(Long deviceDriverId, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(deviceDriverId)) {
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_STRING_MSG + "Please enter valid device profile name");
            } else {
                QDeviceDriver qDeviceDriver = QDeviceDriver.deviceDriver;
                BooleanExpression boolExp = qDeviceDriver.isNotNull();
                if (mvnoId == null || mvnoId != 1)
                    boolExp = boolExp.and(qDeviceDriver.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)).and(qDeviceDriver.deviceDriverId.eq(deviceDriverId)));
                boolExp = boolExp.and(qDeviceDriver.deviceDriverId.eq(deviceDriverId));
                Optional<DeviceDriver> deviceOptional = deviceDriverRepository.findOne(boolExp);
                if (deviceOptional.isPresent()) {
                    deviceOptional.get().setIsDelete(true);
                    deviceDriverRepository.save(deviceOptional.get());
                   // log.info("Device Driver deleted successfully: " + deviceOptional.get().getUserName());
                } else {
                    throw new IllegalArgumentException("You do not have access to update or delete this record.");
                }
            }
        } catch (Throwable e) {
            log.error("Error while deleting device: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    private DeviceDriver validateDeviceDriverData(DeviceDriverDTO deviceDriverDTO, Integer mvnoId, boolean isUpdate) {
        try {
            DeviceDriver deviceDriver = new DeviceDriver(deviceDriverDTO);
            if (mvnoId == null || mvnoId != 1)
                deviceDriver.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            else
                deviceDriver.setMvnoId(mvnoId);
            return deviceDriver;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void newLdapConnection(){
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY , "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL , "ldap://localhost:10389");
        env.put(Context.SECURITY_PRINCIPAL , "uid=admin, ou=system");
        env.put(Context.SECURITY_CREDENTIALS,"secret");
        try{
            connection = new InitialDirContext(env);
            //		System.out.println("Hello World! " +connection);
        }
        catch (AuthenticationException ex){
            //		System.out.println(ex.getMessage());
        }
        catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean authUser(String username , String password , String address){
        try{
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY , "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL , address);
            env.put(Context.SECURITY_PRINCIPAL , username);
            env.put(Context.SECURITY_CREDENTIALS,password);
            DirContext conn = new InitialDirContext(env);
            conn.close();
           // log.info("Succefully connected to AD with "+address+" and username "+username+" and password "+password);
            return true;
        }
        catch(Exception e){
        //    log.error("cant connect with +"+address+"with error "+e.getMessage());
          //		System.out.println(e.getMessage());
          return false;
        }
    }

    @Override
    public List<DeviceDriver> getDeviceDriverByName(String deviceDriverName , Integer mvnoId){
        DeviceDriverRepository deviceDriverRepository1 = SpringContext.getBean(DeviceDriverRepository.class);
        QDeviceDriver qDeviceDriver = QDeviceDriver.deviceDriver;
        BooleanExpression exp = qDeviceDriver.isNotNull();
        exp = exp.and(qDeviceDriver.name.equalsIgnoreCase(deviceDriverName));
        exp = exp.and(qDeviceDriver.isDelete.eq(false));
        if(mvnoId != 1){
            exp = exp.and(qDeviceDriver.mvnoId.eq(mvnoId));
        }
        return (List<DeviceDriver>)deviceDriverRepository1.findAll(exp);
    }

    @Override
    public Boolean verifyAuthUser(String name , Integer mvnoId){
        try{
            List<DeviceDriver> deviceDriverList = getDeviceDriverByName(name,mvnoId);
            if(!deviceDriverList.isEmpty()) {
                DeviceDriver deviceDriver = deviceDriverList.get(0);
                Properties env = new Properties();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, deviceDriver.getAddress());
                env.put(Context.SECURITY_PRINCIPAL, deviceDriver.getUserName());
                env.put(Context.SECURITY_CREDENTIALS, deviceDriver.getPassword());
                DirContext conn = new InitialDirContext(env);
                conn.close();
            //    log.info("Device Driver authenticate with user "+deviceDriver.getUserName());
                return true;
            }
            else{
            //    log.info("Device driver not found with name: "+name);
                return false;
            }
        }
        catch(Exception e){
         //   log.info("Device driver not connect with AD server with given username");
          //  log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public CustomerData isUserExist(String configurationName , String username , String password , Integer mvnoId) {
        CustomerData customerData = new CustomerData();
        try {
            List<DeviceDriver> deviceDriverList = getDeviceDriverByName(configurationName , mvnoId);
            if(!deviceDriverList.isEmpty()) {
                DeviceDriver deviceDriver = deviceDriverList.get(0);
                Properties env = new Properties();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, deviceDriver.getAddress());
                env.put(Context.SECURITY_PRINCIPAL, deviceDriver.getUserName());
                env.put(Context.SECURITY_CREDENTIALS, deviceDriver.getPassword());
                DirContext conn = new InitialDirContext(env);
                String baseDN = deviceDriver.getUserDn();

                // Create the search controls
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                // Define the search filter
                String filter = "(&(objectClass=*)("+deviceDriver.getUserNameAttribute()+"=" + username + ")("+deviceDriver.getPasswordAttribute()+"=" + password + "))";

                // Perform the LDAP search
                NamingEnumeration<SearchResult> results = conn.search(baseDN, filter, searchControls);
                HashMap<String , String> objectList = new HashMap<>();
                // Process the search results
                while (results.hasMore()) {
                    SearchResult result = results.next();
                    //		System.out.println("DN: " + result.getName());
                    Attributes attrs = result.getAttributes();
                    if (attrs != null) {
                        NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll();
                        while (attrEnum.hasMore()) {
                            Attribute attr = attrEnum.next();
                            objectList.put(attr.getID() , attr.get().toString());
                            //		System.out.println(attr.getID() + ": " + attr.get());
                        }
                    }
                }
                //		System.out.println(objectList);

                // Close the context when done
                conn.close();
                if(!objectList.isEmpty()){
                    customerData.setUsername(username);
                    customerData.setPassword(password);
                    customerData.setStatus("Active");
                    customerData.setFailcount(0);
                    customerData.setMvnoId(mvnoId);
                    customerData.setAuthStatus(true);
                    return customerData;
                }
                else{
                    return customerData;
                }
            }
            else{
                return  customerData;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
          //  log.error("Error while fetch data from LDAP for username: "+username+" configurationName: "+configurationName);
        }
        return customerData;

    }

    @Override
    public Boolean validateByName(String name , Integer mvnoId){
        Boolean flag = false;
        List<DeviceDriver> deviceDriverList = getDeviceDriverByName(name , mvnoId);
        if(!deviceDriverList.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }
}
