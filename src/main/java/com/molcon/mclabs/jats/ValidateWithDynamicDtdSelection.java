package com.molcon.mclabs.jats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidateWithDynamicDtdSelection {
	private File baseDTDfolder;
	public boolean groundLevel = true;
	
	public ValidateWithDynamicDtdSelection(String baseDtdFolder) {
		this.baseDTDfolder = new File(baseDtdFolder);
	}
	
	private String locateDtdFile(DocumentType doctype) {
		String type = doctype.getName().trim().toLowerCase();
		
		String ver = doctype.getPublicId().replaceAll(".*\\bv([^\\s]*)\\b.*", "$1");
		String dtdname = doctype.getSystemId();
		if(type.equals("article")) {
			if(dtdname.contains("atypon")) {
				return baseDTDfolder.getAbsolutePath() + "/" + "dtd_mashup/atypon-archivearticle3.dtd";
			} else {
				return baseDTDfolder.getAbsolutePath() + "/" + "niso-jats-master/archiving/" + ver + "/" + dtdname;
			}
			
		} else if (type.equals("book")) {
			return baseDTDfolder.getAbsolutePath() + "/" + "atypon_book_dtd/" + ver + "/" + dtdname;
		} 
		return "";
	}

	private DocumentType getDoctype(String fileXml) throws Exception {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setValidating(false);
		domFactory.setNamespaceAware(false);
		domFactory.setFeature("http://xml.org/sax/features/namespaces", false);
		domFactory.setFeature("http://xml.org/sax/features/validation", false);
		domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document document = builder.parse(fileXml); 
		return document.getDoctype();
	}
	
	public XmlValidatorResponseObject validate(String xmlFilePath, 
			boolean activateEntityResolver) throws Exception {		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setValidating(true);
		domFactory.setNamespaceAware(true);
		domFactory.setFeature("http://xml.org/sax/features/namespaces", true);
		domFactory.setFeature("http://xml.org/sax/features/validation", true);
		domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", true);
		domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
		
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		XmlValidatorResponseObject mxlRs = new XmlValidatorResponseObject();
		DocumentType doctype = getDoctype(xmlFilePath);
		if (activateEntityResolver) {
			builder.setEntityResolver(new EntityResolver() {
			    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			    	if (groundLevel) {
			    		mxlRs.setXmlType(doctype.getName());
			    		groundLevel = false;
			    		InputStream dtdStream = new FileInputStream(locateDtdFile(doctype));
			    		return new InputSource(dtdStream);
			    	} else {
			    		File dtd = new File(locateDtdFile(doctype));
			    		InputStream dtdStream = new FileInputStream(new File(dtd.getParentFile(), systemId.replaceAll("file://" + System.getProperty("user.dir") ,"")));
			    		return new InputSource(dtdStream);
			    	} 	
			    }
			    });
		}
		
		builder.setErrorHandler(new ErrorHandler() {
		    @Override
		    public void error(SAXParseException exception) throws SAXException {
		    	throw new SAXException(exception);
		               
		    }
		    @Override
		    public void fatalError(SAXParseException exception) throws SAXException {
		    	throw new SAXException(exception);
		    }

		    @Override
		    public void warning(SAXParseException exception) throws SAXException {
		    	throw new SAXException(exception);
		    }
		});
		
		mxlRs.setFilePath(xmlFilePath);
	            try {
					builder.parse(xmlFilePath);
					mxlRs.setValid(true);
				}catch (Exception e) {
					mxlRs.setValid(false);
					mxlRs.setErrorMsg(e.getMessage());
				}
	            return mxlRs;
	}

}
