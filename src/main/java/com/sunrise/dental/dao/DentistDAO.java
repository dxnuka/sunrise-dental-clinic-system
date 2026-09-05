package com.sunrise.dental.dao;

import com.sunrise.dental.model.Dentist;
import java.math.BigDecimal;
import java.util.List;

public interface DentistDAO {
    List<Dentist> findAllActive();

    List<Dentist> findAll();

    Dentist findById(int dentistId);

    int create(String dentistName, String specialization, BigDecimal consultationFee);

    void deactivate(int dentistId);
}
