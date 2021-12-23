package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.NewMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
//기본적으로 JPA는 트렌잭션 안에서 동작해야 한다.//javax,spring에서 제공하는것 두가지가 있지만 spring에서 사용하기 때문에 spring에서 제공하는것을 쓰는게 좋다.
//JPA가 조회하는것을 최적화 해준다, 읽기 전용임으로
@RequiredArgsConstructor//final 필드 값을 채워준다.
public class MemberService {
    /* 주입 방법 1 : 필드 주입
    @Autowired
    private MemberRepository memberRepository;// 해당 주입을 바꿀수가 없기떄문에 최근에는 많이 사용하지 않느다.
    */
    /* 주입 방법 2 : Setter 주입, 중간에 바뀔수 있는 문제가 있다.
    private MemberRepository memberRepository;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    */
    //주입 방법 3 : 생성자 주입 , 한번 생성되면 바뀔일이 없기 때문에 생성자에서 생성하여 준다
    private final NewMemberRepository memberRepository;//변경될 일이 없기 때문에 final로 해주어 컴파일시 에러를 잡는다.

    /*
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    */

    /**
     *회원 가입
     */
    @Transactional
    public Long join(Member member){
        //회원은 중복되면 안되기 때문에 validation
        validateDuplicateMember(member);
        memberRepository.save(member);
        //em.persist를 하게되면 member ID에 값이 있다(영속화가 되기 떄문에)
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        //동시에 Member A라는 이름으로 등록을 하게 될경우 문제가 발생
        //실무에서는 Member의 Name을 유니크 제약조건을 또 걸어 최악을 상황을 고려한다.
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    //회원 전체 조회

    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //한건만 조회
    @Transactional(readOnly = true)
    public Member findOne(Long memberId){
        //return memberRepository.findOne(memberId);
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public void update(Long id, String name) {
        //Member member = memberRepository.findOne(id);
        Member member = memberRepository.findById(id).get();
        member.setName(name); //영속상태에서 변경감지를 사용한다.
    }
}
