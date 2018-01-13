package com.github.conanchen.gedit.user.repository;

import com.github.conanchen.gedit.user.model.CaptchaImg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaptchaImgRepository extends JpaRepository<CaptchaImg, Long> {
    long countByTypeUuid(Long uuid);

    List<CaptchaImg> findByTypeUuid(Long uuid, Pageable pageable);

    long countByTypeUuidNot(Long uuid);

    List<CaptchaImg> findByTypeUuidNot(Long uuid, Pageable pageable);
}
