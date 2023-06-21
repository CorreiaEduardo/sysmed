package br.uneb.sis032.sysmed.domain.exception;

import lombok.Getter;

@Getter
public class PatientHasOtherAppointmentException extends Exception {
    public PatientHasOtherAppointmentException() {
        super();
    }
}
