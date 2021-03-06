package org.oasis_open.docs.wsrf.rw_2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.3.2
 * 2012-11-13T15:21:18.356+08:00
 * Generated source version: 2.3.2
 */

@WebFault(name = "ResourceUnknownFault", targetNamespace = "http://docs.oasis-open.org/wsrf/r-2")
public class ResourceUnknownFault extends Exception {
	public static final long serialVersionUID = 20121113152118L;

	private org.oasis_open.docs.wsrf.r_2.ResourceUnknownFaultType resourceUnknownFault;

	public ResourceUnknownFault() {
		super();
	}

	public ResourceUnknownFault(String message) {
		super(message);
	}

	public ResourceUnknownFault(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceUnknownFault(String message, org.oasis_open.docs.wsrf.r_2.ResourceUnknownFaultType resourceUnknownFault) {
		super(message);
		this.resourceUnknownFault = resourceUnknownFault;
	}

	public ResourceUnknownFault(String message, org.oasis_open.docs.wsrf.r_2.ResourceUnknownFaultType resourceUnknownFault, Throwable cause) {
		super(message, cause);
		this.resourceUnknownFault = resourceUnknownFault;
	}

	public org.oasis_open.docs.wsrf.r_2.ResourceUnknownFaultType getFaultInfo() {
		return this.resourceUnknownFault;
	}
}
