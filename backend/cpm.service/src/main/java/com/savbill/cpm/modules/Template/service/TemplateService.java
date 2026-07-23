package com.savbill.cpm.modules.Template.service;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.Alert.communication.service.CommunicationService;
import com.savbill.cpm.modules.Template.domain.Template;
import com.savbill.cpm.modules.Template.mapper.TemplateMapper;
import com.savbill.cpm.modules.Template.model.TemplateDTO;
import com.savbill.cpm.modules.Template.repository.TemplateRepository;
import com.savbill.cpm.spring.MessagesPropertyConfig;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class TemplateService extends ExBaseAbstractService<TemplateDTO, Template, Long> {
    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    TemplateMapper tamplateMapper;

    @Autowired
    CommunicationService communicationService;

    @Autowired
    private MessagesPropertyConfig messagesProperty;


    private static final String MODULE = " [Template File] ";

    public TemplateService(TemplateRepository repository, TemplateMapper mapper) {
        super(repository, mapper);
        this.tamplateMapper = mapper;
        this.templateRepository = repository;
    }

    public Template save(HttpServletRequest request) {
        String SUBMODULE = MODULE + " [saveTemplate] ";
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile templateFile = multipartRequest.getFile("templateFile");
        String file = multipartRequest.getParameter("file");
        String id = multipartRequest.getParameter("id");
        String name = multipartRequest.getParameter("name");
        String type = multipartRequest.getParameter("type");
        String status = multipartRequest.getParameter("status");

        Template template = new Template();
        try {
            if (id != null) {
                template.setId(Long.parseLong(id));
            }
            if (templateFile.isEmpty() == false && templateFile != null) {
                String fileContent = new String(templateFile.getBytes());
                template.setFile(fileContent);
            } else {
                template.setFile(file);
            }
            template.setName(name);
            template.setStatus(status);
            template.setType(type);
            templateRepository.save(template);
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
        }
        return template;
    }

    @Override
    public String getModuleNameForLog() {
        return "[TemplateService]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Template");
        createExcel(workbook, sheet, TemplateDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, TemplateDTO.class, null);
    }
}
