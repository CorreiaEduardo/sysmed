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
    private LocalDateTime scheduledAt;

    public Appointment(Patient patient, Doctor doctor, Procedure procedure, LocalDateTime date) {
        super();
        this.patient = patient;
        this.doctor = doctor;
        this.procedure = procedure;
        this.date = date;
    }

    public Appointment(Patient patient, Doctor doctor, Procedure procedure, LocalDateTime date, LocalDateTime scheduledAt) {
        super();
        this.patient = patient;
        this.doctor = doctor;
        this.procedure = procedure;
        this.date = date;
        this.scheduledAt = scheduledAt;
    }
}
