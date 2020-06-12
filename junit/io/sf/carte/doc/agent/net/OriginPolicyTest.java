/*

 Copyright (c) 2005-2020, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OriginPolicyTest {

	private static DefaultOriginPolicy policy = DefaultOriginPolicy.getInstance();

	@Test
	public void testIsTopLevelSuffix() {
		assertTrue(policy.isTopLevelSuffix("com"));
		assertTrue(policy.isTopLevelSuffix("uk"));
		assertTrue(policy.isTopLevelSuffix("co.uk"));
		assertTrue(policy.isTopLevelSuffix("com.es"));
		assertTrue(policy.isTopLevelSuffix("nom.br"));
		assertTrue(policy.isTopLevelSuffix("ck"));
		assertTrue(policy.isTopLevelSuffix("edu.ck"));
		assertFalse(policy.isTopLevelSuffix("ibm.com"));
		assertFalse(policy.isTopLevelSuffix("nic.uk"));
		assertFalse(policy.isTopLevelSuffix("educ.ar"));
		assertFalse(policy.isTopLevelSuffix("example.gob.ar"));
		assertFalse(policy.isTopLevelSuffix("example.co.uk"));
		assertFalse(policy.isTopLevelSuffix("example.es"));
		assertFalse(policy.isTopLevelSuffix("www.ck"));
	}

	@Test
	public void testDomainFromHostname() {
		assertEquals("example.com", policy.domainFromHostname("www.example.com"));
		assertEquals("example.co.uk", policy.domainFromHostname("www.example.co.uk"));
		assertEquals("example.co.uk", policy.domainFromHostname("example.co.uk"));
		assertEquals("example.co.uk", policy.domainFromHostname("www.sub.example.co.uk"));
		assertEquals("example.edu.ck", policy.domainFromHostname("www.example.edu.ck"));
		assertEquals("www.ck", policy.domainFromHostname("www.ck"));
	}

}
