package br.uneb.sis032.sysmed.domain.model.base;

import java.io.Serializable;
import java.util.Random;

public class IdentifiableEntity implements Serializable {
    private Long id;

    public IdentifiableEntity() {
        this.id = new Random().nextLong();
        ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentifiableEntity that)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
