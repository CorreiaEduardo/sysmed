package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Appointment extends IdentifiableEntity {
    private final Patient patient;
    private final Doctor doctor;
    private final Procedure procedure;
    private final LocalDateTime date;
    private final ResponsibleParty patientResponsibleParty;
    private LocalDateTime scheduledAt;

    public Appointment(Patient patient, Doctor doctor, Procedure procedure, LocalDateTime date, ResponsibleParty patientResponsibleParty) {
        super();
        this.patient = patient;
        this.doctor = doctor;
        this.procedure = procedure;
        this.date = date;
        this.patientResponsibleParty = patientResponsibleParty;
    }

    public Appointment(Patient patient, Doctor doctor, Procedure procedure, LocalDateTime date, ResponsibleParty patientResponsibleParty, LocalDateTime scheduledAt) {
        super();
        this.patient = patient;
        this.doctor = doctor;
        this.procedure = procedure;
        this.date = date;
        this.patientResponsibleParty = patientResponsibleParty;
        this.scheduledAt = scheduledAt;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "patient=" + patient +
                ", doctor=" + doctor +
                ", procedure=" + procedure +
                ", date=" + date +
                ", patientResponsibleParty=" + patientResponsibleParty +
                ", scheduledAt=" + scheduledAt +
                '}';
    }
}
