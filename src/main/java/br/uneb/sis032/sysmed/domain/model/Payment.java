package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentMethod;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.math.BigDecimal;

@Getter
@Setter
public class Payment extends IdentifiableEntity {
    private PaymentMethod method;
    private BigDecimal totalAmount;
    private Pair<HealthInsurance, BigDecimal> insuranceCoverage;
    private Appointment appointment;
    private PaymentStatus status;

    public Payment(Appointment appointment) {
        this.appointment = appointment;
        this.status = PaymentStatus.CREATED;
    }

    public void calculateTotal(HealthInsurance usedInsurance) {
        final Clinic clinic = this.appointment.getDoctor().getConsultationRoom().getClinic();
        final BigDecimal procedurePrice = clinic.getProcedurePrice(this.appointment.getProcedure());

        if (usedInsurance != null && usedInsurance.covers(this.appointment.getProcedure())) {
            final BigDecimal copaymentAmount = procedurePrice.multiply(BigDecimal.valueOf(usedInsurance.getCopaymentFee()));
            this.totalAmount = copaymentAmount;
            this.insuranceCoverage = Pair.with(usedInsurance, procedurePrice.subtract(copaymentAmount));
        } else {
            this.totalAmount = procedurePrice;
        }
    }
}
