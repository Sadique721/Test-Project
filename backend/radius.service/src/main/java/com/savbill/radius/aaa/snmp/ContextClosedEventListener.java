
package com.savbill.radius.aaa.snmp;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class ContextClosedEventListener implements DisposableBean {

    @Override
    public void destroy() throws Exception {

	SNMPTrapGenerator trapV2 = new SNMPTrapGenerator();
	trapV2.serverDownV2("public", "127.0.0.1", 9090, "Rdaius Server Down", ".1.3.6.1.6.3.1.1.5.3");
    }

}
