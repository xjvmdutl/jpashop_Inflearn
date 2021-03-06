package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;

import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class OrderRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class,id);
    }
    /*
    public List<Order> findAll(OrderSearch orderSearch){
     */
        //값이 있다면 쿼리는 이렇게 쓰면 되지만, 값이 없다면 문제가 된다.
        /*
        String jpql = "select o from Order o join o.member m" +
                "where o.status = :status" +
                " and m.name like :name";
        em.createQuery(jpql,Order.class)//JPA에서 Join
                .setParameter("status",orderSearch.getOrderStatus())
                .setParameter("name",orderSearch.getMemberName())
                //.setFirstResult(100)//페이징이 가능
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
       */
        //방법 1 : jpql을 동적으로 빌드해서 만든다. 실무에서 사용하지 않는다.
        //        문자열을 더해서 하는것은 실수로 인한 버그가 발생할 상황이 많이 있다.
    /*
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();

    }
    */
    /**
     *
     * JPA Criteria
     */
    //JPA에서 표준으로 동적쿼리 문제를 해결하기 위해 제공하는 함수
    //실무에서 사용하지 못한다, 유지보수성이 너무 좋지 않다.
    //무슨 쿼리를 동작시키는지 알수 없다.
    //실무에서는 QueryDSL 를 사용한다.
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq= cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object,Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = cb.like(m.<String>get("name"),"%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(criteria.toArray(new Predicate[criteria.size()]));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }
    public List<Order> findAll(OrderSearch orderSearch){
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        //Q파일을 생성해 주어야한다.
        //컴파일 시점에 오류를 다 잡아준다.
        return query
                .select(order)
                .from(order)
                .join(order.member,member)
                .where(statusEq(orderSearch.getOrderStatus())//상태가 같으면, 실행시켜준다.
                        , nameLike(orderSearch.getMemberName())) //이름값이 같다면 실행히켜준다,
                .limit(1000)
                .fetch();
    }

    private BooleanExpression nameLike(String memberName) {
        if(StringUtils.hasText(memberName))
            return null;
        return QMember.member.name.like(memberName);
    }

    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null){
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }

    public List<Order> findAllWithMemberDelivery() {
        //Order 를 조회할때 sql에서 member,delivery를 한번에 조회해서 다 가지고 온다.
        return em.createQuery(
                "select o from Order o"
                + " join fetch o.member m"
                + " join fetch o.delivery d ",Order.class
        ).getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o"
                + " join fetch o.member m"
                + " join fetch o.delivery d"
                + " join fetch o.orderItems o1"
                + " join fetch o1.item i",Order.class)
                //.setFirstResult(1)
                //.setMaxResults(100)
                //1대다 패치 조인에서는 절대 페이징을 해주면 안된다.
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o"
                        + " join fetch o.member m"
                        + " join fetch o.delivery d ",Order.class
        )
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}
