package br.org.institutobushido.controllers.dtos.aluno.dados_escolares;

import br.org.institutobushido.providers.enums.aluno.Turno;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record DadosEscolaresDTOResponse(String escola, Turno turno, String serie) {
}
