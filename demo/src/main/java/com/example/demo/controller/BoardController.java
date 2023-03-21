package com.example.demo.controller;

import com.example.demo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//http://localhost:8080/blog/
@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;


    @GetMapping({"","/"})
    public String index(Model model, @PageableDefault(size=3, sort="id",direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("boards",boardService.readAllWriting(pageable));
        return "index";
    }

    @GetMapping("/board/{id}")
    public String findById(@PathVariable int id, Model model) {
        model.addAttribute("board", boardService.readWriting(id));
        return "board/detail";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        return "board/saveForm";
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, Model model) {
        model.addAttribute("board", boardService.readWriting(id));
        return "/board/updateForm";
    }


}