package com.increff.pos.service;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import com.increff.pos.model.data.InventoryReportData;
import com.increff.pos.model.data.ProductRevenueData;
import com.increff.pos.pojo.BrandCategoryPojo;
import com.increff.pos.pojo.DailySalesPojo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class CSVGenerator {

    public ResponseEntity<byte[]> brandPojoToCSV(List<BrandCategoryPojo> brandCategoryPojoList) {
        String[] csvHeader = {
                "ID", "BRAND", "CATEGORY"
        };
        String filename = "BrandReport.csv";
        byte[] csvBytes = generateCSV(csvHeader, brandCategoryPojoList);

        return prepareResponse(csvBytes, filename);
    }

    public ResponseEntity<byte[]> inventoryToCSV(List<InventoryReportData> inventoryReportDataList) {
        String[] csvHeader = {
                "PRODUCT NAME", "MRP", "QUANTITY"
        };
        String filename = "InventoryReport.csv";
        byte[] csvBytes = generateCSV(csvHeader, inventoryReportDataList);

        return prepareResponse(csvBytes, filename);
    }

    public ResponseEntity<byte[]> dailySalesToCSV(List<DailySalesPojo> dailySalesPojoList) {
        String[] csvHeader = {
                "DATE", "INVOICED ORDER COUNT", "INVOICED ITEMS COUNT", "TOTAL REVENUE"
        };

        String filename = "DailySalesReport.csv";
        byte[] csvBytes = generateCSV(csvHeader, dailySalesPojoList);

        return prepareResponse(csvBytes, filename);
    }

    public ResponseEntity<byte[]> productRevenueToCSV(List<ProductRevenueData> productRevenueDataList) {
        String[] csvHeader = {
                "PRODUCT NAME", "MRP", "QUANTITY", "REVENUE"
        };

        String filename = "RevenueReport.csv";
        byte[] csvBytes = generateCSV(csvHeader, productRevenueDataList);

        return prepareResponse(csvBytes, filename);
    }

    private <T> byte[] generateCSV(String[] csvHeader, List<T> dataList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (
                PrintWriter printWriter = new PrintWriter(out);
                CSVPrinter csvPrinter = new CSVPrinter(printWriter, CSVFormat.DEFAULT.withHeader(csvHeader))
        ) {
            for (T data : dataList) {
                if (data instanceof DailySalesPojo) {
                    DailySalesPojo dailySalesPojo = (DailySalesPojo) data;
                    csvPrinter.printRecord(dailySalesPojo.getDate().toLocalDate(),
                            dailySalesPojo.getInvoicedOrdersCount(),
                            dailySalesPojo.getInvoicedItemsCount(),
                            dailySalesPojo.getTotalRevenue());
                } else if (data instanceof ProductRevenueData) {
                    ProductRevenueData productRevenueData = (ProductRevenueData) data;
                    csvPrinter.printRecord(productRevenueData.getProductName(),
                            productRevenueData.getMrp(),
                            productRevenueData.getQuantity(),
                            productRevenueData.getRevenue());
                }
                else if (data instanceof BrandCategoryPojo) {
                    BrandCategoryPojo brandCategoryPojo = (BrandCategoryPojo) data;
                    csvPrinter.printRecord(
                            brandCategoryPojo.getId(),
                            brandCategoryPojo.getBrand(),
                            brandCategoryPojo.getCategory()
                    );
                } else if (data instanceof InventoryReportData) {
                    InventoryReportData inventoryReportData = (InventoryReportData) data;
                    csvPrinter.printRecord(
                            inventoryReportData.getProductName(),
                            inventoryReportData.getMrp(),
                            inventoryReportData.getQuantity()
                    );
                }
                csvPrinter.flush();
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return out.toByteArray();
    }

    private ResponseEntity<byte[]> prepareResponse(byte[] csvBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(csvBytes);
    }
}
