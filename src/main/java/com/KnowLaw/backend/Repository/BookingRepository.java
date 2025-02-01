package com.KnowLaw.backend.Repository;

import com.KnowLaw.backend.Entity.Booking;
import com.KnowLaw.backend.Entity.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.lawyer = :lawyer " +
            "AND b.workingDate BETWEEN :startDate AND :endDate")
    List<Booking> findByLawyerAndWorkingDateBetween(
            @Param("lawyer") Lawyer lawyer,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.lawyer.uuid = :id " +
            "AND b.workingDate = :workingDate " +
            "AND b.slot.time = :slot")
    Optional<Booking> findByLawyerWorkingDateAndTime(
            @Param("id") UUID id,
            @Param("workingDate") LocalDate workingDate,
            @Param("slot") String slot);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.lawyer.uuid = :id AND b.workingDate >= :workingDate AND b.status = :status")
    Long countSlots(@Param("id") UUID id, @Param("workingDate") LocalDate workingDate, @Param("status")Booking.BookingStatus status);

    @Query("SELECT COUNT( DISTINCT(b.bookedBy.id)) FROM Booking b WHERE b.lawyer.uuid = :id AND b.bookedBy is not null")
    Long countClients(@Param("id") UUID id);

}
