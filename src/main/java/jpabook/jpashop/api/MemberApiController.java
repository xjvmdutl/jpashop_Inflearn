package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.xml.transform.Result;
import java.util.List;
import java.util.stream.Collectors;

//@Controller @ResponseBody
@RestController //2개의 어노테이션을 합친것이다.
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        //문제가 있다.
        //회원만 출력하였지만, 회원 주문정보까지 준다.
        //@JsonIgnore로 제외하여 해결할 수 있지만, API가 많아 질수록 유지보수가 힘들어 진다.
        //Entity 에 화면에 관련된 의존성이 들어온것이다(문제)
        //또한 Entity를 수정했다고, API 스팩이 변경이 된다.
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(),collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data; //제네릭 타입으로 만든다

    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private  String name;
    }


    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        //@RequestBody -> json객체를 member에 넣어준다
        //Controller 에서 Validation 을 하고 싶으면 @Valid를 붙힌뒤, 어노테이션을 달아주면 된다.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        //Request, Response 객체를 다르게 생성해 주었다.
        //Member 엔티티를 변경하여도 API 스팩이 변경되지 않는다.
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id
            ,@RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id,request.getName()); //update 를 한뒤 해당 객체를 반환해도 되고, 안해도된다
        Member finMember = memberService.findOne(id);//커맨드랑 조회를 분리했다
        return new UpdateMemberResponse(finMember.getId(),finMember.getName());
    }


    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
