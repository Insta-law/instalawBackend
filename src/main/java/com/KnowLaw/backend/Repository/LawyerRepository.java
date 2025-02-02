package com.KnowLaw.backend.Repository;

import com.KnowLaw.backend.Entity.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {

    @Query("SELECT l from Lawyer l where l.user.id = :id")
    Optional<Lawyer> findLawyerByUserId(@Param("id") UUID id);
}
