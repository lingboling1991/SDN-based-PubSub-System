package org.oasis_open.docs.wsrf.bf_2;

import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType;
import org.oasis_open.docs.wsn.br_2.PublisherRegistrationRejectedFaultType;
import org.oasis_open.docs.wsn.br_2.ResourceNotDestroyedFaultType;
import org.oasis_open.docs.wsrf.r_2.ResourceUnavailableFaultType;
import org.oasis_open.docs.wsrf.r_2.ResourceUnknownFaultType;
import org.oasis_open.docs.wsrf.rp_2.*;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>Java class for BaseFaultType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="BaseFaultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Originator" type="{http://www.w3.org/2005/08/addressing}EndpointReferenceType" minOccurs="0"/>
 *         &lt;element name="ErrorCode" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="dialect" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                 &lt;anyAttribute processContents='skip'/>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Description" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="FaultCause" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' namespace='##other'/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseFaultType", propOrder = {
		"any",
		"timestamp",
		"originator",
		"errorCode",
		"description",
		"faultCause"
})
@XmlSeeAlso({
		QueryEvaluationErrorFaultType.class,
		InvalidResourcePropertyQNameFaultType.class,
		UnableToModifyResourcePropertyFaultType.class,
		UpdateResourcePropertiesRequestFailedFaultType.class,
		InvalidModificationFaultType.class,
		DeleteResourcePropertiesRequestFailedFaultType.class,
		UnknownQueryExpressionDialectFaultType.class,
		InsertResourcePropertiesRequestFailedFaultType.class,
		InvalidQueryExpressionFaultType.class,
		SetResourcePropertyRequestFailedFaultType.class,
		UnableToPutResourcePropertyDocumentFaultType.class,
		TopicExpressionDialectUnknownFaultType.class,
		UnableToGetMessagesFaultType.class,
		ResumeFailedFaultType.class,
		InvalidProducerPropertiesExpressionFaultType.class,
		SubscribeCreationFailedFaultType.class,
		UnableToDestroySubscriptionFaultType.class,
		UnrecognizedPolicyRequestFaultType.class,
		NotifyMessageNotSupportedFaultType.class,
		UnableToCreatePullPointFaultType.class,
		UnacceptableInitialTerminationTimeFaultType.class,
		InvalidTopicExpressionFaultType.class,
		UnsupportedPolicyRequestFaultType.class,
		PauseFailedFaultType.class,
		InvalidMessageContentExpressionFaultType.class,
		UnableToDestroyPullPointFaultType.class,
		MultipleTopicsSpecifiedFaultType.class,
		NoCurrentMessageOnTopicFaultType.class,
		InvalidFilterFaultType.class,
		TopicNotSupportedFaultType.class,
		UnacceptableTerminationTimeFaultType.class,
		ResourceUnknownFaultType.class,
		ResourceUnavailableFaultType.class,
		ResourceNotDestroyedFaultType.class,
		PublisherRegistrationRejectedFaultType.class,
		PublisherRegistrationFailedFaultType.class
})
public class BaseFaultType {

	@XmlAnyElement(lax = true)
	protected List<Object> any;
	@XmlElement(name = "Timestamp", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar timestamp;
	@XmlElement(name = "Originator")
	protected W3CEndpointReference originator;
	@XmlElement(name = "ErrorCode")
	protected ErrorCode errorCode;
	@XmlElement(name = "Description")
	protected List<Description> description;
	@XmlElement(name = "FaultCause")
	protected FaultCause faultCause;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
	 * Gets the value of the timestamp property.
	 *
	 * @return possible object is
	 * {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the value of the timestamp property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setTimestamp(XMLGregorianCalendar value) {
		this.timestamp = value;
	}

	/**
	 * Gets the value of the originator property.
	 *
	 * @return possible object is
	 * {@link W3CEndpointReference }
	 */
	public W3CEndpointReference getOriginator() {
		return originator;
	}

	/**
	 * Sets the value of the originator property.
	 *
	 * @param value allowed object is
	 *              {@link W3CEndpointReference }
	 */
	public void setOriginator(W3CEndpointReference value) {
		this.originator = value;
	}

	/**
	 * Gets the value of the errorCode property.
	 *
	 * @return possible object is
	 * {@link ErrorCode }
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the value of the errorCode property.
	 *
	 * @param value allowed object is
	 *              {@link ErrorCode }
	 */
	public void setErrorCode(ErrorCode value) {
		this.errorCode = value;
	}

	/**
	 * Gets the value of the description property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the description property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getDescription().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Description }
	 */
	public List<Description> getDescription() {
		if (description == null) {
			description = new ArrayList<Description>();
		}
		return this.description;
	}

	/**
	 * Gets the value of the faultCause property.
	 *
	 * @return possible object is
	 * {@link FaultCause }
	 */
	public FaultCause getFaultCause() {
		return faultCause;
	}

	/**
	 * Sets the value of the faultCause property.
	 *
	 * @param value allowed object is
	 *              {@link FaultCause }
	 */
	public void setFaultCause(FaultCause value) {
		this.faultCause = value;
	}

	/**
	 * Gets a map that contains attributes that aren't bound to any typed property on this class.
	 * <p/>
	 * <p/>
	 * the map is keyed by the name of the attribute and
	 * the value is the string value of the attribute.
	 * <p/>
	 * the map returned by this method is live, and you can add new attribute
	 * by updating the map directly. Because of this design, there's no setter.
	 *
	 * @return always non-null
	 */
	public Map<QName, String> getOtherAttributes() {
		return otherAttributes;
	}


	/**
	 * <p>Java class for anonymous complex type.
	 * <p/>
	 * <p>The following schema fragment specifies the expected content contained within this class.
	 * <p/>
	 * <pre>
	 * &lt;complexType>
	 *   &lt;simpleContent>
	 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
	 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
	 *     &lt;/extension>
	 *   &lt;/simpleContent>
	 * &lt;/complexType>
	 * </pre>
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"value"
	})
	public static class Description {

		@XmlValue
		protected String value;
		@XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
		@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
		@XmlSchemaType(name = "language")
		protected String lang;

		/**
		 * Gets the value of the value property.
		 *
		 * @return possible object is
		 * {@link String }
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the value of the value property.
		 *
		 * @param value allowed object is
		 *              {@link String }
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Gets the value of the lang property.
		 *
		 * @return possible object is
		 * {@link String }
		 */
		public String getLang() {
			return lang;
		}

		/**
		 * Sets the value of the lang property.
		 *
		 * @param value allowed object is
		 *              {@link String }
		 */
		public void setLang(String value) {
			this.lang = value;
		}

	}


	/**
	 * <p>Java class for anonymous complex type.
	 * <p/>
	 * <p>The following schema fragment specifies the expected content contained within this class.
	 * <p/>
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="dialect" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
	 *       &lt;anyAttribute processContents='skip'/>
	 *     &lt;/extension>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"content"
	})
	public static class ErrorCode {

		@XmlMixed
		@XmlAnyElement
		protected List<Object> content;
		@XmlAttribute(name = "dialect", required = true)
		@XmlSchemaType(name = "anyURI")
		protected String dialect;
		@XmlAnyAttribute
		private Map<QName, String> otherAttributes = new HashMap<QName, String>();

		/**
		 * Gets the value of the content property.
		 * <p/>
		 * <p/>
		 * This accessor method returns a reference to the live list,
		 * not a snapshot. Therefore any modification you make to the
		 * returned list will be present inside the JAXB object.
		 * This is why there is not a <CODE>set</CODE> method for the content property.
		 * <p/>
		 * <p/>
		 * For example, to add a new item, do as follows:
		 * <pre>
		 *    getContent().add(newItem);
		 * </pre>
		 * <p/>
		 * <p/>
		 * <p/>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 * {@link Element }
		 */
		public List<Object> getContent() {
			if (content == null) {
				content = new ArrayList<Object>();
			}
			return this.content;
		}

		/**
		 * Gets the value of the dialect property.
		 *
		 * @return possible object is
		 * {@link String }
		 */
		public String getDialect() {
			return dialect;
		}

		/**
		 * Sets the value of the dialect property.
		 *
		 * @param value allowed object is
		 *              {@link String }
		 */
		public void setDialect(String value) {
			this.dialect = value;
		}

		/**
		 * Gets a map that contains attributes that aren't bound to any typed property on this class.
		 * <p/>
		 * <p/>
		 * the map is keyed by the name of the attribute and
		 * the value is the string value of the attribute.
		 * <p/>
		 * the map returned by this method is live, and you can add new attribute
		 * by updating the map directly. Because of this design, there's no setter.
		 *
		 * @return always non-null
		 */
		public Map<QName, String> getOtherAttributes() {
			return otherAttributes;
		}

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
	 *         &lt;any processContents='lax' namespace='##other'/>
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
	public static class FaultCause {

		@XmlAnyElement(lax = true)
		protected Object any;

		/**
		 * Gets the value of the any property.
		 *
		 * @return possible object is
		 * {@link Object }
		 * {@link Element }
		 */
		public Object getAny() {
			return any;
		}

		/**
		 * Sets the value of the any property.
		 *
		 * @param value allowed object is
		 *              {@link Object }
		 *              {@link Element }
		 */
		public void setAny(Object value) {
			this.any = value;
		}

	}

}
