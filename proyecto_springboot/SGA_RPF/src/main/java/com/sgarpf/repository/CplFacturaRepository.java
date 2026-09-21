package com.sgarpf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.CplFactura;

public interface CplFacturaRepository extends JpaRepository<CplFactura, Long> {

    List<CplFactura> findByCompraId(Long compraId);

    boolean existsByNumeroFacturaAndNitEmisorAndCompraIdNot(String numeroFactura, String nitEmisor, Long compraId);
}
