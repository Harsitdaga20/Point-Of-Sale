package com.increff.pos.dao;

import com.increff.pos.pojo.UserPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class UserDao extends AbstractDao {


    public UserPojo selectUsers(String email, String password, String role) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<UserPojo> cq = cb.createQuery(UserPojo.class);

        Root<UserPojo> query = cq.from(UserPojo.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(email)) {
            predicates.add(cb.equal(query.get("email"), email));
        }
        if (Objects.nonNull(password)) {
            predicates.add(cb.equal(query.get("password"), password));
        }
        if (Objects.nonNull(role)) {
            predicates.add(cb.equal(query.get("role"), role));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em().createQuery(cq).getResultList().stream()
                .findFirst()
                .orElse(null);
    }


}
