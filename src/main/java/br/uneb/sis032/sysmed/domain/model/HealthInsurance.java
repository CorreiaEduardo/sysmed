package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HealthInsurance extends IdentifiableEntity {
    private final String name;
    private final Map<Procedure, Boolean> coveredProcedures;
    private final Double copaymentFee;

    public HealthInsurance(String name, Map<Procedure, Boolean> coveredProcedures, Double copaymentFee) {
        super();
        this.name = name;
        this.coveredProcedures = coveredProcedures;
        this.copaymentFee = copaymentFee;
    }

    public boolean covers(Procedure p) {
        return this.coveredProcedures.get(p);
    }
}
