package org.oasis_open.docs.wsrf.rp_2;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ResourcePropertyChangeFailureType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ResourcePropertyChangeFailureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CurrentValue" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RequestedValue" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="Restored" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourcePropertyChangeFailureType", propOrder = {
		"currentValue",
		"requestedValue"
})
public class ResourcePropertyChangeFailureType {

	@XmlElement(name = "CurrentValue")
	protected ResourcePropertyChangeFailureType.CurrentValue currentValue;
	@XmlElement(name = "RequestedValue")
	protected ResourcePropertyChangeFailureType.RequestedValue requestedValue;
	@XmlAttribute(name = "Restored")
	protected Boolean restored;

	/**
	 * Gets the value of the currentValue property.
	 *
	 * @return possible object is
	 * {@link ResourcePropertyChangeFailureType.CurrentValue }
	 */
	public ResourcePropertyChangeFailureType.CurrentValue getCurrentValue() {
		return currentValue;
	}

	/**
	 * Sets the value of the currentValue property.
	 *
	 * @param value allowed object is
	 *              {@link ResourcePropertyChangeFailureType.CurrentValue }
	 */
	public void setCurrentValue(ResourcePropertyChangeFailureType.CurrentValue value) {
		this.currentValue = value;
	}

	/**
	 * Gets the value of the requestedValue property.
	 *
	 * @return possible object is
	 * {@link ResourcePropertyChangeFailureType.RequestedValue }
	 */
	public ResourcePropertyChangeFailureType.RequestedValue getRequestedValue() {
		return requestedValue;
	}

	/**
	 * Sets the value of the requestedValue property.
	 *
	 * @param value allowed object is
	 *              {@link ResourcePropertyChangeFailureType.RequestedValue }
	 */
	public void setRequestedValue(ResourcePropertyChangeFailureType.RequestedValue value) {
		this.requestedValue = value;
	}

	/**
	 * Gets the value of the restored property.
	 *
	 * @return possible object is
	 * {@link Boolean }
	 */
	public Boolean isRestored() {
		return restored;
	}

	/**
	 * Sets the value of the restored property.
	 *
	 * @param value allowed object is
	 *              {@link Boolean }
	 */
	public void setRestored(Boolean value) {
		this.restored = value;
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
	 *         &lt;any maxOccurs="unbounded"/>
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
	public static class CurrentValue {

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
		 */
		public List<Object> getAny() {
			if (any == null) {
				any = new ArrayList<Object>();
			}
			return this.any;
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
	 *         &lt;any maxOccurs="unbounded"/>
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
	public static class RequestedValue {

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
		 */
		public List<Object> getAny() {
			if (any == null) {
				any = new ArrayList<Object>();
			}
			return this.any;
		}

	}

}
