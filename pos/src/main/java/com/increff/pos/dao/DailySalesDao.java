package com.increff.pos.dao;

import com.increff.pos.pojo.DailySalesPojo;
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
public class DailySalesDao extends AbstractDao {


    public List<DailySalesPojo> selectDailySales(ZonedDateTime date, ZonedDateTime start, ZonedDateTime end) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<DailySalesPojo> cq = cb.createQuery(DailySalesPojo.class);

        Root<DailySalesPojo> query = cq.from(DailySalesPojo.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(date)) {
            predicates.add(cb.equal(query.get("date"), date));
        }
        if (Objects.nonNull(start)) {
            predicates.add(cb.greaterThanOrEqualTo(query.get("date"), start));
        }
        if (Objects.nonNull(end)) {
            predicates.add(cb.lessThanOrEqualTo(query.get("date"), end));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em().createQuery(cq).getResultList();
    }
}
