package com.KnowLaw.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "lawyer_id",nullable = false)
    private Lawyer lawyer;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User bookedBy;
    @Column(nullable = false)
    private LocalDate workingDate;
    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slots slot;

    public Booking(Lawyer lawyer,User bookedBy,LocalDate workingDate,Slots slot)
    {
        this.lawyer = lawyer;
        this.bookedBy = bookedBy;
        this.workingDate = workingDate;
        this.slot = slot;
    }

    @Override
    public String toString() {
        return  "Id=" + id +
                ", lawyer=" + (lawyer != null ? lawyer.getUserName() : "null") +
                ", bookedBy=" + (bookedBy != null ? bookedBy.getUsername() : "null") +
                ", workingDate=" + workingDate +
                ", slot=" + (slot != null ? slot.getTime() : "null") ;
    }
}
