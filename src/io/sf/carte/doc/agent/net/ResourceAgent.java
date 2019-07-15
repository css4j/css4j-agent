/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://carte.sourceforge.io/css4j/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

/**
 * Agent for retrieving and handling embeddable device-specific 
 * resources (like images).
 * 
 * @author Carlos Amengual
 * 
 * @param <C> The device-specific resource class (typically an image).
 * @param <U> The object containing source information (typically an URL).
 */
public interface ResourceAgent<C,U> {

	/**
	 * Registers a source for downloading with this user agent.
	 * 
	 * @param url the source to be downloaded.
	 * @return the downloader for the source.
	 */
	public ResourceDownloader<C> download(U url);

	/**
	 * Adds a download listener for asynchronous retrieval of resources 
	 * (embedded images, etc.)
	 * <p>
	 * In the process, it creates a resource downloader object.
	 * 
	 * @param url the source to download from.
	 * @param listener the download listener.
	 */
	public void addDownloadListener(U url, DownloadListener<C> listener);

	/**
	 * Gets the resource downloader for the given source, creating one if 
	 * none exists.
	 * 
	 * @param url the source.
	 * @return the resource downloader.
	 */
	public ResourceDownloader<C> getResourceDownloader(U url);

}