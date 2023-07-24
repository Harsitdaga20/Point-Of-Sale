package com.increff.pos.util;

import com.increff.pos.pojo.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class GetPojo {

    public static BrandCategoryPojo getBrandPojo(String brand, String category){
        BrandCategoryPojo brandCategoryPojo =new BrandCategoryPojo();
        brandCategoryPojo.setBrand(brand);
        brandCategoryPojo.setCategory(category);
        return brandCategoryPojo;
    }

    public static ProductPojo getProductPojo(String barcode, Integer brandCategoryId, String productName, Double mrp){
        ProductPojo productPojo=new ProductPojo();
        productPojo.setBarcode(barcode);
        productPojo.setName(productName);
        productPojo.setMrp(mrp);
        productPojo.setBrandCategoryId(brandCategoryId);
        return productPojo;
    }

    public static InventoryPojo getInventoryPojo(Integer productId, Integer quantity){
        InventoryPojo inventoryPojo=new InventoryPojo();
        inventoryPojo.setQuantity(quantity);
        inventoryPojo.setProductId(productId);
        return inventoryPojo;
    }

    public static OrderPojo getOrderPojo(String status, String orderCode){
        OrderPojo orderPojo=new OrderPojo();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        orderPojo.setOrderInvoicedTime(now.minusDays(1).withHour(0).withMinute(13).withSecond(14));
        orderPojo.setStatus(status);
        orderPojo.setOrderCode(orderCode);
        return orderPojo;
    }

    public static OrderItemPojo getOrderItemPojo(Integer orderId, Integer productId, Integer quantity, Double sellingPrice){
        OrderItemPojo orderItemPojo=new OrderItemPojo();
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setProductId(productId);
        orderItemPojo.setQuantity(quantity);
        orderItemPojo.setSellingPrice(sellingPrice);
        return orderItemPojo;
    }
}
