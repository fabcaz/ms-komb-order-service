package com.proj.msorder.repositories;

import com.proj.msorder.domain.KombuchaOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KombuchaOrderLineRepository extends JpaRepository<KombuchaOrderLine, UUID> {
}
