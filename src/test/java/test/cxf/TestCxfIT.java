package test.cxf;


import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.Future;

import org.ebxml.namespaces.messageheader.MessageHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentravel.ota._2003._05.AirSearchPrefsType;
import org.opentravel.ota._2003._05.CompanyNameType;
import org.opentravel.ota._2003._05.ExchangeOriginDestinationInformationType.DateFlexibility;
import org.opentravel.ota._2003._05.ExchangeOriginDestinationInformationType.SegmentType;
import org.opentravel.ota._2003._05.NumTripsType;
import org.opentravel.ota._2003._05.OTAAirLowFareSearchRQ;
import org.opentravel.ota._2003._05.OTAAirLowFareSearchRQ.OriginDestinationInformation;
import org.opentravel.ota._2003._05.OTAAirLowFareSearchRQ.OriginDestinationInformation.TPAExtensions;
import org.opentravel.ota._2003._05.OTAAirLowFareSearchRQ.TPAExtensions.SplitTaxes;
import org.opentravel.ota._2003._05.OTAAirLowFareSearchRS;
import org.opentravel.ota._2003._05.OriginDestinationInformationType.DestinationLocation;
import org.opentravel.ota._2003._05.OriginDestinationInformationType.OriginLocation;
import org.opentravel.ota._2003._05.POSType;
import org.opentravel.ota._2003._05.PassengerTypeQuantityType;
import org.opentravel.ota._2003._05.PriceRequestInformationType;
import org.opentravel.ota._2003._05.PriceRequestInformationType.AccountCode;
import org.opentravel.ota._2003._05.SourceType;
import org.opentravel.ota._2003._05.TransactionType;
import org.opentravel.ota._2003._05.TransactionType.RequestType;
import org.opentravel.ota._2003._05.TransactionType.ServiceTag;
import org.opentravel.ota._2003._05.TravelerInfoSummaryType;
import org.opentravel.ota._2003._05.TravelerInformationType;
import org.opentravel.ota._2003._05.UniqueIDType;
import org.xmlsoap.schemas.ws._2002._12.secext.Security;

import com.dataaccess.webservicesserver.NumberConversionSoapType;
import com.dataaccess.webservicesserver.NumberToWordsResponse;

import https.webservices_sabre_com.websvc.AdvancedAirShoppingPortType;
import io.quarkiverse.cxf.annotation.CXFClient;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.soap.SOAPFaultException;

@QuarkusTest
class TestCxfIT {

	@CXFClient("numberConversion") 
	NumberConversionSoapType numberConversionSoapType;
	
	@CXFClient("sabre") 
	AdvancedAirShoppingPortType advancedAirShoppingPortType;
	
	@Test
	void sabreAsyncFail() {
		Holder<MessageHeader> header = HeadersUtil.getHeaderMessage("AdvancedAirShoppingRQ");
		Holder<Security> header2 = HeadersUtil.getHeaderSecurity("x");
		OTAAirLowFareSearchRQ body = createBody();
		
		// fail, after about 30 seconds of execution, receive 'Unable to internalize message'
		// expect to receive 'Invalid or Expired binary security token', because i didnt put real credencials
		OTAAirLowFareSearchRS sabre = Uni.createFrom()
			.future((Future<OTAAirLowFareSearchRS>)advancedAirShoppingPortType.advancedAirShoppingRQAsync(header , header2 , body , r->{}))
			.await().atMost(Duration.ofMinutes(1));
		System.out.println(">>>"+sabre);
	}
	
	@Test
	void sabreSyncSuccess() {
		Holder<MessageHeader> header = HeadersUtil.getHeaderMessage("AdvancedAirShoppingRQ");
		Holder<Security> header2 = HeadersUtil.getHeaderSecurity("x");
		OTAAirLowFareSearchRQ body = createBody();
		
		Exception exception = Assertions.assertThrows(SOAPFaultException.class,
				() -> advancedAirShoppingPortType.advancedAirShoppingRQ(header , header2 , body));
		
		// success, expect to receive 'Invalid or Expired binary security token', because i didnt put real credencials
		Assertions.assertTrue(exception.getMessage().contains("Invalid or Expired binary security token"));
	}

	@Test
	void numberConversionSuccess() {
		// test another webservice using https, https://documenter.getpostman.com/view/8854915/Szf26WHn
		String numberConversion = Uni.createFrom()
			.future((Future<NumberToWordsResponse>)numberConversionSoapType.numberToWordsAsync(new BigInteger("1"), r->{}))
			.map(a->a.getNumberToWordsResult())
			.await().atMost(Duration.ofSeconds(10));
		
		System.out.println(">>>"+numberConversion);
	}
	
	private OTAAirLowFareSearchRQ createBody() {
		OTAAirLowFareSearchRQ body = new OTAAirLowFareSearchRQ();
		body.setTarget("x");
		body.setVersion("x");
		body.setResponseType("x");
		body.setResponseVersion("x");

		POSType pos = new POSType();
		SourceType source = new SourceType();
		source.setAccountingCode("x");
		source.setDefaultTicketingCarrier("x");
		source.setOfficeCode("x");
		source.setPersonalCityCode("x");
		source.setPseudoCityCode("x");
		UniqueIDType requestor = new UniqueIDType();
		requestor.setType("x");
		requestor.setID("x");
		CompanyNameType companyName = new CompanyNameType();
		companyName.setCode("x");
		requestor.setCompanyName(companyName);
		source.setRequestorID(requestor);
		pos.getSource().add(source);
		body.setPOS(pos);

		body.getOriginDestinationInformation().add(getOriginDestination("x", "x", LocalDate.now(), 1));

		AirSearchPrefsType travelPreferences = new AirSearchPrefsType();
		travelPreferences.setAllFlightsData(true);
		travelPreferences.setMaxStopsQuantity(3);
		org.opentravel.ota._2003._05.AirSearchPrefsType.TPAExtensions tpaTravel = new org.opentravel.ota._2003._05.AirSearchPrefsType.TPAExtensions();
		NumTripsType trips = new NumTripsType();
		trips.setNumber((short) 10);
		tpaTravel.setNumTrips(trips);
		travelPreferences.setTPAExtensions(tpaTravel);
		body.setTravelPreferences(travelPreferences);

		TravelerInfoSummaryType traveler = new TravelerInfoSummaryType();
		traveler.getSeatsRequested().add(BigInteger.valueOf(1));
		TravelerInformationType travelerInfo = new TravelerInformationType();
		PassengerTypeQuantityType passengerType = new PassengerTypeQuantityType();
		passengerType.setCode("x");
		passengerType.setQuantity(1);
		travelerInfo.getPassengerTypeQuantity().add(passengerType);
		traveler.getAirTravelerAvail().add(travelerInfo);
		PriceRequestInformationType price = new PriceRequestInformationType();
		price.setCurrencyCode("x");
		AccountCode accountCode = new AccountCode();
		accountCode.setCode("x");
		price.getNegotiatedFareCodeOrAccountCode().add(accountCode);
		org.opentravel.ota._2003._05.PriceRequestInformationType.TPAExtensions tpaPrice = new org.opentravel.ota._2003._05.PriceRequestInformationType.TPAExtensions();
		org.opentravel.ota._2003._05.PriceRequestInformationType.TPAExtensions.PrivateFare privateFare = new org.opentravel.ota._2003._05.PriceRequestInformationType.TPAExtensions.PrivateFare();
		privateFare.setInd(true);
		tpaPrice.setPrivateFare(privateFare);
		price.setTPAExtensions(tpaPrice);
		traveler.setPriceRequestInformation(price);
		body.setTravelerInfoSummary(traveler);

		org.opentravel.ota._2003._05.OTAAirLowFareSearchRQ.TPAExtensions tpaExtensions = new org.opentravel.ota._2003._05.OTAAirLowFareSearchRQ.TPAExtensions();
		TransactionType intelli = new TransactionType();
		intelli.setDebug(false);
		RequestType requestType = new RequestType();
		requestType.setName("x");
		ServiceTag serviceTag = new ServiceTag();
		serviceTag.setName("x");
		intelli.setServiceTag(serviceTag);
		intelli.setRequestType(requestType);
		tpaExtensions.setIntelliSellTransaction(intelli);
		SplitTaxes split = new SplitTaxes();
		split.setByLeg(true);
		split.setByFareComponent(true);
		tpaExtensions.setSplitTaxes(split);
		body.setTPAExtensions(tpaExtensions);
		return body;
	}

	private OriginDestinationInformation getOriginDestination(String origin, String destination, LocalDate date, Integer dateFlexibilityNumber) {
		OriginDestinationInformation originDestination = new OriginDestinationInformation();
		originDestination.setRPH("x");
		originDestination.setDepartureDateTime("x");
		OriginLocation originLocation = new OriginLocation();
		originLocation.setLocationCode(origin);
		originDestination.setOriginLocation(originLocation);
		DestinationLocation destinationLocation = new DestinationLocation();
		destinationLocation.setLocationCode(destination);
		originDestination.setDestinationLocation(destinationLocation);
		TPAExtensions tpa = new TPAExtensions();
		DateFlexibility dateFlexibility = new DateFlexibility();
		dateFlexibility.setNbrOfDays(dateFlexibilityNumber);
		tpa.getDateFlexibility().add(dateFlexibility);
		SegmentType segment = new SegmentType();
		segment.setCode("x");
		tpa.setSegmentType(segment);
		originDestination.setTPAExtensions(tpa);

		return originDestination;
	}
}
