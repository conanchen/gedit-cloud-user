package com.github.conanchen.gedit.user.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author hai
 * @description 粉丝关系表
 * @email hilin2333@gmail.com
 * @date 29/01/2018 2:11 PM
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class FansShip {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(
            name = "uuid",
            strategy = "com.github.conanchen.gedit.user.utils.database.CustomUUIDGenerator"
    )
    @Column(columnDefinition = "char(32)")
    private String uuid;

    @Column(columnDefinition = "char(32)")
    private String fanUuid;
    @Column(columnDefinition = "char(32)")
    private String parentUuid;

    //TODO sync name from user table
    @Column(columnDefinition = "varchar(32)")
    private String fanName;
    @Column(columnDefinition = "varchar(32)")
    private String parentName;

    @Column(columnDefinition = "tinyint(1)",nullable = false)
    @NonNull
    private Boolean active;

    @Column(columnDefinition = "datetime")
    private Date createdDate;

    @Column(columnDefinition = "datetime")
    private Date updatedDate;
}
