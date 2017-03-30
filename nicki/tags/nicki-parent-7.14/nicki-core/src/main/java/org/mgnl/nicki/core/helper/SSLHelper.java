/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLHelper {

	private static final Logger LOG = LoggerFactory.getLogger(SSLHelper.class);
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			throw new Exception("Wrong number of arguments");
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String passphrase = args[2];
		String filename = args[3];
		
		installCerts(host, port, passphrase.toCharArray(), filename);
	}

	public static void installCerts(String host, int port, char[] passphrase, String filename) throws Exception {
		File file = new File(filename);
		LOG.debug(file.getAbsolutePath());
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		//ks.load(null);
		if (file.isFile() == false) {
			final char SEP = File.separatorChar;
			File dir = new File(System.getProperty("java.home") + SEP + "lib"
					+ SEP + "security");
			file = new File(dir, filename);
			if (file.isFile() == false) {
				file = new File(dir, "cacerts");
			}
		}
		LOG.debug("Loading KeyStore " + file + "...");
		InputStream in = new FileInputStream(file);
		ks.load(in, null);
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf
				.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		LOG.debug("Opening connection to " + host + ":" + port + "...");
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		try {
			LOG.debug("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			LOG.debug("No errors, certificate is already trusted");
		} catch (SSLException e) {
			LOG.error("Error", e);
		}

		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			LOG.debug("Could not obtain server certificate chain");
			return;
		}

		LOG.debug("Server sent " + chain.length + " certificate(s):");

		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			LOG.debug(" " + (i + 1) + " Subject "
					+ cert.getSubjectDN());
			LOG.debug("   Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			LOG.debug("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			LOG.debug("   md5     " + toHexString(md5.digest()));
			String alias = host + "-" + (i + 1);
			ks.setCertificateEntry(alias, cert);

			OutputStream out = new FileOutputStream(filename);
			ks.store(out, passphrase);
			out.close();

			LOG.debug(cert.toString());
			LOG.debug("Added certificate to keystore 'jssecacerts' using alias '"
							+ alias + "'");
		}

	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}

}
