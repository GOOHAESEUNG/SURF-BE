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
        List<Long> orderedIds = req.orderedIds();

        List<HomeBanner> banners = validateAndLoadAllBanners(orderedIds);
        if (banners.isEmpty()) return List.of();

        Map<Long, HomeBanner> map = banners.stream()
                .collect(Collectors.toMap(HomeBanner::getId, b -> b));

        int displayOrder = 1;
        for (Long id : orderedIds) {
            map.get(id).changeDisplayOrder(displayOrder++);
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

    private List<HomeBanner> validateAndLoadAllBanners(List<Long> orderedIds) {
        long total = homeBannerRepository.count();

        // 배너가 없는 경우: 요청도 비어 있어야 정상
        if (total == 0) {
            if (!orderedIds.isEmpty()) {
                throw new IllegalArgumentException("저장된 배너가 없는데 reorder 요청이 전달되었습니다.");
            }
            return List.of();
        }

        // 배너가 있는데 요청이 비어있으면 에러
        if (orderedIds.isEmpty()) {
            throw new IllegalArgumentException("전체 배너 id를 포함해야 합니다.");
        }

        // 전체 포함 검증
        if (orderedIds.size() != total) {
            throw new IllegalArgumentException("전체 배너를 포함해야 합니다.");
        }

        // 중복 id 검증
        if (orderedIds.stream().distinct().count() != orderedIds.size()) {
            throw new IllegalArgumentException("중복된 배너 id가 있습니다.");
        }

        List<HomeBanner> banners = homeBannerRepository.findAllById(orderedIds);

        // 존재하지 않는 id 검증
        if (banners.size() != orderedIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 배너 id가 포함되어 있습니다.");
        }

        return banners;
    }
}
