package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentMethod;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentStatus;
import br.uneb.sis032.sysmed.service.strategy.PriceCalculationStrategy;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Payment extends IdentifiableEntity {
    private PaymentMethod method;
    private BigDecimal totalAmount;
    private Appointment appointment;
    private PaymentStatus status;
    private PriceCalculationStrategy priceCalculationStrategy;

    public Payment(Appointment appointment) {
        this.appointment = appointment;
        this.status = PaymentStatus.CREATED;
    }

    public void calculateTotal() {
        this.totalAmount = priceCalculationStrategy.calculate(this.appointment);
    }
}
