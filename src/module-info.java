/*

 Copyright (c) 2005-2025, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

/**
 * A simple implementation for CSS4J user agent functionality, allowing cookie
 * management when retrieving documents and resources.
 */
module io.sf.carte.css4j.agent.net {
	exports io.sf.carte.doc.agent.net;

	requires transitive io.sf.carte.css4j;
	requires static io.sf.carte.xml.dtd;
	requires htmlparser;
}
