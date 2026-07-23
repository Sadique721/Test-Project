package com.savbill.radius.aaa.snmp;

import java.util.Date;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPTrapGenerator {

	private static final Logger log = LoggerFactory.getLogger(SNMPTrapGenerator.class);

    public void serverDownV2(String strCommunity, String strIpaddress, int intPort, String strMsg, String strOid) {
	try {
	    // Create Transport Mapping
	    TransportMapping transport = new DefaultUdpTransportMapping();
	    transport.listen();

	    // Create Target
	    CommunityTarget cTarget = new CommunityTarget();
	    cTarget.setCommunity(new OctetString(strCommunity));
	    cTarget.setVersion(SnmpConstants.version2c);
	    cTarget.setAddress(new UdpAddress(strIpaddress + "/" + intPort));
	    cTarget.setRetries(2);
	    cTarget.setTimeout(5000);

	    // Create PDU for V2
	    PDU pdu = new PDU();

	    // need to specify the system up time
	    pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
	    pdu.add(new VariableBinding(SnmpConstants.linkDown, new OctetString(new Date().toString())));
	    pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(strOid)));
	    pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(strIpaddress)));

	    pdu.add(new VariableBinding(new OID(strOid), new OctetString(strMsg)));
	    pdu.setType(PDU.TRAP);

	    // Send the PDU
	    Snmp snmp = new Snmp(transport);
	    log.info("Sending V2 Trap... When Radius server down!! ");
	    snmp.send(pdu, cTarget);
	    snmp.close();
	} catch (Exception e) {
	    log.error("Error while seding V2 Trap", e);
	}
    }

    public void serverUpV2(String strCommunity, String strIpaddress, int intPort, String strMsg, String strOid) {
	try {
	    // Create Transport Mapping
	    TransportMapping transport = new DefaultUdpTransportMapping();
	    transport.listen();

	    // Create Target
	    CommunityTarget cTarget = new CommunityTarget();
	    cTarget.setCommunity(new OctetString(strCommunity));
	    cTarget.setVersion(SnmpConstants.version2c);
	    cTarget.setAddress(new UdpAddress(strIpaddress + "/" + intPort));
	    cTarget.setRetries(2);
	    cTarget.setTimeout(5000);

	    // Create PDU for V2
	    PDU pdu = new PDU();

	    // need to specify the system up time
	    pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
	    pdu.add(new VariableBinding(SnmpConstants.linkUp, new OctetString(new Date().toString())));
	    pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(strOid)));
	    pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(strIpaddress)));

	    pdu.add(new VariableBinding(new OID(strOid), new OctetString(strMsg)));
	    pdu.setType(PDU.TRAP);

	    // Send the PDU
	    Snmp snmp = new Snmp(transport);
	    log.info("Sending V2 Trap... When Radius server up!! ");
	    snmp.send(pdu, cTarget);
	    snmp.close();
	} catch (Exception e) {
	    log.error("Error while seding V2 Trap", e);
	}
    }
}
