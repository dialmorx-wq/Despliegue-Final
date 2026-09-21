package com.sgarpf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.CplTrazabilidad;

public interface CplTrazabilidadRepository extends JpaRepository<CplTrazabilidad, Long> {

    List<CplTrazabilidad> findByCompraIdOrderByOrdenAsc(Long compraId);
}
