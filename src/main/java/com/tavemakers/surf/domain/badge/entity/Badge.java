package com.tavemakers.surf.domain.badge.entity;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 뱃지 동적 관리는 MVP1에서는 사용하지 않을 예정.
* 관리자페이지가 나오지 않음.
* */

//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Badge extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "badge_id")
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    public Badge(String name) {
//        this.name = name;
//    }
//}