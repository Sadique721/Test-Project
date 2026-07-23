package com.savbill.inventorymanagement.utils;

import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import org.apache.poi.openxml4j.opc.internal.FileHelper;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class PropertyReaderUtil {

    public static Properties getPropValues(String fileName) throws IOException {
        InputStream inputStream = null;
        Properties prop = new Properties();
        try {
            inputStream = FileHelper.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" + fileName + "' not found in the classpath");
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("GetPropValues() : " + e.getMessage(), e);
        }
        return prop;
    }
}
