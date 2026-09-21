package com.sgarpf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.CplCompra;

public interface CplCompraRepository extends JpaRepository<CplCompra, Long> {

    List<CplCompra> findByContratoIdAndPeriodo(Long contratoId, String periodo);

    List<CplCompra> findByPeriodo(String periodo);
}
