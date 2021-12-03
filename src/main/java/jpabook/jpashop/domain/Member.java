package jpabook.jpashop.domain;




import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;


    //연관관계의 주인이 아니기 때문에 mappedBy로 주인이 아님의 표기
    //해당 값에 있는것이 바뀌더라도 주인이 아니기 때문에 Order가 변경되지 않는다
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();



}
