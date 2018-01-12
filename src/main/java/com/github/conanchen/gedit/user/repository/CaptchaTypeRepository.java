package com.github.conanchen.gedit.user.repository;

import com.github.conanchen.gedit.user.model.CaptchaType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface CaptchaTypeRepository<String extends Serializable> extends JpaRepository<CaptchaType,String> {
    long countByActive(boolean active);

    CaptchaType findByActive(boolean active, Pageable pageable);

}
