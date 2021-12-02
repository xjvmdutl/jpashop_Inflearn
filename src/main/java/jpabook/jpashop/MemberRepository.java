package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;//스프링 컨테이너가 다 만들어 준다.

    public Long save(Member member){
        em.persist(member);
        return member.getId();//저장 후에는 ID정도만 리턴하도록 설계
    }

    public Member find(Long id){
        return em.find(Member.class,id);
    }

}
