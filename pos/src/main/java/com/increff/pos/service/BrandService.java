package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandCategoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.StringUtil.getSingle;


@Service
public class BrandService {
    @Autowired
    private BrandDao brandDao;

    @Transactional(rollbackFor = ApiException.class)
    public void addBrandCategory(BrandCategoryPojo brandCategoryPojo) throws ApiException {
        getCheckBrandCategoryAlreadyExist(brandCategoryPojo.getBrand(),brandCategoryPojo.getCategory());
        brandDao.insert(brandCategoryPojo);
    }

    @Transactional
    public BrandCategoryPojo getBrandCategoryById(Integer brandCategoryId) throws ApiException {
        return getCheckBrandCategoryById(brandCategoryId);
    }

    @Transactional
    public List<BrandCategoryPojo> getAllBrandCategory() {
        return brandDao.getAll(BrandCategoryPojo.class);
    }

    @Transactional
    public void updateBrandCategory(Integer brandCategoryId, BrandCategoryPojo brandCategoryPojo) throws ApiException {
        getCheckBrandCategoryAlreadyExist(brandCategoryPojo.getBrand(), brandCategoryPojo.getCategory());
        BrandCategoryPojo exBrandCategoryPojo = getCheckBrandCategoryById(brandCategoryId);
        exBrandCategoryPojo.setBrand(brandCategoryPojo.getBrand());
        exBrandCategoryPojo.setCategory(brandCategoryPojo.getCategory());
    }

    @Transactional
    public BrandCategoryPojo getByBrandCategory(String brand, String category) throws ApiException {
        BrandCategoryPojo exBrandCategoryPojo =getCheckBrandCategory(brand,category);
        if(Objects.isNull(exBrandCategoryPojo)) {
            throw new ApiException("Brand-category doesn't exist");
        }
        return exBrandCategoryPojo;
    }

    @Transactional
    public BrandCategoryPojo getCheckBrandCategoryById(Integer brandCategoryId) throws ApiException {
        BrandCategoryPojo brandCategoryPojo =brandDao.getById(BrandCategoryPojo.class,brandCategoryId);
        if (Objects.isNull(brandCategoryPojo)) {
            throw new ApiException("Brand-Category with given ID does not exist, id: " + brandCategoryId);
        }
        return brandCategoryPojo;
    }


    private void getCheckBrandCategoryAlreadyExist(String brand,String category) throws ApiException {
        BrandCategoryPojo brandCategoryPojo =getCheckBrandCategory(brand,category);
        if(Objects.nonNull(brandCategoryPojo)){
            throw new ApiException("Brand-Category already exists");
        }
    }

    @Transactional
    private BrandCategoryPojo getCheckBrandCategory(String brand, String category){
        return getSingle(brandDao.selectBrandCategory(brand,category));
    }
}
