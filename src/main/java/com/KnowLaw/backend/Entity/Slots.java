package com.KnowLaw.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="Slots")
public class Slots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String time;
}
