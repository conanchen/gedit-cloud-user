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
public class CaptchaType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "tinyint(1)")
    private Boolean active;

    @Column(columnDefinition = "varchar(20)")
    private String name;

    @Column(columnDefinition = "datetime")
    private Date createdDate;
}