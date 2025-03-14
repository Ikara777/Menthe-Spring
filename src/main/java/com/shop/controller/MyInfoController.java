package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/myInfo")
@Controller
@RequiredArgsConstructor
public class MyInfoController {
    private final MemberService memberService;

    @GetMapping(value = "/update/{memberId}")
    public String member(@PathVariable("memberId") Long memberId, Model model, Principal principal) {
        try {
            MemberFormDto memberFormDto = memberService.getMemberId(memberId);
            model.addAttribute("memberFormDto", memberFormDto);
            model.addAttribute("memberId", memberId);
            return "member/myInfo";
        } catch (EntityNotFoundException e) {
            model.addAttribute("memberFormDto", new MemberFormDto());
            return "member/myInfo";
        }
    }


    @PostMapping(value = "/update/{memberId}")
    public String memberUpdate(@PathVariable("memberId") Long memberId, @ModelAttribute MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/myInfo";
        }

        try {
            memberService.updateMember(memberFormDto);
            model.addAttribute("updateSuccess", true);
            return "redirect:/?updateSuccess=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "수정 중 오류가 발생했습니다.");
            return "member/myInfo";
        }
    }
}


