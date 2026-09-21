package com.sgarpf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.CplSoporte;

public interface CplSoporteRepository extends JpaRepository<CplSoporte, Long> {

    List<CplSoporte> findByCompraId(Long compraId);
}
