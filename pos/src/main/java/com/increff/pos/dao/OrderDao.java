package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderDao extends AbstractDao {


    public Integer insertOrder(OrderPojo orderPojo) {
        em().persist(orderPojo);
        return orderPojo.getId();
    }


    public List<OrderPojo> selectOrders(Integer orderId, ZonedDateTime start, ZonedDateTime end, String orderCode, String status) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);

        Root<OrderPojo> query = cq.from(OrderPojo.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(orderId)) {
            predicates.add(cb.equal(query.get("orderId"), orderId));
        }
        if (Objects.nonNull(start)) {
            predicates.add(cb.greaterThanOrEqualTo(query.get("orderInvoicedTime"), start));
        }
        if (Objects.nonNull(end)) {
            predicates.add(cb.lessThanOrEqualTo(query.get("orderInvoicedTime"), end));
        }
        if (Objects.nonNull(orderCode)) {
            predicates.add(cb.equal(query.get("orderCode"), orderCode));
        }
        if (Objects.nonNull(status)) {
            predicates.add(cb.equal(query.get("status"), status));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em().createQuery(cq).getResultList();
    }
}

