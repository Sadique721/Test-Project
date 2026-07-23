package com.savbill.notification.snmp;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

public class SNMPConfig implements CommandResponder{

	private final Logger log = Logger.getLogger(SNMPConfig.class);
	
	
	public synchronized void listen(TransportIpAddress address) throws IOException {
		AbstractTransportMapping transport;
		if (address instanceof TcpAddress) {
			transport = new DefaultTcpTransportMapping((TcpAddress) address);
		} else {
			transport = new DefaultUdpTransportMapping((UdpAddress) address);
		}

		ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
		MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

// add message processing models
		mtDispatcher.addMessageProcessingModel(new MPv1());
		mtDispatcher.addMessageProcessingModel(new MPv2c());

// add all security protocols
		SecurityProtocols.getInstance().addDefaultProtocols();
		SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

//Create Target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));

		Snmp snmp = new Snmp(mtDispatcher, transport);
		snmp.addCommandResponder(this);

		transport.listen();
		//System.out.println("SNMP Agent Listening on " + address);

		try {
			this.wait();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * This method will be called whenever a pdu is received on the given port
	 * specified in the listen() method
	 */
	@Override
	public void processPdu(CommandResponderEvent cmdRespEvent) {
		PDU pdu = cmdRespEvent.getPDU();
		if(pdu!=null)
        {
            int pduType=pdu.getType();

            if( (pduType!=PDU.TRAP) && (pduType!=PDU.V1TRAP) && (pduType!=PDU.REPORT) && (pduType !=PDU.RESPONSE) )
            {
                pdu.setErrorIndex(0);
                pdu.setErrorStatus(0);

                String pduObject=pdu.get(0).getOid().toString();
                String newstring="."+pduObject;

                Integer request_count=SNMPCounters.getCountForValue(newstring);

                if(request_count != null) {
                    VariableBinding variableBinding = new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(request_count));
                    pdu.set(0, variableBinding);
                }
//                else
//			     {
//				   //log.info("Unknown SNMP OID received");
//			     }
                pdu.setType(PDU.RESPONSE);

                try {
                    cmdRespEvent.getMessageDispatcher().returnResponsePdu(cmdRespEvent.getMessageProcessingModel(), cmdRespEvent.getSecurityModel(), cmdRespEvent.getSecurityName(), cmdRespEvent.getSecurityLevel(), pdu, cmdRespEvent.getMaxSizeResponsePDU(), cmdRespEvent.getStateReference(),new StatusInformation());
                } catch(MessageException messageexception) {
                    //log.error("Error occureed in sending response");
                }
            }

        }
		
	}
}
