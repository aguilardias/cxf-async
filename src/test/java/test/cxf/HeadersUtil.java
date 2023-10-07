package test.cxf;


import org.ebxml.namespaces.messageheader.From;
import org.ebxml.namespaces.messageheader.MessageData;
import org.ebxml.namespaces.messageheader.MessageHeader;
import org.ebxml.namespaces.messageheader.PartyId;
import org.ebxml.namespaces.messageheader.Service;
import org.ebxml.namespaces.messageheader.To;
import org.xmlsoap.schemas.ws._2002._12.secext.Security;
import org.xmlsoap.schemas.ws._2002._12.secext.Security.UsernameToken;

import jakarta.xml.ws.Holder;

public abstract class HeadersUtil {

	private HeadersUtil() {}

	public static Holder<MessageHeader> getHeaderMessage(String serviceName) {
		Holder<MessageHeader> header = new Holder<>();
		MessageHeader messageHeader = new MessageHeader();
		From from = new From();
		PartyId partyIdFrom = new PartyId();
		partyIdFrom.setValue("x");
		from.getPartyId().add(partyIdFrom);
		messageHeader.setFrom(from);
		To to = new To();
		PartyId partyIdTo = new PartyId();
		partyIdTo.setValue("x");
		to.getPartyId().add(partyIdTo);
		
		configureMessageHeader(messageHeader, serviceName, to);
		header.value = messageHeader;
		return header;
	}
	
	public static Holder<MessageHeader> getHeaderMessageAuth(String serviceName) {
		Holder<MessageHeader> header = new Holder<>();
    	MessageHeader messageHeader = new MessageHeader();
    	From from = new From();
    	PartyId partyId = new PartyId();
		from.getPartyId().add(partyId );
		messageHeader.setFrom(from );
		
		To to = new To();
		to.getPartyId().add(partyId);

		configureMessageHeader(messageHeader, serviceName, to);
		header.value = messageHeader;
		
		return header;
	}
	
	private static void configureMessageHeader(MessageHeader messageHeader, String serviceName, To to) {
		messageHeader.setTo(to);
    	messageHeader.setCPAId("x");
    	messageHeader.setConversationId("x");
    	Service service = new Service();
    	service.setValue(serviceName);
    	messageHeader.setService(service);
    	messageHeader.setAction(serviceName);
    	MessageData messageData = new MessageData();
    	messageData.setMessageId("x");
    	messageData.setTimestamp("x");
		messageHeader.setMessageData(messageData);
	}

	public static Holder<Security> getHeaderSecurity(String securityToken) {
		Holder<Security> header = new Holder<>();
		Security security = new Security();
		security.setBinarySecurityToken("x");
		header.value = security;
		return header;
	}
	
	public static Holder<Security> getHeaderSecurityAuth() {
		Holder<Security> header = new Holder<>();
    	Security security = new Security();
    	UsernameToken token = new UsernameToken();
    	token.setUsername("x");
    	token.setPassword("x");
    	token.setOrganization("x");
    	token.setDomain("x");
		security.setUsernameToken(token );
		header.value = security;
		return header;
	}
}
