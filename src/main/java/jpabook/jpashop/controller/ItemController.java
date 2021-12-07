package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form",new BookForm());
        return "items/createItemForm";
    }
    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        //좋은 코드는 setter를 없애고 create 메소드를 만들어 생성하게 하는것이 가장 좋다
        itemService.saveItem(book);
        return "redirect:/";
   }

   @GetMapping("/items")
   public String list(Model model){
        model.addAttribute("items",itemService.findItems());
        return "items/itemList";
   }


    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId,Model model){
        Book item = (Book)itemService.findOne(itemId);
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form",form);
        return "items/updateItemForm";
    }

    //PathVariable 를 사용하면, 해당 id를 변경해서 다른 사람껄 변경할 수 있기때문에, 보안상 권한체크를 해서 사용해야 한다.
    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId,
                             @ModelAttribute("form") BookForm form){
        //객체는 새로 만들었지만 ID가 있으므로, DB를 한번 갔다온 데이터로 준영속 엔티티라고 한다.
        //영속성 컨택스트가 더는 관리하지 않는다.
        //이러한 준영속 엔티티를 수정하는 방법 2가지
        /*
        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
         */
        itemService.updateItem(itemId,form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }


}
