package com.savbill.radius.controller;

import com.savbill.radius.entity.Template;
import com.savbill.radius.helper.TemplateDto;
import com.savbill.radius.services.TemplateService;
import com.savbill.radius.utils.RadiusConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class TemplateControllerTest {
    @InjectMocks
    TemplateController templateController;
    @Mock
    TemplateService templateService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
        @Test
        public void findAllTest(){
            Template template=gettemplate();
            List<Template>  templateList=new ArrayList<>();
             templateList.add(template );
            Map<String, Object> response = new HashMap<>();
            response.put("templateList",templateList);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(templateService.findAll(1)).thenReturn(templateList);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=templateController.findAll(1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        public void saveTemplateTest(){
            Template template=gettemplate();
            TemplateDto templateDto=new TemplateDto();
            templateDto.setTemplateName("Radius");
            templateDto.setEventId(1L);
            templateDto.setSmsEventConfigured(true);
             Map<String, Object> response = new HashMap<>();
            response.put("template",template);
            response.put(RadiusConstants.MESSAGE, "Template has been added successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(templateService.saveTemplate(templateDto,1)).thenReturn(template);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=templateController.saveTemplate(templateDto,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        public void updateTemplateTest(){
            Template template=gettemplate();
            TemplateDto templateDto=new TemplateDto();
            templateDto.setTemplateName("Radius");
            templateDto.setEventId(1L);
            templateDto.setSmsEventConfigured(true);
            List<TemplateDto> templateDtos=new ArrayList<>();
            templateDtos.add(templateDto);
            Map<String, Object> response = new HashMap<>();
            response.put("template",templateDtos);
            response.put(RadiusConstants.MESSAGE, "Template has been updated successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(templateService.udpateTemplate(templateDtos,1,httpServletRequest)).thenReturn(templateDtos);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=templateController.updateTemplate(templateDtos,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);


        }
        @Test
        public void deleteTest(){

            Map<String, Object> response = new HashMap<>();
            response.put(RadiusConstants.MESSAGE, "Template has been deleted successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
             Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=templateController.deleteTemplate(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);

        }
        Template gettemplate(){
            Template template=new Template();
            template.setTemplateId(1L);
            template.setTemplateName("ADPT");
            template.setEmailTemplateData("nil@gmail.com");
            return template;
        }

}
