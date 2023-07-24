package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="order_item",indexes = {
        @Index(name = "idx_order_items_orderID", columnList = "orderId"),
        @Index(name = "idx_order_items_productID", columnList = "productId")
})
public class OrderItemPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_sequence_generator")
    @SequenceGenerator(name="order_item_sequence_generator",sequenceName = "order_item_sequence",initialValue = 100001)
    private Integer orderItemId;
    @Column(nullable = false)
    private Integer orderId;
    @Column(nullable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Double sellingPrice;

}
