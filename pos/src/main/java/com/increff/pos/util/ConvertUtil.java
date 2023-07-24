package com.increff.pos.util;

import com.increff.pos.model.data.*;
import com.increff.pos.model.errordata.BrandErrorData;
import com.increff.pos.model.errordata.InventoryErrorData;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;
import com.increff.pos.model.errordata.ProductErrorData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConvertUtil {


    public static ProductPojo convertProductFormToProductPojo(ProductForm productForm) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setName(productForm.getProductName());
        productPojo.setMrp(productForm.getMrp());
        return productPojo;
    }
    public static BrandData convertBrandCategoryPojotoBrandData(BrandCategoryPojo brandCategoryPojo) {
        BrandData brandData = new BrandData();
        brandData.setBrand(brandCategoryPojo.getBrand());
        brandData.setCategory(brandCategoryPojo.getCategory());
        brandData.setId(brandCategoryPojo.getId());
        return brandData;
    }

    public static BrandCategoryPojo convertBrandFormtoBrandPojo(BrandForm brandForm) {
        BrandCategoryPojo brandCategoryPojo = new BrandCategoryPojo();
        brandCategoryPojo.setBrand(brandForm.getBrand());
        brandCategoryPojo.setCategory(brandForm.getCategory());
        return brandCategoryPojo;
    }

    public static ProductData convertProductPojotoProductData(ProductPojo productPojo, BrandCategoryPojo brandCategoryPojo){
        ProductData productData=new ProductData();
        productData.setId(productPojo.getId());
        productData.setBarcode(productPojo.getBarcode());
        productData.setMrp(productPojo.getMrp());
        productData.setProductName(productPojo.getName());
        productData.setBrandName(brandCategoryPojo.getBrand());
        productData.setBrandCategory(brandCategoryPojo.getCategory());
        return productData;
    }

    public static InventoryData convertInventoryPojotoInventoryData(InventoryPojo inventoryPojo, ProductPojo productPojo){
        InventoryData inventoryData=new InventoryData();
        inventoryData.setId(inventoryPojo.getId());
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        inventoryData.setBarcode(productPojo.getBarcode());
        inventoryData.setProductName(productPojo.getName());
        return inventoryData;
    }

    public static InventoryPojo convertInventoryFormtoInventoryPojo(InventoryForm inventoryForm){
        InventoryPojo inventoryPojo=new InventoryPojo();
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        return inventoryPojo;
    }

    public static OrderData convertOrderPojotoOrderData(OrderPojo orderPojo){
        OrderData orderData=new OrderData();
        orderData.setOrderId(orderPojo.getId());
        orderData.setOrderCode(orderPojo.getOrderCode());
        orderData.setStatus(orderPojo.getStatus());
        orderData.setOrderCreatedTime(orderPojo.getCreatedAt());
        orderData.setOrderInvoicedTime(orderPojo.getOrderInvoicedTime());
        return orderData;
    }

    public static OrderPojo convertOrderPojo(){
        OrderPojo orderPojo=new OrderPojo();
        orderPojo.setStatus("created");
        return orderPojo;
    }

    public static OrderItemData convertOrderItemPojotoOrderItemData(OrderItemPojo orderItemPojo, ProductPojo productPojo){
        OrderItemData orderItemData=new OrderItemData();
        orderItemData.setOrderId(orderItemPojo.getOrderId());
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setBarcode(productPojo.getBarcode());
        orderItemData.setSellingPrice(orderItemPojo.getSellingPrice());
        orderItemData.setProductName(productPojo.getName());
        orderItemData.setProductId(productPojo.getId());
        orderItemData.setOrderItemId(orderItemPojo.getOrderItemId());
        return orderItemData;
    }

    public static OrderItemPojo convertCartItemFormtoOrderItemPojo(CartItemForm cartItemForm){
        OrderItemPojo orderItemPojo=new OrderItemPojo();
        orderItemPojo.setQuantity(cartItemForm.getQuantity());
        orderItemPojo.setSellingPrice(cartItemForm.getSellingPrice());
        return orderItemPojo;
    }

    public static ProductRevenueData convertProductPojotoProductRevenueData(ProductPojo productPojo){
        ProductRevenueData productRevenueData=new ProductRevenueData();
        productRevenueData.setProductName(productPojo.getName());
        productRevenueData.setMrp(productPojo.getMrp());
        productRevenueData.setRevenue(0.0);
        productRevenueData.setQuantity(0);
        return productRevenueData;
    }

    public static DailySalesData convertDailySalesPojotoDailySalesData(DailySalesPojo dailySalesPojo){
        DailySalesData dailySalesData=new DailySalesData();
        dailySalesData.setDate(dailySalesPojo.getDate());
        dailySalesData.setTotalRevenue(dailySalesPojo.getTotalRevenue());
        dailySalesData.setInvoicedItemsCount(dailySalesPojo.getInvoicedItemsCount());
        dailySalesData.setInvoicedOrdersCount(dailySalesData.getInvoicedOrdersCount());
        return dailySalesData;
    }

    public static InventoryReportData convertPojotoInventoryReportData(InventoryPojo inventoryPojo,
                                                                       ProductPojo productPojo){
        InventoryReportData inventoryReportData=new InventoryReportData();
        inventoryReportData.setProductName(productPojo.getName());
        inventoryReportData.setQuantity(inventoryPojo.getQuantity());
        inventoryReportData.setMrp(productPojo.getMrp());
        return inventoryReportData;
    }

    public static InvoiceForm convertOrderPojotoInvoiceForm(OrderPojo orderPojo, List<OrderItemData> orderItemDataList){
        InvoiceForm invoiceForm=new InvoiceForm();
        invoiceForm.setOrderCode(orderPojo.getOrderCode());
        invoiceForm.setOrderItemDataList(orderItemDataList);
        invoiceForm.setOrderInvoicedTime(orderPojo
                .getOrderInvoicedTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return invoiceForm;
    }


    public static UserPojo convertLoginFormToUserPojo(LoginForm loginForm){
        UserPojo userPojo=new UserPojo();
        userPojo.setEmail(loginForm.getEmail());
        userPojo.setPassword(loginForm.getPassword());
        return userPojo;
    }

    public static BrandErrorData convertBrandFormToBrandErrorData(BrandForm brandForm){
        BrandErrorData brandErrorData=new BrandErrorData();
        brandErrorData.setBrand(brandForm.getBrand());
        brandErrorData.setCategory(brandForm.getCategory());
        return brandErrorData;
    }

    public static ProductErrorData convertProductFormToProductErrorData(ProductForm productForm){
        ProductErrorData productErrorData=new ProductErrorData();
        productErrorData.setProductName(productForm.getProductName());
        productErrorData.setMrp(productForm.getMrp());
        productErrorData.setBrandCategory(productForm.getBrandCategory());
        productErrorData.setBrandName(productForm.getBrandName());
        return productErrorData;
    }

    public static InventoryErrorData convertInventoryFormToInventoryErrorData(InventoryForm inventoryForm){
        InventoryErrorData inventoryErrorData=new InventoryErrorData();
        inventoryErrorData.setBarcode(inventoryForm.getBarcode());
        inventoryErrorData.setQuantity(inventoryForm.getQuantity());
        return inventoryErrorData;
    }

    public static Double formatDouble(Double value) {
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(2, RoundingMode.HALF_UP);

        return decimal.doubleValue();
    }
}
