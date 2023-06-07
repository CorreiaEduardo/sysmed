package br.uneb.sis032.sysmed.service.facade;

import br.uneb.sis032.sysmed.domain.exception.TimeSlotUnavailableException;
import br.uneb.sis032.sysmed.domain.model.Appointment;
import br.uneb.sis032.sysmed.domain.model.HealthInsurance;
import br.uneb.sis032.sysmed.domain.model.Payment;

public interface BookingFacade {
    Payment book(Appointment appointment, HealthInsurance optionalHealthInsurance) throws TimeSlotUnavailableException;
}
