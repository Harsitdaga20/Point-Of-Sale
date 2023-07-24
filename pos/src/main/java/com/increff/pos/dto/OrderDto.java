package com.increff.pos.dto;

import com.increff.pos.flow.OrderFlow;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.CartItemForm;
import com.increff.pos.model.form.InvoiceForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.ValidateFormUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.increff.pos.util.NormalizeUtil.normalizeCartItemForm;

@Service
public class OrderDto {

    private static final String pdf_Path = "src/main/resources/pdf/";

    private static final String url = "http://localhost:8080/invoice_app/api/invoices";
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderFlow orderFlow;
    @Autowired
    private ProductService productService;
    @Autowired
    private RestTemplate restTemplate;


    public void addNewOrder(List<CartItemForm> cartItemFormList) throws ApiException {
        for (CartItemForm cartItemForm : cartItemFormList) {
            normalizeCartItemForm(cartItemForm);
            ValidateFormUtil.validateForm(cartItemForm);
        }
        orderFlow.addNewOrder(cartItemFormList);
    }

    public OrderData getOrderByOrderCode(String orderCode) throws ApiException {
        OrderPojo orderPojo = orderService.getCheckByOrderCode(orderCode);
        return ConvertUtil.convertOrderPojotoOrderData(orderPojo);
    }

    public OrderData getOrderByOrderId(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderService.getCheckOrder(orderId);
        return ConvertUtil.convertOrderPojotoOrderData(orderPojo);
    }

    public List<OrderData> getAllOrders() {
        List<OrderPojo> orderPojoList = orderService.getAllOrders();
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderPojo orderPojo : orderPojoList) {
            OrderData orderData = ConvertUtil.convertOrderPojotoOrderData(orderPojo);
            orderDataList.add(orderData);
        }
        return orderDataList;
    }

    public void updateOrder(Integer orderId, List<CartItemForm> cartItemFormList) throws ApiException {
        for (CartItemForm cartItemForm : cartItemFormList) {
            normalizeCartItemForm(cartItemForm);
            ValidateFormUtil.validateForm(cartItemForm);
        }
        orderFlow.updateOrder(orderId, cartItemFormList);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void invoiceOrder(Integer orderId) throws ApiException {
        try {
            orderService.invoiceOrder(orderId);
            createInvoice(orderId);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

    }

    public List<OrderItemData> getOrderItemByOrderId(Integer orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderService.getAllOrderItemByOrderId(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo = productService.getProductByProductId(orderItemPojo.getProductId());
            orderItemDataList.add(ConvertUtil.convertOrderItemPojotoOrderItemData(orderItemPojo, productPojo));
        }
        return orderItemDataList;
    }

    public ResponseEntity<byte[]> getInvoicePdf(OrderData orderData) throws IOException {
        Path pdfPath = Paths.get(pdf_Path + orderData.getOrderId() + "invoice.pdf");
        byte[] contents = Files.readAllBytes(pdfPath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "Invoice_" + orderData.getOrderId() + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    private void createInvoice(Integer orderId) throws ApiException {
        try {
            InvoiceForm invoiceForm =
                    ConvertUtil.convertOrderPojotoInvoiceForm(orderService.getCheckOrder(orderId), getOrderItemByOrderId(orderId));
            ResponseEntity<byte[]> response = restTemplate.postForEntity(url, invoiceForm, byte[].class);
            Path pdfPath = Paths.get(pdf_Path + orderId + "invoice.pdf");
            try {
                byte[] contents = Base64.getDecoder().decode(response.getBody());
                Files.write(pdfPath, contents);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

    }
}
