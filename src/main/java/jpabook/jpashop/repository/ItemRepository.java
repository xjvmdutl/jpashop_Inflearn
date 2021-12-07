package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){ // 새로 생성하는 객체
            em.persist(item);//처음 저장할떄 id가 없다 // 신규 등록
        }else{
            Item updateItem = em.merge(item);
            //DB에 등록된것을 가지고 온것이다
            //update 와 비슷
            //영속성 컨텍스트에서 Data를 찾아와서 모든 데이터를 바꿔치기 한다.
            //파라미터로 들어온 item 같은경우 영속성에 관리되는 대상이 아니고, merge를 통해 리턴받은 객체가 관리되는 대상이다.

        }
    }

    public Item findOne(Long id){
        return em.find(Item.class,id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i",Item.class)
                .getResultList();
    }
}
