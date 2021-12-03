package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable//내장 될수 있음을 표기
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;
    protected Address() { //JPA가 프록시 패턴을 사용하기 위해서는 기본 생성자가 필요하다
    }
    public Address(String city, String street, String zipcode) {
        //주소와 같은 경우 생성할 떄 한번만 호출되기 떄문에 Setter를 제거하고 생성자에서 값을 모두 초기화 하자

        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
