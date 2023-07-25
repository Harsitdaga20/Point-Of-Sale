package com.increff.invoice.service;

import com.increff.invoice.model.PathData;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class GeneratePDFFromFOP {


    public void generatePDF() {
        try {
            File xslFile = new File(PathData.xslFilePath);
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            StreamSource xslSource = new StreamSource(xslFile);
            File xmlFile = new File(PathData.xmlFilePath);
            StreamSource xmlSource = new StreamSource(xmlFile);
            File pdfFile = new File(PathData.pdfFilePath);
            OutputStream pdfStream = new BufferedOutputStream(new FileOutputStream(pdfFile));
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslSource);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdfStream);
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(xmlSource, result);
            pdfStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
