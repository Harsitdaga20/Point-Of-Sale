package com.increff.invoice.service;

import com.increff.invoice.model.InvoiceForm;
import com.increff.invoice.model.OrderItemData;
import com.increff.invoice.model.PathData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.DecimalFormat;


public class CreateXMLFile {


    

    public void convertToXml(InvoiceForm invoiceForm) {

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            // Create the document
            Document doc = docBuilder.newDocument();

            Element invoiceElement = doc.createElement("invoice");
            doc.appendChild(invoiceElement);

            Element orderCodeElement = doc.createElement("orderCode");
            orderCodeElement.setTextContent(invoiceForm.getOrderCode());
            invoiceElement.appendChild(orderCodeElement);

            Element orderInvoicedTimeElement = doc.createElement("orderInvoicedTime");
            orderInvoicedTimeElement.setTextContent(invoiceForm.getOrderInvoicedTime());
            invoiceElement.appendChild(orderInvoicedTimeElement);

            Element orderItemsElement = doc.createElement("orderItems");
            invoiceElement.appendChild(orderItemsElement);

            Double totalAmount=0.0;
            Integer totalQuantity=0;
            Integer index=1;
            for (OrderItemData orderItemData : invoiceForm.getOrderItemDataList()) {
                Element orderItemElement = doc.createElement("orderItem");
                orderItemsElement.appendChild(orderItemElement);

                Element serialNumberElement = doc.createElement("serialNumber");
                serialNumberElement.setTextContent(index.toString());
                orderItemElement.appendChild(serialNumberElement);

                Element productNameElement = doc.createElement("productName");
                productNameElement.setTextContent(orderItemData.getProductName());
                orderItemElement.appendChild(productNameElement);

                Element barcodeElement = doc.createElement("barcode");
                barcodeElement.setTextContent(orderItemData.getBarcode());
                orderItemElement.appendChild(barcodeElement);

                Element quantityElement = doc.createElement("quantity");
                quantityElement.setTextContent(String.valueOf(orderItemData.getQuantity()));
                orderItemElement.appendChild(quantityElement);

                Element sellingPriceElement = doc.createElement("sellingPrice");
                sellingPriceElement.setTextContent(new DecimalFormat("#.##").format(orderItemData.getSellingPrice()));
                orderItemElement.appendChild(sellingPriceElement);

                Element amountElement = doc.createElement("amount");
                amountElement.setTextContent(new DecimalFormat("#.##").format(orderItemData.getSellingPrice()*orderItemData.getQuantity()));
                orderItemElement.appendChild(amountElement);

                index++;
                totalAmount+=orderItemData.getSellingPrice()*orderItemData.getQuantity();
                totalQuantity+=orderItemData.getQuantity();
            }

            Element totalQuantityElement = doc.createElement("totalQuantity");
            totalQuantityElement.setTextContent(totalQuantity.toString());
            invoiceElement.appendChild(totalQuantityElement);

            Element totalAmountElement = doc.createElement("totalAmount");
            totalAmountElement.setTextContent(new DecimalFormat("#.##").format(totalAmount));
            invoiceElement.appendChild(totalAmountElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File(PathData.xmlFilePath));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | javax.xml.transform.TransformerException e) {
            e.printStackTrace();
        }
    }
}
