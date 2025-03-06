package br.org.institutobushido.controllers.dtos.aluno.dados_escolares;

import br.org.institutobushido.providers.enums.aluno.Turno;

public record UpdateDadosEscolaresDTORequest(String escola, Turno turno, String serie) {
}
