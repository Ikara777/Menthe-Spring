package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@Table(name = "Rental")
@ToString
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long id;

    private String name;

    @Column(length = 65535, columnDefinition = "TEXT") // 길이를 충분히 늘림
    private String imgUrl;
    private long totalCount;


}
