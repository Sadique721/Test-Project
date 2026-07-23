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
 * import com.savbill.notification.entity.Event; import
 * com.savbill.notification.entity.Sms; import
 * com.savbill.notification.helper.SmsDto; import
 * com.savbill.notification.repository.SmsRepository;
 * 
 * @RunWith(SpringJUnit4ClassRunner.class)
 * 
 * @SpringBootTest public class SmsServiceTest {
 * 
 * @MockBean SmsRepository smsRepository;
 * 
 * @Autowired SmsService smsService;
 * 
 * @Test public void testFindSmsById() { Sms sms = getSms(); Optional<Sms>
 * optSms = Optional.of(sms);
 * Mockito.when(smsRepository.findById(sms.getSmsId())).thenReturn(optSms);
 * assertThat(smsService.findSmsById(sms.getSmsId())).isEqualTo(sms); } //@Test
 * // public void testFindBySourceName() // { // Sms sms = getSms(); //
 * List<Sms> smsList = new ArrayList<>(); // smsList.add(sms); //
 * Mockito.when(smsRepository.findSmsBySourceName(sms.getSourceName())).
 * thenReturn(smsList); //
 * assertThat(smsService.findSmsBySourceName(sms.getSourceName())).isEqualTo(
 * smsList); // }
 * 
 * @Test public void testFindAllSms() { Sms sms = getSms(); List<Sms> smsList =
 * new ArrayList<>(); smsList.add(sms);
 * 
 * Mockito.when(smsRepository.findAll()).thenReturn(smsList);
 * assertThat(smsService.findAllSmss()).isEqualTo(smsList); }
 * 
 * @Test public void testSaveEmail() { Sms smsVo = getSms(); SmsDto smsDto =
 * getSmsDto();
 * Mockito.when(smsRepository.save(any(Sms.class))).thenReturn(smsVo);
 * assertThat(smsService.saveSms(smsDto)).isEqualTo(smsVo); }
 * 
 * @Test public void testDeleteEmail() { Sms sms = getSms(); Optional<Sms>
 * smsOpt = Optional.of(sms);
 * Mockito.when(smsRepository.findById(3L)).thenReturn(smsOpt);
 * Mockito.when(smsRepository.existsById(sms.getSmsId())).thenReturn(false);
 * assertFalse(smsRepository.existsById(sms.getSmsId())); } private SmsDto
 * getSmsDto() { SmsDto smsDto = new SmsDto(); smsDto.setMobileNo("9106593444");
 * smsDto.setCountryCode("+91"); smsDto.setMessage("Testing Email");
 * smsDto.setSourceName("Savbill Notification"); smsDto.setEventId(3L); return
 * smsDto; } private Event getEvent() { Event eventVo = new Event();
 * eventVo.setEventId(3L); eventVo.setEventName("Login Success");
 * eventVo.setEventType("Schedule"); eventVo.setDescription("Login Success");
 * eventVo.setStatus("Active"); return eventVo; } private Sms getSms() { Sms
 * smsVo = new Sms(); smsVo.setSmsId(3L); smsVo.setMobileNo("9106593444");
 * smsVo.setCountryCode("+91"); smsVo.setMessage("Testing Email");
 * smsVo.setCreatedOn(new Timestamp(new Date().getTime()));
 * smsVo.setSourceName("Savbill Notification"); smsVo.setSmsConfigId(1L);
 * smsVo.setEvent(getEvent()); return smsVo; } }
 */