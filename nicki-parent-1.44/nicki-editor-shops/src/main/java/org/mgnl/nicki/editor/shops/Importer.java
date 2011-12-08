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
