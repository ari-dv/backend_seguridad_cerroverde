package com.alexander.sistema_cerro_verde_backend.repository;

import com.alexander.sistema_cerro_verde_backend.entity.AuditoriaHttp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaHttpRepository extends JpaRepository<AuditoriaHttp, Long> {
}