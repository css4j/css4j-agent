/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;

import io.sf.carte.doc.DocumentException;
import io.sf.carte.doc.agent.MockURLFactory;
import io.sf.carte.doc.style.css.CSSDocument;
import io.sf.carte.doc.style.css.om.DOMCSSStyleSheetFactory;
import io.sf.carte.doc.style.css.om.DOMCSSStyleSheetFactory.WrapperUserAgent;
import io.sf.carte.doc.style.css.om.TestCSSStyleSheetFactory;

public class WrapperUserAgentTest {

	private static DOMCSSStyleSheetFactory factory = new TestCSSStyleSheetFactory();

	@Test
	public void testGetUserAgent() throws MalformedURLException, IOException, DocumentException {
		WrapperUserAgent agent = factory.getUserAgent();
		assertNotNull(agent);
		agent.setOriginPolicy(DefaultOriginPolicy.getInstance());
		DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docbuilder;
		try {
			docbuilder = dbFac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new DocumentException("Error creating a document builder", e);
		}
		agent.setDocumentBuilder(docbuilder);
		CSSDocument doc = agent.readURL(new URL(MockURLFactory.SAMPLE_URL));
		assertNotNull(doc);
		assertEquals("http://www.example.com/", doc.getBaseURI());
		assertEquals(MockURLFactory.SAMPLE_URL, doc.getDocumentURI());
		assertEquals("same-origin", doc.getReferrerPolicy());
	}

}
