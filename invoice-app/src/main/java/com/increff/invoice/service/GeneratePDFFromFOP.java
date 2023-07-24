package com.increff.invoice.service;

import com.increff.invoice.model.PathData;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
public class GeneratePDFFromFOP {

    @Autowired
    private PathData pathData;

    public  void generatePDF() {
        try {
            // Load the XSL-FO file
            File xslFile = new File(pathData.getXslFilePath());
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            StreamSource xslSource = new StreamSource(xslFile);

            // Create the XML input source
            File xmlFile = new File(pathData.getXmlFilePath());
            StreamSource xmlSource = new StreamSource(xmlFile);

            // Create the PDF output file
            File pdfFile = new File(pathData.getPdfFilePath());
            OutputStream pdfStream = new BufferedOutputStream(new FileOutputStream(pdfFile));

            // Create the FOP transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslSource);

            // Generate the PDF
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdfStream);
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(xmlSource, result);

            // Close the streams
            pdfStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
