package com.increff.pos.util;

import com.increff.pos.model.form.*;

public class NormalizeUtil {
    public static void normalizeBrandForm(BrandForm brandForm) {
        brandForm.setBrand(StringUtil.toLowerCase(brandForm.getBrand()));
        brandForm.setCategory(StringUtil.toLowerCase(brandForm.getCategory()));
    }
    public static void normalizeProductForm(ProductForm productForm){
        productForm.setProductName(StringUtil.toLowerCase(productForm.getProductName()));
        productForm.setBrandName(StringUtil.toLowerCase(productForm.getBrandName()));
        productForm.setBrandCategory(StringUtil.toLowerCase(productForm.getBrandCategory()));
        productForm.setMrp(ConvertUtil.formatDouble(productForm.getMrp()));
    }
    public static void normalizeInventoryForm(InventoryForm inventoryForm){
        inventoryForm.setBarcode(StringUtil.toLowerCase(inventoryForm.getBarcode()));
    }


    public static void normalizeCartItemForm(CartItemForm cartItemForm){
        cartItemForm.setBarcode(StringUtil.toLowerCase(cartItemForm.getBarcode()));
        cartItemForm.setSellingPrice(ConvertUtil.formatDouble(cartItemForm.getSellingPrice()));
    }

    public static void normalizeDateWithBrandCategoryForm(DateWithBrandCategoryForm dateWithBrandCategoryForm){
        dateWithBrandCategoryForm.setBrand(StringUtil.toLowerCase(dateWithBrandCategoryForm.getBrand()));
        dateWithBrandCategoryForm.setCategory(StringUtil.toLowerCase(dateWithBrandCategoryForm.getCategory()));
    }

    public static void normalizeLoginForm(LoginForm loginForm){
        loginForm.setEmail(StringUtil.toLowerCase(loginForm.getEmail()));
        loginForm.setPassword(StringUtil.toLowerCase(loginForm.getPassword()));
    }
}
