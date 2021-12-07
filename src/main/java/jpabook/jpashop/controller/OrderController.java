package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final MemberService memberService;

    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){
       List<Member> members = memberService.findMembers();
       List<Item> items = itemService.findItems();

       model.addAttribute("members",members);
       model.addAttribute("items",items);

       return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId
            ,@RequestParam("itemId") Long itemId
            ,@RequestParam("count") int count){
        //핵심 비지니스 로직이 있는경우 식별자만 넘겨주고 서비스에서 모두 적용시켜주는 것이 좋다.
        //하나의 영속성 컨택스트에서 동작시키기 위해 식별자를 받아 Transaction 에서 동작시킨다.
        orderService.order(memberId,itemId,count);
        return "redirect:/order";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch
    ,Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders",orders);
        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
