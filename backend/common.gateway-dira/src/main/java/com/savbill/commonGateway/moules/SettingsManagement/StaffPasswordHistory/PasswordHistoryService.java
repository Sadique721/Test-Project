package com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory;

import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.exceptions.AlreadyExistException;
import com.savbill.commonGateway.exceptions.CustomMessageException;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordPolicy;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Configuration
@EnableScheduling
@Service
public class PasswordHistoryService extends AbstractService<PasswordHistory, PasswordHistoryDTO, Long> {

    public static final String MODULE = "[PasswordHistoryService]";

    @Autowired
    PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    PasswordRepository passwordRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    @Value("${scheduler.cron.password-expiration}")
    private String passwordExpirationCron;

    @Override
    protected JpaRepository<PasswordHistory, Long> getRepository() {
        return passwordHistoryRepository;
    }

    /**
     * Save password history
     *
     * @param passwordHistoryDTO the password history data
     * @return PasswordHistory the saved password history entity
     */

    public PasswordHistory GeneratePassword(PasswordHistoryDTO passwordHistoryDTO) throws AlreadyExistException, CustomMessageException {
        StaffUser staffUser = staffUserRepository.findByUuid(passwordHistoryDTO.getUuid())
                .orElseThrow(() -> new CustomMessageException("Staff user not found"));

        String password = staffUser.getPassword();
        if (password != null) {
            throw new CustomMessageException("The password has already been generated.");
        } else {
            // Get the staffId from StaffUser
            Integer staffId = staffUser.getId();

            // Fetch the password_policy_id from tblm_mvno using the mvnoId
            Long passwordPolicyId = mvnoRepository.findPasswordPolicyIdByMvnoId(Long.valueOf(staffUser.getMvnoId()))
                    .orElseThrow(() -> new CustomMessageException("Password policy not found for mvnoId: " + staffUser.getMvnoId()));

            // Fetch the PasswordPolicy from tblm_password_policy
            PasswordPolicy passwordPolicy = passwordRepository.findById(passwordPolicyId)
                    .orElseThrow(() -> new CustomMessageException("Password policy not found for id: " + passwordPolicyId));

            // Password length validation
            if (passwordHistoryDTO.getPassword().length() < passwordPolicy.getMin_length()) {
                throw new CustomMessageException("Password is too short. Minimum length is " + passwordPolicy.getMin_length());
            }

            if (passwordHistoryDTO.getPassword().length() > passwordPolicy.getMax_length()) {
                throw new CustomMessageException("Password is too long. Maximum length is " + passwordPolicy.getMax_length());
            }

            String passwordPattern = passwordPolicy.getPattern();
            if (passwordPattern != null && !passwordPattern.isEmpty()) {
                try {
                    // Compile the pattern to check for validity
                    Pattern compiledPattern = Pattern.compile(passwordPattern);
                    // If the password doesn't match the valid pattern
                    if (!compiledPattern.matcher(passwordHistoryDTO.getPassword()).matches()) {
                        // Generate user-friendly message
                        throw new CustomMessageException(passwordPolicy.getPattern_description());
                    }
                } catch (PatternSyntaxException e) {
                    // Handle invalid regex patterns gracefully
                    throw new CustomMessageException("Password policy is incorrect. Please contact support.");
                }
            }

            if (passwordPolicy.getDisable_recycling_prevention() != null && passwordPolicy.getDisable_recycling_prevention() > 0) {
                PasswordEncoder encoder = new BCryptPasswordEncoder();

                // Fetch recent passwords for this staff user based on the disable_recycling_prevention value
                int recentPasswordCount = passwordPolicy.getDisable_recycling_prevention().intValue();
                List<PasswordHistory> recentPasswords = passwordHistoryRepository.findByStaffIdOrderByPasswordAttemptNumberDesc(
                        staffId, PageRequest.of(0, recentPasswordCount));

                // Check if the new password matches any of the last N passwords
                for (PasswordHistory history : recentPasswords) {
                    if (encoder.matches(passwordHistoryDTO.getPassword(), history.getPassword())) {
                        throw new AlreadyExistException("The password matches one of the last " + recentPasswordCount + " passwords");
                    }
                }
            }

            // Initialize password encoder
            PasswordEncoder encoder = new BCryptPasswordEncoder();

            // Retrieve the maximum password attempt number for the staff user
            Long maxAttemptNumber = passwordHistoryRepository.findMaxPasswordAttemptNumberByStaffId(staffId);

            // Auto-increment the password attempt number
            Long newAttemptNumber = (maxAttemptNumber != null) ? maxAttemptNumber + 1 : 1L;

            // Now encode and save the password for the staff user
            staffUser.setPassword(encoder.encode(passwordHistoryDTO.getPassword()));
            staffUser.setPasswordDate(LocalDateTime.now());
            staffUserRepository.save(staffUser);

            // Map PasswordHistoryDTO to PasswordHistory entity and save password history
            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setId(passwordHistoryDTO.getId());
            passwordHistory.setPasswordAttemptNumber(newAttemptNumber);
            passwordHistory.setPassword(staffUser.getPassword());
            passwordHistory.setStaffId(staffId);
            passwordHistory.setUuid(passwordHistoryDTO.getUuid());

//        // Clear the UUID in staffUser after saving the password history
//        staffUser.setUuid(null);
//        staffUserRepository.save(staffUser);

            return passwordHistoryRepository.save(passwordHistory);
        }
    }


    /**
     * Scheduler to check expiration days every day
     */
    @Scheduled(cron = "${scheduler.cron.password-expiration}")
    public void checkPasswordExpiration() {
        ApplicationLogger.logger.info("Scheduler running with cron expression for Password Expire: " + passwordExpirationCron);
        // Fetch all staff users with passwords
        List<StaffUser> allStaffUsers = staffUserRepository.findAll();

        for (StaffUser staffUser : allStaffUsers) {
            // Fetch the PasswordPolicy using the mvnoId from the staff user
            Long mvnoId = Long.valueOf(staffUser.getMvnoId());
            if (staffUser.getUsername()!= "superadmin" && staffUser.getId()!= 1) {
                Long passwordPolicyId = mvnoRepository.findPasswordPolicyIdByMvnoId(mvnoId)
                        .orElse(null); // Handle possible null value

                if (passwordPolicyId != null) {
                    PasswordPolicy passwordPolicy = passwordRepository.findById(passwordPolicyId)
                            .orElse(null); // Handle possible null value

                    if (passwordPolicy != null) {
                        Long expirationDays = passwordPolicy.getExpiration_days();

                        // Check the difference between password date and current date
                        LocalDateTime passwordDate = staffUser.getPasswordDate();
                        if (passwordDate != null) {
                            LocalDateTime now = LocalDateTime.now();
                            long daysSincePasswordChange = java.time.Duration.between(passwordDate, now).toDays();

                            // Check if the password has expired
                            if (daysSincePasswordChange >= expirationDays) {
                                staffUser.setIsPasswordExpired(true);
                                staffUser.setPasswordDate(LocalDateTime.now());
//                            staffUser.setStatus("INACTIVE");
                                staffUserRepository.save(staffUser);
                                // Handle expiration logic (e.g., notify the user or enforce password ch`ange)
                                ApplicationLogger.logger.info("Password for staff user " + staffUser.getFullName() + " has expired.");
                            }
                        }
                    }
                }
            }
        }
    }

}
