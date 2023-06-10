package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import br.uneb.sis032.sysmed.domain.model.enums.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class Patient extends IdentifiableEntity {
    private final String name;
    private final String idDocument;
    private final String cpf;
    private final LocalDate birthDay;
    private final GenderEnum gender;

    public Patient(String name, String idDocument, String cpf, LocalDate birthDay, GenderEnum gender) {
        this.name = name;
        this.idDocument = idDocument;
        this.cpf = cpf;
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public boolean isMinor() {
        LocalDate currentDate = LocalDate.now();
        Period ageDifference = Period.between(this.birthDay, currentDate);
        return ageDifference.getYears() < 18;
    }
}
