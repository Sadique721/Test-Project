package com.savbill.commonGateway.utils;


import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;


import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Properties;

@Component
public class PropertyReaderUtil {

    static final BigInteger preA = new BigInteger("3781927463263421");
    static final BigInteger preC = new BigInteger("2113323684682149");
    static final int length = 10;

    public static Properties getPropValues(String fileName) throws IOException {
        InputStream inputStream = null;
        Properties prop = new Properties();
        try {
//            inputStream = FilterHelper.class.getClassLoader().getResourceAsStream(fileName);
//            if (inputStream != null) {
//                prop.load(inputStream);
//            } else {
//                throw new FileNotFoundException("Property file '" + fileName + "' not found in the classpath");
//            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("GetPropValues() : " + e.getMessage(), e);
        }
        return prop;
    }

    public static String random(long seedValue) {
        long lMod = (long) Math.pow(10,length);
        BigInteger preM = new BigInteger(String.valueOf(lMod));
        BigInteger x = new BigInteger(seedValue+"");
        BigInteger y = x.multiply(preA).add(preC);
        StringBuilder res = new StringBuilder(y.mod(preM).toString());
        while(res.length() < length) res.insert(0, "0"); // supply leading 0s to small numbers.
        return res.toString();
    }
}
