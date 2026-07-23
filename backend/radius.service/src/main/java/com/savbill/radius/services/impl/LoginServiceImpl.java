package com.savbill.radius.services.impl;

import com.savbill.radius.MyUserDetail;
import com.savbill.radius.entity.Customer;
import com.savbill.radius.entity.Staff;
import com.savbill.radius.entity.Template;
import com.savbill.radius.helper.LoginDto;
import com.savbill.radius.jwt.JwtUtil;
import com.savbill.radius.kafka.CustomerMessage;
import com.savbill.radius.kafka.KafkaMessageData;
import com.savbill.radius.kafka.KafkaMessageSender;
import com.savbill.radius.repository.CustomerRepository;
import com.savbill.radius.repository.StaffRepository;
import com.savbill.radius.repository.TemplateRepository;
import com.savbill.radius.services.LoginService;
import com.savbill.radius.utils.PasswordGenerator;
import com.savbill.radius.utils.RadiusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl extends PasswordGenerator implements LoginService {

    private static final String BAD_CREDENTIALS = "Bad Credentials";
    private static final String LOGIN_SUCCESS = "Login Success";
    private static final String LOGIN_FAILURE = "Login Failure";

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

//    @Autowired
//    MessageReceiver messageReceiver;
//
//    @Autowired
//    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    CustomerRepository customerRepository;
    
    public static Long mvno=null;
    
    @Override
    public String login(LoginDto loginData) {

        try {

            if (loginData.getUserName() == null || loginData.getUserName().isEmpty()
                    || loginData.getUserName().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_STRING_MSG + "User name is mandatory.Please enter valid user name.");
            } else if (loginData.getPassword() == null || loginData.getPassword().isEmpty()
                    || loginData.getPassword().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_STRING_MSG + "Password is mandatory.Please enter valid password.");
            } else {

//    		this.authenticationManager.authenticate(
//    			new UsernamePasswordAuthenticationToken(loginData.getUserName(), loginData.getPassword()));
                UserDetails userDetails = loadUserByUsername(loginData.getUserName());
                //sendLoginSuccessMessage(loginData.getUserName(), loginData.getPassword());
                String token = this.jwtUtil.generateToken(userDetails);
                return token;
            }
        }catch (UsernameNotFoundException e) {
            // sendLoginFailureMessage(loginData.getUserName(), loginData.getPassword());
            throw new RuntimeException(BAD_CREDENTIALS);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
        }
    private void sendLoginSuccessMessage(String userName, String password) 
    {
        try {
            Optional<Template> optionalTemplate = templateRepository.findByTemplateName(LOGIN_SUCCESS);
            if(optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                Optional<Customer> optionalCustomer = customerRepository.findByUserName(userName);
                // Set message in queue to send notification after successful login.
                CustomerMessage customerMessage = new CustomerMessage(optionalCustomer.get(),password,LOGIN_SUCCESS,optionalTemplate.get().getEmailTemplateData(),optionalTemplate.get().getSmsTemplateData(),optionalTemplate.get().getAppendUrl());
                //LoginMessage loginMessage = new LoginMessage(optionalStaff.get(), LOGIN_SUCCESS, password,
                //RabbitMqConstants.SOURCE_NAME_SAVBILL_WIFI);

                //messageSender.send(customerMessage, RabbitMqConstants.QUEUE_LOGIN_SUCCESS_RADIUS);
                kafkaMessageSender.send(new KafkaMessageData(customerMessage,customerMessage.getClass().getSimpleName(),"LOGIN_SUCCESS_RADIUS"));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendLoginFailureMessage(String userName, String password) 
    {
        try {
            Optional<Template> optionalTemplate = templateRepository.findByTemplateName(LOGIN_FAILURE);
            if(optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                Optional<Customer> optionalCustomer = customerRepository.findByUserName(userName);
                if (optionalCustomer.isPresent()) {
                    //Set message in queue to send notification after login failure.
                    //LoginMessage loginMessage = new LoginMessage(optionalStaff.get(), LOGIN_FAILURE, password, RabbitMqConstants.SOURCE_NAME_SAVBILL_COMMON);
                    //CustomerMessage customerMessage = new CustomerMessage(optionalCustomer.get(),password,LOGIN_FAILURE,optionalTemplate.get().getEmailTemplateData(),optionalTemplate.get().getSmsTemplateData(),optionalTemplate.get().getAppendUrl());

                    //messageSender.send(customerMessage, RabbitMqConstants.QUEUE_LOGIN_FAILURE_RADIUS);
                    kafkaMessageSender.send(new KafkaMessageData(optionalCustomer,optionalCustomer.getClass().getSimpleName(),"LOGIN_FAILURE"));
                }}
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public UserDetails loadUserByUsername(String userName) {

        try {

            Optional<Staff> userOptional = staffRepository.findByUserName(userName);
            userOptional.orElseThrow(() -> new UsernameNotFoundException(BAD_CREDENTIALS));
            mvno = userOptional.get().getMvnoId();
            return userOptional.map(users -> new MyUserDetail(users)).get();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
