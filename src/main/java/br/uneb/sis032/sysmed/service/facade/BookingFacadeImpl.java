package br.uneb.sis032.sysmed.service.facade;

import br.uneb.sis032.sysmed.domain.exception.PatientHasOtherAppointmentException;
import br.uneb.sis032.sysmed.domain.exception.TimeSlotUnavailableException;
import br.uneb.sis032.sysmed.domain.model.*;
import br.uneb.sis032.sysmed.memorydb.DataRepository;
import br.uneb.sis032.sysmed.service.strategy.InsurancePriceCalculationStrategy;
import br.uneb.sis032.sysmed.service.strategy.NoInsurancePriceCalculationStrategy;
import br.uneb.sis032.sysmed.service.strategy.PriceCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookingFacadeImpl implements BookingFacade {
    private static final int APPOINTMENT_HOUR_RANGE = 1;

    private final DataRepository dataRepository;

    public BookingFacadeImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Payment book(Appointment appointment, HealthInsurance optionalHealthInsurance) throws TimeSlotUnavailableException, PatientHasOtherAppointmentException {
        final Doctor doctor = appointment.getDoctor();
        final List<TimeSlot> doctorSchedule = doctor.getScheduleFor(appointment.getDate().toLocalDate());
        final Payment futurePayment = new Payment(appointment);
        final TimeSlot selectedSlot = doctor
                .selectTimeSlot(appointment.getDate())
                .orElseThrow(() -> new TimeSlotUnavailableException(doctorSchedule));

        final boolean patientHasOtherAppointment = checkIfPatientIsAlreadyScheduled(appointment);
        if (patientHasOtherAppointment) {
            throw new PatientHasOtherAppointmentException();
        }

        futurePayment.setPriceCalculationStrategy(selectStrategy(optionalHealthInsurance, appointment.getProcedure()));
        futurePayment.calculateTotal();

        doctorSchedule.remove(selectedSlot);
        appointment.setScheduledAt(LocalDateTime.now());

        if (futurePayment.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
            dataRepository.add(futurePayment);
        }
        dataRepository.add(appointment);
        dataRepository.add(appointment.getPatient());

        return futurePayment;
    }

    private boolean checkIfPatientIsAlreadyScheduled(Appointment appointment) {
        final Optional<Appointment> scheduledAppointment = dataRepository
                .getAppointments()
                .stream()
                .filter(it -> {
                    LocalDateTime lowerBound = it.getDate().minusHours(APPOINTMENT_HOUR_RANGE);
                    LocalDateTime upperBound = it.getDate().plusHours(APPOINTMENT_HOUR_RANGE);
                    final boolean dateWithinRange = appointment.getDate().isAfter(lowerBound) && appointment.getDate().isBefore(upperBound);

                    return appointment.getPatient().equals(appointment.getPatient()) && dateWithinRange;
                })
                .findFirst();

        return scheduledAppointment.isPresent();
    }

    private PriceCalculationStrategy selectStrategy(HealthInsurance optionalHealthInsurance, Procedure p) {
        if (optionalHealthInsurance == null || !optionalHealthInsurance.covers(p)) return new NoInsurancePriceCalculationStrategy();

        return new InsurancePriceCalculationStrategy(optionalHealthInsurance);
    }
}
