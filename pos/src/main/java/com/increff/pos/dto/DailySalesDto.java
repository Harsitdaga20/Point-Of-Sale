package com.increff.pos.dto;

import com.increff.pos.pojo.DailySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.DailySalesService;
import com.increff.pos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class DailySalesDto {
    @Autowired
    private DailySalesService dailySalesService;
    @Autowired
    private OrderService orderService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void createDailySales() throws ApiException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime startDateTime = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(00);
        ZonedDateTime endDateTime = now.minusDays(1).withHour(23).withMinute(59).withSecond(59);
        List<OrderPojo> orderPojoList = orderService.getOrdersByDateWithInvoiced(startDateTime, endDateTime);
        DailySalesPojo dailySalesPojo = getRevenueAndCounts(orderPojoList);
        dailySalesPojo.setDate(startDateTime);
        dailySalesService.addDailySale(dailySalesPojo);
    }

    private DailySalesPojo getRevenueAndCounts(List<OrderPojo> orderPojoList) throws ApiException {
        Integer totalItemsCount = 0;
        Double totalRevenue = 0.0;
        Integer totalOrders = 0;
        for (OrderPojo orderPojo : orderPojoList) {
            totalOrders++;
            List<OrderItemPojo> orderItemPojoList = orderService.getAllOrderItemByOrderId(orderPojo.getId());
            for (OrderItemPojo orderItemPojo : orderItemPojoList) {
                totalItemsCount += orderItemPojo.getQuantity();
                totalRevenue += (orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());
            }
        }
        return getDailySalesPojoFromArgs(totalItemsCount, totalRevenue, totalOrders);
    }

    private DailySalesPojo getDailySalesPojoFromArgs(Integer totalItemsCount, Double totalRevenue, Integer totalOrders) {
        DailySalesPojo dailySalesPojo = new DailySalesPojo();
        dailySalesPojo.setTotalRevenue(totalRevenue);
        dailySalesPojo.setInvoicedItemsCount(totalItemsCount);
        dailySalesPojo.setInvoicedOrdersCount(totalOrders);
        return dailySalesPojo;
    }


}
