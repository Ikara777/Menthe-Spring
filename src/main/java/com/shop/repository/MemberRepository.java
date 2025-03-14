package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUserId(String userId);
    Member findByEmail(String userId);
    Member findByTel(String tel);

    @Query("SELECT m FROM Member m WHERE m.userId IN :userIds")
    List<Member> findMembersByUserIds(@Param("userIds") List<String> userIds);
}
