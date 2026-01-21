package com.tavemakers.surf.domain.home.entity;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeContent extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 2000)
    private String message;

    public static HomeContent of(String message) {
        return HomeContent.builder()
                .id(1L)
                .message(message)
                .build();
    }

    public void changeMessage(String message) {
        this.message = message;
    }
}