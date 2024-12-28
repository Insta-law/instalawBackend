package com.KnowLaw.backend.Repository;

import com.KnowLaw.backend.Entity.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {
}
