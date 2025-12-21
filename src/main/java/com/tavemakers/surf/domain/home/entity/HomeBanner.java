package com.tavemakers.surf.domain.home.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String imageUrl;

    @Column(length = 1000)
    private String linkUrl;

    @Column(nullable = false)
    private Integer displayOrder;

    public static HomeBanner of(String imageUrl, String linkUrl, Integer displayOrder) {
        return HomeBanner.builder()
                .imageUrl(imageUrl)
                .linkUrl(linkUrl)
                .displayOrder(displayOrder)
                .build();
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void changeDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}