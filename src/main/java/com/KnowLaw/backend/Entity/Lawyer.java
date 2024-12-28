package com.KnowLaw.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lawyers")
public class Lawyer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    private String userName;
    @Column(nullable = false,unique = true)
    private String governmentId;
    @OneToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;
    private Double pricing;

    public Lawyer(String userName,String governmentId,User user,Double pricing)
    {
        this.userName = userName;
        this.governmentId = governmentId;
        this.user = user;
        this.pricing = pricing;
    }

}
