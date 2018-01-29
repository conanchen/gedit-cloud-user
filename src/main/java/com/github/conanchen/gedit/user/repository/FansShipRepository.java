package com.github.conanchen.gedit.user.repository;

import com.github.conanchen.gedit.user.model.FansShip;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FansShipRepository extends JpaRepository<FansShip,String> {
    boolean existsByActiveIsTrueAndFanUuid(String fanUuid);

    List<FansShip>  findByActiveIsTrueAndParentUuid(String parentUuid,Pageable pageable);

    FansShip findByActiveIsTrueAndFanUuid(String fanUuid);

    List<FansShip>  findByActiveIsTrueAndParentUuidAAndUpdatedDate(String parentUuid, Date lastUpdated);
}
