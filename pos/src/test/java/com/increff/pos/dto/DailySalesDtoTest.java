package com.increff.pos.dto;

import com.increff.pos.dao.*;
import com.increff.pos.pojo.*;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.DailySalesService;
import com.increff.pos.config.AbstractUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertEquals;
import static com.increff.pos.util.GetPojo.*;
import static com.increff.pos.util.GetPojo.getInventoryPojo;

public class DailySalesDtoTest extends AbstractUnitTest {

    @Autowired
    private DailySalesDto dailySalesDto;

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
    public void setup(){
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
    }

    @Test
    public void testCreateDailySales() throws ApiException {
        dailySalesDto.createDailySales();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        DailySalesPojo dailySalesPojo= dailySalesService.getSalesByDate(now.minusDays(1).withHour(7).withMinute(12).withSecond(11));
        assertEquals((Integer)20,dailySalesPojo.getInvoicedItemsCount());
        assertEquals((Integer)1,dailySalesPojo.getInvoicedOrdersCount());
        assertEquals((Double)1215.0,dailySalesPojo.getTotalRevenue());
    }

    @Test(expected= ApiException.class)
    public void testCreateDailySalesFetchNonExistDate() throws ApiException {
        dailySalesDto.createDailySales();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        DailySalesPojo dailySalesPojo= dailySalesService.getSalesByDate(now.plusDays(1).withHour(7).withMinute(12).withSecond(11));
    }
}
