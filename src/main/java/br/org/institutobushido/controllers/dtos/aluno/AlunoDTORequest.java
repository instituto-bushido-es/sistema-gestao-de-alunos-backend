package br.org.institutobushido.controllers.dtos.aluno;

import br.org.institutobushido.controllers.dtos.aluno.endereco.EnderecoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.graduacao.GraduacaoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.dados_escolares.DadosEscolaresDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.dados_sociais.DadosSociaisDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.historico_de_saude.HistoricoSaudeDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.models.aluno.responsaveis.Responsavel;
import br.org.institutobushido.models.turma.Turma;
import br.org.institutobushido.providers.enums.aluno.CorDePele;
import br.org.institutobushido.providers.enums.aluno.Genero;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AlunoDTORequest(
        @NotNull(message = "Nome é obrigatório") @NotEmpty(message = "Nome é obrigatório")
        String nome,

        @NotNull(message = "Data de nascimento é obrigatória") @Min(value = 10000, message = "Data de nascimento deve ser no passado!")
        long dataNascimento,

        //@NotNull(message = "Genero é obrigatório")
        String genero,

        @NotNull(message = "Turma é obrigatório", groups = {
                Turma.class }) String turma,

        DadosSociaisDTORequest dadosSociais,

        DadosEscolaresDTORequest dadosEscolares,

        EnderecoDTORequest endereco,

        String cpf,

        @NotEmpty(message = "Insira pelo menos um Responsavel!", groups = {
                Responsavel.class }) ResponsavelDTORequest responsaveis,

        HistoricoSaudeDTORequest historicoSaude,

        @NotNull(message = "Graduacao é obrigatório!") GraduacaoDTORequest graduacao,

        String cartaoSus,

        @Email(message = "Formato de email inválido") String email,

        String telefone,

        //@NotNull(message = "Cor de pele é obrigatório!")
        String corDePele,

        String rg){
}