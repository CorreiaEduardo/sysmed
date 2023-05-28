package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultationRoom extends IdentifiableEntity {
    private final Integer roomNumber;
    private final Clinic clinic;

    public ConsultationRoom(Integer roomNumber, Clinic clinic) {
        super();
        this.roomNumber = roomNumber;
        this.clinic = clinic;
    }
}
