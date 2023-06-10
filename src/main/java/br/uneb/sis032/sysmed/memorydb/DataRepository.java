package br.uneb.sis032.sysmed.memorydb;

import br.uneb.sis032.sysmed.domain.model.*;
import br.uneb.sis032.sysmed.domain.model.enums.GenderEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class DataRepository {
    private static DataRepository instance;

    private final List<Specialty> specialties = new ArrayList<>();
    private final List<Procedure> procedures = new ArrayList<>();
    private final List<Clinic> clinics = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<ConsultationRoom> consultationRooms = new ArrayList<>();
    private final List<HealthInsurance> healthInsurances = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();

    public List<Specialty> getSpecialties() {
        return Collections.unmodifiableList(specialties);
    }

    public List<Procedure> getProcedures() {
        return Collections.unmodifiableList(procedures);
    }

    public List<Clinic> getClinics() {
        return Collections.unmodifiableList(clinics);
    }

    public List<Patient> getPatients() {
        return Collections.unmodifiableList(patients);
    }

    public List<Doctor> getDoctors() {
        return Collections.unmodifiableList(doctors);
    }

    public List<ConsultationRoom> getConsultationRooms() {
        return Collections.unmodifiableList(consultationRooms);
    }

    public List<HealthInsurance> getHealthInsurances() {
        return Collections.unmodifiableList(healthInsurances);
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public void add(Specialty specialty) {
        this.specialties.add(specialty);
    }

    public void add(Procedure procedure) {
        this.procedures.add(procedure);
    }

    public void add(Clinic clinic) {
        this.clinics.add(clinic);
    }

    public void add(Patient patient) {
        this.patients.add(patient);
    }

    public void add(Doctor doctor) {
        this.doctors.add(doctor);
    }

    public void add(ConsultationRoom consultationRoom) {
        this.consultationRooms.add(consultationRoom);
    }

    public void add(HealthInsurance healthInsurance) {
        this.healthInsurances.add(healthInsurance);
    }

    public void add(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public void add(Payment payment) {
        this.payments.add(payment);
    }

    public static DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }

        return instance;
    }

    private DataRepository() {
        // Specialties
        Specialty pediatria = new Specialty("Pediatria");
        Specialty dermatologia = new Specialty("Dermatologia");
        Specialty endocrinologia = new Specialty("Endocrinologia");
        specialties.add(pediatria);
        specialties.add(dermatologia);
        specialties.add(endocrinologia);

        // Procedures
        Procedure consultaPediatria = new Procedure("CPED", "Consulta Pediatria", pediatria);
        Procedure consultaDermatologia = new Procedure("CDER", "Consulta Dermatologia", dermatologia);
        Procedure consultaEndocrinologia = new Procedure("CEND", "Consulta Endocrinologia", endocrinologia);

        procedures.add(consultaPediatria);
        procedures.add(consultaDermatologia);
        procedures.add(consultaEndocrinologia);

        // Health Insurances
        Map<Procedure, BigDecimal> bradescoCoveredProcedureTable = new HashMap<>();
        bradescoCoveredProcedureTable.put(consultaPediatria, BigDecimal.valueOf(150.0));
        bradescoCoveredProcedureTable.put(consultaDermatologia, BigDecimal.valueOf(200.0));
        HealthInsurance bradesco = new HealthInsurance("Bradesco", bradescoCoveredProcedureTable, 0.0);

        Map<Procedure, BigDecimal> unimedCoveredProcedureTable = new HashMap<>();
        unimedCoveredProcedureTable.put(consultaPediatria, BigDecimal.valueOf(80.0));
        unimedCoveredProcedureTable.put(consultaDermatologia, BigDecimal.valueOf(80.0));
        HealthInsurance unimed = new HealthInsurance("Unimed", unimedCoveredProcedureTable, 0.1);

        healthInsurances.add(bradesco);
        healthInsurances.add(unimed);

        // Clinics
        Map<Procedure, BigDecimal> procedurePriceTable = new HashMap<>();
        procedurePriceTable.put(consultaPediatria, BigDecimal.valueOf(100.0));
        procedurePriceTable.put(consultaDermatologia, BigDecimal.valueOf(200.0));
        List<HealthInsurance> acceptedHealthInsurances = new ArrayList<>();
        acceptedHealthInsurances.add(bradesco);
        acceptedHealthInsurances.add(unimed);

        Clinic clinic = new Clinic("123123", "ABC Clinic", doctors, consultationRooms, procedurePriceTable, acceptedHealthInsurances, true);
        clinics.add(clinic);

        // Consultation Rooms
        ConsultationRoom room1 = new ConsultationRoom(1, clinic);
        ConsultationRoom room2 = new ConsultationRoom(2, clinic);

        consultationRooms.add(room1);
        consultationRooms.add(room2);

        // Patients
        ResponsibleParty janeSmith = new ResponsibleParty("Jane Smith", "987654321");
        Patient johnSmith = new Patient("John Smith", "123456789", "123456789",
                LocalDate.of(1990, 5, 10), GenderEnum.MALE);

        Patient aliceJohnson = new Patient("Alice Johnson", "987654321", "987654321",
                LocalDate.of(2005, 8, 20), GenderEnum.FEMALE);

        patients.add(johnSmith);
        patients.add(aliceJohnson);

        // Doctors
        Map<LocalDate, List<TimeSlot>> johnDoeSchedule = new HashMap<>();
        List<TimeSlot> johnDoeTimeSlots = new ArrayList<>();
        johnDoeTimeSlots.add(new TimeSlot(LocalTime.of(7, 0), LocalTime.of(9, 0)));
        johnDoeTimeSlots.add(new TimeSlot(LocalTime.of(14, 0), LocalTime.of(16, 0)));
        johnDoeSchedule.put(LocalDate.of(2023, 6, 12), johnDoeTimeSlots);

        Map<LocalDate, List<TimeSlot>> oliviaSmithSchedule = new HashMap<>();
        List<TimeSlot> oliviaSmithTimeSlots = new ArrayList<>();
        oliviaSmithTimeSlots.add(new TimeSlot(LocalTime.of(7, 0), LocalTime.of(16, 0)));
        oliviaSmithSchedule.put(LocalDate.of(2023, 6, 28), oliviaSmithTimeSlots);

        Doctor johnDoe = new Doctor("Dr. John Doe", pediatria, room1, johnDoeSchedule);
        Doctor oliviaSmith = new Doctor("Dra. Olivia Smith", dermatologia, room2, oliviaSmithSchedule);

        doctors.add(johnDoe);
        doctors.add(oliviaSmith);

        // Appointments
        Appointment appointment1 = new Appointment(aliceJohnson, johnDoe, consultaPediatria,
                LocalDateTime.of(2023, 7, 25, 9, 0), janeSmith, LocalDateTime.of(2023, 5, 24, 16, 30));

        Appointment appointment2 = new Appointment(aliceJohnson, oliviaSmith, consultaDermatologia,
                LocalDateTime.of(2023, 7, 26, 13, 0), janeSmith, LocalDateTime.of(2023, 5, 25, 14, 45));

        appointments.add(appointment1);
        appointments.add(appointment2);
    }
}
