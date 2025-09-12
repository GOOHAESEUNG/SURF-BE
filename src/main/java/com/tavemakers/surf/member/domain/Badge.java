package com.tavemakers.surf.member.domain;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge extends BaseEntity {

    @Id
    @Column(name = "badge_name")
    private String name;

    public Badge(String name) {
        this.name = name;
    }
}