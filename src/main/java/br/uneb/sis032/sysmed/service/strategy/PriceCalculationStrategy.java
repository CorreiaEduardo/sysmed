package br.uneb.sis032.sysmed.service.strategy;

import br.uneb.sis032.sysmed.domain.model.Appointment;

import java.math.BigDecimal;

public interface PriceCalculationStrategy {
    BigDecimal calculate(Appointment appointment);
}
