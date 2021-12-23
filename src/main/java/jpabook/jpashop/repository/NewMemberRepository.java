package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//이미 공동화 할수 있는 모든 코드를 구현해 두었다.
public interface NewMemberRepository extends JpaRepository<Member,Long> {
    //findByName 같은 메소드는 공동화 할수 없는 것이다.
    //내가 직접 만들어야 한다.
    //원래는 JPQL을 작성해 주어야 하지만 메소드 명을 규칙에 맞게 만들게 된다면 알아서 JPQL을 작성해 준다.
    //select m from member m where name = ?
    List<Member> findByName(String name);
}
