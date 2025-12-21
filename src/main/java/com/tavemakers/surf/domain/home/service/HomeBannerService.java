package com.tavemakers.surf.domain.home.service;

import com.tavemakers.surf.domain.home.dto.request.HomeBannerCreateReqDTO;
import com.tavemakers.surf.domain.home.dto.request.HomeBannerReorderReqDTO;
import com.tavemakers.surf.domain.home.dto.request.HomeBannerUpdateReqDTO;
import com.tavemakers.surf.domain.home.dto.response.HomeBannerResDTO;
import com.tavemakers.surf.domain.home.entity.HomeBanner;
import com.tavemakers.surf.domain.home.repository.HomeBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeBannerService {

    private final HomeBannerRepository homeBannerRepository;

    @Transactional
    public HomeBannerResDTO createBanner(HomeBannerCreateReqDTO req) {
        int nextOrder = homeBannerRepository.findMaxDisplayOrder().orElse(0) + 1;

        HomeBanner banner = HomeBanner.builder()
                .imageUrl(req.imageUrl())
                .linkUrl(req.linkUrl())
                .displayOrder(nextOrder)
                .build();

        return HomeBannerResDTO.from(homeBannerRepository.save(banner));
    }

    @Transactional(readOnly = true)
    public List<HomeBannerResDTO> getBanners() {
        return homeBannerRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(HomeBannerResDTO::from)
                .toList();
    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        HomeBanner target = homeBannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("배너가 존재하지 않습니다."));

        homeBannerRepository.delete(target);

        List<HomeBanner> remain = homeBannerRepository.findAllByOrderByDisplayOrderAsc();

        // 1부터 다시 정렬
        int order = 1;
        for (HomeBanner banner : remain) {
            if (!banner.getDisplayOrder().equals(order)) {
                banner.changeDisplayOrder(order);
            }
            order++;
        }
    }

    @Transactional
    public List<HomeBannerResDTO> reorderBanners(HomeBannerReorderReqDTO req) {
        List<Long> ids = req.items().stream().map(HomeBannerReorderReqDTO.Item::id).toList();
        Map<Long, Integer> orderMap = req.items().stream()
                .collect(Collectors.toMap(HomeBannerReorderReqDTO.Item::id, HomeBannerReorderReqDTO.Item::displayOrder));

        List<HomeBanner> banners = homeBannerRepository.findAllById(ids);
        for (HomeBanner b : banners) {
            Integer newOrder = orderMap.get(b.getId());
            if (newOrder != null) b.changeDisplayOrder(newOrder);
        }

        return homeBannerRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(HomeBannerResDTO::from)
                .toList();
    }

    @Transactional
    public HomeBannerResDTO updateBanner(Long bannerId, HomeBannerUpdateReqDTO req) {
        HomeBanner banner = homeBannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("배너가 존재하지 않습니다."));

        banner.changeImageUrl(req.imageUrl());

        if (req.linkUrl() != null)
            banner.changeLinkUrl(req.linkUrl());

        return HomeBannerResDTO.from(banner);
    }
}
