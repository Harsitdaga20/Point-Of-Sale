package com.increff.pos.service;

import com.increff.pos.util.StringUtil;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;


@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private OrderItemDao orderItemDao;

    @Transactional(rollbackFor = ApiException.class)
    public Integer addOrder(OrderPojo orderPojo){
        String orderCode= StringUtil.generateOrderCode();
        List<OrderPojo> orderPojoList=orderDao.
                selectOrders(null,null,null,orderCode,null);

        OrderPojo exOrderPojo= StringUtil.getSingle(orderPojoList);
        while(!Objects.isNull(exOrderPojo)){
            orderCode= StringUtil.generateOrderCode();
            orderPojoList=orderDao.
                    selectOrders(null,null,null,orderCode,null);
            exOrderPojo= StringUtil.getSingle(orderPojoList);
        }
        orderPojo.setOrderCode(orderCode);
        return orderDao.insertOrder(orderPojo);
    }

    @Transactional
    public List<OrderPojo> getAllOrders() {
        return orderDao.getAll(OrderPojo.class);
    }

    @Transactional
    public List<OrderPojo> getOrdersByDateWithInvoiced(ZonedDateTime start,ZonedDateTime end){
        return orderDao.selectOrders(null,start,end,null,"invoiced");
    }

    @Transactional(rollbackFor = ApiException.class)
    public void invoiceOrder(Integer orderId) throws ApiException{
        OrderPojo exOrderPojo=getCheckOrder(orderId);
        List<OrderItemPojo> orderItemPojoList=getAllOrderItemByOrderId(orderId);
        if(orderItemPojoList.size()== 0){
            throw new ApiException("Order with no items in cart can't be invoiced");
        }
        if(Objects.equals(exOrderPojo.getStatus(),"invoiced")){
            throw new ApiException("Order already invoiced");
        }
        exOrderPojo.setStatus("invoiced");
        exOrderPojo.setOrderInvoicedTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    @Transactional
    public OrderPojo getCheckOrder(Integer orderId) throws ApiException{
        OrderPojo orderPojo=orderDao.getById(OrderPojo.class,orderId);
        if(Objects.isNull(orderPojo)){
            throw new ApiException("No order with given Order Id exist, id:"+orderId);
        }
        return orderPojo;
    }

    @Transactional
    public OrderPojo getCheckByOrderCode(String orderCode) throws ApiException{
        List<OrderPojo> orderPojoList=orderDao
                .selectOrders(null,null,null,orderCode,null);
        OrderPojo orderPojo= StringUtil.getSingle(orderPojoList);
        if(Objects.isNull(orderPojo)){
            throw new ApiException("No order with given Order Code exist");
        }
        return orderPojo;
    }
    
    //OrderItem codes

    @Transactional(rollbackFor = ApiException.class)
    public void addOrderItem(OrderItemPojo orderItemPojo) throws ApiException {
        OrderPojo exOrderPojo=getCheckOrder(orderItemPojo.getOrderId());
        getCheckOrderItemAlreadyExist(orderItemPojo);
        if(Objects.equals(exOrderPojo.getStatus(),"invoiced")){
            throw new ApiException("Invoiced order can't be changed");
        }
        List<OrderItemPojo> orderItemPojoList=orderItemDao
                .selectOrderItems(orderItemPojo.getOrderId(), orderItemPojo.getProductId());
        OrderItemPojo exOrderItemPojo= StringUtil.getSingle(orderItemPojoList);
        if(Objects.isNull(exOrderItemPojo)){
            orderItemDao.insert(orderItemPojo);
        }
        else{
            exOrderItemPojo.setQuantity(exOrderItemPojo.getQuantity()+exOrderItemPojo.getQuantity());
            exOrderItemPojo.setSellingPrice(orderItemPojo.getSellingPrice());
        }
        
    }
    @Transactional(rollbackFor = ApiException.class)
    public void deleteOrderItem(Integer orderItemId) throws ApiException {
        OrderItemPojo orderItemPojo=getCheckByOrderItemId(orderItemId);
        OrderPojo exOrderPojo=getCheckOrder(orderItemPojo.getOrderId());
        if(Objects.equals(exOrderPojo.getStatus(),"invoiced")){
            throw new ApiException("Invoiced order can't be changed");
        }
        orderItemDao.deleteOrderItemByOrderItemId(orderItemId);
    }



    @Transactional
    public List<OrderItemPojo> getAllOrderItemByOrderId(Integer orderId) throws ApiException {
        getCheckOrder(orderId);
        return orderItemDao.selectOrderItems(orderId,null);
    }

    @Transactional
    public OrderItemPojo getCheckByOrderItemId(Integer orderItemId) throws ApiException {
        OrderItemPojo orderItemPojo=orderItemDao.getById(OrderItemPojo.class,orderItemId);
        if(Objects.isNull(orderItemPojo)){
            throw new ApiException("Order Item with given id doesn't exist ,id:"+orderItemId);
        }
        return orderItemPojo;
    }

    @Transactional
    private void getCheckOrderItemAlreadyExist(OrderItemPojo orderItemPojo) throws ApiException {
        List<OrderItemPojo> orderItemPojoList=orderItemDao.
                selectOrderItems(orderItemPojo.getOrderId(),
                        orderItemPojo.getProductId());
        OrderItemPojo exOrderItemPojo= StringUtil.getSingle(orderItemPojoList);
        if(Objects.nonNull(exOrderItemPojo))
            throw new ApiException("OrderItem Already Exist in Order kindly edit it");
    }

}

