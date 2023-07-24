package com.increff.pos.dao;

import com.increff.pos.pojo.BrandCategoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class BrandDao extends AbstractDao {

    public List<BrandCategoryPojo> selectBrandCategory(String brand, String category) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<BrandCategoryPojo> cq = cb.createQuery(BrandCategoryPojo.class);

        Root<BrandCategoryPojo> query = cq.from(BrandCategoryPojo.class);
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(category)) {
            predicates.add(cb.equal(query.get("category"), category));
        }
        if (Objects.nonNull(brand)) {
            predicates.add(cb.equal(query.get("brand"), brand));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em().createQuery(cq).getResultList();
    }
}