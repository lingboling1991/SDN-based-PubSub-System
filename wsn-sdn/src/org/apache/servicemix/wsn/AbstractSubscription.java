/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.wsn;

import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.servicemix.application.WsnProcessImpl;
import org.apache.servicemix.wsn.push.ListItem;
import org.apache.servicemix.wsn.push.NotifyObserver;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.oasis_open.docs.wsn.b_2.InvalidFilterFaultType;
import org.oasis_open.docs.wsn.b_2.InvalidMessageContentExpressionFaultType;
import org.oasis_open.docs.wsn.b_2.InvalidProducerPropertiesExpressionFaultType;
import org.oasis_open.docs.wsn.b_2.InvalidTopicExpressionFaultType;
import org.oasis_open.docs.wsn.b_2.PauseSubscription;
import org.oasis_open.docs.wsn.b_2.PauseSubscriptionResponse;
import org.oasis_open.docs.wsn.b_2.QueryExpressionType;
import org.oasis_open.docs.wsn.b_2.Renew;
import org.oasis_open.docs.wsn.b_2.RenewResponse;
import org.oasis_open.docs.wsn.b_2.ResumeSubscription;
import org.oasis_open.docs.wsn.b_2.ResumeSubscriptionResponse;
import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.SubscribeCreationFailedFaultType;
import org.oasis_open.docs.wsn.b_2.TopicExpressionType;
import org.oasis_open.docs.wsn.b_2.UnableToDestroySubscriptionFaultType;
import org.oasis_open.docs.wsn.b_2.UnacceptableInitialTerminationTimeFaultType;
import org.oasis_open.docs.wsn.b_2.UnacceptableTerminationTimeFaultType;
import org.oasis_open.docs.wsn.b_2.Unsubscribe;
import org.oasis_open.docs.wsn.b_2.UnsubscribeResponse;
import org.oasis_open.docs.wsn.b_2.UseRaw;
import org.oasis_open.docs.wsn.b_2.UnrecognizedPolicyRequestFaultType;
import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.PausableSubscriptionManager;
import org.oasis_open.docs.wsn.bw_2.PauseFailedFault;
import org.oasis_open.docs.wsn.bw_2.ResumeFailedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnableToDestroySubscriptionFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;



/**
 * 
 * @re-writer ���׺�
 * @difference 	Add the subscriberAddress field.
 * 				make change in {@link b-2.xsd} : Add SubscriberAddress schema definition
 * 				make change in {@link Subscribe} : Add subscriberAddress field and related operation.
 * 			   	Add the SubscriberAddress processing in validateSubscription operation. 
 *				make change in {@link Unsubscribe} : Add notify observer operation.
 */


@WebService(endpointInterface = "org.oasis_open.docs.wsn.bw_2.PausableSubscriptionManager")
public abstract class AbstractSubscription extends AbstractEndpoint implements PausableSubscriptionManager {

    public static final String WSN_URI = "http://docs.oasis-open.org/wsn/b-2";

    public static final String XPATH1_URI = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    public static final QName QNAME_TOPIC_EXPRESSION = new QName(WSN_URI, "TopicExpression");

    public static final QName QNAME_PRODUCER_PROPERTIES = new QName(WSN_URI, "ProducerProperties");

    public static final QName QNAME_MESSAGE_CONTENT = new QName(WSN_URI, "MessageContent");

    public static final QName QNAME_USE_RAW = new QName(WSN_URI, "UseRaw");
    
    protected DatatypeFactory datatypeFactory;

    protected XMLGregorianCalendar terminationTime;

    protected boolean useRaw;

    protected TopicExpressionType topic;

    protected QueryExpressionType contentFilter;

    protected W3CEndpointReference consumerReference;

    protected AbstractNotificationBroker broker;
    
    public String subscriberAddress;  
    
    private NotifyObserver notifyObserver;

    public AbstractSubscription(String name) {
        super(name);
        try {
            this.datatypeFactory = DatatypeFactory.newInstance();
            notifyObserver = new NotifyObserver();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Unable to initialize subscription", e);
        }
    }

    /**
     * 
     * @param renewRequest
     * @return returns org.oasis_open.docs.wsn.b_1.RenewResponse
     * @throws UnacceptableTerminationTimeFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "Renew")
    @WebResult(name = "RenewResponse", 
               targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
               partName = "RenewResponse")
    public RenewResponse renew(
            @WebParam(name = "Renew", 
                      targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
                      partName = "RenewRequest")
            Renew renewRequest) throws ResourceUnknownFault, UnacceptableTerminationTimeFault {

        XMLGregorianCalendar time = validateTerminationTime(renewRequest.getTerminationTime());
        renew(time);
        RenewResponse response = new RenewResponse();
        response.setTerminationTime(time);
        response.setCurrentTime(getCurrentTime());
        return response;
    }

    /**
     * 
     * @param unsubscribeRequest
     * @return returns org.oasis_open.docs.wsn.b_1.UnsubscribeResponse
     * @throws UnableToDestroySubscriptionFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "Unsubscribe")
    @WebResult(name = "UnsubscribeResponse", 
               targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
               partName = "UnsubscribeResponse")
    public UnsubscribeResponse unsubscribe(
            @WebParam(name = "Unsubscribe", 
                      targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
                      partName = "UnsubscribeRequest")
            Unsubscribe unsubscribeRequest) throws ResourceUnknownFault, UnableToDestroySubscriptionFault {

        broker.unsubscribe(getAddress());
        return new UnsubscribeResponse();
    }

    /**
     * 
     * @param pauseSubscriptionRequest
     * @return returns org.oasis_open.docs.wsn.b_1.PauseSubscriptionResponse
     * @throws PauseFailedFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "PauseSubscription")
    @WebResult(name = "PauseSubscriptionResponse", 
               targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
               partName = "PauseSubscriptionResponse")
    public PauseSubscriptionResponse pauseSubscription(
            @WebParam(name = "PauseSubscription", 
                      targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
                      partName = "PauseSubscriptionRequest")
            PauseSubscription pauseSubscriptionRequest) throws PauseFailedFault, ResourceUnknownFault {

        pause();
        return new PauseSubscriptionResponse();
    }

    /**
     * 
     * @param resumeSubscriptionRequest
     * @return returns org.oasis_open.docs.wsn.b_1.ResumeSubscriptionResponse
     * @throws ResumeFailedFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "ResumeSubscription")
    @WebResult(name = "ResumeSubscriptionResponse", 
               targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
               partName = "ResumeSubscriptionResponse")
    public ResumeSubscriptionResponse resumeSubscription(
            @WebParam(name = "ResumeSubscription", 
                      targetNamespace = "http://docs.oasis-open.org/wsn/b-2", 
                      partName = "ResumeSubscriptionRequest")
            ResumeSubscription resumeSubscriptionRequest) throws ResourceUnknownFault, ResumeFailedFault {

        resume();
        return new ResumeSubscriptionResponse();
    }

    protected XMLGregorianCalendar validateInitialTerminationTime(String value) 
        throws UnacceptableInitialTerminationTimeFault {
        XMLGregorianCalendar tt = parseTerminationTime(value);
        if (tt == null) {
            UnacceptableInitialTerminationTimeFaultType fault = new UnacceptableInitialTerminationTimeFaultType();
            throw new UnacceptableInitialTerminationTimeFault("Unable to parse initial termination time: '" + value
                    + "'", fault);
        }
        XMLGregorianCalendar ct = getCurrentTime();
        int c = tt.compare(ct);
        if (c == DatatypeConstants.LESSER || c == DatatypeConstants.EQUAL) {
            UnacceptableInitialTerminationTimeFaultType fault = new UnacceptableInitialTerminationTimeFaultType();
            fault.setMinimumTime(ct);
            throw new UnacceptableInitialTerminationTimeFault("Invalid initial termination time", fault);
        }
        return tt;
    }

    protected XMLGregorianCalendar validateTerminationTime(String value) throws UnacceptableTerminationTimeFault {
        XMLGregorianCalendar tt = parseTerminationTime(value);
        if (tt == null) {
            UnacceptableTerminationTimeFaultType fault = new UnacceptableTerminationTimeFaultType();
            throw new UnacceptableTerminationTimeFault("Unable to parse termination time: '" + value + "'", fault);
        }
        XMLGregorianCalendar ct = getCurrentTime();
        int c = tt.compare(ct);
        if (c == DatatypeConstants.LESSER || c == DatatypeConstants.EQUAL) {
            UnacceptableTerminationTimeFaultType fault = new UnacceptableTerminationTimeFaultType();
            fault.setMinimumTime(ct);
            throw new UnacceptableTerminationTimeFault("Invalid termination time", fault);
        }
        return tt;
    }

    protected XMLGregorianCalendar parseTerminationTime(String value) {
        try {
            Duration d = datatypeFactory.newDuration(value);
            XMLGregorianCalendar c = getCurrentTime();
            c.add(d);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            Duration d = datatypeFactory.newDurationDayTime(value);
            XMLGregorianCalendar c = getCurrentTime();
            c.add(d);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            Duration d = datatypeFactory.newDurationYearMonth(value);
            XMLGregorianCalendar c = getCurrentTime();
            c.add(d);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            return datatypeFactory.newXMLGregorianCalendar(value);
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    protected XMLGregorianCalendar getCurrentTime() {
        return datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
    }

    public XMLGregorianCalendar getTerminationTime() {
        return terminationTime;
    }

    public void setTerminationTime(XMLGregorianCalendar terminationTime) {
        this.terminationTime = terminationTime;
    }

    public void create(Subscribe subscribeRequest) throws InvalidFilterFault, InvalidMessageContentExpressionFault,
            InvalidProducerPropertiesExpressionFault, InvalidTopicExpressionFault, SubscribeCreationFailedFault,
            TopicExpressionDialectUnknownFault, TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault,
            UnrecognizedPolicyRequestFault, UnsupportedPolicyRequestFault {
    	System.out.println("validateSubscription3");
        validateSubscription(subscribeRequest);
        start();
    }

    protected abstract void start() throws SubscribeCreationFailedFault;

    protected abstract void pause() throws PauseFailedFault;

    protected abstract void resume() throws ResumeFailedFault;

    protected abstract void renew(XMLGregorianCalendar time) throws UnacceptableTerminationTimeFault;

    protected synchronized void unsubscribe() throws UnableToDestroySubscriptionFault {
        try {
            unregister();
                        
        } catch (EndpointRegistrationException e) {
            UnableToDestroySubscriptionFaultType fault = new UnableToDestroySubscriptionFaultType();
            throw new UnableToDestroySubscriptionFault("Error unregistering endpoint", fault, e);
        }
        if(subscriberAddress != null&&WsnProcessImpl.localtable!=null){
        	String topicName = convertTopic(topic);
        	Iterator<ListItem> iter = WsnProcessImpl.localtable.iterator();
        	while(iter.hasNext()){
        		ListItem item = (ListItem)iter.next();
        		if(item.getTopicName().equals(topicName))
        			iter.remove();
        	}
            
        	ListItem listCheck = null;
        	
        	for(ListItem listItem:WsnProcessImpl.localtable)
        		if(listItem.getTopicName().equals(topicName))
        			listCheck = listItem;
        	if(listCheck == null)
        	{
            	//NotifyObserver notify = new NotifyObserver(topicName,0);
        		notifyObserver.setTopicName(topicName);
        		notifyObserver.setKind(0);
            	notifyObserver.addObserver(RtMgr.getInstance());
            	notifyObserver.notifyMessage();
        	}
        }
    }

    protected String createAddress() {
        return "http://servicemix.org/wsnotification/Subscription/" + getName();
    }

    protected String convertTopic(TopicExpressionType topic){
    	String answer = null; 
    	for (Iterator iter = topic.getContent().iterator(); iter.hasNext();) {
    		Object contentItem = iter.next();
    		if (contentItem instanceof String)
    			answer = ((String)contentItem).trim();
    		if(contentItem instanceof QName)
    			answer = ((QName)contentItem).toString();             
             if (answer != null) {
                 return answer;
             }
    	
    	}
		return answer;
    }
    
    protected void validateSubscription(Subscribe subscribeRequest) throws InvalidFilterFault,
            InvalidMessageContentExpressionFault, InvalidProducerPropertiesExpressionFault,
            InvalidTopicExpressionFault, SubscribeCreationFailedFault, TopicExpressionDialectUnknownFault,
            TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault, UnrecognizedPolicyRequestFault,
            UnsupportedPolicyRequestFault {
    	// Check consumer reference
        consumerReference = subscribeRequest.getConsumerReference();
        // Check terminationTime
        if (subscribeRequest.getInitialTerminationTime() != null
                && !subscribeRequest.getInitialTerminationTime().isNil()
                && subscribeRequest.getInitialTerminationTime().getValue() != null) {
            String strTerminationTime = subscribeRequest.getInitialTerminationTime().getValue();
            terminationTime = validateInitialTerminationTime(strTerminationTime.trim());
        }
        // Check filter
        if (subscribeRequest.getFilter() != null) {
            for (Object f : subscribeRequest.getFilter().getAny()) {
                JAXBElement e = null;
                if (f instanceof JAXBElement) {
                    e = (JAXBElement) f;
                    f = e.getValue();
                }
                
                if (f instanceof TopicExpressionType) {
                    if (!e.getName().equals(QNAME_TOPIC_EXPRESSION)) {
                        InvalidTopicExpressionFaultType fault = new InvalidTopicExpressionFaultType();
                        throw new InvalidTopicExpressionFault("Unrecognized TopicExpression: " + e, fault);
                    }
                    topic = (TopicExpressionType) f;
                } else if (f instanceof QueryExpressionType) {
                    if (e != null && e.getName().equals(QNAME_PRODUCER_PROPERTIES)) {
                        InvalidProducerPropertiesExpressionFaultType fault = 
                            new InvalidProducerPropertiesExpressionFaultType();
                        throw new InvalidProducerPropertiesExpressionFault("ProducerProperties are not supported",
                                fault);
                    } else if (e != null && e.getName().equals(QNAME_MESSAGE_CONTENT)) {
                        if (contentFilter != null) {
                            InvalidMessageContentExpressionFaultType fault = 
                                new InvalidMessageContentExpressionFaultType();
                            throw new InvalidMessageContentExpressionFault(
                                    "Only one MessageContent filter can be specified", fault);
                        }
                        contentFilter = (QueryExpressionType) f;
                        // Defaults to XPath 1.0
                        if (contentFilter.getDialect() == null) {
                            contentFilter.setDialect(XPATH1_URI);
                        }
                    } else {
                        InvalidFilterFaultType fault = new InvalidFilterFaultType();
                        throw new InvalidFilterFault("Unrecognized filter: " + (e != null ? e.getName() : f), fault);
                    }
                } else {
                    InvalidFilterFaultType fault = new InvalidFilterFaultType();
                    throw new InvalidFilterFault("Unrecognized filter: " + (e != null ? e.getName() : f), fault);
                }
            }
        }
        // Check policy
        if (subscribeRequest.getSubscriptionPolicy() != null) {
            for (Object p : subscribeRequest.getSubscriptionPolicy().getAny()) {
                JAXBElement e = null;
                if (p instanceof JAXBElement) {
                    e = (JAXBElement) p;
                    p = e.getValue();
                }
                if (p instanceof UseRaw) {
                    useRaw = true;
                } else {
                    UnrecognizedPolicyRequestFaultType fault = new UnrecognizedPolicyRequestFaultType();
                    throw new UnrecognizedPolicyRequestFault("Unrecognized policy: " + p, fault);
                }
            }
        }
        
        //Check subscriber address
        if(subscribeRequest.getSubscriberAddress() != null){
        	String value = subscribeRequest.getSubscriberAddress();
        	if(WsnProcessImpl.localtable == null){
        		subscriberAddress = value.trim();
        	}else{
        		int TopicNameCounter = 0;
        		int AddressCounter = 0;
        		for(ListItem listItem : WsnProcessImpl.localtable){
        			if(listItem.getTopicName().equals(convertTopic(topic)))
        				TopicNameCounter ++;
        			if(listItem.getSubscriberAddress().equals(value.trim()))
        				AddressCounter ++;
        		}
        		if((AddressCounter == 0) || (TopicNameCounter == 0))
        			subscriberAddress = value.trim();
        		else
        			System.out.println("subscribe failed! there is already a same subscription existed in wsn!");
        	}
        }
        
        // Check all parameters
        if (consumerReference == null) {
            SubscribeCreationFailedFaultType fault = new SubscribeCreationFailedFaultType();
            throw new SubscribeCreationFailedFault("Invalid ConsumerReference: null", fault);
        }
        // TODO check we can resolve endpoint
        if (topic == null) {
            InvalidFilterFaultType fault = new InvalidFilterFaultType();
            throw new InvalidFilterFault("Must specify a topic to subscribe on", fault);
        }
        if (contentFilter != null && !contentFilter.getDialect().equals(XPATH1_URI)) {
            InvalidMessageContentExpressionFaultType fault = new InvalidMessageContentExpressionFaultType();
            throw new InvalidMessageContentExpressionFault("Unsupported MessageContent dialect: '"
                    + contentFilter.getDialect() + "'", fault);
        }
        if (terminationTime != null) {
            UnacceptableInitialTerminationTimeFaultType fault = new UnacceptableInitialTerminationTimeFaultType();
            throw new UnacceptableInitialTerminationTimeFault("InitialTerminationTime is not supported", fault);
        }
    }
    
    public AbstractNotificationBroker getBroker() {
        return broker;
    }

    public void setBroker(AbstractNotificationBroker broker) {
        this.broker = broker;
    }
}
