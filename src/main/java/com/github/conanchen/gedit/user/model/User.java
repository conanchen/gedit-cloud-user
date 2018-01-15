package com.github.conanchen.gedit.user.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "char(32)")
    private String uuid;

    @Column(columnDefinition = "varchar(11)",unique = true)
    private String mobile;

    @Column(columnDefinition = "varchar(32)")
    private String password;

    @Column(columnDefinition = "varchar(255)")
    private String name;

    @Column(columnDefinition = "varchar(255)")
    private String descr;


    @Column(columnDefinition = "varchar(64)")
    private String qq;

    @Column(columnDefinition = "varchar(64)")
    private String wechat;

    @Column(columnDefinition = "tinyint(1)",nullable = false)
    @NonNull
    private Boolean active;

    @Column(columnDefinition = "varchar(255)")
    private String logo;

    @Column(columnDefinition = "datetime")
    private Date createdDate;

    @Column(columnDefinition = "datetime")
    private Date updatedDate;

}
