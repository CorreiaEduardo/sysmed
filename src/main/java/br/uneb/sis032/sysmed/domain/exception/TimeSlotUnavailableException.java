package br.uneb.sis032.sysmed.domain.exception;

import br.uneb.sis032.sysmed.domain.model.TimeSlot;
import lombok.Getter;

import java.util.List;

@Getter
public class TimeSlotUnavailableException extends Exception {
    private final List<TimeSlot> suggestedSlots;

    public TimeSlotUnavailableException(List<TimeSlot> suggestedSlots) {
        super();
        this.suggestedSlots = suggestedSlots;
    }
}
