package br.org.institutobushido.controllers.dtos.turma.lista_de_chamada;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlunosTurmaListaDeChamadaDTORequest {
    private String matricula;
    private String nome;
    private FaltaListaDeChamadaDTORequest falta;
}
