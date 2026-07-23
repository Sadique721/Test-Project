package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
//import com.savbill.integrationsystem.rabbitmq.MessageSender;
import com.savbill.integrationsystem.rms.entity.Product;
import com.savbill.integrationsystem.rms.entity.ProductCategory;
import com.savbill.integrationsystem.rms.mapper.ProductCategoryMapper;
import com.savbill.integrationsystem.rms.mapper.ProductIntegrationMapper;
import com.savbill.integrationsystem.rms.model.ProductCategoryDto;
import com.savbill.integrationsystem.rms.model.ProductDto;
import com.savbill.integrationsystem.rms.model.ProductRmsDto;
import com.savbill.integrationsystem.rms.repository.ProductCategoryRepo;
import com.savbill.integrationsystem.rms.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductIntegrationMapper productIntegrationMapper;

    @Autowired
    ProductCategoryMapper productCategoryMapper;

    @Autowired
    ProductRepo productRepo;

//    @Autowired
//    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    ProductCategoryRepo productCategoryRepo;

    @Override
    public Product saveProductFromRms(ProductRmsDto productRmsDto) {
        try{
            ProductDto productDto = mapRmsProductToBssProduct(productRmsDto);
            Product product = productIntegrationMapper.dtoToDomain(productDto,new CycleAvoidingMappingContext());
            productRepo.save(product);
//            messageSender.send(productDto, RabbitMqConstants.QUEUE_PRODUCT_FROM_RMS);
            kafkaMessageSender.send(new KafkaMessageData(productDto, productDto.getClass().getSimpleName()));

            return product;
        }catch (Exception e){
            throw new PersistenceException("Not able to Save Product From Rms : " + e);
        }
    }

    public ProductDto mapRmsProductToBssProduct(ProductRmsDto productRmsDto){
        ProductDto productDto = new ProductDto();
        productDto.setName(productRmsDto.getProductName());
        ProductCategory productCategory = productCategoryRepo.findByName(productRmsDto.getProductCategory());
        ProductCategoryDto productCategoryDto = productCategoryMapper.domainToDTO(productCategory,new CycleAvoidingMappingContext());
        productDto.setProductCategory(productCategoryDto);
        productDto.setStatus("ACTIVE");
        productDto.setMvnoId(2L);
        productDto.setExpiryTime(0);
        productDto.setExpiryTimeUnit("Month");
        return productDto;

    }
}
