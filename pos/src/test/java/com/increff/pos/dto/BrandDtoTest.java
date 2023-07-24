package com.increff.pos.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.GetForm;
import com.increff.pos.config.AbstractUnitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class BrandDtoTest extends AbstractUnitTest {
    @Autowired
    private BrandDto brandDto;

    @Test
    public void testAddBrand() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
        List<BrandData> brandDataList=brandDto.getAllBrandCategory();
        assertEquals("brand",brandDataList.get(0).getBrand());
        assertEquals("category",brandDataList.get(0).getCategory());
        assertEquals(1,brandDataList.size());
    }



    @Test
    public void testGetBrand() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
        BrandData brandData=brandDto.getAllBrandCategory().get(0);
        BrandData brandData1=brandDto.getBrandCategoryById(brandData.getId());
        assertEquals("brand",brandData.getBrand());
        assertEquals("category",brandData.getCategory());
        assertEquals(brandData.getId(),brandData1.getId());
    }

    @Test
    public void testUpdateBrand() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        BrandForm updateBrandForm= GetForm.getBrandForm("TeStBrAnD","TeStCaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
        BrandData brandData=brandDto.getAllBrandCategory().get(0);
        brandDto.updateBrandCategory(brandData.getId(),updateBrandForm);
        brandData=brandDto.getAllBrandCategory().get(0);
        assertEquals("testbrand",brandData.getBrand());
        assertEquals("testcategory",brandData.getCategory());
    }

    @Test
    public void testBrandListSize() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        BrandForm brandForm1= GetForm.getBrandForm("TeStBrAnD","TeStCaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.addAll(Arrays.asList(brandForm1,brandForm));
        brandDto.addBrandCategory(brandFormList);
        assertEquals(2,brandDto.getAllBrandCategory().size());
    }

    @Test(expected = ApiException.class)
    public void testEmptyListAdd() throws JsonProcessingException, ApiException {
        List<BrandForm> brandFormList=new ArrayList<>();
        brandDto.addBrandCategory(brandFormList);
    }

    @Test(expected= ApiException.class)
    public void testBrandCategoryUniquenessOnOneAdd() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        BrandForm brandForm1= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.addAll(Arrays.asList(brandForm1,brandForm));
        brandDto.addBrandCategory(brandFormList);
    }

    @Test(expected = ApiException.class)
    public void testBrandCategoryUniquenessOnMultipleAdd() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
        assertEquals(1,brandDto.getAllBrandCategory().size());
        BrandForm brandForm1= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        List<BrandForm> brandFormList2=new ArrayList<>();
        brandFormList2.add(brandForm1);
        brandDto.addBrandCategory(brandFormList2);
    }

    @Test(expected = ApiException.class)
    public void testBrandCategoryUniquenessOnUpdate() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
        brandDto.updateBrandCategory(brandDto.getAllBrandCategory().get(0).getId(),brandForm);
    }

    @Test(expected = ApiException.class)
    public void testBrandCategoryExist() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","CaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
        brandDto.getBrandCategoryById(brandDto.getAllBrandCategory().get(0).getId()+1);
    }

    @Test(expected = ApiException.class)
    public void testBrandCategoryNonNull() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm(null,null);
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
    }

    @Test(expected = ApiException.class)
    public void testBrandNonEmpty() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("  ","CaTeGoRy");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
    }

    @Test(expected = ApiException.class)
    public void testCategoryNonEmpty() throws ApiException, JsonProcessingException {
        BrandForm brandForm= GetForm.getBrandForm("BrAnD","   ");
        List<BrandForm> brandFormList=new ArrayList<>();
        brandFormList.add(brandForm);
        brandDto.addBrandCategory(brandFormList);
    }
}
