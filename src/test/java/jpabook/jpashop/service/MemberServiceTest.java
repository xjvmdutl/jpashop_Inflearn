package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;

    @Autowired MemberRepository memberRepository;

    @Autowired
    EntityManager em;
    @Test
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);
        //쿼리를 확인해보니 Insert를 실행하지 않는다.
        //why? JPA가 Transaction commit을 할때 저장이 실행이 되기 때문(em.persist 를 하더라도 시작되는것이 아니다.)
        //Test에서 @Transactional어노테이션이 달릴경우 기본으로 Rollback이 실행되기 떄문에 insert가 실행되지 않는것이다.
        //em.flush();

        //then
        assertEquals(member,memberRepository.findOne(saveId));
    }
    
    @Test()
    public void 중복_회원_예약() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        //when
        memberService.join(member1);
        assertThrows(IllegalStateException.class,() -> memberService.join(member2));
        //JUnit5 에서 제공하는 Exception을 잡는 방식
        //then
        //위에서 에러가 발생해서 여기까지 코드가 흐르게 되면 안된다,
        //fail("예외가 발생해야 한다.");//코드가 여기까지 오면 안된다.
    }
}