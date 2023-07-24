package com.increff.pos.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.errordata.BrandErrorData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateFormUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ErrorUtil.errors;

@Component
public class BrandDto {
    @Autowired
    private BrandService brandService;

    public void addBrandCategory(List<BrandForm> brandFormList) throws ApiException, JsonProcessingException {
        if (brandFormList.isEmpty()) throw new ApiException("Brand list is empty");
        List<BrandErrorData> errorData = new ArrayList<>();
        int errorSize = 0;
        Integer count = 0;
        for (BrandForm brandForm : brandFormList) {
            count++;
            BrandErrorData brandErrorData = ConvertUtil.convertBrandFormToBrandErrorData(brandForm);
            try {
                NormalizeUtil.normalizeBrandForm(brandForm);
                ValidateFormUtil.validateForm(brandForm);
                BrandCategoryPojo brandCategoryPojo = ConvertUtil.convertBrandFormtoBrandPojo(brandForm);
                brandService.addBrandCategory(brandCategoryPojo);
            } catch (Exception e) {
                errorSize++;
                brandErrorData.setMessage(e.getMessage());
                brandErrorData.setLineNumber(count);
                errorData.add(brandErrorData);
            }
        }
        if (errorSize > 0) {
            errors(errorData);
        }
    }

    public BrandData getBrandCategoryById(Integer brandCategoryId) throws ApiException {
        BrandCategoryPojo brandCategoryPojo = brandService.getBrandCategoryById(brandCategoryId);
        return ConvertUtil.convertBrandCategoryPojotoBrandData(brandCategoryPojo);
    }

    public List<BrandData> getAllBrandCategory() {
        List<BrandCategoryPojo> brandCategoryPojoList = brandService.getAllBrandCategory();
        List<BrandData> brandDataList = new ArrayList<>();
        for (BrandCategoryPojo brandCategoryPojo : brandCategoryPojoList) {
            brandDataList.add(ConvertUtil.convertBrandCategoryPojotoBrandData(brandCategoryPojo));
        }
        return brandDataList;
    }

    public void updateBrandCategory(Integer brandCategoryId, BrandForm brandForm) throws ApiException {
        ValidateFormUtil.validateForm(brandForm);
        NormalizeUtil.normalizeBrandForm(brandForm);
        BrandCategoryPojo brandCategoryPojo = ConvertUtil.convertBrandFormtoBrandPojo(brandForm);
        brandService.updateBrandCategory(brandCategoryId, brandCategoryPojo);
    }

}
