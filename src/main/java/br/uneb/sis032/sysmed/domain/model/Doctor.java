package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class Doctor extends IdentifiableEntity {
    private final String name;
    private final Specialty specialty;
    private final ConsultationRoom consultationRoom;
    private final Map<LocalDate, List<TimeSlot>> currentSchedule;

    public Doctor(String name, Specialty specialty, ConsultationRoom consultationRoom, Map<LocalDate, List<TimeSlot>> currentSchedule) {
        super();
        this.name = name;
        this.specialty = specialty;
        this.consultationRoom = consultationRoom;
        this.currentSchedule = currentSchedule;
    }

    public boolean does(Procedure s) {
        return this.specialty.equals(s.getSpecialty());
    }

    public List<TimeSlot> getScheduleFor(LocalDate localDate) {
        return this.currentSchedule.get(localDate);
    }

    public Optional<TimeSlot> selectTimeSlot(LocalDateTime startsAt) {
        return this.currentSchedule
                .get(startsAt.toLocalDate())
                .stream()
                .filter(it -> it.getStartTime().equals(startsAt.toLocalTime()))
                .findFirst();
    }
}
