package br.org.institutobushido.controllers.dtos.turma.lista_de_chamada;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ListaDeChamadaDTORequest {
    private LocalDate diaAula;
    private List<AlunosTurmaListaDeChamadaDTORequest> listaDeChamada;
}
