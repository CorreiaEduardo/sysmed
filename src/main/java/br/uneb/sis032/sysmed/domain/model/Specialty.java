package br.uneb.sis032.sysmed.domain.model;

import br.uneb.sis032.sysmed.domain.model.base.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Specialty extends IdentifiableEntity {
    private final String name;

    public Specialty(String name) {
        super();
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Specialty) {
            return this.name.equals(((Specialty) o).getName());
        }

        return false;
    }

    @Override
    public String toString() {
        return "Specialty{" +
                "name='" + name + '\'' +
                '}';
    }
}
