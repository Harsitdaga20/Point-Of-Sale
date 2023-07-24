package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.CartItemForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/")
public class SharedController {
    @Autowired
    private BrandDto brandDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private OrderDto orderDto;


    //Brand Controller
    @ApiOperation(value = "Get list of all brands and categories")
    @RequestMapping(path = "brands", method = RequestMethod.GET)
    public List<BrandData> getAllBrandCategory() {
        return brandDto.getAllBrandCategory();
    }

    //Product Controller
    @ApiOperation(value = "Gets list of all Products")
    @RequestMapping(path = "products", method = RequestMethod.GET)
    public List<ProductData> getAllProducts() throws ApiException {
        return productDto.getAllProduct();
    }

    @ApiOperation(value = "Gets a Product by Barcode")
    @RequestMapping(path = "products/{barcode}", method = RequestMethod.GET)
    public ProductData getProductByBarcode(@PathVariable String barcode) throws ApiException {
        return productDto.getProductByBarcode(barcode);
    }

    //Inventory Controller
    @ApiOperation(value = "Gets list of all inventories")
    @RequestMapping(path = "inventory", method = RequestMethod.GET)
    public List<InventoryData> getAllInventory() throws ApiException {
        return inventoryDto.getAllInventory();
    }

    //Order Controller
    @ApiOperation(value = "Create order")
    @RequestMapping(path = "orders", method = RequestMethod.POST)
    public void addOrder(@RequestBody List<CartItemForm> cartItemFormList) throws ApiException {
        orderDto.addNewOrder(cartItemFormList);
    }

    @ApiOperation(value = "Select order by orderId")
    @RequestMapping(path = "orders/{orderId}", method = RequestMethod.GET)
    public OrderData getOrder(@PathVariable Integer orderId) throws ApiException {
        return orderDto.getOrderByOrderId(orderId);
    }

    @ApiOperation(value = "Select all orders")
    @RequestMapping(path = "orders", method = RequestMethod.GET)
    public List<OrderData> getAllOrders() {
        return orderDto.getAllOrders();
    }

    @ApiOperation(value = "Mark order as invoiced and generate it")
    @RequestMapping(path = "orders/invoice/{orderId}", method = RequestMethod.PUT)
    public void markOrderAsInvoice(@PathVariable Integer orderId) throws ApiException {
        orderDto.invoiceOrder(orderId);
    }

    @ApiOperation(value = "Update Order items")
    @RequestMapping(path = "orders/{orderId}", method = RequestMethod.PUT)
    public void updateOrder(@PathVariable Integer orderId, @RequestBody List<CartItemForm> cartItemFormList) throws ApiException {
        orderDto.updateOrder(orderId, cartItemFormList);
    }

    @ApiOperation(value = "Download Invoice")
    @RequestMapping(path = "invoice/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Integer orderId) throws ApiException, IOException {
        OrderData orderData = orderDto.getOrderByOrderId(orderId);
        return orderDto.getInvoicePdf(orderData);
    }

    @ApiOperation(value = "Get OrderItems by an order id")
    @RequestMapping(path = "orders-items/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> getOrderItemByOrderId(@PathVariable Integer orderId) throws ApiException {
        return orderDto.getOrderItemByOrderId(orderId);
    }
}
