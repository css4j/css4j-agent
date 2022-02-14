/*

 Copyright (c) 2005-2022, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumSet;

import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.sf.carte.doc.DocumentException;
import io.sf.carte.doc.agent.AbstractUserAgent;
import io.sf.carte.doc.agent.AgentUtil;
import io.sf.carte.doc.dom.CSSDOMImplementation;
import io.sf.carte.doc.dom.DOMDocument;
import io.sf.carte.doc.dom.HTMLDocument;
import io.sf.carte.doc.dom.XMLDocumentBuilder;
import io.sf.carte.doc.style.css.CSSDocument;
import io.sf.carte.doc.style.css.nsac.Parser2;
import io.sf.carte.doc.xml.dtd.DefaultEntityResolver;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlParser;

/**
 * Default User Agent.
 * <p>
 * 
 * @author Carlos Amengual
 */
public class DefaultUserAgent extends AbstractUserAgent {

	private final CSSDOMImplementation domImpl;
	private EntityResolver resolver = createEntityResolver();

	protected DefaultUserAgent(EnumSet<Parser2.Flag> parserFlags, boolean setDefaultSheet) {
		super(parserFlags);
		setOriginPolicy(DefaultOriginPolicy.getInstance());
		domImpl = new MyDOMImplementation(getParserFlags());
		if (setDefaultSheet) {
			domImpl.setDefaultHTMLUserAgentSheet();
		}
	}

	/**
	 * Creates an user agent that reads HTML documents.
	 * 
	 * @param setDefaultSheet
	 *            if true, a default user agent HTML style sheet is loaded.
	 * @return the user agent.
	 */
	public static DefaultUserAgent createUserAgent(EnumSet<Parser2.Flag> parserFlags, boolean setDefaultSheet) {
		return new DefaultUserAgent(parserFlags, setDefaultSheet);
	}

	protected EntityResolver createEntityResolver() {
		return new DefaultEntityResolver();
	}

	/**
	 * Gets the DOM implementation used by this user agent.
	 * 
	 * @return the DOM implementation.
	 */
	public CSSDOMImplementation getDOMImplementation() {
		return domImpl;
	}

	/**
	 * Sets the entity resolver to be used when parsing documents.
	 * 
	 * @param resolver
	 *            the entity resolver.
	 */
	@Override
	public void setEntityResolver(EntityResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * Reads and parses a markup document located at the given URL.
	 * 
	 * @param url
	 *            the URL that points to the document.
	 * @return the DOMDocument.
	 * @throws IOException
	 *             if there is an I/O problem reading the URL.
	 * @throws io.sf.carte.doc.DocumentException
	 *             if there is a problem parsing the document.
	 */
	@Override
	public DOMDocument readURL(URL url) throws IOException, io.sf.carte.doc.DocumentException {
		long time = System.currentTimeMillis();
		URLConnection con = openConnection(url, time);
		con.connect();
		String conType = con.getContentType();
		String contentEncoding = con.getContentEncoding();
		InputStream is = null;
		MyDocument htmlDoc = null;
		boolean isHtml = true;
		if (conType != null) {
			String mimeType;
			int i = conType.indexOf(';');
			if (i == -1) {
				mimeType = conType.trim();
			} else {
				mimeType = conType.substring(0, i).trim();
			}
			isHtml = mimeType.equals("text/html");
		}
		XMLDocumentBuilder builder = new XMLDocumentBuilder(domImpl);
		if (isHtml) {
			HtmlParser parser = new HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
			parser.setReportingDoctype(true);
			parser.setCommentPolicy(XmlViolationPolicy.ALLOW);
			parser.setXmlnsPolicy(XmlViolationPolicy.ALLOW);
			builder.setHTMLProcessing(true);
			builder.setXMLReader(parser);
		} else {
			builder.setIgnoreElementContentWhitespace(true);
			builder.setEntityResolver(resolver);
		}
		try {
			is = openInputStream(con);
			InputSource source = new InputSource(AgentUtil.inputStreamToReader(is, conType, contentEncoding, "utf-8"));
			htmlDoc = (MyDocument) builder.parse(source);
		} catch (IOException e) {
			throw e;
		} catch (SAXException e) {
			throw new DocumentException("Error parsing HTML document at " + url.toExternalForm(), e);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		htmlDoc.setDocumentURI(url.toExternalForm());
		htmlDoc.setLoadingTime(time);
		// Check for preferred style
		String defStyle = con.getHeaderField("Default-Style");
		NodeList list = htmlDoc.getElementsByTagName("meta");
		int listL = list.getLength();
		for (int i = listL - 1; i >= 0; i--) {
			if ("default-style".equalsIgnoreCase(((Element) list.item(i)).getAttribute("http-equiv"))) {
				String metaDefStyle = ((Element) list.item(i)).getAttribute("content");
				if (metaDefStyle.length() != 0) {
					// Per HTML4 spec § 14.3.2:
					// "If two or more META declarations or HTTP headers specify 
					//  the preferred style sheet, the last one takes precedence."
					defStyle = metaDefStyle;
				}
			}
		}
		if (defStyle != null) {
			htmlDoc.setSelectedStyleSheetSet(defStyle);
		}
		// Referrer Policy
		String referrerPolicy = con.getHeaderField("Referrer-Policy");
		if (referrerPolicy != null) {
			htmlDoc.setReferrerPolicyHeader(referrerPolicy);
		}
		// Read cookies and close connection, if appropriate
		if (con instanceof HttpURLConnection) {
			HttpURLConnection hcon = (HttpURLConnection) con;
			readCookies(hcon, time);
			hcon.disconnect();
		}
		return (DOMDocument) htmlDoc;
	}

	protected InputStream openInputStream(URLConnection con) throws IOException {
		return con.getInputStream();
	}

	interface MyDocument extends CSSDocument {
		void setLoadingTime(long time);

		void setReferrerPolicyHeader(String policy);
	}

	class MyDOMImplementation extends CSSDOMImplementation {

		MyDOMImplementation(EnumSet<Parser2.Flag> parserFlags) {
			super(parserFlags);
		}

		@Override
		protected DOMDocument createXMLDocument(DocumentType doctype) {
			return new MyXMLDocument(doctype);
		}

		@Override
		protected HTMLDocument createHTMLDocument(DocumentType doctype) {
			return new MyHTMLDocument(doctype);
		}

		class MyHTMLDocument extends HTMLDocument implements MyDocument {

			private long loadingTime;

			public MyHTMLDocument(DocumentType documentType) {
				super(documentType);
			}

			@Override
			public CSSDOMImplementation getImplementation() {
				return MyDOMImplementation.this;
			}

			/**
			 * Opens a connection for the given URL.
			 * 
			 * @param url
			 *            the URL to open a connection to.
			 * @return the URL connection.
			 * @throws IOException
			 *             if the connection could not be opened.
			 */
			@Override
			public URLConnection openConnection(URL url) throws IOException {
				return DefaultUserAgent.this.openConnection(url, loadingTime);
			}

			@Override
			public boolean isVisitedURI(String href) {
				try {
					return isVisitedURL(getURL(href));
				} catch (MalformedURLException e) {
					return false;
				}
			}

			/**
			 * Set the time at which this document was loaded from origin.
			 * 
			 * @param time
			 *            the time of loading, in milliseconds.
			 */
			@Override
			public void setLoadingTime(long time) {
				this.loadingTime = time;
			}

			@Override
			public void setReferrerPolicyHeader(String policy) {
				super.setReferrerPolicyHeader(policy);
			}

			@Override
			protected CSSDOMImplementation getStyleSheetFactory() {
				return MyDOMImplementation.this;
			}

		}

		class MyXMLDocument extends DOMDocument implements MyDocument {

			private long loadingTime;

			public MyXMLDocument(DocumentType documentType) {
				super(documentType);
			}

			@Override
			public CSSDOMImplementation getImplementation() {
				return MyDOMImplementation.this;
			}

			@Override
			public URLConnection openConnection(URL url) throws IOException {
				return DefaultUserAgent.this.openConnection(url, loadingTime);
			}

			@Override
			public boolean isVisitedURI(String href) {
				try {
					return isVisitedURL(getURL(href));
				} catch (MalformedURLException e) {
					return false;
				}
			}

			/**
			 * Set the time at which this document was loaded from origin.
			 * 
			 * @param time
			 *            the time of loading, in milliseconds.
			 */
			@Override
			public void setLoadingTime(long time) {
				this.loadingTime = time;
			}

			@Override
			public void setReferrerPolicyHeader(String policy) {
				super.setReferrerPolicyHeader(policy);
			}

			@Override
			protected CSSDOMImplementation getStyleSheetFactory() {
				return MyDOMImplementation.this;
			}

		}

	}

}
