package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository { //화면에 의존적인 쿼리는 여기서 찾는다.

    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); //query 1번 -> N개
        result.forEach(o ->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //loof로 컬랙션을 직접채운다, query N번
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)"
                +" from OrderItem oi"
                +" join oi.item i"
                +" where oi.order.id = :orderId",OrderItemQueryDto.class
        ).setParameter("orderId",orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address) from Order o"
                        + " join o.member m"
                        + " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }

    public List<OrderQueryDto> findAllByDtoOptimization() {
        List<OrderQueryDto> result = findOrders();
        //이전꺼는 루프를 도는 단점이 있었음

        Collection<Long> orderIds = toOrderIds(result);

        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)"
                        +" from OrderItem oi"
                        +" join oi.item i"
                        +" where oi.order.id in :orderIds",OrderItemQueryDto.class
        )
        .setParameter("orderIds",orderIds)
        .getResultList();
        //메모리 맵에 올려놓고 매핑한다.
        Map<Long,List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderItems);
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        //쿼리가 총 2번 나간다.

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<OrderItemQueryDto> orderItems) {
        return orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    public List<OrderFlatDto> findAllByDtoFlat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto"
                        + "(o.id,m.name,o.orderDate,o.status,d.address,i.name,oi.orderPrice,oi.count)"
                +" from Order o"
                +" join o.member m"
                +" join o.delivery d"
                +" join o.orderItems oi"
                +" join oi.item i" , OrderFlatDto.class
        ).getResultList();
    }
}
