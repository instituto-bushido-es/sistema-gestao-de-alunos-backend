package br.org.institutobushido.models.aluno.dados_escolares;

import java.io.Serializable;

import br.org.institutobushido.providers.enums.aluno.Turno;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DadosEscolares implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;
    private String escola;
    private Turno turno;
    private String serie;

    public void setEscola(String escola) {
        if (escola == null || escola.isEmpty()) {
            return;
        }
        this.escola = escola;
    }

    public void setTurno(Turno turno) {
        if (turno == null) {
            return;
        }
        this.turno = turno;
    }

    public void setSerie(String serie) {
        if (serie == null) {
            return;
        }
        this.serie = serie;
    }
}
