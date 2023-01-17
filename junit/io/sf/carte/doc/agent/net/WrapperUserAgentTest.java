/*

 Copyright (c) 2005-2023, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMStringList;

import io.sf.carte.doc.DocumentException;
import io.sf.carte.doc.agent.MockURLConnectionFactory;
import io.sf.carte.doc.style.css.CSSDocument;
import io.sf.carte.doc.style.css.om.DOMCSSStyleSheetFactory.WrapperUserAgent;
import io.sf.carte.doc.style.css.om.TestCSSStyleSheetFactory;

public class WrapperUserAgentTest {

	TestCSSStyleSheetFactory factory;
	WrapperUserAgent agent;

	@Before
	public void setUp() throws DocumentException, ParserConfigurationException {
		factory = new TestCSSStyleSheetFactory();
		agent = factory.getUserAgent();
		agent.setOriginPolicy(DefaultOriginPolicy.getInstance());
		DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docbuilder = dbFac.newDocumentBuilder();
		agent.setDocumentBuilder(docbuilder);
	}

	@Test
	public void getSelectedStyleSheetSet() throws MalformedURLException, IOException, DocumentException {
		WrapperUserAgent agent = factory.getUserAgent();
		CSSDocument doc = agent.readURL(new URL(MockURLConnectionFactory.SAMPLE_URL));
		assertNotNull(doc);
		assertEquals("http://www.example.com/", doc.getBaseURI());
		assertEquals(MockURLConnectionFactory.SAMPLE_URL, doc.getDocumentURI());
		assertEquals("same-origin", doc.getReferrerPolicy());
		DOMStringList list = doc.getStyleSheetSets();
		assertEquals(3, list.getLength());
		assertTrue(list.contains("Default"));
		assertTrue(list.contains("Alter 1"));
		assertTrue(list.contains("Alter 2"));
		assertEquals("Default", doc.getSelectedStyleSheetSet());
		doc.setSelectedStyleSheetSet("foo");
		assertEquals("Default", doc.getSelectedStyleSheetSet());
		doc.setSelectedStyleSheetSet("Alter 2");
		assertEquals("Alter 2", doc.getSelectedStyleSheetSet());
	}

	@Test
	public void getSelectedStyleSheetSetHeader() throws IOException, DocumentException {
		URL url = new URL("http://www.example.com/xhtml/htmlsample.html");
		factory.getConnectionFactory().setHeader("html", "Default-Style", "Alter 2");
		CSSDocument xhtmlDoc = agent.readURL(url);
		DOMStringList list = xhtmlDoc.getStyleSheetSets();
		assertEquals(3, list.getLength());
		assertTrue(list.contains("Default"));
		assertTrue(list.contains("Alter 1"));
		assertTrue(list.contains("Alter 2"));
		assertEquals("Alter 2", xhtmlDoc.getSelectedStyleSheetSet());
		xhtmlDoc.setSelectedStyleSheetSet("foo");
		assertEquals("Alter 2", xhtmlDoc.getSelectedStyleSheetSet());
		xhtmlDoc.setSelectedStyleSheetSet("Alter 1");
		assertEquals("Alter 1", xhtmlDoc.getSelectedStyleSheetSet());
	}

	@Test
	public void getSelectedStyleSheetSetMeta() throws IOException, DocumentException {
		URL url = new URL("http://www.example.com/xhtml/meta-default-style.html");
		factory.getConnectionFactory().registerURL(url.toExternalForm(),
				"meta-default-style.html");
		CSSDocument xhtmlDoc = agent.readURL(url);
		DOMStringList list = xhtmlDoc.getStyleSheetSets();
		assertEquals(3, list.getLength());
		assertTrue(list.contains("Default"));
		assertTrue(list.contains("Alter 1"));
		assertTrue(list.contains("Alter 2"));
		assertEquals("Alter 1", xhtmlDoc.getSelectedStyleSheetSet());
		xhtmlDoc.setSelectedStyleSheetSet("foo");
		assertEquals("Alter 1", xhtmlDoc.getSelectedStyleSheetSet());
		xhtmlDoc.setSelectedStyleSheetSet("Alter 2");
		assertEquals("Alter 2", xhtmlDoc.getSelectedStyleSheetSet());
	}

	/*
	 * HTML 4.01, § 14.3.2:
	 * "If two or more META declarations or HTTP headers specify the preferred style
	 * sheet, the last one takes precedence. HTTP headers are considered to occur
	 * earlier than the document HEAD for this purpose."
	 */
	@Test
	public void getSelectedStyleSheetSetMetaOverride() throws IOException, DocumentException {
		URL url = new URL("http://www.example.com/xhtml/meta-default-style.html");
		MockURLConnectionFactory connFactory = factory.getConnectionFactory();
		connFactory.registerURL(url.toExternalForm(), "meta-default-style.html");
		connFactory.setHeader("html", "Default-Style", "Alter 2");
		CSSDocument xhtmlDoc = agent.readURL(url);
		DOMStringList list = xhtmlDoc.getStyleSheetSets();
		assertEquals(3, list.getLength());
		assertTrue(list.contains("Default"));
		assertTrue(list.contains("Alter 1"));
		assertTrue(list.contains("Alter 2"));
		assertEquals("Alter 1", xhtmlDoc.getSelectedStyleSheetSet());
		xhtmlDoc.setSelectedStyleSheetSet("foo");
		assertEquals("Alter 1", xhtmlDoc.getSelectedStyleSheetSet());
		xhtmlDoc.setSelectedStyleSheetSet("Alter 2");
		assertEquals("Alter 2", xhtmlDoc.getSelectedStyleSheetSet());
	}

}
