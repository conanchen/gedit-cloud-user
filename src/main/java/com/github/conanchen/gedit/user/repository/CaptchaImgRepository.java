package com.github.conanchen.gedit.user.repository;

import com.github.conanchen.gedit.user.model.CaptchaImg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface CaptchaImgRepository<String extends Serializable> extends JpaRepository<CaptchaImg,String> {
    long countByTypeUuid(String uuid);

    List<CaptchaImg> findByTypeUuid(String uuid, Pageable pageable);

    long countByTypeUuidNot(String uuid);

    List<CaptchaImg> findByTypeUuidNot(String uuid, Pageable pageable);
}
