package org.oasis_open.docs.wsn.b_2;

import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ConsumerReference" type="{http://www.w3.org/2005/08/addressing}EndpointReferenceType"/>
 *         &lt;element name="Filter" type="{http://docs.oasis-open.org/wsn/b-2}FilterType" minOccurs="0"/>
 *         &lt;element name="InitialTerminationTime" type="{http://docs.oasis-open.org/wsn/b-2}AbsoluteOrRelativeTimeType" minOccurs="0"/>
 *         &lt;element name="SubscriptionPolicy" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SubscriberAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"consumerReference",
		"filter",
		"initialTerminationTime",
		"subscriptionPolicy",
		"subscriberAddress",
		"any"
})
@XmlRootElement(name = "Subscribe")
public class Subscribe {

	@XmlElement(name = "ConsumerReference", required = true)
	protected W3CEndpointReference consumerReference;
	@XmlElement(name = "Filter")
	protected FilterType filter;
	@XmlElementRef(name = "InitialTerminationTime", namespace = "http://docs.oasis-open.org/wsn/b-2", type = JAXBElement.class)
	protected JAXBElement<String> initialTerminationTime;
	@XmlElement(name = "SubscriptionPolicy")
	protected SubscriptionPolicy subscriptionPolicy;
	@XmlElement(name = "SubscriberAddress")
	protected String subscriberAddress;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the consumerReference property.
	 *
	 * @return possible object is
	 * {@link W3CEndpointReference }
	 */
	public W3CEndpointReference getConsumerReference() {
		return consumerReference;
	}

	/**
	 * Sets the value of the consumerReference property.
	 *
	 * @param value allowed object is
	 *              {@link W3CEndpointReference }
	 */
	public void setConsumerReference(W3CEndpointReference value) {
		this.consumerReference = value;
	}

	/**
	 * Gets the value of the filter property.
	 *
	 * @return possible object is
	 * {@link FilterType }
	 */
	public FilterType getFilter() {
		return filter;
	}

	/**
	 * Sets the value of the filter property.
	 *
	 * @param value allowed object is
	 *              {@link FilterType }
	 */
	public void setFilter(FilterType value) {
		this.filter = value;
	}

	/**
	 * Gets the value of the initialTerminationTime property.
	 *
	 * @return possible object is
	 * {@link JAXBElement }{@code <}{@link String }{@code >}
	 */
	public JAXBElement<String> getInitialTerminationTime() {
		return initialTerminationTime;
	}

	/**
	 * Sets the value of the initialTerminationTime property.
	 *
	 * @param value allowed object is
	 *              {@link JAXBElement }{@code <}{@link String }{@code >}
	 */
	public void setInitialTerminationTime(JAXBElement<String> value) {
		this.initialTerminationTime = ((JAXBElement<String>) value);
	}

	/**
	 * Gets the value of the subscriptionPolicy property.
	 *
	 * @return possible object is
	 * {@link SubscriptionPolicy }
	 */
	public SubscriptionPolicy getSubscriptionPolicy() {
		return subscriptionPolicy;
	}

	/**
	 * Sets the value of the subscriptionPolicy property.
	 *
	 * @param value allowed object is
	 *              {@link SubscriptionPolicy }
	 */
	public void setSubscriptionPolicy(SubscriptionPolicy value) {
		this.subscriptionPolicy = value;
	}

	/**
	 * Gets the value of the subscriberAddress property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getSubscriberAddress() {
		return subscriberAddress;
	}

	/**
	 * Sets the value of the subscriberAddress property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setSubscriberAddress(String value) {
		this.subscriberAddress = value;
	}

	/**
	 * Gets the value of the any property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the any property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getAny().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Object }
	 * {@link Element }
	 */
	public List<Object> getAny() {
		if (any == null) {
			any = new ArrayList<Object>();
		}
		return this.any;
	}


	/**
	 * <p>Java class for anonymous complex type.
	 * <p/>
	 * <p>The following schema fragment specifies the expected content contained within this class.
	 * <p/>
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"any"
	})
	public static class SubscriptionPolicy {

		@XmlAnyElement(lax = true)
		protected List<Object> any;

		/**
		 * Gets the value of the any property.
		 * <p/>
		 * <p/>
		 * This accessor method returns a reference to the live list,
		 * not a snapshot. Therefore any modification you make to the
		 * returned list will be present inside the JAXB object.
		 * This is why there is not a <CODE>set</CODE> method for the any property.
		 * <p/>
		 * <p/>
		 * For example, to add a new item, do as follows:
		 * <pre>
		 *    getAny().add(newItem);
		 * </pre>
		 * <p/>
		 * <p/>
		 * <p/>
		 * Objects of the following type(s) are allowed in the list
		 * {@link Object }
		 * {@link Element }
		 */
		public List<Object> getAny() {
			if (any == null) {
				any = new ArrayList<Object>();
			}
			return this.any;
		}

	}

}
