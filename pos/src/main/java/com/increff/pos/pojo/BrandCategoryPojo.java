package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="brand_category",uniqueConstraints = {@UniqueConstraint(columnNames = {"brand","category"})})
public class BrandCategoryPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String category;

}
