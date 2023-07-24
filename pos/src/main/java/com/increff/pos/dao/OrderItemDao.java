package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderItemDao extends AbstractDao {

    private static final String delete_order_item_by_order_item_id =
            "delete from OrderItemPojo p where p.orderItemId=:orderItemId";

    @Transactional
    public void deleteOrderItemByOrderItemId(Integer orderItemId) {
        Query query = em().createQuery(delete_order_item_by_order_item_id);
        query.setParameter("orderItemId", orderItemId);
        query.executeUpdate();
    }

    public List<OrderItemPojo> selectOrderItems(Integer orderId, Integer productId) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<OrderItemPojo> cq = cb.createQuery(OrderItemPojo.class);

        Root<OrderItemPojo> query = cq.from(OrderItemPojo.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(orderId)) {
            predicates.add(cb.equal(query.get("orderId"), orderId));
        }
        if (Objects.nonNull(productId)) {
            predicates.add(cb.equal(query.get("productId"), productId));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em().createQuery(cq).getResultList();
    }
}

