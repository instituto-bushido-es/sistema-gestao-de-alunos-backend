package br.org.institutobushido.controllers.dtos.turma.lista_de_chamada;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListaDePresencaDTOResponse {
    private String motivo;
    private String presenca;
    private String dataFalta;
}
