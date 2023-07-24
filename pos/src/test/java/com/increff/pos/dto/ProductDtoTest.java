package com.increff.pos.dto;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.GetForm;
import com.increff.pos.util.GetPojo;
import junit.framework.TestCase;
import com.increff.pos.config.AbstractUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.TestCase.assertEquals;

public class ProductDtoTest extends AbstractUnitTest {
    @Autowired
    private ProductDto productDto;

    @Autowired
    private BrandDao brandDao;

    @Before
    public void setup(){
        BrandCategoryPojo brandCategoryPojo = GetPojo.getBrandPojo("brand","category");
        brandDao.insert(brandCategoryPojo);
    }

    @Test
    public void testAddProduct() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
        List<ProductData> productDataList=productDto.getAllProduct();
        TestCase.assertEquals("brand",productDataList.get(0).getBrandName());
        TestCase.assertEquals("category",productDataList.get(0).getBrandCategory());
        TestCase.assertEquals("product1",productDataList.get(0).getProductName());
        TestCase.assertEquals((Double)123.45,productDataList.get(0).getMrp());
        assertEquals(1,productDataList.size());
    }

    @Test
    public void testGetProduct() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
        ProductData productData=productDto.getAllProduct().get(0);
        ProductData productData1=productDto.getProduct(productData.getId());
        TestCase.assertEquals("brand",productData1.getBrandName());
        TestCase.assertEquals("category",productData1.getBrandCategory());
        TestCase.assertEquals("product1",productData1.getProductName());
        TestCase.assertEquals((Double)123.45,productData1.getMrp());
    }

    @Test
    public void testGetProductByBarcode() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
        ProductData productData=productDto.getAllProduct().get(0);
        ProductData productData1=productDto.getProductByBarcode(productData.getBarcode());
        TestCase.assertEquals("brand",productData1.getBrandName());
        TestCase.assertEquals("category",productData1.getBrandCategory());
        TestCase.assertEquals("product1",productData1.getProductName());
        TestCase.assertEquals((Double)123.45,productData1.getMrp());
    }

    @Test
    public void testUpdateProduct() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
        ProductForm updateProductForm= GetForm.getProductForm("tEsTPrOdUcT1",123.45,"bRaNd","cAtEgOrY");
        ProductData productData=productDto.getAllProduct().get(0);
        productDto.updateProduct(productData.getId(),updateProductForm);
        ProductData productData1=productDto.getProduct(productData.getId());
        TestCase.assertEquals("brand",productData1.getBrandName());
        TestCase.assertEquals("category",productData1.getBrandCategory());
        TestCase.assertEquals("testproduct1",productData1.getProductName());
        TestCase.assertEquals((Double)123.45,productData1.getMrp());
    }

    @Test
    public void testSizeOfProductList() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
        ProductForm productForm1= GetForm.getProductForm("PrOdUcT2",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList2=new ArrayList<>();
        productFormList2.add(productForm1);
        productDto.addProduct(productFormList2);
        List<ProductData> productDataList=productDto.getAllProduct();
        assertEquals(2,productDataList.size());
    }

    @Test(expected = ApiException.class)
    public void testAddProductEmpty() throws JsonProcessingException, ApiException {
        List<ProductForm> productFormList=new ArrayList<>();
        productDto.addProduct(productFormList);
    }

    @Test(expected = ApiException.class)
    public void testBrandCategoryNonExist() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"TeStbRaNd","TeStcAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
    }

    @Test(expected= ApiException.class)
    public void testUpdateNonExistBrandCategory() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
        ProductData productData=productDto.getAllProduct().get(0);
        productForm.setBrandName("TeStBrAnD");
        productForm.setBrandCategory("TeStCaTeGoRy");
        productDto.updateProduct(productData.getId(),productForm);
    }

    @Test(expected = ApiException.class)
    public void testBrandNameNull() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,null,"category");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);

    }
    @Test(expected = ApiException.class)
    public void testBrandCategoryNull() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("PrOdUcT1",123.45,"brand",null);
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
    }

    @Test(expected= ApiException.class)
    public void testProductNameNull() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm(null,123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
    }

    @Test(expected=ApiException.class)
    public void testProductNameEmpty() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("  ",123.45,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
    }

    @Test(expected = ApiException.class)
    public void testMrpPositive() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("product1",-2.66,"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
    }

    @Test(expected = ApiException.class)
    public void testMrpMaximum() throws ApiException, JsonProcessingException {
        ProductForm productForm= GetForm.getProductForm("product1",new Double(100000000.12),"bRaNd","cAtEgOrY");
        List<ProductForm> productFormList=new ArrayList<>();
        productFormList.add(productForm);
        productDto.addProduct(productFormList);
    }

    @Test(expected = ApiException.class)
    public void testGetProductByNonExistingBarcode() throws ApiException {
        productDto.getProductByBarcode("barcode2");
    }
}
