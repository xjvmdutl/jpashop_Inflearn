package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 총 주문 2개
 * UserA
 *  JPA1 BOOK
 *  JPA2 BOOK
 * UserB
 *  SPRING1 BOOK
 *  SPRING2 BOOK
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;
    
    @PostConstruct //스프링이 로딩이 되면 실행시켜준다,
    public void init(){
        initService.doInit1();
        initService.doInit2();
        //트랜잭션 떄문에 별도의 빈으로 등록해야된다.
    }
    
    
    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;
        public void doInit1(){
            Member member = createMember("userA","서울","1","1111");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1,10000,1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,20000,2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member,delivery,orderItem1,orderItem2);
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        private Member createMember(String name,String city,String street,String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city,street,zipcode));
            return member;
        }

        public void doInit2(){
            Member member = createMember("userB","부산","2","2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1,20000,1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,40000,2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member,delivery,orderItem1,orderItem2);
            em.persist(order);
        }
        public static void main(String[] args) {

        }
    }
}
