package com.increff.invoice.service;

import com.increff.invoice.model.InvoiceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService{

    public void generatePdf(InvoiceForm invoiceForm){
        CreateXMLFile createXMLFile=new CreateXMLFile();
        createXMLFile.convertToXml(invoiceForm);
        GeneratePDFFromFOP generatePDFFromFOP=new GeneratePDFFromFOP();
        generatePDFFromFOP.generatePDF();
    }


}
