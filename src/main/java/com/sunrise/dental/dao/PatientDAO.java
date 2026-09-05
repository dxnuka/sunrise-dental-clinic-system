package com.sunrise.dental.dao;

import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;
import java.util.List;

public interface PatientDAO {
    List<Patient> search(String query, int limit);

    Patient findById(int patientId);

    PageResult<Patient> findPaged(PatientFilter filter);

    void delete(int patientId);
}
