package com.sgarpf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.CplObservacion;

public interface CplObservacionRepository extends JpaRepository<CplObservacion, Long> {

    List<CplObservacion> findByCompraIdOrderByIdDesc(Long compraId);
}
