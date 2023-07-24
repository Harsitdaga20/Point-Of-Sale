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

@Service
public class CreateXMLFile {

    @Autowired
    private PathData pathData;
    

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
                double roundedPrice = roundToTwoDecimalPlaces(orderItemData.getSellingPrice());
                sellingPriceElement.setTextContent(String.valueOf(roundedPrice));
                orderItemElement.appendChild(sellingPriceElement);

                Element amountElement = doc.createElement("amount");
                double roundedPriceAmount =
                        roundToTwoDecimalPlaces(orderItemData.getSellingPrice()*orderItemData.getQuantity());
                amountElement.setTextContent(String.valueOf(roundedPriceAmount));
                orderItemElement.appendChild(amountElement);

                index++;
                totalAmount+=orderItemData.getSellingPrice()*orderItemData.getQuantity();
                totalQuantity+=orderItemData.getQuantity();
            }

            Element totalQuantityElement = doc.createElement("totalQuantity");
            totalQuantityElement.setTextContent(totalQuantity.toString());
            invoiceElement.appendChild(totalQuantityElement);

            Element totalAmountElement = doc.createElement("totalAmount");
            double roundedPriceTotalAmount = roundToTwoDecimalPlaces(totalAmount);
            totalAmountElement.setTextContent(String.valueOf(roundedPriceTotalAmount));
            invoiceElement.appendChild(totalAmountElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathData.getXmlFilePath()));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | javax.xml.transform.TransformerException e) {
            e.printStackTrace();
        }
    }
    private static double roundToTwoDecimalPlaces(Double value) {
        if (value == null) {
            return 0.0;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }
}
