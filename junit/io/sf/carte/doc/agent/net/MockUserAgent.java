/*

 Copyright (c) 2005-2024, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumSet;

import io.sf.carte.doc.agent.DeviceFactory;
import io.sf.carte.doc.agent.MockURLConnectionFactory;
import io.sf.carte.doc.style.css.nsac.Parser.Flag;
import io.sf.carte.doc.style.css.om.DummyDeviceFactory;

class MockUserAgent extends DefaultUserAgent {

	private static final long serialVersionUID = 1L;

	private final MockURLConnectionFactory urlFactory = new MockURLConnectionFactory();

	public MockUserAgent(EnumSet<Flag> parserFlags, boolean setDefaultSheet) {
		super(parserFlags, setDefaultSheet);
		DeviceFactory deviceFactory = new DummyDeviceFactory();
		getDOMImplementation().setDeviceFactory(deviceFactory);
	}

	public MockURLConnectionFactory getConnectionFactory() {
		return urlFactory;
	}

	@Override
	protected URLConnection openConnection(URL url, long creationDate) throws IOException {
		return urlFactory.createConnection(url);
	}

}
