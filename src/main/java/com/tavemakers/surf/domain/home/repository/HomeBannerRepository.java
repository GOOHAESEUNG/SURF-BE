package com.tavemakers.surf.domain.home.repository;

import com.tavemakers.surf.domain.home.entity.HomeBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeBannerRepository extends JpaRepository<HomeBanner, Long> {
    List<HomeBanner> findAllByOrderBySortOrderAsc();
}