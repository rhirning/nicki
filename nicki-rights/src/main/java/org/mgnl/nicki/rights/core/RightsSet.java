package org.mgnl.nicki.rights.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class RightsSet {
	private List<Right> rightsList = new ArrayList<Right>();
	private List<RightData> rightsDataList = new ArrayList<RightData>();
	private List<RightsGroup> rightsGroupsList = new ArrayList<RightsGroup>();
	Document document = null;

	public RightsSet (String classLoaderPath) throws JDOMException, IOException {
		InputStream inStream = getClass().getClassLoader().getResourceAsStream(classLoaderPath);
		Reader in = new InputStreamReader(inStream);
		SAXBuilder builder = new SAXBuilder();
		document = builder.build(in);
		load();
	}
	
	private void load() {
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> data = root.getChildren("data");
		if (data != null && data.size() > 0) {
			for (Iterator<Element> iterator = data.iterator(); iterator.hasNext();) {
				Element dataElement = iterator.next();
				try {
					rightsDataList.add(new RightData(dataElement));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		@SuppressWarnings("unchecked")
		List<Element> rights = root.getChildren("right");
		if (rights != null && rights.size() > 0) {
			for (Iterator<Element> iterator = rights.iterator(); iterator.hasNext();) {
				Element rightElement = iterator.next();
				try {
					rightsList.add(new Right(rightElement));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		@SuppressWarnings("unchecked")
		List<Element> rightsGroup = root.getChildren("rightsGroup");
		if (rightsGroup != null && rightsGroup.size() > 0) {
			for (Iterator<Element> iterator = rightsGroup.iterator(); iterator.hasNext();) {
				Element rightsGroupElement = iterator.next();
				try {
					rightsGroupsList.add(new RightsGroup(rightsGroupElement));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}

	public Document getDocument() {
		return document;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Iterator<RightData> iterator = rightsDataList.iterator(); iterator.hasNext();) {
			RightData rightData = iterator.next();
			sb.append(rightData.toString()).append("\n");
		}
		for (Iterator<Right> iterator = rightsList.iterator(); iterator.hasNext();) {
			Right right = iterator.next();
			sb.append(right.toString()).append("\n");
		}
		for (Iterator<RightsGroup> iterator = rightsGroupsList.iterator(); iterator.hasNext();) {
			RightsGroup rightsGroup = iterator.next();
			sb.append(rightsGroup.toString()).append("\n");
		}
		return sb.toString();
	}

	public boolean hasRights() {
		return rightsList != null && rightsList.size() > 0;
	}

	public List<Right> getRightsList() {
		return rightsList;
	}

	public List<RightsGroup> getRightsGroupsList() {
		return rightsGroupsList;
	}

	public boolean hasRightsGroups() {
		return rightsGroupsList != null && rightsGroupsList.size() > 0;
	}

	public boolean hasRightDatas() {
		return rightsDataList != null && rightsDataList.size() > 0;
	}

	public List<RightData> getRightsDataList() {
		return rightsDataList;
	}



}
