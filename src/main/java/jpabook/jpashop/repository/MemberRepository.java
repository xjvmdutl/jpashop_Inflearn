package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository //스프링 빈으로 Repository 등록
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext //JPA 엔티티 매니저를 자동으로 주입해준다.
    //SpringBoot JPA 라이브러리를 쓰게되면 더욱 간단하게 쓸수 있다.
    //SpringBoot JPA 가 @Autowired를 하게 되면 @PersistenceContext를 해주는 거와 같이 동작시켜 주기때문에 가능한 것이다.
    private final EntityManager em;



    //@PersistenceUnit
    //private EntityManagerFactory entityManagerFactory;
    //펙토리 매니져를 직접 주입받고 싶을 때 사용

    public void save(Member member){
        em.persist(member);//JPA가 member를 저장한다.
        //영속성 컨텍스트가 해당 값을 영속화 한다.
    }

    public Member findOne(Long id){
        return em.find(Member.class,id);//Member를 찾아서 반환
    }

    public List<Member> findAll(){
        //jpql를 사용해서 쿼리를 생성할수 있다.
        //SQL과 차이점 : 엔티티를 대상으로 쿼리를 발생시킨다.
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
