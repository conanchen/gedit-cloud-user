package com.github.conanchen.gedit.user.repository;

import com.github.conanchen.gedit.user.model.CaptchaImg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaptchaImgRepository extends JpaRepository<CaptchaImg, Long> {
    long countByTypeId(Long typeId);

    List<CaptchaImg> findByTypeId(Long typeId, Pageable pageable);

    long countByTypeIdNot(Long typeId);

    List<CaptchaImg> findByTypeIdNot(Long typeId, Pageable pageable);
}
