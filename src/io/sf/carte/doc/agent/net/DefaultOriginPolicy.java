/*

 Copyright (c) 2005-2019, Carlos Amengual.

 SPDX-License-Identifier: BSD-3-Clause

 Licensed under a BSD-style License. You can find the license here:
 https://css4j.github.io/LICENSE.txt

 */

package io.sf.carte.doc.agent.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.sf.carte.doc.agent.OriginPolicy;

/**
 * Origin policy.
 * <p>
 * 
 * @author Carlos Amengual
 */
public class DefaultOriginPolicy implements OriginPolicy {

	private static final List<String> TOP_LEVEL_SUFFIX;
	private static final List<String> SUFFIX_EXCEPTIONS;
	private static final List<String> SUFFIX_WILDCARDS;

	static {
		TOP_LEVEL_SUFFIX = new ArrayList<String>();
		SUFFIX_EXCEPTIONS = new ArrayList<String>();
		SUFFIX_WILDCARDS = new ArrayList<String>();
		readSuffixes();
	}

	private DefaultOriginPolicy() {
		super();
	}

	private static void readSuffixes() {
		BufferedReader re = new BufferedReader(new InputStreamReader(
				openStream("/io/sf/carte/doc/agent/net/public_suffix_list.dat"), StandardCharsets.UTF_8));
		String line = null;
		try {
			while ((line = re.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("//")) {
					if (line.charAt(0) == '*') {
						SUFFIX_WILDCARDS.add(line.substring(2));
					} else if (line.charAt(0) == '!') {
						SUFFIX_EXCEPTIONS.add(line.substring(1));
					} else {
						TOP_LEVEL_SUFFIX.add(line);
					}
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				re.close();
			} catch (IOException e) {
			}
		}
	}

	private static InputStream openStream(final String fileName) {
		return java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<InputStream>() {
			@Override
			public InputStream run() {
				return DefaultOriginPolicy.class.getResourceAsStream(fileName);
			}
		});
	}

	private static class DefaultOriginPolicyHolder {
		static final DefaultOriginPolicy HOLDER = new DefaultOriginPolicy();
	}

	public static DefaultOriginPolicy getInstance() {
		return DefaultOriginPolicyHolder.HOLDER;
	}

	@Override
	public boolean isTopLevelSuffix(String possibleTld) {
		if (TOP_LEVEL_SUFFIX.contains(possibleTld) || SUFFIX_WILDCARDS.contains(possibleTld)) {
			return true;
		}
		int idx = possibleTld.indexOf('.');
		if (idx > 0 && SUFFIX_WILDCARDS.contains(possibleTld.substring(idx + 1))
				&& !SUFFIX_EXCEPTIONS.contains(possibleTld)) {
			return true;
		}
		return false;
	}

	@Override
	public String domainFromHostname(String host) {
		CharSequence domain = host;
		int count = host.length();
		int idx = count - 1;
		while (idx >= 0) {
			if (domain.charAt(idx) == '.') {
				String possibleTld = domain.subSequence(idx + 1, count).toString();
				if (!isTopLevelSuffix(possibleTld)) {
					return possibleTld;
				}
			}
			idx--;
		}
		return host;
	}

}