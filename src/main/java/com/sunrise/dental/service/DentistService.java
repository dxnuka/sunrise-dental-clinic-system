package com.sunrise.dental.service;

import com.sunrise.dental.dao.DentistDAO;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;

public class DentistService {

    private final DentistDAO dentistDAO;

    public DentistService() { this(DAOFactory.getDentistDAO()); }
    public DentistService(DentistDAO dentistDAO) { this.dentistDAO = dentistDAO; }

    public List<Dentist> findAll() {
        return dentistDAO.findAll();
    }

    public int addDentist(String dentistName, String specialization, BigDecimal consultationFee)
            throws ValidationException {
        ValidationUtil.requireValidDentistName(dentistName);
        ValidationUtil.requireNonBlank(specialization, "Specialization");
        ValidationUtil.requireValidFee(consultationFee, "Consultation fee");
        return dentistDAO.create(dentistName.trim(), specialization.trim(), consultationFee);
    }

    public void deactivateDentist(int dentistId) throws ValidationException {
        Dentist dentist = dentistDAO.findById(dentistId);
        if (dentist == null) throw new ValidationException("Dentist not found.");
        dentistDAO.deactivate(dentistId);
    }
}
