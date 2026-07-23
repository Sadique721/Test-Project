package com.savbill.radius.aaa.db;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.savbill.radius.config.JpaEntityManagerFactory;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.Dictionary;
import com.savbill.radius.entity.DictionaryAttribute;
import com.savbill.radius.entity.DictionaryValue;
import com.savbill.radius.helper.AttributeCategory;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.fusesource.hawtbuf.ByteArrayInputStream;

import javax.persistence.EntityManager;

public class DBDictionaryDriver {

    private static final String SQL_EXCEPTION = "SQLException";
    private static final String RESULT_SET_IS = "Result Set is ";

    private static String strDictionaryQuery="select concat(category,\"   \",name,\"   \",attributeid,\"   \",type) standardval from TBLTDICTIONARYATTRIBUTE where dictionaryid=1";

    private static String strDictionaryMasterQuery="select concat(vendortype,\"  \",vendorid,\"  \",vendor) standardval from tblmdictionary where vendorid!=0";

    private static String strDictionaryVSAQuery="select concat(category,\"   \",vendorid,\"  \",name,\"   \",attributeid,\"   \",type) standardval from TBLTDICTIONARYATTRIBUTE attri,\r\n"
            + "tblmdictionary dic where attri.dictionaryid!=1 and attri.dictionaryid=dic.dictionaryid";

    private static String strDictionaryAttributeVal="select concat(dval.type,\"  \",datt.name,\"  \",dval.name,\"  \",dval.value) standardval from TBLTDICTIONARYVALUE dval,TBLTDICTIONARYATTRIBUTE datt where "
            + "dval.dictionaryattributeid=datt.dictionaryattributeid";

    private static final Logger log = LoggerFactory.getLogger(DBDictionaryDriver.class);

    public InputStream getDictionary()  {
        InputStream inputStream=null;
        EntityManager em =  new JpaEntityManagerFactory(
                new Class[]{DictionaryAttribute.class, AttributeCategory.class, Dictionary.class, DictionaryValue.class}).getEntityManager();
        QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
        QDictionary qDictionary = QDictionary.dictionary;
        QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;

        if (log.isDebugEnabled()) {
            log.debug(String.format("getDictionary",strDictionaryQuery));
        }
        try {
            JPAQuery<Tuple> jpaQueryDictionaryData = new JPAQuery<>(em);
            String dictionaryData=null;
            List<Tuple> tuples = jpaQueryDictionaryData.select(qDictionaryAttribute.category, qDictionaryAttribute.name,
                    qDictionaryAttribute.attributeId, qDictionaryAttribute.type).from(qDictionaryAttribute)
                    .where(qDictionaryAttribute.dictionary.dictionaryId.eq(1L)).fetch();
            for (Tuple row: tuples){
                if(dictionaryData==null) {
                    dictionaryData=row.get(qDictionaryAttribute.category)+"   "+row.get(qDictionaryAttribute.name)+"   "
                            +row.get(qDictionaryAttribute.attributeId)+"   "+row.get(qDictionaryAttribute.type);
                }
                else {
                    dictionaryData=dictionaryData+System.lineSeparator()+
                            row.get(qDictionaryAttribute.category)+"   "+row.get(qDictionaryAttribute.name)+"   "+
                            row.get(qDictionaryAttribute.attributeId)+"   "+row.get(qDictionaryAttribute.type);
                }
            }

            JPAQuery<Tuple> jpaQueryDictionary = new JPAQuery<>(em);
            List<Tuple> tuples1 = jpaQueryDictionary.select(qDictionary.vendorType, qDictionary.vendorId, qDictionary.vendor).from(qDictionary)
                    .where(qDictionary.vendorId.ne("0")).fetch();
            for (Tuple row: tuples1) {
                if(dictionaryData==null) {
                    dictionaryData=row.get(qDictionary.vendorType)+"   "+row.get(qDictionary.vendorId)+"   "+row.get(qDictionary.vendor);

                }
                else {
                    dictionaryData=dictionaryData+System.lineSeparator()+
                            row.get(qDictionary.vendorType)+"   "+row.get(qDictionary.vendorId)+"   "+row.get(qDictionary.vendor);
                }
            }

            JPAQuery<Tuple> jpaQueryDictionaryVSA = new JPAQuery<>(em);
            List<Tuple> tupleList = jpaQueryDictionaryVSA.select(qDictionaryAttribute.category, qDictionary.vendorId,
                    qDictionaryAttribute.name, qDictionaryAttribute.attributeId, qDictionaryAttribute.type)
                    .from(qDictionaryAttribute).join(qDictionary).on(qDictionary.dictionaryId.eq(qDictionaryAttribute.dictionary.dictionaryId)).
                            where(qDictionaryAttribute.dictionary.dictionaryId.ne(1L)).fetch();
            for (Tuple row: tupleList) {
                String str = row.get(qDictionaryAttribute.category)+"   "+row.get(qDictionary.vendorId)+"   "+
                        row.get(qDictionaryAttribute.name)+"   "+row.get(qDictionaryAttribute.attributeId)+"   "+row.get(qDictionaryAttribute.type);
                if(dictionaryData==null) {
                    dictionaryData= str;
                }
                else {
                    dictionaryData=dictionaryData+System.lineSeparator()+str;
                }
            }

            JPAQuery<Tuple> jpaQueryDictionaryValue = new JPAQuery<>(em);
            List<Tuple> tuplesDictionaryValue = jpaQueryDictionaryValue.select(qDictionaryValue.type, qDictionaryAttribute.name, qDictionaryValue.name, qDictionaryValue.value)
                    .from(qDictionaryValue).join(qDictionaryAttribute).on(qDictionaryValue.dictionaryAttribute.attributeId.eq(qDictionaryAttribute.attributeId)).fetch();
            for (Tuple row:tuplesDictionaryValue) {
                String str = row.get(qDictionaryValue.type)+"   "+row.get(qDictionaryAttribute.name)+"   "
                        +row.get(qDictionaryValue.name)+"   "+row.get(qDictionaryValue.value);
                if(dictionaryData==null) {
                    dictionaryData=str;
                }
                else {
                    dictionaryData=dictionaryData+System.lineSeparator()+str;
                }
            }
            inputStream = new ByteArrayInputStream(dictionaryData.getBytes(StandardCharsets.UTF_8));
            return inputStream;
        }
        catch(Exception e) {
            log.error(SQL_EXCEPTION,e);
        }
        finally {
        	if(em != null) {em.close();}
        }

        return inputStream;
    }


}
