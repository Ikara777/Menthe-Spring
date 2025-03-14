package com.shop.controller;

import com.shop.entity.Member;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class LayoutController {

    private final MemberService memberService;


    @ModelAttribute("currentUser")
    public Member getCurrentUser(Principal principal) {


        if (principal != null) {
            Member member = memberService.getMemberByNullTest(principal.getName());


            if(member == null) {
                return null;
            }

            return memberService.getMemberById(principal.getName());
        }


        return null;
    }
}
