package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;

import com.savbill.commonGateway.exceptions.AccountLockedException;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordPolicy;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordRepository;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    PasswordRepository passwordRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    // Use ConcurrentHashMap to track login attempts
    private final ConcurrentHashMap<String, Integer> loginAttempts = new ConcurrentHashMap<>();

    public void handleLoginAttempt(String username, HttpServletResponse response) throws IOException, AccountLockedException,UsernameNotFoundException {

        List<StaffUser> staffUserList = staffUserRepository.findByUsername(username);

        if (staffUserList != null && !staffUserList.isEmpty()) {
            StaffUser staffUser = staffUserList.get(0); // Handle the first user in the list

            if(staffUser.getPassword() == null){
                throw new UsernameNotFoundException("Password is not Generated , Please Generate password.");
            }
            if ("INACTIVE".equals(staffUser.getStatus())) {
                throw new AccountLockedException("Account is locked due to too many failed login attempts, Please Contact administrator");
            }

            // Fetch the password policy for the user
            Long passwordPolicyId = mvnoRepository.findPasswordPolicyIdByMvnoId(Long.valueOf(staffUser.getMvnoId())).get();

            // Fetch the PasswordPolicy from tblm_password_policy
            PasswordPolicy passwordPolicy = passwordRepository.findById(passwordPolicyId).get();

            // Get the account lockout threshold from the password policy
            Long maxAttempts = passwordPolicy.getDisable_account_lockout();

            // Track failed login attempts
            int currentAttempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, currentAttempts);

            // If attempts exceed the threshold, lock the user account
            if (maxAttempts != null && currentAttempts >= maxAttempts && staffUser.getId() != 1) {
                staffUser.setStatus("INACTIVE");
                staffUserRepository.save(staffUser);
                MDC.clear();
                loginAttempts.remove(username); // Reset the login attempts after account is locked
                throw new AccountLockedException("Account is locked due to too many failed login attempts");
            }
        } else {
            throw new UsernameNotFoundException("User not Found.");
        }
    }
}

