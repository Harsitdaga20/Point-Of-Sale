package com.increff.pos.flow;

import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductFlow {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    @Transactional(rollbackFor = ApiException.class)
    public void addProduct(ProductPojo productPojo, String brand, String category) throws ApiException {
        BrandCategoryPojo brandCategoryPojo = brandService.getByBrandCategory(brand, category);
        productPojo.setBrandCategoryId(brandCategoryPojo.getId());
        productPojo.setBarcode(StringUtil.generateBarCode());
        productService.addProduct(productPojo, brandCategoryPojo);
        inventoryService.addInventory(productPojo.getId());
    }
}
