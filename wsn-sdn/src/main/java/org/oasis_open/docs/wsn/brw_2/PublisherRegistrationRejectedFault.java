package org.oasis_open.docs.wsn.brw_2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.3.2
 * 2012-11-13T15:21:18.393+08:00
 * Generated source version: 2.3.2
 */

@WebFault(name = "PublisherRegistrationRejectedFault", targetNamespace = "http://docs.oasis-open.org/wsn/br-2")
public class PublisherRegistrationRejectedFault extends Exception {
	public static final long serialVersionUID = 20121113152118L;

	private org.oasis_open.docs.wsn.br_2.PublisherRegistrationRejectedFaultType publisherRegistrationRejectedFault;

	public PublisherRegistrationRejectedFault() {
		super();
	}

	public PublisherRegistrationRejectedFault(String message) {
		super(message);
	}

	public PublisherRegistrationRejectedFault(String message, Throwable cause) {
		super(message, cause);
	}

	public PublisherRegistrationRejectedFault(String message, org.oasis_open.docs.wsn.br_2.PublisherRegistrationRejectedFaultType publisherRegistrationRejectedFault) {
		super(message);
		this.publisherRegistrationRejectedFault = publisherRegistrationRejectedFault;
	}

	public PublisherRegistrationRejectedFault(String message, org.oasis_open.docs.wsn.br_2.PublisherRegistrationRejectedFaultType publisherRegistrationRejectedFault, Throwable cause) {
		super(message, cause);
		this.publisherRegistrationRejectedFault = publisherRegistrationRejectedFault;
	}

	public org.oasis_open.docs.wsn.br_2.PublisherRegistrationRejectedFaultType getFaultInfo() {
		return this.publisherRegistrationRejectedFault;
	}
}
