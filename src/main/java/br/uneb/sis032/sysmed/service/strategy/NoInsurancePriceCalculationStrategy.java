package br.uneb.sis032.sysmed.service.strategy;

import br.uneb.sis032.sysmed.domain.model.Appointment;

import java.math.BigDecimal;

public class NoInsurancePriceCalculationStrategy implements PriceCalculationStrategy {

    @Override
    public BigDecimal calculate(Appointment appointment) {
        return appointment.getDoctor().getConsultationRoom().getClinic().getProcedurePrice(appointment.getProcedure());
    }
}
