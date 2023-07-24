package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class InventoryDao extends AbstractDao {

    public List<InventoryPojo> selectInventory(Integer productId) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<InventoryPojo> cq = cb.createQuery(InventoryPojo.class);

        Root<InventoryPojo> query = cq.from(InventoryPojo.class);
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(productId)) {
            predicates.add(cb.equal(query.get("productId"), productId));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em().createQuery(cq).getResultList();
    }
}
