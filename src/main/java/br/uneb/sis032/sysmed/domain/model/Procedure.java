package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Procedure extends IdentifiableEntity {
    private final String code;
    private final String name;
    private final Specialty specialty;

    public Procedure(String code, String name, Specialty specialty) {
        super();
        this.code = code;
        this.name = name;
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Procedure that) {
            return that.getCode().equals(this.code);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Procedure{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", specialty=" + specialty +
                '}';
    }
}
