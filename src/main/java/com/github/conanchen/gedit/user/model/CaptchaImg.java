package com.github.conanchen.gedit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class CaptchaImg {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "bigint(20)")
    private Long typeId;

    @Column(columnDefinition = "varchar(255)")
    private String url;

    @Column(columnDefinition = "datetime")
    private Date createdDate;
}