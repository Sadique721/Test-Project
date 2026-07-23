/*
 * package com.savbill.notification.services;
 * 
 * import static org.assertj.core.api.Assertions.assertThat; import static
 * org.junit.Assert.assertFalse; import static org.mockito.ArgumentMatchers.any;
 * 
 * import java.sql.Timestamp; import java.util.ArrayList; import java.util.Date;
 * import java.util.List; import java.util.Optional;
 * 
 * import org.junit.Test; import org.junit.runner.RunWith; import
 * org.mockito.Mockito; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.boot.test.context.SpringBootTest; import
 * org.springframework.boot.test.mock.mockito.MockBean; import
 * org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 * 
 * import com.savbill.notification.entity.Email; import
 * com.savbill.notification.entity.Event; import
 * com.savbill.notification.helper.EmailDto; import
 * com.savbill.notification.repository.EmailRepository;
 * 
 * @RunWith(SpringJUnit4ClassRunner.class)
 * 
 * @SpringBootTest public class EmailServiceTest {
 * 
 * @MockBean EmailRepository emailRepository;
 * 
 * @Autowired EmailService emailService;
 * 
 * @Test public void testFindEmailById() { Email email = getEmail();
 * Optional<Email> optEmail = Optional.of(email);
 * Mockito.when(emailRepository.findById(email.getEmailId())).thenReturn(
 * optEmail);
 * assertThat(emailService.findEmailById(email.getEmailId())).isEqualTo(email);
 * }
 * 
 * //@Test // public void testFindBySourceName() // { // Email email =
 * getEmail(); // List<Email> emailList = new ArrayList<>(); //
 * emailList.add(email); //
 * Mockito.when(emailRepository.findEmailBySourceName(email.getSourceName())).
 * thenReturn(emailList); //
 * assertThat(emailService.findEmailBySourceName(email.getSourceName())).
 * isEqualTo(emailList); // }
 * 
 * @Test public void testFindAllEmail() { Email email = getEmail(); List<Email>
 * emailList = new ArrayList<>(); emailList.add(email);
 * 
 * Mockito.when(emailRepository.findAll()).thenReturn(emailList);
 * assertThat(emailService.findAllEmails()).isEqualTo(emailList); }
 * 
 * @Test public void testSaveEmail() { Email emailVo = getEmail(); EmailDto
 * emailDto = getEmailDto();
 * Mockito.when(emailRepository.save(any(Email.class))).thenReturn(emailVo);
 * assertThat(emailService.saveEmail(emailDto)).isEqualTo(emailVo); }
 * 
 * @Test public void testDeleteEmail() { Email email = getEmail();
 * Optional<Email> emailOpt = Optional.of(email);
 * Mockito.when(emailRepository.findById(3L)).thenReturn(emailOpt);
 * Mockito.when(emailRepository.existsById(email.getEmailId())).thenReturn(false
 * ); assertFalse(emailRepository.existsById(email.getEmailId())); } private
 * EmailDto getEmailDto() { EmailDto emailDto = new EmailDto();
 * emailDto.setEmailAddress("manalisoni@bhartiinfosoft.com");
 * emailDto.setMessage("Testing Email");
 * emailDto.setSourceName("Savbill Notification"); emailDto.setEventId(3L); return
 * emailDto; } private Event getEvent() { Event eventVo = new Event();
 * eventVo.setEventId(3L); eventVo.setEventName("Login Success");
 * eventVo.setEventType("Schedule"); eventVo.setDescription("Login Success");
 * eventVo.setStatus("Active"); return eventVo; } private Email getEmail() {
 * Email emailVo = new Email(); emailVo.setEmailId(3L);
 * emailVo.setEmailAddress("manalisoni@bhartiinfosoft.com");
 * emailVo.setMessage("Testing Email"); emailVo.setCreatedOn(new Timestamp(new
 * Date().getTime())); emailVo.setSourceName("Savbill Notification");
 * emailVo.setEmailConfigId(1L); emailVo.setEvent(getEvent()); return emailVo; }
 * }
 */