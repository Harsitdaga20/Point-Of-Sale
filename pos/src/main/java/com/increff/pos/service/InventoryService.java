package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.StringUtil.getSingle;


@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    @Transactional(rollbackFor = ApiException.class)
    public void addInventory(Integer productId) {
        InventoryPojo inventoryPojo=new InventoryPojo();
        inventoryPojo.setProductId(productId);
        inventoryPojo.setQuantity(0);
        inventoryDao.insert(inventoryPojo);
    }


    @Transactional
    public InventoryPojo getInventoryByInventoryId(Integer inventoryId) throws ApiException {
        return getCheck(inventoryId);
    }

    @Transactional
    public List<InventoryPojo> getAllInventory() {
        return inventoryDao.getAll(InventoryPojo.class);
    }

    @Transactional(rollbackFor  = ApiException.class)
    public void updateInventory(Integer productId,InventoryPojo inventoryPojo) throws ApiException {
        InventoryPojo exInventoryPojo = getCheckProductId(productId);
        exInventoryPojo.setQuantity(inventoryPojo.getQuantity());
    }

    @Transactional
    public InventoryPojo getCheckProductId(Integer productId) throws ApiException {
        List<InventoryPojo> inventoryPojoList=inventoryDao
                .selectInventory(productId);
        InventoryPojo inventoryPojo =getSingle(inventoryPojoList);
        if (Objects.isNull(inventoryPojo)) {
            throw new ApiException("Inventory with given product ID does not exist, id: " + productId);
        }
        return inventoryPojo;
    }

    @Transactional
    private InventoryPojo getCheck(Integer inventoryId) throws ApiException {
        InventoryPojo inventoryPojo=inventoryDao.getById(InventoryPojo.class,inventoryId);
        if (Objects.isNull(inventoryPojo)) {
            throw new ApiException("Inventory with given ID does not exist, id: " + inventoryId);
        }
        return inventoryPojo;
    }
}
