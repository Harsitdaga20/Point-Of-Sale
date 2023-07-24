package com.increff.pos.dto;

import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.ProductRevenueData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.model.form.DateForm;
import com.increff.pos.model.form.DateWithBrandCategoryForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.util.ConvertUtil;
import com.increff.pos.util.ValidateFormUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.increff.pos.util.NormalizeUtil.normalizeBrandForm;
import static com.increff.pos.util.NormalizeUtil.normalizeDateWithBrandCategoryForm;

@Service
public class ReportDto {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private DailySalesService dailySalesService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CSVGenerator csvGenerator;

    public ResponseEntity<byte[]> getBrandReport() {
        List<BrandCategoryPojo> brandCategoryPojoList = brandService.getAllBrandCategory();
        return csvGenerator.brandPojoToCSV(brandCategoryPojoList);
    }


    public ResponseEntity<byte[]> getInventoryReport(BrandForm brandForm) throws ApiException {
        ValidateFormUtil.validateForm(brandForm);
        normalizeBrandForm(brandForm);
        BrandCategoryPojo brandCategoryPojo = brandService
                .getByBrandCategory(brandForm.getBrand(), brandForm.getCategory());
        Integer brandCategoryId = brandCategoryPojo.getId();
        List<ProductPojo> productPojoList = productService.getProductByBrandCategoryId(brandCategoryId);
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        for (ProductPojo productPojo : productPojoList) {
            InventoryPojo inventoryPojo = inventoryService.getCheckProductId(productPojo.getId());
            InventoryReportData inventoryReportData = ConvertUtil.convertPojotoInventoryReportData(inventoryPojo, productPojo);
            inventoryReportDataList.add(inventoryReportData);
        }
        return csvGenerator.inventoryToCSV(inventoryReportDataList);
    }

    public ResponseEntity<byte[]> getDailySalesReport(DateForm dateForm) throws ApiException {
        ValidateFormUtil.validateForm(dateForm);
        ValidateFormUtil.validateDate(dateForm.getStart(), dateForm.getEnd());
        List<DailySalesPojo> dailySalesPojoList = dailySalesService.
                getDailySalesByDateFilter(dateForm.getStart(), dateForm.getEnd());

        return csvGenerator.dailySalesToCSV(dailySalesPojoList);
    }

    public ResponseEntity<byte[]> getRevenueReport(DateWithBrandCategoryForm dateWithBrandCategoryForm) throws ApiException {
        ValidateFormUtil.validateForm(dateWithBrandCategoryForm);
        ValidateFormUtil.validateDate(dateWithBrandCategoryForm.getStart(), dateWithBrandCategoryForm.getEnd());
        normalizeDateWithBrandCategoryForm(dateWithBrandCategoryForm);
        Integer brandCategoryId = brandService
                .getByBrandCategory(dateWithBrandCategoryForm.getBrand(), dateWithBrandCategoryForm.getCategory())
                .getId();
        List<ProductPojo> productPojoList = productService.getProductByBrandCategoryId(brandCategoryId);
        HashMap<Integer, ProductRevenueData> productRevenueDataByProductId = new HashMap<>();

        //creates productRevenueData for each Product By ProductId
        for (ProductPojo productPojo : productPojoList) {
            ProductRevenueData productRevenueData = ConvertUtil.convertProductPojotoProductRevenueData(productPojo);
            productRevenueDataByProductId.put(productPojo.getId(), productRevenueData);
        }

        List<OrderPojo> orderPojoList = orderService.
                getOrdersByDateWithInvoiced(dateWithBrandCategoryForm.getStart(),
                        dateWithBrandCategoryForm.getEnd());
        updateRevenueAndQuantityOfEachProduct(orderPojoList, productRevenueDataByProductId);
        //convert map to list;
        List<ProductRevenueData> productRevenueDataList = new ArrayList<>(productRevenueDataByProductId.values());

        return csvGenerator.productRevenueToCSV(productRevenueDataList);
    }

    private void updateRevenueAndQuantityOfEachProduct(
            List<OrderPojo> orderPojoList,
            HashMap<Integer,ProductRevenueData> productRevenueDataByProductId) throws ApiException {
        for (OrderPojo orderPojo : orderPojoList) {
            List<OrderItemPojo> orderItemPojoList = orderService.getAllOrderItemByOrderId(orderPojo.getId());
            for (OrderItemPojo orderItemPojo : orderItemPojoList) {
                ProductRevenueData productRevenueData = productRevenueDataByProductId.get(orderItemPojo.getProductId());
                if (productRevenueData != null) {
                    productRevenueData.setQuantity(productRevenueData.getQuantity() + orderItemPojo.getQuantity());
                    productRevenueData
                            .setRevenue(productRevenueData.getRevenue() +
                                    (orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice()));
                }
            }
        }
    }
}
