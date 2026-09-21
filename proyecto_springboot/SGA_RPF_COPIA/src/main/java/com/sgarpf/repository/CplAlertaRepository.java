package com.sgarpf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sgarpf.model.CplAlerta;

public interface CplAlertaRepository extends JpaRepository<CplAlerta, Long> {

    List<CplAlerta> findByEstadoOrderByIdDesc(String estado);
}
