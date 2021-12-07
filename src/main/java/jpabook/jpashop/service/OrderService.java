package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;

    private final ItemRepository itemRepository;
    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId,Long itemId,int count){
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item,item.getPrice(),count);
        //다른 로직에서 내가 만든 createOrderItem을 사용하는것이 아니라, setter로 만들수도 있다.
        //OrderItem o = new OrderItem();//에러 발생
        //주문 생성
        Order order = Order.createOrder(member,delivery,orderItem);

        //주문 저장
        //cascade 옵션 때문에 order를 persist 하면 연관된 컬럼도 persist 해준다
        //cascade는 다른곳에서 참조할수 없는 곳에서 사용하는 것이 좋다.
        //현재는 order만 orderItem,delivery 를 사용하기 때문에 cascade 옵션을 사용한다.
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        //SQL을 직접 사용하는 라이브러리 같은경우 직접 쿼리를 작성하고 업데이트를 해줘야 하지만 JPA는 더티체크를 해서 변경된 부분을 알아서 변경해 주기 때문에 훨씬 로직이 간단해 진다.
    }

    /**
     * 검색
     */

    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }
}
