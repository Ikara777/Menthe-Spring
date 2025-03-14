package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String userId;

    private String password;

    @Column(unique = true)
    private String email;

    private String address;

    private String postcode;

    @Column(unique = true)
    private String tel;

    @Enumerated(EnumType.STRING)
    private Role role;


    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setUserId(memberFormDto.getUserId());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        member.setPostcode(memberFormDto.getPostcode());
        member.setTel(memberFormDto.getTel());
        member.setRole(Role.USER);
        return member;
    }

    public void updateMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        // 비밀번호 암호화 처리
        this.password = passwordEncoder.encode(memberFormDto.getPassword());
        this.email = memberFormDto.getEmail();
        this.address = memberFormDto.getAddress();
        this.postcode = memberFormDto.getPostcode();
        this.tel = memberFormDto.getTel();
    }

}
