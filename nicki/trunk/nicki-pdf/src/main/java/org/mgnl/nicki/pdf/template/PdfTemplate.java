package org.mgnl.nicki.pdf.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class PdfTemplate {

	private Pdf pdf = null;
	public PdfTemplate(String templatePath) {
		try {
			pdf = unmarshal(Pdf.class, templatePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    
    private Object unmarshal(ByteArrayOutputStream outputStream, Class<?> rootClazz) throws JAXBException  {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		return unmarshal(rootClazz, inputStream);
    }
    
    
    private <T> T unmarshal(Class<T> docClass, String path) throws JAXBException, FileNotFoundException {

		FileInputStream file = new FileInputStream(path);
		return unmarshal(docClass, file);
    }
    
    public <T> T unmarshal( Class<T> docClass, InputStream inputStream )
    	    throws JAXBException {
    	    String packageName = docClass.getPackage().getName();
    	    JAXBContext jc = JAXBContext.newInstance( packageName );
    	    Unmarshaller u = jc.createUnmarshaller();
    	    @SuppressWarnings("unchecked")
			T doc = (T)u.unmarshal( inputStream );
    	    return doc;
    	}



	public Pdf getPdf() {
		return pdf;
	}



	public void setPdf(Pdf pdf) {
		this.pdf = pdf;
	}

}
