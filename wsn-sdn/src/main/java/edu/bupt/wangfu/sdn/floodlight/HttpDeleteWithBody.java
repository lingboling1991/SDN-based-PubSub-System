package edu.bupt.wangfu.sdn.floodlight;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

@NotThreadSafe

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

	public static final String METHOD_NAME = "DELETE";

	public HttpDeleteWithBody(final String uri) {

		super();

		setURI(URI.create(uri));

	}

	public HttpDeleteWithBody(final URI uri) {

		super();

		setURI(uri);

	}

	public HttpDeleteWithBody() {
		super();
	}

	public String getMethod() {
		return METHOD_NAME;
	}

}
