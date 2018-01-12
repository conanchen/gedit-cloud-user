package com.github.conanchen.gedit.user.repository;

import com.github.conanchen.gedit.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface UserRepository<String extends Serializable> extends JpaRepository<User,String> {
    long countByMobile(String mobile);

    boolean existsByMobile(String mobile);
}
