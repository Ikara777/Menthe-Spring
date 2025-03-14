package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember;
        findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
        findMember = memberRepository.findByTel(member.getTel());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 전화번호입니다.");
        }
        findMember = memberRepository.findByUserId(member.getUserId());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 이름입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId);

        if (member == null) {
            throw new UsernameNotFoundException(userId);
        }

        return User.builder()
                .username(member.getUserId())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    // 현재 로그인한 사용자의 Member 정보를 가져오는 메서드
    public Member getMemberById(String userId) {
        Member member = memberRepository.findByUserId(userId);

        if (member == null) {
            throw new RuntimeException("로그인된 사용자를 찾을 수 없습니다.");
        }
        return member;
    }

    @Transactional(readOnly = true)
    public MemberFormDto getMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        MemberFormDto memberFormDto = MemberFormDto.of(member);
        return memberFormDto;
    }


    @Transactional
    public Long updateMember(MemberFormDto memberFormDto) throws Exception {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberFormDto.getId())
                .orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));

        // 회원 정보 업데이트
        member.updateMember(memberFormDto, passwordEncoder);

        // DB에 저장
        memberRepository.save(member);

        return member.getId();
    }

    public Map<String, String> getNamesByUserIds(List<String> userIds) {
        List<Member> members = memberRepository.findMembersByUserIds(userIds);

        // userId와 name의 매핑 생성
        return members.stream()
                .collect(Collectors.toMap(Member::getUserId, Member::getName));
    }

    // 현재 로그인한 사용자의 Member 정보를 가져오는 메서드
    public Member getMemberByNullTest(String userId) {
        Member member = memberRepository.findByUserId(userId);

        System.out.println("===맴버는 무엇을 포함할까 member==="+member);

        if(member==null){
            return null;
        }

        return member;
    }
}
