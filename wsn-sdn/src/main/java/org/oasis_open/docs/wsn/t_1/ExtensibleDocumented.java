package org.oasis_open.docs.wsn.t_1;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Java class for ExtensibleDocumented complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ExtensibleDocumented">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="documentation" type="{http://docs.oasis-open.org/wsn/t-1}Documentation" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensibleDocumented", propOrder = {
		"documentation"
})
@XmlSeeAlso({
		TopicSetType.class,
		TopicNamespaceType.class,
		TopicType.class
})
public abstract class ExtensibleDocumented {

	protected Documentation documentation;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * Gets the value of the documentation property.
	 *
	 * @return possible object is
	 * {@link Documentation }
	 */
	public Documentation getDocumentation() {
		return documentation;
	}

	/**
	 * Sets the value of the documentation property.
	 *
	 * @param value allowed object is
	 *              {@link Documentation }
	 */
	public void setDocumentation(Documentation value) {
		this.documentation = value;
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
