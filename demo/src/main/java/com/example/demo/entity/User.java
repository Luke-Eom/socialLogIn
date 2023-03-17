package com.example.demo.entity;

import com.example.demo.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(nullable=false, length = 100, unique = true)
    private String username;

    @Column(nullable=false, length = 100)
    private String password;

    @Column(nullable=false, length = 50)
    private String email;

    // User의 Role은 enum 타입으로
    @Enumerated(EnumType.STRING)
    private Roles role;

    private String oauth; //kakao, google

    @CreationTimestamp
    private Timestamp createDate;

}
