package com.sunrise.dental.dao;

import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;
import java.util.List;

public interface PatientDAO {
    /** Lightweight lookup for the "existing patient" search box on the add-appointment page. */
    List<Patient> search(String query, int limit);

    Patient findById(int patientId);

    /** Backs the Patients page's searchable/filterable/sortable/paginated list. */
    PageResult<Patient> findPaged(PatientFilter filter);
}
