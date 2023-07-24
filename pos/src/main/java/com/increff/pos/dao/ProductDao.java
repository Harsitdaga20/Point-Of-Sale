package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProductDao extends AbstractDao {


    public List<ProductPojo> selectProducts(Integer brandCategoryId, String barcode) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);

        Root<ProductPojo> query = cq.from(ProductPojo.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(brandCategoryId)) {
            predicates.add(cb.equal(query.get("brandCategoryId"), brandCategoryId));
        }
        if (Objects.nonNull(barcode)) {
            predicates.add(cb.equal(query.get("barcode"), barcode));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em().createQuery(cq).getResultList();
    }
}

