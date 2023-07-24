package com.increff.pos.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.errordata.InventoryErrorData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateFormUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ErrorUtil.errors;

@Service
public class InventoryDto {
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    public InventoryData getInventoryByInventoryId(Integer inventoryId) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.getInventoryByInventoryId(inventoryId);
        return ConvertUtil.convertInventoryPojotoInventoryData(inventoryPojo,
                productService.getProductByProductId(inventoryPojo.getProductId()));
    }

    public List<InventoryData> getAllInventory() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAllInventory();
        List<InventoryData> inventoryDataList = new ArrayList<>();
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            inventoryDataList.add(ConvertUtil.convertInventoryPojotoInventoryData(inventoryPojo,
                    productService.getProductByProductId(inventoryPojo.getProductId())));
        }
        return inventoryDataList;
    }

    public void updateInventory(List<InventoryForm> inventoryFormList) throws ApiException, JsonProcessingException {
        if (inventoryFormList.isEmpty()) throw new ApiException("Inventory List is empty");
        int errorSize = 0;
        Integer count = 0;
        List<InventoryErrorData> errorData = new ArrayList<>();
        for (InventoryForm inventoryForm : inventoryFormList) {
            count++;
            InventoryErrorData inventoryErrorData = ConvertUtil.convertInventoryFormToInventoryErrorData(inventoryForm);
            try {
                NormalizeUtil.normalizeInventoryForm(inventoryForm);
                ValidateFormUtil.validateForm(inventoryForm);
                ProductPojo productPojo = productService.getByBarcode(inventoryForm.getBarcode());
                InventoryPojo inventoryPojo = ConvertUtil.convertInventoryFormtoInventoryPojo(inventoryForm);
                inventoryPojo.setProductId(productPojo.getId());
                inventoryService.updateInventory(productPojo.getId(), inventoryPojo);
            } catch (Exception e) {
                errorSize++;
                inventoryErrorData.setMessage(e.getMessage());
                inventoryErrorData.setLineNumber(count);
                errorData.add(inventoryErrorData);
            }

        }
        if (errorSize > 0) {
            errors(errorData);
        }
    }
}
