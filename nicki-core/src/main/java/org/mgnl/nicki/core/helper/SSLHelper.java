
package org.mgnl.nicki.core.helper;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
