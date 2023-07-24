package com.increff.invoice.controller;

import com.increff.invoice.model.InvoiceForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.increff.invoice.dto.InvoiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@Api
@RestController
public class InvoiceAPIController {
    @Autowired
    private InvoiceDto invoiceDto;

    @ApiOperation(value = "Creates invoice for orders ")
    @RequestMapping(path = "/api/invoices", method = RequestMethod.POST)
    public ResponseEntity<byte[]> add(@Valid @RequestBody InvoiceForm invoiceForm) throws IOException {
        return invoiceDto.generatePDF(invoiceForm);
    }

}
