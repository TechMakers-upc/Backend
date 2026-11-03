package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities.SparePartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface SparePartJpaRepository extends JpaRepository<SparePartEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from SparePart p where p.id = :id")
    Optional<SparePartEntity> findByIdForUpdate(@Param("id") Long id);

    List<SparePartEntity> findAllByOrderByNameAsc();

    List<SparePartEntity> findAllByPlantIdOrderByNameAsc(Long plantId);

    boolean existsByPlantIdAndSku(Long plantId, String sku);

    boolean existsByPlantIdAndSkuAndIdNot(Long plantId, String sku, Long id);
}
