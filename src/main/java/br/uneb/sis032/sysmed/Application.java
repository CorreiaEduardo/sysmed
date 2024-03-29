package br.uneb.sis032.sysmed;

import br.uneb.sis032.sysmed.domain.exception.PatientHasOtherAppointmentException;
import br.uneb.sis032.sysmed.domain.exception.TimeSlotUnavailableException;
import br.uneb.sis032.sysmed.domain.model.*;
import br.uneb.sis032.sysmed.domain.model.enums.GenderEnum;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentMethod;
import br.uneb.sis032.sysmed.domain.model.enums.PaymentStatus;
import br.uneb.sis032.sysmed.memorydb.DataRepository;
import br.uneb.sis032.sysmed.service.AppointmentBookingService;
import br.uneb.sis032.sysmed.service.facade.BookingFacadeImpl;
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
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final DataRepository repository = DataRepository.getInstance();
        final BookingFacadeImpl bookingFacade = new BookingFacadeImpl(repository);
        final AppointmentBookingService bookingService = new AppointmentBookingService(repository, bookingFacade);

        final Clinic clinic = setupApplication(repository, scanner);

        // Main Loop
        while (true) {
            final int selectedMenuOption = promptForMenuOption(scanner);
            switch (selectedMenuOption) {
                case 1 -> displayAppointmentSearch(scanner, bookingService);
                case 2 -> displayBookingForm(bookingService, clinic, repository, scanner);
                case 3 -> exitWithReport(repository);
            }
        }
    }

    private static void exitWithReport(DataRepository repository) {
        repository.getAppointments().forEach(System.out::println);
        repository.getPayments().forEach(System.out::println);
        System.exit(1);
    }

    private static int promptForMenuOption(Scanner scanner) {
        jumpStandardOutput();
        System.out.println("========== Menu ==========");
        System.out.println("1. Consultar agendamento");
        System.out.println("2. Criar novo agendamento");
        System.out.println("3. Sair");
        System.out.println("==========================");
        System.out.print("Escolha uma opção: ");

        return nextInt(scanner);
    }

    private static void displayAppointmentSearch(Scanner scanner, AppointmentBookingService bookingFacade) {
        jumpStandardOutput();

        System.out.print("CPF do paciente: ");
        final String cpf = scanner.nextLine();

        final List<Appointment> appointments = bookingFacade.search(cpf);

        if (appointments.isEmpty()) {
            System.out.println("Nenhum agendamento encontrado para este paciente.");
            pauseConsole(scanner);
            return;
        }

        appointments.forEach(appointment -> {
            System.out.println("-> (" + appointment.getDoctor().getSpecialty().getName() + ") " + appointment.getDoctor().getName() + " - " + appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " às " + appointment.getDate().toLocalTime());
        });

        pauseConsole(scanner);
    }

    private static void displayBookingForm(AppointmentBookingService bookingFacade, Clinic clinic, DataRepository repository, Scanner scanner) {
        jumpStandardOutput();
        ResponsibleParty responsibleParty = null;
        final Patient patient = promptForPatient(repository, scanner);
        // Check if the patient is a minor and prompt for additional information
        if (patient.isMinor()) {
            responsibleParty = promptForResponsibleParty(scanner);
        }

        final HealthInsurance insurance = promptForInsurance(clinic, scanner);

        Appointment appointment = promptForAppointment(scanner, bookingFacade, clinic, patient, responsibleParty, insurance);
        if (appointment == null) return;

        try {
            final Payment futurePayment = bookingFacade.book(appointment, insurance);

            if (futurePayment.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                final PaymentMethod paymentMethod = promptForPaymentMethod(futurePayment, scanner);
                futurePayment.setMethod(paymentMethod);
                futurePayment.setStatus(PaymentStatus.PAID);
            } else {
                System.out.println("Procedimento coberto pelo plano.");
            }
            System.out.println("Agendamento realizado com sucesso.");
            pauseConsole(scanner);
        } catch (TimeSlotUnavailableException e) {
            System.out.println("Desculpe, o atendimento não pode ser confirmado porque o médico não possui disponibilidade.");
            pauseConsole(scanner);
        } catch (PatientHasOtherAppointmentException e) {
            System.out.println("Desculpe, o atendimento não pode ser confirmado porque o paciente possui outros agendamentos para o mesmo horário.");
            pauseConsole(scanner);
        }
    }

    private static PaymentMethod promptForPaymentMethod(Payment futurePayment, Scanner scanner) {
        System.out.println("Valor total: " + futurePayment.getTotalAmount());
        System.out.println("Métodos de pagamento disponíveis: \n1. Dinheiro\n2. Cartão");
        System.out.print("Selecione uma opção: ");
        final int selectedPaymentMethod = nextInt(scanner);

        return selectedPaymentMethod == 1 ? PaymentMethod.CASH : PaymentMethod.CREDIT_CARD;
    }

    private static Appointment promptForAppointment(Scanner scanner, AppointmentBookingService bookingFacade, Clinic clinic, Patient patient, ResponsibleParty responsibleParty, HealthInsurance insurance) {
        Appointment appointment;
        do {
            final Procedure procedure = promptForProcedure(clinic, scanner);

            if ((insurance == null || !insurance.covers(procedure)) && !clinic.getAcceptsPrivateAppointments()) {
                System.out.println("A clinica não aceita agendamento particular.");
                pauseConsole(scanner);
                return null;
            }

            if (insurance != null && !insurance.covers(procedure)) {
                final boolean shouldContinue = promptToProceedWithoutCoverage(scanner);
                if (!shouldContinue) {
                    return null;
                }
            }

            appointment = promptForAppointment(bookingFacade, patient, responsibleParty, procedure, scanner);

            if (appointment == null) {
                final boolean shouldContinue = prompToContinueWithOtherProcedure(scanner);
                if (!shouldContinue) {
                    return null;
                }
            }
        } while (appointment == null);

        return appointment;
    }

    private static Appointment promptForAppointment(AppointmentBookingService bookingFacade, Patient patient, ResponsibleParty responsibleParty, Procedure procedure, Scanner scanner) {
        System.out.println("Horários disponíveis para agendamento:");
        final List<Pair<Doctor, LocalDateTime>> schedulingOptions = new ArrayList<>();
        final List<Doctor> doctors = bookingFacade.calculateAvailability(procedure);

        AtomicInteger count = new AtomicInteger(1);
        doctors.forEach(doctor -> doctor.getCurrentSchedule()
                .forEach((day, schedule) -> schedule
                        .forEach(slot -> {
                            schedulingOptions.add(Pair.with(doctor, LocalDateTime.of(day, slot.getStartTime())));
                            System.out.println((count.getAndIncrement()) + ". " + doctor.getName() + " - " + day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " às " + slot.getStartTime());
                        })));

        if (schedulingOptions.isEmpty()) {
            return null;
        } else {
            System.out.print("Selecione uma opção: ");
            final int selectedDate = nextInt(scanner);
            final Doctor doctor = schedulingOptions.get(selectedDate - 1).getValue0();
            final LocalDateTime appointmentDate = schedulingOptions.get(selectedDate - 1).getValue1();

            jumpStandardOutput();
            return new Appointment(patient, doctor, procedure, appointmentDate, responsibleParty);
        }
    }

    private static boolean prompToContinueWithOtherProcedure(Scanner scanner) {
        System.out.println("Nenhum horário disponível, você pode tentar novamente mais tarde.");
        System.out.println("\n1. Tentar outro procedimento\n2. Voltar ao menu");
        System.out.print("Selecione uma opção: ");
        final int selected = nextInt(scanner);

        jumpStandardOutput();

        return selected == 1;
    }

    private static boolean promptToProceedWithoutCoverage(Scanner scanner) {
        System.out.println("Procedimento não é coberto pelo plano, deseja prosseguir com o agendamento?");
        System.out.println("1. Prosseguir");
        System.out.println("2. Cancelar");
        System.out.print("Selecione uma opção: ");
        final int proceed = nextInt(scanner);

        return proceed == 1;
    }

    private static Procedure promptForProcedure(Clinic clinic, Scanner scanner) {
        System.out.println("Procedimentos disponíveis: ");
        final List<Procedure> procedures = clinic.getProcedurePriceTable().keySet().stream().toList();
        for (int i = 0; i < procedures.size(); i++) {
            Procedure p = procedures.get(i);
            System.out.println((i + 1) + ". " + p.getName());
        }
        System.out.print("Selecione uma opção: ");
        int selectedProcedure = nextInt(scanner) - 1;

        jumpStandardOutput();
        return procedures.get(selectedProcedure);
    }

    private static HealthInsurance promptForInsurance(Clinic clinic, Scanner scanner) {
        // Prompt for insurance and procedure information
        System.out.println("Plano de saúde atendidos: ");

        List<HealthInsurance> acceptedHealthInsurances = clinic.getAcceptedHealthInsurances();
        for (int i = 0; i < acceptedHealthInsurances.size(); i++) {
            HealthInsurance hi = acceptedHealthInsurances.get(i);
            System.out.println((i + 1) + ". " + hi.getName());
        }
        System.out.print("Selecione uma opção" + (clinic.getAcceptsPrivateAppointments() ? ", ou digite 0 para atendimento particular: " : ": "));
        int selectedInsurance = nextInt(scanner) - 1;

        HealthInsurance insurance = null;

        if (selectedInsurance >= 0) {
            insurance = acceptedHealthInsurances.get(selectedInsurance);
        }

        jumpStandardOutput();
        return insurance;
    }

    private static ResponsibleParty promptForResponsibleParty(Scanner scanner) {
        System.out.print("Nome da mãe/responsável: ");
        final String responsiblePartyName = scanner.nextLine();

        System.out.print("Documento de identificação do responsável: ");
        final String responsiblePartyId = scanner.nextLine();

        final ResponsibleParty responsibleParty = new ResponsibleParty(responsiblePartyName, responsiblePartyId);

        jumpStandardOutput();
        return responsibleParty;
    }

    private static Patient promptForPatient(DataRepository repository, Scanner scanner) {
        Patient patient;

        // Prompt for basic patient information
        System.out.print("CPF: ");
        final String cpf = scanner.nextLine();

        final Optional<Patient> opPatient = repository.getPatients().stream().filter(it -> it.getCpf().equals(cpf)).findFirst();
        if (opPatient.isEmpty()) {
            System.out.print("Nome do paciente: ");
            final String name = scanner.nextLine();

            System.out.print("Documento de identificação: ");
            final String id = scanner.nextLine();

            System.out.print("Data de Nascimento (formato: dd/mm/yyyy): ");
            final String birthday = scanner.nextLine();
            LocalDate birthDate = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            System.out.print("Sexo (H/M): ");
            final String gender = scanner.nextLine();

            patient = new Patient(name, id, cpf, birthDate, GenderEnum.of(gender));
        } else {
            patient = opPatient.get();
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

    private static int nextInt(Scanner scanner) {
        final int nextInt = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        return nextInt;
    }

    private static void pauseConsole(Scanner scanner) {
        System.out.print("Digite qualquer tecla para continuar...");
        scanner.nextLine();
    }
}
