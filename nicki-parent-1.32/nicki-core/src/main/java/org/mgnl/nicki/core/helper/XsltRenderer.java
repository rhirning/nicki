package org.mgnl.nicki.core.helper;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XsltRenderer extends Thread implements Runnable {
	InputStream in;
	OutputStream out;
	InputStream xslTemplate;
	

	public XsltRenderer(InputStream in, OutputStream out,
			InputStream xslTemplate) {
		super();
		this.in = in;
		this.out = out;
		this.xslTemplate = xslTemplate;
	}

	public void run() {
		try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(xslTemplate));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource(in);
            Result result = new StreamResult(out);

            // Apply the xsl file to the source file and write the result to the output file
            xformer.transform(source, result);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
