package com.increff.pos.service;

import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.util.StringUtil;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Objects;


@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    //if-else is for uniqueness of barcode, there is a chance that barcode generated in StringUtil may be already in use
    @Transactional(rollbackFor = ApiException.class)
    public void addProduct(ProductPojo productPojo, BrandCategoryPojo brandCategoryPojo) {
        List<ProductPojo> productPojoList=productDao.selectProducts(null,productPojo.getBarcode());
        ProductPojo exProductPojo= StringUtil.getSingle(productPojoList);
        if(exProductPojo==null) {
            productDao.insert(productPojo);
        }
        else{
            String barcode=StringUtil.generateBarCode();
            ProductPojo newProductPojo=new ProductPojo();
            newProductPojo.setBarcode(barcode);
            newProductPojo.setBrandCategoryId(brandCategoryPojo.getId());
            newProductPojo.setName(productPojo.getName());
            newProductPojo.setMrp(productPojo.getMrp());
            addProduct(newProductPojo, brandCategoryPojo);
        }
    }


    @Transactional
    public ProductPojo getProductByProductId(Integer productId) throws ApiException {
        return getCheck(productId);
    }

    @Transactional
    public List<ProductPojo> getAllProduct() {
        return productDao.getAll(ProductPojo.class);
    }

    @Transactional
    public ProductPojo getByBarcode(String barcode) throws ApiException{
        barcode=StringUtil.toLowerCase(barcode);
        List<ProductPojo> productPojoList=productDao.selectProducts(null,barcode);
        ProductPojo productPojo= StringUtil.getSingle(productPojoList);
        if(Objects.isNull(productPojo)){
            throw new ApiException("Barcode doesn't exist");
        }
        return productPojo;
    }

    @Transactional
    public List<ProductPojo> getProductByBrandCategoryId(Integer brandCategoryId){
        return productDao.selectProducts(brandCategoryId,null);

    }

    @Transactional(rollbackFor = ApiException.class)
    public void updateProduct(Integer productId,
                              ProductPojo productPojo,
                              BrandCategoryPojo brandCategoryPojo) throws ApiException {
        ProductPojo exProductPojo = getCheck(productId);
        exProductPojo.setBrandCategoryId(brandCategoryPojo.getId());
        exProductPojo.setMrp(productPojo.getMrp());
        exProductPojo.setName(productPojo.getName());
    }

    @Transactional
    private ProductPojo getCheck(Integer productId) throws ApiException {
        ProductPojo productPojo = productDao.getById(ProductPojo.class,productId);
        if (Objects.isNull(productPojo)) {
            throw new ApiException("Product with given ID does not exist, id: " +productId);
        }
        return productPojo;
    }

}

