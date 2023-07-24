package com.increff.pos.util;

import com.increff.pos.model.form.*;

import java.time.ZonedDateTime;

public class GetForm {

    public static BrandForm getBrandForm(String brand, String category){
        BrandForm brandForm=new BrandForm();
        brandForm.setCategory(category);
        brandForm.setBrand(brand);
        return brandForm;
    }

    public static InventoryForm getInventoryForm(String barcode, Integer quantity){
        InventoryForm inventoryForm=new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public static ProductForm getProductForm(String productName, Double mrp, String brand, String category){
        ProductForm productForm=new ProductForm();
        productForm.setMrp(mrp);
        productForm.setProductName(productName);
        productForm.setBrandCategory(category);
        productForm.setBrandName(brand);
        return productForm;
    }

    public static CartItemForm getCartItemForm(String barcode, Integer quantity, Double selllingPrice){
        CartItemForm cartItemForm=new CartItemForm();
        cartItemForm.setSellingPrice(selllingPrice);
        cartItemForm.setQuantity(quantity);
        cartItemForm.setBarcode(barcode);
        return cartItemForm;
    }

    public static DateForm getDateForm(ZonedDateTime start, ZonedDateTime end){
        DateForm dateForm=new DateForm();
        dateForm.setStart(start);
        dateForm.setEnd(end);
        return dateForm;
    }

    public static DateWithBrandCategoryForm getRevenueForm(String brand,String category,ZonedDateTime start,ZonedDateTime end){
        DateWithBrandCategoryForm dateWithBrandCategoryForm=new DateWithBrandCategoryForm();
        dateWithBrandCategoryForm.setBrand(brand);
        dateWithBrandCategoryForm.setCategory(category);
        dateWithBrandCategoryForm.setStart(start);
        dateWithBrandCategoryForm.setEnd(end);
        return dateWithBrandCategoryForm;
    }
}
