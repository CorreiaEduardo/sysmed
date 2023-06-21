package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Clinic extends IdentifiableEntity {
    private final String legalId;
    private final String name;
    private final List<Doctor> doctors;
    private final List<ConsultationRoom> consultationRooms;
    private final Map<Procedure, BigDecimal> procedurePriceTable;
    private final List<HealthInsurance> acceptedHealthInsurances;
    private final Boolean acceptsPrivateAppointments;

    public Clinic(String legalId, String name, Boolean acceptsPrivateAppointments) {
        super();
        this.legalId = legalId;
        this.name = name;
        this.doctors = new ArrayList<>();
        this.consultationRooms = new ArrayList<>();
        this.procedurePriceTable = new HashMap<>();
        this.acceptedHealthInsurances = new ArrayList<>();
        this.acceptsPrivateAppointments = acceptsPrivateAppointments;
    }

    public Clinic(String legalId, String name, List<Doctor> doctors, List<ConsultationRoom> consultationRooms, Map<Procedure, BigDecimal> procedurePriceTable, List<HealthInsurance> acceptedHealthInsurances, Boolean acceptsPrivateAppointments) {
        super();
        this.legalId = legalId;
        this.name = name;
        this.doctors = doctors;
        this.consultationRooms = consultationRooms;
        this.procedurePriceTable = procedurePriceTable;
        this.acceptedHealthInsurances = acceptedHealthInsurances;
        this.acceptsPrivateAppointments = acceptsPrivateAppointments;
    }

    public BigDecimal getProcedurePrice(Procedure p) {
        return procedurePriceTable.get(p);
    }

    @Override
    public String toString() {
        return "Clinic{" +
                "legalId='" + legalId + '\'' +
                ", name='" + name + '\'' +
                ", acceptsPrivateAppointments=" + acceptsPrivateAppointments +
                '}';
    }
}
