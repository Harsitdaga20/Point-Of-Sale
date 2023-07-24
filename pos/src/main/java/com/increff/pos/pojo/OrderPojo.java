package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
@Entity
@Getter
@Setter
@Table(name="orders")
public class OrderPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_sequence_generator")
    @SequenceGenerator(name="order_sequence_generator",sequenceName = "order_sequence",initialValue = 1001)
    private Integer id;
    private ZonedDateTime orderInvoicedTime;
    @Column(nullable = false)
    private String orderCode;
    @Column(nullable = false)
    private String status;
}
