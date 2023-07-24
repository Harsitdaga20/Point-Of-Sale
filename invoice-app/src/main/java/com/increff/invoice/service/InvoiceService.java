package com.increff.invoice.service;

import com.increff.invoice.model.InvoiceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService{

    @Autowired
    private CreateXMLFile createXMLFile;
    @Autowired
    private GeneratePDFFromFOP generatePDFFromFOP;

    public void generatePdf(InvoiceForm invoiceForm){
        createXMLFile.convertToXml(invoiceForm);
        generatePDFFromFOP.generatePDF();
    }


}
