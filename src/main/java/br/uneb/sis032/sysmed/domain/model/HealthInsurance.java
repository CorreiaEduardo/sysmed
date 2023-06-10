package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class HealthInsurance extends IdentifiableEntity {
    private final String name;
    private final Map<Procedure, BigDecimal> coveredProcedures;
    private final Double copaymentFee;

    public HealthInsurance(String name, Map<Procedure, BigDecimal> coveredProcedures, Double copaymentFee) {
        super();
        this.name = name;
        this.coveredProcedures = coveredProcedures;
        this.copaymentFee = copaymentFee;
    }

    public boolean covers(Procedure p) {
        return this.coveredProcedures.containsKey(p);
    }

    public BigDecimal getProcedurePrice(Procedure p) {
        return coveredProcedures.get(p);
    }
}
