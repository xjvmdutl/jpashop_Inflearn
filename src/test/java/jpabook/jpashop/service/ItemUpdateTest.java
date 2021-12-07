package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void 업데이트_테스트() throws Exception{
        Book book = em.find(Book.class,1L);

        //TX
        book.setName("asdfghj");

        //변경감지 == dirty check
        //TX commit
    }
    
}
