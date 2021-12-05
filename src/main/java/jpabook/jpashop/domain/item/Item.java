package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)//하나의 테이블에 다 넣는다
@DiscriminatorColumn(name = "dtype")//구분자를 넣어준다.
public abstract class Item {//상속하는 애들을 만들어주어야 한다.
//전략을 부모에서 정해야 된다.
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //== 비즈니스 로직 ==/
    //데이터를 가지고 있는 곳에서 데이터를 수정하는것이 가장 응집도가 높다
    //Setter를 통해서 값을 가지고 나와 수정하고 로직을 완성하는것이 아닌 엔티티 자체에서 값을 변경하는 로직이 있는것이 가장 좋다.

    /**
     * Stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;//재고 수량
    }

    /**
     * Stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
            //0보다 재고가 적을경우 발생하는 Custom 에러
        }
        this.stockQuantity = restStock;
    }
}
