package com.increff.invoice.dto;


import com.increff.invoice.model.InvoiceForm;
import com.increff.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class InvoiceDto {
    @Autowired
    private InvoiceService invoiceService;

    public ResponseEntity<byte[]> generatePDF(InvoiceForm invoiceForm) throws IOException {
        invoiceService.generatePdf(invoiceForm);
        String filePath="src/main/resources/pdf/Invoice.pdf";
        Path pdfPath = Paths.get(filePath);
        byte[] contents=Base64.getEncoder().encode(Files.readAllBytes(pdfPath));
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String fileName="output.pdf";
        headers.setContentDispositionFormData(fileName,fileName);
        headers.setCacheControl("must-revalidate, post-check=0,pre-check=0");
        ResponseEntity<byte[]> response=new ResponseEntity<>(contents,headers, HttpStatus.OK);
        return response;
    }
}
