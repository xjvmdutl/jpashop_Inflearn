package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long itemId, String name,int price,int stockQuantity){
        //방법 1
        Item findItem = itemRepository.findOne(itemId);//ID 기반으로 영속성을 찾아서
        //수정 같은 경우도 의미있는 메소드를 만들어서 엔티티에 넣어서 수정해야된다.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

        //findItem으로 영속화된 데이터를 가지고 값을 변경하였기 때문에 스프링이 해당 트랜젝션이 끝날때 더티체크를 한다.
        //변경이 감지되었으므로 이상태로 종료시켜도 자동으로 쿼리를 실행시켜 준다.
        return findItem;
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
