package com.increff.pos.dto;

import com.increff.pos.dao.*;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.DateForm;
import com.increff.pos.model.form.DateWithBrandCategoryForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.DailySalesService;
import com.increff.pos.config.AbstractUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.increff.pos.util.GetForm.*;
import static com.increff.pos.util.GetPojo.*;
import static com.increff.pos.util.GetPojo.getOrderItemPojo;
import static org.junit.Assert.assertNotEquals;

public class ReportDtoTest extends AbstractUnitTest {

    @Autowired
    private DailySalesDto dailySalesDto;
    @Autowired
    private ReportDto reportDto;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private DailySalesService dailySalesService;
    @Autowired
    private OrderItemDao orderItemDao;

    @Before
    public void setup() throws ApiException {
        BrandCategoryPojo brandCategoryPojo =getBrandPojo("brand","category");
        brandDao.insert(brandCategoryPojo);

        Integer brandCategoryId=brandDao
                .selectBrandCategory("brand","category")
                .get(0)
                .getId();
        ProductPojo productPojo1=getProductPojo("barcode1",brandCategoryId,"product1",123.45);
        productDao.insert(productPojo1);

        ProductPojo productPojo2=getProductPojo("barcode2",brandCategoryId,"product2",123.45);
        productDao.insert(productPojo2);

        Integer productId1=productDao
                .selectProducts(brandCategoryId,"barcode1")
                .get(0)
                .getId();
        Integer productId2=productDao
                .selectProducts(brandCategoryId,"barcode2")
                .get(0)
                .getId();

        InventoryPojo inventoryPojo1=getInventoryPojo(productId1,100);
        inventoryDao.insert(inventoryPojo1);

        InventoryPojo inventoryPojo2=getInventoryPojo(productId2,100);
        inventoryDao.insert(inventoryPojo2);

        OrderPojo orderPojo1=getOrderPojo("created","abcdefghij");
        OrderPojo orderPojo2=getOrderPojo("invoiced","qwertyuiop");
        orderDao.insertOrder(orderPojo1);
        orderDao.insertOrder(orderPojo2);
        Integer orderId1= orderDao
                .selectOrders(null,null,null,"abcdefghij",null)
                .get(0)
                .getId();
        Integer orderId2=orderDao
                .selectOrders(null,null,null,"qwertyuiop",null)
                .get(0)
                .getId();
        OrderItemPojo orderItemPojo1=getOrderItemPojo(orderId1,productId1,10,99.99);
        OrderItemPojo orderItemPojo2=getOrderItemPojo(orderId2,productId1,11,99.9);
        OrderItemPojo orderItemPojo3=getOrderItemPojo(orderId2,productId2,9,12.9);
        orderItemDao.insert(orderItemPojo1);
        orderItemDao.insert(orderItemPojo2);
        orderItemDao.insert(orderItemPojo3);
        dailySalesDto.createDailySales();
    }

    @Test
    public void testGetBrandReport(){
        ResponseEntity<byte[]> response= reportDto.getBrandReport();
        assertNotEquals(null,response);
    }

    @Test
    public void testGetInventoryReport() throws ApiException {
        BrandForm brandForm=getBrandForm("brand","category");
        ResponseEntity<byte[]> response=reportDto.getInventoryReport(brandForm);
        assertNotEquals(null,response);
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryReportBrandNull() throws ApiException {
        BrandForm brandForm=getBrandForm(null,"category");
        ResponseEntity<byte[]> response=reportDto.getInventoryReport(brandForm);
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryReportCategoryNull() throws ApiException {
        BrandForm brandForm=getBrandForm("brand",null);
        ResponseEntity<byte[]> response=reportDto.getInventoryReport(brandForm);
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryReportBrandEmpty() throws ApiException {
        BrandForm brandForm=getBrandForm("  ","category");
        ResponseEntity<byte[]> response=reportDto.getInventoryReport(brandForm);
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryReportCategoryEmpty() throws ApiException {
        BrandForm brandForm=getBrandForm("brand","   ");
        ResponseEntity<byte[]> response=reportDto.getInventoryReport(brandForm);
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryReportCategoryNonExistBrandCategory() throws ApiException {
        BrandForm brandForm=getBrandForm("testBrand","testCategory");
        ResponseEntity<byte[]> response=reportDto.getInventoryReport(brandForm);
    }

    @Test
    public void testGetDailySalesReport() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime start=now.minusDays(3).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime end=now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        DateForm dateForm=getDateForm(start,end);
        ResponseEntity<byte[]> response=reportDto.getDailySalesReport(dateForm);
        assertNotEquals(null,response);
    }

    @Test(expected= ApiException.class)
    public void testGetDailySalesReportStartDateGreaterThanEndDate() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime end=now.minusDays(3).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime start=now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        DateForm dateForm=getDateForm(start,end);
        ResponseEntity<byte[]> response=reportDto.getDailySalesReport(dateForm);
    }

    @Test(expected = ApiException.class)
    public void testGetDailySalesReportStartDateAndEndDateEqualCurrentDate() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime end=now.withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime start=now.withHour(0).withMinute(0).withSecond(0);
        DateForm dateForm=getDateForm(start,end);
        ResponseEntity<byte[]> response=reportDto.getDailySalesReport(dateForm);
    }

    @Test(expected = ApiException.class)
    public void testGetDailySalesReportWithNull() throws ApiException {
        DateForm dateForm=getDateForm(null,null);
        ResponseEntity<byte[]> response=reportDto.getDailySalesReport(dateForm);
    }

    @Test
    public void testGetRevenueReport() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime start=now.minusDays(3).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime end=now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        DateWithBrandCategoryForm dateWithBrandCategoryForm=getRevenueForm("brand","category",start,end);
        ResponseEntity<byte[]> response=reportDto.getRevenueReport(dateWithBrandCategoryForm);
        assertNotEquals(null,response);
    }

    @Test(expected= ApiException.class)
    public void testGetRevenueReportStartDateGreaterThanEndDate() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime end=now.minusDays(3).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime start=now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        DateWithBrandCategoryForm dateWithBrandCategoryForm=getRevenueForm("brand","category",start,end);
        ResponseEntity<byte[]> response=reportDto.getRevenueReport(dateWithBrandCategoryForm);
    }

    @Test(expected = ApiException.class)
    public void testGetRevenueReportStartDateAndEndDateEqualCurrentDate() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime end=now.withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime start=now.withHour(0).withMinute(0).withSecond(0);
        DateWithBrandCategoryForm dateWithBrandCategoryForm=getRevenueForm("brand","category",start,end);
        ResponseEntity<byte[]> response=reportDto.getRevenueReport(dateWithBrandCategoryForm);
    }

    @Test(expected = ApiException.class)
    public void testGetRevenueReportWithdatesNull() throws ApiException {
        DateWithBrandCategoryForm dateWithBrandCategoryForm=getRevenueForm("brand","category",null,null);
        ResponseEntity<byte[]> response=reportDto.getRevenueReport(dateWithBrandCategoryForm);
    }

    @Test(expected= ApiException.class)
    public void testGetRevenueReportNonExistBrandCategory() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime start=now.minusDays(3).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime end=now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        DateWithBrandCategoryForm dateWithBrandCategoryForm=getRevenueForm("testbrand","testcategory",start,end);
        ResponseEntity<byte[]> response=reportDto.getRevenueReport(dateWithBrandCategoryForm);
    }

    @Test(expected= ApiException.class)
    public void testGetRevenueReportNullBrandCategory() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime start=now.minusDays(3).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime end=now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        DateWithBrandCategoryForm dateWithBrandCategoryForm=getRevenueForm(null,null,start,end);
        ResponseEntity<byte[]> response=reportDto.getRevenueReport(dateWithBrandCategoryForm);
    }
}


