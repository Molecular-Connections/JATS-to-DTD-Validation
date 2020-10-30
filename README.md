# JATS XML Validation

Aim - Utility to dynamically select Jats DTD version and validate input XML.
About Molecular Connections We are the largest STM Indexing, Abstracting & technology Company from India. Our 360º solutions help publishers and pharmaceutical companies maximize the value of their information assets. With expertise spanning across multiple domains including, machine learning, text mining, literature curation, ontology development, content analytics, and visualization we serve various industries with proprietary services and products.

Visit : http://www.molecularconnections.com


#### Usage :
			URL url = Example.class.getClassLoader().getResource("DTD");
		    File file = new File(url.toURI());
		    
		    ValidateWithDynamicDtdSelection validator = new   ValidateWithDynamicDtdSelection(file.getAbsolutePath());
		    validator.groundLevel = true;
		    XmlValidatorResponseObject obj = validator.validate("4.101786.xml", true);
		    System.out.println("Is valid? " + obj.isValid());
		    System.out.println("Validation Msg " + obj.getErrorMsg());
