package com.molcon.mclabs.jats;

import java.io.File;
import java.net.URL;

public class Example {
	public static void main(String[] args) {
		try {
			URL url = Example.class.getClassLoader().getResource("DTD");
		    File file = new File(url.toURI());
		    
		    ValidateWithDynamicDtdSelection validator = new ValidateWithDynamicDtdSelection(file.getAbsolutePath());
		    validator.groundLevel = true;
		    XmlValidatorResponseObject obj = validator.validate("4.101786.xml", true);
		    System.out.println("Is valid? " + obj.isValid());
		    System.out.println("Validation Msg " + obj.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    
	}
}
