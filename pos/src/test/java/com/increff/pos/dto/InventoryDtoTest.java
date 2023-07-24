package com.increff.pos.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static com.increff.pos.util.GetForm.getInventoryForm;
import static com.increff.pos.util.GetPojo.*;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;



    @Before
    public void setup(){
        BrandCategoryPojo brandCategoryPojo =getBrandPojo("brand","category");
        brandDao.insert(brandCategoryPojo);

        Integer brandCategoryId=brandDao
                .selectBrandCategory("brand","category")
                .get(0)
                .getId();
        ProductPojo productPojo=getProductPojo("barcode1",brandCategoryId,"product1",123.45);
        productDao.insert(productPojo);
        Integer productId=productDao
                .selectProducts(brandCategoryId,"barcode1")
                .get(0)
                .getId();
        InventoryPojo inventoryPojo=getInventoryPojo(productId,0);
        inventoryDao.insert(inventoryPojo);
    }

    @Test
    public void testUpdateInventory() throws ApiException, JsonProcessingException {
        InventoryForm inventoryForm=getInventoryForm("barcode1",100);
        List<InventoryForm> inventoryFormList=new ArrayList<>();
        inventoryFormList.add(inventoryForm);
        inventoryDto.updateInventory(inventoryFormList);
        List<InventoryData> inventoryDataList=inventoryDto.getAllInventory();
        assertEquals(1,inventoryDataList.size());
        assertEquals("barcode1",inventoryDataList.get(0).getBarcode());
        assertEquals((Integer) 100,inventoryDataList.get(0).getQuantity());
        assertEquals("product1",inventoryDataList.get(0).getProductName());
    }

    @Test
    public void testGetInventoryById() throws ApiException {
        InventoryData inventoryData=inventoryDto.getAllInventory().get(0);
        InventoryData inventoryData1=inventoryDto.getInventoryByInventoryId(inventoryData.getId());
        assertEquals("barcode1",inventoryData1.getBarcode());
        assertEquals((Integer) 0,inventoryData1.getQuantity());
        assertEquals("product1",inventoryData1.getProductName());
    }

    @Test(expected= ApiException.class)
    public void testBarcodeNonNull() throws ApiException, JsonProcessingException {
        InventoryForm inventoryForm=getInventoryForm(null,100);
        List<InventoryForm> inventoryFormList=new ArrayList<>();
        inventoryFormList.add(inventoryForm);
        inventoryDto.updateInventory(inventoryFormList);
    }

    @Test(expected= ApiException.class)
    public void testQuantityNonNull() throws ApiException, JsonProcessingException {
        InventoryForm inventoryForm=getInventoryForm("barcode1",null);
        List<InventoryForm> inventoryFormList=new ArrayList<>();
        inventoryFormList.add(inventoryForm);
        inventoryDto.updateInventory(inventoryFormList);
    }

    @Test(expected= ApiException.class)
    public void testBarcodeNonEmpty() throws ApiException, JsonProcessingException {
        InventoryForm inventoryForm=getInventoryForm("    ",100);
        List<InventoryForm> inventoryFormList=new ArrayList<>();
        inventoryFormList.add(inventoryForm);
        inventoryDto.updateInventory(inventoryFormList);
    }

    @Test(expected= ApiException.class)
    public void testQuantityPositive() throws ApiException, JsonProcessingException {
        InventoryForm inventoryForm=getInventoryForm("barcode1",-2);
        List<InventoryForm> inventoryFormList=new ArrayList<>();
        inventoryFormList.add(inventoryForm);
        inventoryDto.updateInventory(inventoryFormList);
    }

    @Test(expected= ApiException.class)
    public void testQuantityRange() throws ApiException, JsonProcessingException {
        InventoryForm inventoryForm=getInventoryForm("barcode1",1000000001);
        List<InventoryForm> inventoryFormList=new ArrayList<>();
        inventoryFormList.add(inventoryForm);
        inventoryDto.updateInventory(inventoryFormList);
    }
    @Test(expected= ApiException.class)
    public void testBarcodeExist() throws ApiException, JsonProcessingException {
        InventoryForm inventoryForm=getInventoryForm("barcode3",null);
        List<InventoryForm> inventoryFormList=new ArrayList<>();
        inventoryFormList.add(inventoryForm);
        inventoryDto.updateInventory(inventoryFormList);
    }

}
