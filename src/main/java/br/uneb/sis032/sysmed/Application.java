package br.uneb.sis032.sysmed;

import br.uneb.sis032.sysmed.domain.exception.TimeSlotUnavailableException;
import br.uneb.sis032.sysmed.domain.model.*;
import br.uneb.sis032.sysmed.domain.model.enums.GenderEnum;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentMethod;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentStatus;
import br.uneb.sis032.sysmed.memorydb.DataRepository;
import br.uneb.sis032.sysmed.service.AppointmentBookingFacade;
import org.javatuples.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {

    //TODO fix validation loops and navigation
    //TODO stylize console prompts
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final DataRepository repository = DataRepository.getInstance();
        final AppointmentBookingFacade bookingFacade = new AppointmentBookingFacade(repository);

        final Clinic clinic = setupApplication(repository, scanner);
        final Patient patient = promptForPatient(scanner);
        final HealthInsurance insurance = promptForInsurance(clinic, scanner);
        final Procedure procedure = promptForProcedure(clinic, scanner);
        final Appointment appointment = promptForAppointment(bookingFacade, patient, procedure, scanner);

        try {
            final Payment futurePayment = bookingFacade.book(appointment, insurance);

            if (futurePayment.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Valor total: " + futurePayment.getTotalAmount());
                System.out.println("Selecione um método de pagamento: \n1. Dinheiro\n2. Cartão");
                final int selectedPaymentMethod = scanner.nextInt();
                futurePayment.setMethod(selectedPaymentMethod == 1 ? PaymentMethod.CASH : PaymentMethod.CREDIT_CARD);
                futurePayment.setStatus(PaymentStatus.PAID);
            } else {
                System.out.println("Procedimento coberto pelo plano.");
            }
            System.out.println("Agendamento realizado com sucesso.");
        } catch (TimeSlotUnavailableException e) {
            System.out.println("Desculpe, o atendimento não pode ser confirmado.");
            System.exit(0);
        }
    }

    private static Appointment promptForAppointment(AppointmentBookingFacade bookingFacade, Patient patient, Procedure procedure, Scanner scanner) {
        System.out.println("Selecione um horário disponível para agendamento:");
        final List<Pair<Doctor, LocalDateTime>> schedulingOptions = new ArrayList<>();
        final List<Doctor> doctors = bookingFacade.calculateAvailability(procedure);

        AtomicInteger count = new AtomicInteger(1);
        doctors.forEach(doctor -> doctor.getCurrentSchedule()
                .forEach((day, schedule) -> schedule
                        .forEach(slot -> {
                            schedulingOptions.add(Pair.with(doctor, LocalDateTime.of(day, slot.getStartTime())));
                            System.out.println((count.getAndIncrement()) + ". " + doctor.getName() + " - " + day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " às " + slot.getStartTime());
                        })));

        final int selectedDate = scanner.nextInt();
        final Doctor doctor = schedulingOptions.get(selectedDate - 1).getValue0();
        final LocalDateTime appointmentDate = schedulingOptions.get(selectedDate - 1).getValue1();

        jumpStandardOutput();
        return new Appointment(patient, doctor, procedure, appointmentDate);
    }

    private static Procedure promptForProcedure(Clinic clinic, Scanner scanner) {
        System.out.println("Selecione um procedimento: ");
        final List<Procedure> procedures = clinic.getProcedurePriceTable().keySet().stream().toList();
        for (int i = 0; i < procedures.size(); i++) {
            Procedure p = procedures.get(i);
            System.out.println((i + 1) + ". " + p.getName());
        }
        int selectedProcedure = scanner.nextInt() - 1;

        jumpStandardOutput();
        return procedures.get(selectedProcedure);
    }

    private static HealthInsurance promptForInsurance(Clinic clinic, Scanner scanner) {
        // Prompt for insurance and procedure information
        System.out.println("Selecione um plano de saúde: ");
        System.out.println("0. Sem plano (Atendimento particular)");

        List<HealthInsurance> acceptedHealthInsurances = clinic.getAcceptedHealthInsurances();
        for (int i = 0; i < acceptedHealthInsurances.size(); i++) {
            HealthInsurance hi = acceptedHealthInsurances.get(i);
            System.out.println((i + 1) + ". " + hi.getName());
        }
        int selectedInsurance = scanner.nextInt() - 1;
        HealthInsurance insurance = null;

        if (selectedInsurance >= 0) {
            insurance = acceptedHealthInsurances.get(selectedInsurance);
        }

        jumpStandardOutput();
        return insurance;
    }

    private static Patient promptForPatient(Scanner scanner) {
        // Prompt for basic patient information
        System.out.println("Nome do paciente: ");
        final String name = scanner.nextLine();

        System.out.println("Documento de identificação: ");
        final String id = scanner.nextLine();

        System.out.println("CPF: ");
        final String cpf = scanner.nextLine();

        System.out.println("Data de Nascimento (formato: dd/mm/yyyy): ");
        final String birthday = scanner.nextLine();
        LocalDate birthDate = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.println("Sexo (H/M): ");
        final String gender = scanner.nextLine();

        final Patient patient = new Patient(name, id, cpf, birthDate, GenderEnum.of(gender));

        // Check if the patient is a minor and prompt for additional information
        if (patient.isMinor()) {
            System.out.println("Nome da mãe/responsável: ");
            final String responsiblePartyName = scanner.nextLine();

            System.out.println("Documento de identificação do responsável: ");
            final String responsiblePartyId = scanner.nextLine();

            final ResponsibleParty responsibleParty = new ResponsibleParty(responsiblePartyName, responsiblePartyId);
            patient.setResponsibleParty(responsibleParty);
        }

        jumpStandardOutput();
        return patient;
    }

    // App setup (hypothetical)
    private static Clinic setupApplication(DataRepository repository, Scanner scanner) {
        jumpStandardOutput();
        System.out.println("===== Sysmed Setup =====");
        System.out.print("Digite o CNPJ da clinica: ");
        Optional<Clinic> opClinic = repository.getClinics().stream().filter(it -> it.getLegalId().equals(scanner.nextLine())).findFirst();

        while (opClinic.isEmpty()) {
            System.out.print("Desculpe, a clinica não existe. Tente novamente com outro CNPJ: ");
            opClinic = repository.getClinics().stream().filter(it -> it.getLegalId().equals(scanner.nextLine())).findFirst();
        }

        jumpStandardOutput();
        return opClinic.get();
    }

    private static void jumpStandardOutput() {
        System.out.println(System.lineSeparator().repeat(50));
    }
}
