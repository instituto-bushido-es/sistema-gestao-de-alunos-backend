package br.org.institutobushido.controllers.dtos.aluno.dados_sociais;

import br.org.institutobushido.providers.enums.aluno.Imovel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosSociaisDTORequest(

        boolean bolsaFamilia,

        boolean auxilioBrasil,

        Imovel imovel,

        int numerosDePessoasNaCasa,

        int contribuintesDaRendaFamiliar,

        boolean alunoContribuiParaRenda,

        int rendaFamiliar) {
}
