package com.sunrise.dental.dao;

import com.sunrise.dental.model.Dentist;
import java.util.List;

public interface DentistDAO {
    List<Dentist> findAllActive();
    Dentist findById(int dentistId);
}
