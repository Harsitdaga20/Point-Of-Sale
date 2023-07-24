package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name="daily_sales")
public class DailySalesPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private ZonedDateTime date;
    @Column(nullable = false)
    private Integer invoicedOrdersCount;
    @Column(nullable = false)
    private Integer invoicedItemsCount;
    @Column(nullable = false)
    private Double totalRevenue;
}
