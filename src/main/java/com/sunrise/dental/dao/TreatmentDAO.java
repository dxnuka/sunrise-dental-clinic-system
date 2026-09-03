package com.sunrise.dental.dao;

import com.sunrise.dental.model.Treatment;
import java.util.List;

public interface TreatmentDAO {
    List<Treatment> findAll();
    Treatment findById(int treatmentId);
}
