package com.tavemakers.surf.domain.home.service;

import com.tavemakers.surf.domain.home.dto.request.HomeContentUpsertReqDTO;
import com.tavemakers.surf.domain.home.dto.response.HomeContentResDTO;
import com.tavemakers.surf.domain.home.entity.HomeContent;
import com.tavemakers.surf.domain.home.repository.HomeContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeContentService {

    private static final Long HOME_CONTENT_ID = 1L;

    private final HomeContentRepository homeContentRepository;

    @Transactional
    public HomeContentResDTO upsert(HomeContentUpsertReqDTO req) {

        HomeContent content = homeContentRepository.findById(HOME_CONTENT_ID)
                .orElseGet(() -> HomeContent.of(req.mainText()));

        if (homeContentRepository.existsById(HOME_CONTENT_ID)) {
            content.changeMainText(req.mainText());
        } else {
            homeContentRepository.save(content);
        }

        return HomeContentResDTO.from(content);
    }

    @Transactional(readOnly = true)
    public HomeContentResDTO get() {
        return homeContentRepository.findById(HOME_CONTENT_ID)
                .map(HomeContentResDTO::from)
                .orElse(new HomeContentResDTO(HOME_CONTENT_ID, ""));
    }
}