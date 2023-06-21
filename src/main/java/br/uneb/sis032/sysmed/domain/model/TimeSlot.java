package br.uneb.sis032.sysmed.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@RequiredArgsConstructor
@Getter
public class TimeSlot {
    private final LocalTime startTime;
    private final LocalTime endTime;

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
