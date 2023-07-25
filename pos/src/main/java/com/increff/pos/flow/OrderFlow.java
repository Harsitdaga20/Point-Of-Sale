package com.increff.pos.flow;

import com.increff.pos.model.form.CartItemForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class OrderFlow {
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;

    @Transactional(rollbackFor = ApiException.class)
    public void addNewOrder(List<CartItemForm> cartItemFormList) throws ApiException {
        OrderPojo orderPojo = ConvertUtil.convertOrderPojo();
        if (cartItemFormList.size() == 0) {
            throw new ApiException("Empty order can't be created");
        }
        checkForInventoryExistAndMrpForCart(cartItemFormList);
        Integer orderId = orderService.addOrder(orderPojo);
        addCartItemListToOrder(orderId, cartItemFormList);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void updateOrder(Integer orderId, List<CartItemForm> cartItemFormList) throws ApiException {
        if (cartItemFormList.isEmpty()) {
            throw new ApiException("CartItem can't be empty");
        }
        List<OrderItemPojo> orderItemPojoList = orderService.getAllOrderItemByOrderId(orderId);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            deleteOrderItem(orderItemPojo.getOrderItemId());
        }
        checkForInventoryExistAndMrpForCart(cartItemFormList);
        addCartItemListToOrder(orderId, cartItemFormList);
    }


    @Transactional(rollbackFor = ApiException.class)
    private void deleteOrderItem(Integer orderItemId) throws ApiException {
        OrderItemPojo orderItemPojo = orderService.getCheckByOrderItemId(orderItemId);
        InventoryPojo inventoryPojo = inventoryService.getCheckProductId(orderItemPojo.getProductId());
        inventoryPojo.setQuantity(inventoryPojo.getQuantity() + orderItemPojo.getQuantity());
        inventoryService.updateInventory(orderItemPojo.getProductId(), inventoryPojo);
        orderService.deleteOrderItem(orderItemId);
    }

    private void checkForInventoryExistAndMrpForCart(List<CartItemForm> cartItemFormList) throws ApiException {
        StringBuilder errorMessage = new StringBuilder();
        for (CartItemForm cartItemForm : cartItemFormList) {
            try{
                ProductPojo productPojo = productService.getByBarcode(cartItemForm.getBarcode());
                InventoryPojo inventoryPojo = inventoryService.getCheckProductId(productPojo.getId());
                String productName = productPojo.getName();
                if (inventoryPojo.getQuantity() < cartItemForm.getQuantity()) {
                    throw new ApiException("No inventory Present for the item :"
                            +productName+". Total quantity left is : "+inventoryPojo.getQuantity()+". *");
                }
                if (cartItemForm.getSellingPrice() > productPojo.getMrp()) {
                    throw new ApiException("Selling price for "
                            +productPojo.getName()+" can't be greater than "+productPojo.getMrp()+". *");
                }
            }
            catch(Exception e){
                errorMessage.append(e.getMessage());
            }
        }
        String errors = errorMessage.toString();
        if (errors.length() > 0) throw new ApiException(errors);
    }

    @Transactional(rollbackFor = ApiException.class)
    private void updateInventory(OrderItemPojo orderItemPojo) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.getCheckProductId(orderItemPojo.getProductId());
        ProductPojo productPojo = productService.getProductByProductId(orderItemPojo.getProductId());
        inventoryPojo.setQuantity(inventoryPojo.getQuantity() - orderItemPojo.getQuantity());
        inventoryService.updateInventory(productPojo.getId(), inventoryPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    private void addCartItemListToOrder(Integer orderId, List<CartItemForm> cartItemFormList) throws ApiException {
        for (CartItemForm cartItemForm : cartItemFormList) {
            OrderItemPojo orderItemPojo = ConvertUtil.convertCartItemFormtoOrderItemPojo(cartItemForm);
            orderItemPojo.setProductId(productService.getByBarcode(cartItemForm.getBarcode()).getId());
            orderItemPojo.setOrderId(orderId);
            updateInventory(orderItemPojo);
            orderService.addOrderItem(orderItemPojo);
        }
    }
}
