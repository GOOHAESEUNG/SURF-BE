package com.tavemakers.surf.domain.home.repository;

import com.tavemakers.surf.domain.home.entity.HomeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeContentRepository extends JpaRepository<HomeContent, Long> {

    @Query("select hc.message from HomeContent hc where hc.id = 1")
    Optional<String> findMessage();
}
