package br.org.institutobushido.controllers.dtos.aluno;

import br.org.institutobushido.controllers.dtos.aluno.dados_escolares.UpdateDadosEscolaresDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.dados_sociais.UpdateDadosSociaisDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.endereco.UpdateEnderecoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.historico_de_saude.UpdateHistoricoSaudeDTORequest;
import br.org.institutobushido.providers.enums.aluno.CorDePele;
import br.org.institutobushido.providers.enums.aluno.Genero;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record UpdateAlunoDTORequest(
        String nome,
        long dataNascimento,
        String genero,
        String turma,
        UpdateDadosSociaisDTORequest dadosSociais,
        UpdateDadosEscolaresDTORequest dadosEscolares,
        UpdateEnderecoDTORequest endereco,
        UpdateHistoricoSaudeDTORequest historicoDeSaude,
        String cpf,
        String corDePele,
        String telefone,
        String cartaoSus,
        String email,
        String rg) {
}