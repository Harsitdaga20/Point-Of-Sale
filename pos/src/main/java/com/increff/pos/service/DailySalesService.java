package com.increff.pos.service;

import com.increff.pos.util.StringUtil;
import com.increff.pos.dao.DailySalesDao;
import com.increff.pos.pojo.DailySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DailySalesService {
    @Autowired
    private DailySalesDao dailySalesDao;

    @Transactional(rollbackFor = ApiException.class)
    public void addDailySale(DailySalesPojo dailySalesPojo){
        dailySalesDao.insert(dailySalesPojo);
    }

    @Transactional
    public DailySalesPojo getSalesByDate(ZonedDateTime date) throws ApiException {
        ZonedDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0).withNano(0);
        getCheckDailySalesByDate(startOfDay);
        List<DailySalesPojo> dailySalesPojoList=dailySalesDao.getAll(DailySalesPojo.class);
        return StringUtil.getSingle(dailySalesPojoList);
    }

    @Transactional
    public List<DailySalesPojo> getDailySalesByDateFilter(ZonedDateTime start,ZonedDateTime end){
        return dailySalesDao.selectDailySales(null,start,end);
    }

    @Transactional
    private void getCheckDailySalesByDate(ZonedDateTime date) throws ApiException {
        List<DailySalesPojo> dailySalesPojoList=dailySalesDao
                .selectDailySales(date,null,null);
        DailySalesPojo dailySalesPojo= StringUtil.getSingle(dailySalesPojoList);
        if(Objects.isNull(dailySalesPojo)){
            throw new ApiException("No sales data of given date exist");
        }
    }
}
