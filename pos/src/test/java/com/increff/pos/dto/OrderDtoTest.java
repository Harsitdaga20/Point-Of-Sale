package com.increff.pos.dto;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.CartItemForm;
import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.util.GetForm;
import com.increff.pos.util.GetPojo;
import com.increff.pos.config.AbstractUnitTest;
import com.increff.pos.dao.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class OrderDtoTest extends AbstractUnitTest {
    @Autowired
    private OrderDto orderDto;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;

    @Before
    public void setup() throws IOException, ApiException {
        BrandCategoryPojo brandCategoryPojo = GetPojo.getBrandPojo("brand","category");
        brandDao.insert(brandCategoryPojo);

        Integer brandCategoryId=brandDao
                .selectBrandCategory("brand","category")
                .get(0)
                .getId();
        ProductPojo productPojo1= GetPojo.getProductPojo("barcode1",brandCategoryId,"product1",123.45);
        productDao.insert(productPojo1);

        ProductPojo productPojo2= GetPojo.getProductPojo("barcode2",brandCategoryId,"product2",123.45);
        productDao.insert(productPojo2);

        Integer productId1=productDao
                .selectProducts(brandCategoryId,"barcode1")
                .get(0)
                .getId();
        Integer productId2=productDao
                .selectProducts(brandCategoryId,"barcode2")
                .get(0)
                .getId();

        InventoryPojo inventoryPojo1= GetPojo.getInventoryPojo(productId1,100);
        inventoryDao.insert(inventoryPojo1);

        InventoryPojo inventoryPojo2= GetPojo.getInventoryPojo(productId2,100);
        inventoryDao.insert(inventoryPojo2);

    }

    @Test
    public void testAddNewOrderAndInventory() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        List<OrderData> orderDataList =orderDto.getAllOrders();
        assertEquals(1,orderDataList.size());
        OrderItemData orderItemData=orderDto.getOrderItemByOrderId(orderDataList.get(0).getOrderId()).get(0);
        assertEquals((Integer)90,inventoryDao
                .selectInventory(orderItemData.getProductId())
                .get(0)
                .getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testAddEmptyNewOrder() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        orderDto.addNewOrder(cartItemFormList);
    }

    @Test(expected= ApiException.class)
    public void testAddHighQuantityNewOrder() throws ApiException{
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode2",1001,100.1));
        orderDto.addNewOrder(cartItemFormList);
        List<OrderData> orderDataList =orderDto.getAllOrders();
    }

    @Test(expected = ApiException.class)
    public void testAddHighSellingPriceNewOrder() throws ApiException{
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,1000.1));
        orderDto.addNewOrder(cartItemFormList);
        List<OrderData> orderDataList =orderDto.getAllOrders();
    }

    @Test(expected = ApiException.class)
    public void testAddNonExistingProductNewOrder() throws ApiException{
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode3",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        List<OrderData> orderDataList =orderDto.getAllOrders();
    }

    @Test
    public void testGetOrderByOrderID() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        OrderData orderData1=orderDto.getOrderByOrderId(orderData.getOrderId());
        assertEquals(orderData.getOrderCode(),orderData1.getOrderCode());
        assertEquals("created",orderData1.getStatus());
        assertEquals(orderData.getOrderCreatedTime(),orderData1.getOrderCreatedTime());
        assertEquals(null,orderData1.getOrderInvoicedTime());
    }

    @Test(expected= ApiException.class)
    public void testGetOrderBYNonExistOrderId() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        orderDto.getOrderByOrderId(orderData.getOrderId()+1);
    }

    @Test
    public void testGetOrderByOrderCode() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        OrderData orderData1=orderDto.getOrderByOrderCode(orderData.getOrderCode());
        assertEquals(orderData.getOrderId(),orderData1.getOrderId());
        assertEquals("created",orderData1.getStatus());
        assertEquals(orderData.getOrderCreatedTime(),orderData1.getOrderCreatedTime());
        assertEquals(null,orderData1.getOrderInvoicedTime());
    }

    @Test(expected= ApiException.class)
    public void testGetOrderBYNonExistOrderCode() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        orderDto.getOrderByOrderCode(orderData.getOrderCode()+'a');
    }
    @Test
    public void testGetAllOrder() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        List<CartItemForm> cartItemFormList2=new ArrayList<>();
        cartItemFormList2.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList2);
        List<OrderData> orderDataList=orderDto.getAllOrders();
        assertEquals(2,orderDataList.size());
    }

    @Test
    public void testGetOrderItemsByOrderId() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        List<OrderItemData> orderItemDataList=orderDto.getOrderItemByOrderId(orderData.getOrderId());
        assertEquals(1,orderItemDataList.size());
    }

    @Test(expected= ApiException.class)
    public void testGetOrderItemsByNonExistOrderId() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        List<OrderItemData> orderItemDataList=orderDto.getOrderItemByOrderId(orderData.getOrderId()+1);
    }

    @Test
    public void testInvoiceMarked() throws ApiException, IOException {
        List<CartItemForm> cartItemFormList = new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1", 10, 100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData = orderDto.getAllOrders().get(0);

        try {
            orderDto.invoiceOrder(orderData.getOrderId());
            orderData = orderDto.getOrderByOrderId(orderData.getOrderId());
            assertEquals("invoiced", orderData.getStatus());
        } catch (Exception e) {
        }
    }

    @Test(expected = Exception.class)
    public void testInvoiceMarkedNonExistOrderId() throws ApiException, IOException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        orderDto.invoiceOrder(orderData.getOrderId()+1);
    }

    @Test(expected = Exception.class)
    public void testAlreadyInvoiceMarked() throws ApiException, IOException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        orderDto.invoiceOrder(orderData.getOrderId());
        orderData=orderDto.getOrderByOrderId(orderData.getOrderId());
        assertEquals("invoiced",orderData.getStatus());
        orderDto.invoiceOrder(orderData.getOrderId());
    }


    @Test(expected = Exception.class)
    public void testUpdateOrderForInvoiced() throws ApiException, IOException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        orderDto.invoiceOrder(orderData.getOrderId());
        orderData=orderDto.getOrderByOrderId(orderData.getOrderId());
        assertEquals("invoiced",orderData.getStatus());
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList);
    }

    @Test
    public void testUpdateOrderWithAddAndInventory() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        cartItemFormList.add(GetForm.getCartItemForm("barcode2",10,100.1));
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList);
        orderData=orderDto.getAllOrders().get(0);
        List<OrderItemData> orderItemDataList=orderDto.getOrderItemByOrderId(orderData.getOrderId());
        assertEquals(2,orderItemDataList.size());
        assertEquals((Integer)90,inventoryDao
                .selectInventory(orderItemDataList.get(0).getProductId())
                .get(0)
                .getQuantity());
        assertEquals((Integer)90,inventoryDao
                .selectInventory(orderItemDataList.get(1).getProductId())
                .get(0)
                .getQuantity());
        assertEquals("created",orderData.getStatus());
    }

    @Test
    public void testUpdateOrderWithDeleteAndInventory() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        cartItemFormList.add(GetForm.getCartItemForm("barcode2",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        cartItemFormList.remove(cartItemFormList.size()-1);
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList);
        orderData=orderDto.getAllOrders().get(0);
        List<OrderItemData> orderItemDataList=orderDto.getOrderItemByOrderId(orderData.getOrderId());
        assertEquals(1,orderItemDataList.size());
        assertEquals((Integer)90,inventoryDao
                .selectInventory(orderItemDataList.get(0).getProductId())
                .get(0)
                .getQuantity());
        assertEquals((Integer)100,inventoryDao
                .selectInventory(
                        productDao
                                .selectProducts(null,"barcode2")
                                .get(0).
                                getId())
                .get(0)
                .getQuantity());
        assertEquals("created",orderData.getStatus());
    }

    @Test(expected= ApiException.class)
    public void testUpdateOrderWithZeroItems() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        List<CartItemForm> cartItemFormList2=new ArrayList<>();
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList2);
    }

    @Test(expected= ApiException.class)
    public void testUpdateOrderWithHighQuantity() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        cartItemFormList.add(GetForm.getCartItemForm("barcode2",102,100.1));
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList);
    }

    @Test(expected= ApiException.class)
    public void testUpdateOrderWithHighSellingPrice() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        cartItemFormList.add(GetForm.getCartItemForm("barcode2",102,10000.1));
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList);
    }

    @Test(expected= ApiException.class)
    public void testUpdateOrderWithNonExistentBarcode() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        cartItemFormList.add(GetForm.getCartItemForm("barcode3",102,100.1));
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList);
    }

    @Test(expected= ApiException.class)
    public void testUpdateOrderWithZeroItemInvoiced() throws ApiException, IOException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        cartItemFormList.clear();
        orderDto.updateOrder(orderData.getOrderId(),cartItemFormList);
        orderDto.invoiceOrder(orderData.getOrderId());
    }

    @Test(expected= ApiException.class)
    public void testUpdateOrderWithNonExistOrderId() throws ApiException {
        List<CartItemForm> cartItemFormList=new ArrayList<>();
        cartItemFormList.add(GetForm.getCartItemForm("barcode1",10,100.1));
        orderDto.addNewOrder(cartItemFormList);
        OrderData orderData=orderDto.getAllOrders().get(0);
        orderDto.updateOrder(orderData.getOrderId()+1,cartItemFormList);
    }

}

