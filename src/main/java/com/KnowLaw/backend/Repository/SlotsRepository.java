package com.KnowLaw.backend.Repository;

import com.KnowLaw.backend.Entity.Slots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SlotsRepository extends JpaRepository<Slots,Long> {
    @Query("Select s from Slots s where s.time >= :startTime and s.time <= :endTime order by s.id")
    List<Slots> fetchSlots(@Param("startTime") String startTime , @Param("endTime")String endTime);
}
