package com.KnowLaw.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "KnowLawUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;
    private String username;
    private String phone;
    private String passwordHash;

    public User(String email,String username,String phone,String passwordHash){
        this.email=email;
        this.username=username;
        this.phone =phone;
        this.passwordHash=passwordHash;
    }
}
