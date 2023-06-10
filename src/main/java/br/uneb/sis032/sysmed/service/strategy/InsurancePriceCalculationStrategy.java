package br.uneb.sis032.sysmed.service.strategy;

import br.uneb.sis032.sysmed.domain.model.Appointment;
import br.uneb.sis032.sysmed.domain.model.HealthInsurance;

import java.math.BigDecimal;

public class InsurancePriceCalculationStrategy implements PriceCalculationStrategy {

    private final HealthInsurance insurance;

    public InsurancePriceCalculationStrategy(HealthInsurance insurance) {
        if (insurance == null) {
            throw new IllegalArgumentException();
        }

        this.insurance = insurance;
    }

    @Override
    public BigDecimal calculate(Appointment appointment) {
        final BigDecimal procedurePrice = this.insurance.getProcedurePrice(appointment.getProcedure());
        return procedurePrice.multiply(BigDecimal.valueOf(this.insurance.getCopaymentFee()));
    }
}
