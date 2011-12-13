/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.editor.shops;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogPage;

import freemarker.template.TemplateException;

public class Importer {
	private NickiContext context;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws TemplateException 
	 * @throws InvalidPrincipalException 
	 */
	public static void main(String[] args) throws IOException, TemplateException, InvalidPrincipalException {
		Importer importer = new Importer();
		importer.doImport();
	}

	public void doImport() throws IOException, TemplateException, InvalidPrincipalException {
		context = AppContext.getSystemContext();

		String shopBase = "cn=newShop,ou=shops,o=utopia";
		
		FileReader readerin = new FileReader("c:/Users/rhi/Desktop/Mapping Your Data to BMC Atrium CMDB 7.6.00 Classes.csv");
		
		BufferedReader reader = new BufferedReader(readerin);
		reader.readLine();
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			String parts[] = line.split(";");
			String name = correct(parts[0]);
			String tier1 = correct(parts[3]);
			String tier2 = correct(parts[4]);
			String tier3 = correct(parts[5]);
			String path = shopBase;
			if (StringUtils.isNotEmpty(tier1)) {
				checkObject(path, tier1);
				path = "cn=" + tier1 + "," + path;
				if (StringUtils.isNotEmpty(tier2)) {
					checkObject(path, tier2);
					path = "cn=" + tier2 + "," + path;
					if (StringUtils.isNotEmpty(tier3)) {
						checkObject(path, tier3);
						path = "cn=" + tier3 + "," + path;
						createArticle(path, name);
					}
				}
			}
		}
	}
	
	private String correct(String text) {
		String result = StringUtils.trimToNull(text);
		 result = StringUtils.replace(result, "/", "_");
		return result;
	}

	private CatalogArticle createArticle(String parentPath, String name) {
		String path = "cn=" + name + "," + parentPath;
		if (!context.isExist(path)) {
			try {
				return context.getObjectFactory().createNewDynamicObject(CatalogArticle.class, parentPath, name);
			} catch (InstantiateDynamicObjectException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void checkObject(String parentPath, String name) {
		String path = "cn=" + name + "," + parentPath;
		if (!context.isExist(path)) {
			try {
				context.createDynamicObject(CatalogPage.class, parentPath, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
