/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://carte.sourceforge.io/css4j/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

import io.sf.carte.doc.agent.IllegalOriginException;
import io.sf.carte.doc.style.css.CSSDocument;

/**
 * Asynchronous downloading of an embedded resource of a given type.
 * 
 * @author Carlos Amengual
 *
 */
abstract public class ResourceDownloader<C> extends Thread {

	private URL url;
	protected C nativeContent = null;
	protected List<DownloadListener<C>> listeners = new LinkedList<DownloadListener<C>>();
	private String contentType = null;
	private boolean done = false;

	protected ResourceDownloader(URL url) {
		this.url = url;
	}

	protected ResourceDownloader(CSSFontFaceRule rule) throws IllegalArgumentException {
		URL[] urlist = extractURL(rule.getStyle());
		if (urlist == null) {
			throw new IllegalArgumentException("No URLs to download font");
		}
		this.url = urlist[0];
	}

	private static URL[] extractURL(CSSStyleDeclaration style) throws IllegalArgumentException {
		CSSValue src = style.getPropertyCSSValue("src");
		if (src == null) {
			return null;
		}
		return extractURL(src);
	}

	private static URL[] extractURL(CSSValue src) {
		short type = src.getCssValueType();
		if (type == CSSValue.CSS_PRIMITIVE_VALUE) {
			if (((CSSPrimitiveValue) src).getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
				URL url;
				try {
					url = new URL(((CSSPrimitiveValue) src).getStringValue());
				} catch (Exception e) {
					throw new IllegalArgumentException("Bad URL to download font", e);
				}
				return new URL[]{url};
			}
		} else if (type == CSSValue.CSS_VALUE_LIST) {
			LinkedList<URL> urlist = new LinkedList<URL>();
			CSSValueList list = (CSSValueList) src;
			for (int i = 0; i < list.getLength(); i++) {
				URL[] url = extractURL(list.item(i));
				if (url != null) {
					for (int j = 0; j < url.length; j++) {
						urlist.add(url[j]);
					}
				}
			}
			if (!urlist.isEmpty()) {
				return urlist.toArray(new URL[0]);
			}
		}
		return null;
	}

	public URL getURL() {
		return url;
	}

	public void addListener(DownloadListener<C> listener) {
		if (done) {
			if (nativeContent == null) {
				listener.doFailedDownload();
			} else {
				listener.doContentDownloaded(nativeContent);
			}
		} else {
			this.listeners.add(listener);
		}
	}

	public boolean isDone() {
		return done;
	}

	@Override
	public void run() {
		try {
			InputStream is = openStream();
			if (is == null) {
				notifyFailure(new IOException("Cannot open URL " + getURL().toExternalForm()));
			}
			readContent(is);
			is.close();
			done = true;
			notifyDownload();
		} catch (IOException e) {
			notifyFailure(e);
		}
		listeners.clear();
	}

	protected InputStream openStream() throws IOException {
		CSSDocument doc = null;
		if (!listeners.isEmpty()) {
			int nl = listeners.size();
			for (int i = 0; i < nl; i++) {
				doc = listeners.get(i).getDocument();
				if (doc != null) {
					break;
				}
			}
		}
		URLConnection con;
		if (doc != null) {
			if (doc.isSafeOrigin(getURL())) {
				con = doc.openConnection(getURL());
			} else {
				throw new IllegalOriginException("Illegal origin: " + getURL().toExternalForm());
			}
		} else {
			con = getURL().openConnection();
		}
		contentType = con.getContentType();
		return con.getInputStream();
	}

	public String getContentType() {
		return contentType;
	}

	/**
	 * Reads content from a stream and transforms it to native content.
	 * 
	 * @param is
	 *            the stream with the content.
	 * @throws IOException
	 *             if an error occurs when reading the stream.
	 */
	abstract protected void readContent(InputStream is) throws IOException;

	abstract protected ResourceAgent<C,?> getResourceAgent();

	protected void notifyDownload() {
		Iterator<DownloadListener<C>> it = listeners.iterator();
		while (it.hasNext()) {
			it.next().doContentDownloaded(nativeContent);
		}
		nativeContent = null;
	}

	protected void notifyFailure(Exception e) {
		Iterator<DownloadListener<C>> it = listeners.iterator();
		while (it.hasNext()) {
			it.next().doFailedDownload();
		}
	}

	public C getNativeContent() {
		return nativeContent;
	}

}
