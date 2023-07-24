package com.increff.invoice.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PathData {
    private  final String xmlFilePath="src/main/resources/xml/Invoice.xml";
    private final  String xslFilePath="src/main/resources/xsl/Invoice.xsl";
    private final String pdfFilePath="src/main/resources/pdf/Invoice.pdf";
}
