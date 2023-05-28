package br.uneb.sis032.sysmed.service;

import br.uneb.sis032.sysmed.domain.exception.TimeSlotUnavailableException;
import br.uneb.sis032.sysmed.domain.model.*;
import br.uneb.sis032.sysmed.memorydb.DataRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentBookingFacade {

    private final DataRepository dataRepository;

    public AppointmentBookingFacade(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public List<Doctor> calculateAvailability(Procedure procedure) {
        return dataRepository
                .getDoctors()
                .stream()
                .filter(doctor -> doctor.does(procedure))
                .toList();
    }

    public Payment book(Appointment appointment) throws TimeSlotUnavailableException {
        return this.book(appointment, null);
    }

    public synchronized Payment book(Appointment appointment, HealthInsurance optionalHealthInsurance) throws TimeSlotUnavailableException {
        final Doctor doctor = appointment.getDoctor();
        final List<TimeSlot> doctorSchedule = doctor.getScheduleFor(appointment.getDate().toLocalDate());
        final Payment futurePayment = new Payment(appointment);
        final TimeSlot selectedSlot = doctor
                .selectTimeSlot(appointment.getDate())
                .orElseThrow(() -> new TimeSlotUnavailableException(doctorSchedule));

        futurePayment.calculateTotal(optionalHealthInsurance);
        doctorSchedule.remove(selectedSlot);
        appointment.setScheduledAt(LocalDateTime.now());

        dataRepository.add(futurePayment);
        dataRepository.add(appointment);

        return futurePayment;
    }

}
