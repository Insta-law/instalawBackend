package com.KnowLaw.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "KnowLawUserRole")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleName roleName;
    public enum RoleName{
        ADMIN_ROLE,PROVIDER_ROLE,CONSUMER_ROLE
    }
}
