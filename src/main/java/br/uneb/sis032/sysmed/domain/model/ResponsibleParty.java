package br.uneb.sis032.sysmed.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ResponsibleParty {
    private final String name;
    private final String idDocument;

    @Override
    public String toString() {
        return "ResponsibleParty{" +
                "name='" + name + '\'' +
                ", idDocument='" + idDocument + '\'' +
                '}';
    }
}
