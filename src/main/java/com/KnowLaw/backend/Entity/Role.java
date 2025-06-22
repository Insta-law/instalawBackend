package com.KnowLaw.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "knowlawuserrole")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "rolename")
    private RoleName roleName;
    public enum RoleName{
        ADMIN_ROLE,PROVIDER_ROLE,CONSUMER_ROLE
    }
}
