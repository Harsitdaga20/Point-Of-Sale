package com.increff.pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.*;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/supervisor/")
public class SupervisorController {
    @Autowired
    private BrandDto brandDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private ReportDto reportDto;


    //Brand Controllers
    @ApiOperation(value = "Adds list of brand and category")
    @RequestMapping(path = "brands", method = RequestMethod.POST)
    public void addBrands(@RequestBody List<BrandForm> brandFormList) throws ApiException, JsonProcessingException {
        brandDto.addBrandCategory(brandFormList);
    }

    @ApiOperation(value = "Gets a brand-category by ID")
    @RequestMapping(path = "brands/{brandCategoryId}", method = RequestMethod.GET)
    public BrandData getBrand(@PathVariable Integer brandCategoryId) throws ApiException {
        return brandDto.getBrandCategoryById(brandCategoryId);
    }

    @ApiOperation(value = "Update a brand and category by brandId")
    @RequestMapping(path = "brands/{brandCategoryId}", method = RequestMethod.PUT)
    public void updateBrand(@PathVariable Integer brandCategoryId, @RequestBody BrandForm brandForm) throws ApiException {
        brandDto.updateBrandCategory(brandCategoryId, brandForm);
    }

    //Product Controllers

    @ApiOperation(value = "Adds list of Products")
    @RequestMapping(path = "products", method = RequestMethod.POST)
    public void addProducts(@RequestBody List<ProductForm> productFormList) throws ApiException, JsonProcessingException {
        productDto.addProduct(productFormList);
    }

    @ApiOperation(value = "Gets a Product by productId")
    @RequestMapping(path = "products/{productId}", method = RequestMethod.GET)
    public ProductData getProduct(@PathVariable Integer productId) throws ApiException {
        return productDto.getProduct(productId);

    }

    @ApiOperation(value = "Updates a Product by productId")
    @RequestMapping(path = "products/{productId}", method = RequestMethod.PUT)
    public void updateProduct(@PathVariable Integer productId, @RequestBody ProductForm productForm) throws ApiException {
        productDto.updateProduct(productId, productForm);
    }

    //Inventory Controller

    @ApiOperation(value = "Gets an inventory by inventoryId")
    @RequestMapping(path = "inventory/{inventoryId}", method = RequestMethod.GET)
    public InventoryData getInventory(@PathVariable Integer inventoryId) throws ApiException {
        return inventoryDto.getInventoryByInventoryId(inventoryId);
    }

    @ApiOperation(value = "Updates list of inventory ")
    @RequestMapping(path = "inventory", method = RequestMethod.PUT)
    public void updateInventory(@RequestBody List<InventoryForm> inventoryFormList) throws ApiException, JsonProcessingException {
        inventoryDto.updateInventory(inventoryFormList);
    }

    //Report Controller

    @ApiOperation(value = "Gets report of brand")
    @RequestMapping(path = "reports/brand", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getBrandReport() {
        return reportDto.getBrandReport();
    }

    @ApiOperation(value = "Gets report of inventory")
    @RequestMapping(path = "reports/inventory", method = RequestMethod.POST)
    public ResponseEntity<byte[]> getInventoryReport(@RequestBody BrandForm brandForm) throws ApiException {
        return reportDto.getInventoryReport(brandForm);
    }

    @ApiOperation(value = "Gets report of sales")
    @RequestMapping(path = "reports/daily-sales", method = RequestMethod.POST)
    public ResponseEntity<byte[]> getDailySalesReport(@RequestBody DateForm dateForm) throws ApiException {
        return reportDto.getDailySalesReport(dateForm);
    }

    @ApiOperation(value = "Gets report of revenue")
    @RequestMapping(path = "reports/revenue", method = RequestMethod.POST)
    public ResponseEntity<byte[]> getRevenueReport(@RequestBody DateWithBrandCategoryForm dateWithBrandCategoryForm) throws ApiException {
        return reportDto.getRevenueReport(dateWithBrandCategoryForm);
    }

}
