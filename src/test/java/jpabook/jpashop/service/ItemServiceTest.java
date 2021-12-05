package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    
    @Test
    @Rollback(value = false)
    public void 아이템_저장() throws Exception{
        //given
        Item book = new Book();

        //when
        itemService.saveItem(book);

        //then
        assertEquals(book,itemRepository.findOne(book.getId()));
    }

    @Test
    @Rollback(value = false)
    public void 아이템_저장_업데이트() throws Exception{
        //given
        Item book = new Book();

        //when
        itemService.saveItem(book);
        book.addStock(200);
        itemService.saveItem(book);

        //then
        assertEquals(book,itemRepository.findOne(book.getId()));
        assertEquals(book.getStockQuantity(),itemRepository.findOne(book.getId()).getStockQuantity());
    }
    @Test
    public void 에러_발생() throws Exception{
        //given
        Item item = new Book();
        item.addStock(5000);
        //when
        
        //then
        assertThrows(NotEnoughStockException.class,() ->item.removeStock(6000));//에러 발생
    }
}