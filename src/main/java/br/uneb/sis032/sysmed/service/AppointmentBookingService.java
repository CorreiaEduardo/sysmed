package br.uneb.sis032.sysmed.service;

import br.uneb.sis032.sysmed.domain.exception.PatientHasOtherAppointmentException;
import br.uneb.sis032.sysmed.domain.exception.TimeSlotUnavailableException;
import br.uneb.sis032.sysmed.domain.model.*;
import br.uneb.sis032.sysmed.memorydb.DataRepository;
import br.uneb.sis032.sysmed.service.facade.BookingFacade;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentBookingService {

    private final DataRepository dataRepository;
    private final BookingFacade bookingFacade;

    public AppointmentBookingService(DataRepository dataRepository, BookingFacade bookingFacade) {
        this.dataRepository = dataRepository;
        this.bookingFacade = bookingFacade;
    }

    public List<Doctor> calculateAvailability(Procedure procedure) {
        return dataRepository
                .getDoctors()
                .stream()
                .filter(doctor -> doctor.does(procedure))
                .toList();
    }

    public List<Appointment> search(String patientCPF) {
        return dataRepository
                .getAppointments()
                .stream()
                .filter(ap -> ap.getDate().isAfter(LocalDateTime.now()) && ap.getPatient().getCpf().equals(patientCPF))
                .toList();
    }

    public synchronized Payment book(Appointment appointment, HealthInsurance optionalHealthInsurance) throws TimeSlotUnavailableException, PatientHasOtherAppointmentException {
        return bookingFacade.book(appointment, optionalHealthInsurance);
    }

}
