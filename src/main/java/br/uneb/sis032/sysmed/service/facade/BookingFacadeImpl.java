package br.uneb.sis032.sysmed.service.facade;

import br.uneb.sis032.sysmed.domain.exception.TimeSlotUnavailableException;
import br.uneb.sis032.sysmed.domain.model.*;
import br.uneb.sis032.sysmed.memorydb.DataRepository;
import br.uneb.sis032.sysmed.service.strategy.InsurancePriceCalculationStrategy;
import br.uneb.sis032.sysmed.service.strategy.NoInsurancePriceCalculationStrategy;
import br.uneb.sis032.sysmed.service.strategy.PriceCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingFacadeImpl implements BookingFacade {
    private final DataRepository dataRepository;

    public BookingFacadeImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Payment book(Appointment appointment, HealthInsurance optionalHealthInsurance) throws TimeSlotUnavailableException {
        final Doctor doctor = appointment.getDoctor();
        final List<TimeSlot> doctorSchedule = doctor.getScheduleFor(appointment.getDate().toLocalDate());
        final Payment futurePayment = new Payment(appointment);
        final TimeSlot selectedSlot = doctor
                .selectTimeSlot(appointment.getDate())
                .orElseThrow(() -> new TimeSlotUnavailableException(doctorSchedule));

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

    private PriceCalculationStrategy selectStrategy(HealthInsurance optionalHealthInsurance, Procedure p) {
        if (optionalHealthInsurance == null || !optionalHealthInsurance.covers(p)) return new NoInsurancePriceCalculationStrategy();

        return new InsurancePriceCalculationStrategy(optionalHealthInsurance);
    }
}
