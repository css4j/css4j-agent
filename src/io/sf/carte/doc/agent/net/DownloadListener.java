/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import io.sf.carte.doc.style.css.CSSDocument;

/**
 * Listener for the downloading of document content.
 * 
 * @author Carlos Amengual
 *
 */
public interface DownloadListener<C> {

	/**
	 * Notifies the download of content.
	 * 
	 * @param content
	 *            the content, in a native object.
	 */
	public void doContentDownloaded(C content);

	/**
	 * Notifies a failed download.
	 *
	 */
	public void doFailedDownload();

	/**
	 * Gets the content, put into a native object.
	 * 
	 * @return the downloaded content.
	 */
	public C getNativeContent();

	public CSSDocument getDocument();

}