package com.shop.dto;

import com.shop.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class MemberFormDto {
    private Long id;

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    @NotBlank(message = "아이디는 필수 입력입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "주소는 필수 입력입니다.")
    private String address;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String postcode; // 우편번호

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    private String tel;

    private static ModelMapper modelMapper = new ModelMapper();

    public static MemberFormDto of(Member member){
        return modelMapper.map(member, MemberFormDto.class);
    }

}
