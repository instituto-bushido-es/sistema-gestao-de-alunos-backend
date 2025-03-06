package br.org.institutobushido.controllers.dtos.turma.lista_de_chamada;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListaDeChamadaDTOResponse {

    private String email;
    private List<AlunosTurmaListaDeChamadaDTOResponse> listaDeChamada;
}
