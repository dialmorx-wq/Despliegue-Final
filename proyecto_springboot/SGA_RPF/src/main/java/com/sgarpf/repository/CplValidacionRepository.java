package com.sgarpf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.CplValidacion;

public interface CplValidacionRepository extends JpaRepository<CplValidacion, Long> {

    List<CplValidacion> findByCompraIdOrderByIdDesc(Long compraId);
}
