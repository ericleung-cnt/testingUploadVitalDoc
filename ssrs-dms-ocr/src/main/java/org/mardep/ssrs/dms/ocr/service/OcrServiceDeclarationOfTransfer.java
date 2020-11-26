package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlDeclarationOfTransfer;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceDeclarationOfTransfer implements IOcrServiceDeclarationOfTransfer {

	private final String XML_TAG_DECLARATION_OF_TRANSFER = "_SR-Declaration_of_Transfer:_SR-Declaration_of_Transfer";
	
	private String extractXmlStringValue(Node node) {
		if (node.getChildNodes().getLength()>0) {
			String value = node.getChildNodes().item(0).getNodeValue(); // extractXmlStringValue(firstDocElement, "_Name_of_Ship");
			if (value!=null) {
				return value;
			} else {
				return "";
			}
		}else {
			return "";
		}
	}

	@Override
	public OcrXmlDeclarationOfTransfer getEntityFromOcr(String xmlFile) {
		// TODO Auto-generated method stub
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;			
			Document doc;
			
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(xmlFile));
		
			doc.getDocumentElement().normalize();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

			// tagname checking
			if (doc.getElementsByTagName(XML_TAG_DECLARATION_OF_TRANSFER)
					.getLength()==0) {
				return null;
			}

			NodeList listOfDocs = doc
					.getElementsByTagName(XML_TAG_DECLARATION_OF_TRANSFER)
					.item(0)
					.getChildNodes();
			OcrXmlDeclarationOfTransfer entity = new OcrXmlDeclarationOfTransfer();
			
			System.out.println("list of docs length: " + listOfDocs.getLength());
			for (int s = 0; s < listOfDocs.getLength(); s++) {
				Node firstDocNode = listOfDocs.item(s);
				if (firstDocNode.getNodeType() == Node.ELEMENT_NODE) {
					Element firstDocElement = (Element) firstDocNode;
					
					System.out.println("Node: " + firstDocNode.getNodeName());
					String tagValue = extractXmlStringValue(firstDocNode);
					switch (firstDocNode.getNodeName()) {
						case "_OfficialNumber":
							entity.setOfficialNumber(tagValue);
							entity.setPdfName(tagValue+".pdf");
							break;
						case "_NameofShip":
							entity.setShipName(tagValue);
							break;
						case "_FullName":
							entity.setTransferee(tagValue);
							break;
					}
				}
			}
			return entity;
		} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void uploadToDMS(String imageFile, OcrXmlDeclarationOfTransfer entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlDeclarationOfTransfer entity) {
		// TODO Auto-generated method stub
		System.out.println("official number: " + entity.getOfficialNumber());
		System.out.println("ship name: "+ entity.getShipName());
		System.out.println("full name: " + entity.getTransferee());
	}

}
