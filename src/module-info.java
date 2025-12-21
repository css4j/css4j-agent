/*

 Copyright (c) 2005-2025, Carlos Amengual.

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

// SPDX-License-Identifier: BSD-2-Clause OR BSD-3-Clause

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
