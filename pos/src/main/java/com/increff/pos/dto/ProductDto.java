package com.increff.pos.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.errordata.ProductErrorData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ValidateFormUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ErrorUtil.errors;

@Service
public class ProductDto {
    @Autowired
    private ProductFlow productFlow;
    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Transactional(rollbackFor = ApiException.class)
    public void addProduct(List<ProductForm> productFormList) throws ApiException, JsonProcessingException {
        if (productFormList.isEmpty()) throw new ApiException("product list is empty");
        List<ProductErrorData> errorData = new ArrayList<>();
        int errorSize = 0;
        Integer count = 0;
        for (ProductForm productForm : productFormList) {
            count++;
            ProductErrorData productErrorData = ConvertUtil.convertProductFormToProductErrorData(productForm);
            try {
                NormalizeUtil.normalizeProductForm(productForm);
                ValidateFormUtil.validateForm(productForm);
                ProductPojo productPojo = ConvertUtil.convertProductFormToProductPojo(productForm);
                productFlow.addProduct(productPojo, productForm.getBrandName(), productForm.getBrandCategory());
            } catch (Exception e) {
                errorSize++;
                productErrorData.setMessage(e.getMessage());
                productErrorData.setLineNumber(count);
                errorData.add(productErrorData);
            }
        }
        if (errorSize > 0) {
            errors(errorData);
        }
    }

    public ProductData getProduct(Integer productId) throws ApiException {
        ProductPojo productPojo = productService.getProductByProductId(productId);
        BrandCategoryPojo brandCategoryPojo = brandService.getCheckBrandCategoryById(productPojo.getBrandCategoryId());
        return ConvertUtil.convertProductPojotoProductData(productPojo, brandCategoryPojo);
    }

    public List<ProductData> getAllProduct() throws ApiException {
        List<ProductPojo> productPojoList = productService.getAllProduct();
        List<ProductData> productDataList = new ArrayList<>();
        for (ProductPojo productPojo : productPojoList) {
            BrandCategoryPojo brandCategoryPojo = brandService.getBrandCategoryById(productPojo.getBrandCategoryId());
            productDataList.add(ConvertUtil.convertProductPojotoProductData(productPojo, brandCategoryPojo));
        }
        return productDataList;
    }

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        StringUtil.toLowerCase(barcode);
        ProductPojo productPojo = productService.getByBarcode(barcode);
        BrandCategoryPojo brandCategoryPojo = brandService
                .getCheckBrandCategoryById(productPojo.getBrandCategoryId());
        return ConvertUtil.convertProductPojotoProductData(productPojo, brandCategoryPojo);
    }

    public void updateProduct(Integer productId, ProductForm productForm) throws ApiException {
        ValidateFormUtil.validateForm(productForm);
        NormalizeUtil.normalizeProductForm(productForm);
        ProductPojo productPojo = ConvertUtil.convertProductFormToProductPojo(productForm);
        BrandCategoryPojo brandCategoryPojo = brandService
                .getByBrandCategory(productForm.getBrandName(), productForm.getBrandCategory());
        productPojo.setBrandCategoryId(brandCategoryPojo.getId());
        productService.updateProduct(productId, productPojo, brandCategoryPojo);
    }

}
