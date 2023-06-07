package br.uneb.sis032.sysmed.service.strategy;

import br.uneb.sis032.sysmed.domain.model.Appointment;
import br.uneb.sis032.sysmed.domain.model.Clinic;
import br.uneb.sis032.sysmed.domain.model.HealthInsurance;

import java.math.BigDecimal;

public class InsurancePriceCalculationStrategy implements PriceCalculationStrategy {

    private final HealthInsurance insurance;

    public InsurancePriceCalculationStrategy(HealthInsurance insurance) {
        this.insurance = insurance;
    }

    @Override
    public BigDecimal calculate(Appointment appointment) {
        final Clinic clinic = appointment.getDoctor().getConsultationRoom().getClinic();
        final BigDecimal procedurePrice = clinic.getProcedurePrice(appointment.getProcedure());

        if (this.insurance != null && this.insurance.covers(appointment.getProcedure())) {
            return procedurePrice.multiply(BigDecimal.valueOf(this.insurance.getCopaymentFee()));
        }

        return procedurePrice;
    }
}
