package com.github.conanchen.gedit.user.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Login {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "char(32)")
    private String uuid;

    @Column(columnDefinition = "char(32)")
    private String userUuid;

    @Column(columnDefinition = "tinyint(1)",nullable = false)
    @NonNull
    private Boolean active;

    @Column(columnDefinition = "tinyint(1)",nullable = false)
    private Date expireDate;

    @Column(columnDefinition = "datetime")
    private Date createdDate;

    @Column(columnDefinition = "datetime")
    private Date updatedDate;
}
