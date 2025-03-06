package br.org.institutobushido.controllers.dtos.turma;

import java.io.Serializable;
import java.util.Date;

import br.org.institutobushido.providers.enums.aluno.Genero;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TurmaAlunoDTOResponse implements Serializable {
    private String nome;
    private String matricula;
    private Genero genero;
    private Date dataNascimento;
}
