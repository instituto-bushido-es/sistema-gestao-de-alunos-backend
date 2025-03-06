package br.org.institutobushido.providers.mappers.turma;

import br.org.institutobushido.controllers.dtos.turma.TurmaAlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.AlunosTurmaListaDeChamadaDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.ListaDePresencaDTOResponse;
import br.org.institutobushido.models.aluno.graduacao.Graduacao;
import br.org.institutobushido.models.aluno.graduacao.falta.Falta;
import br.org.institutobushido.providers.utils.date_utils.DateUtil;
import br.org.institutobushido.providers.utils.default_values.ValoresPadraoListaDeChamada;

import java.time.LocalDate;
import java.util.ArrayList;

public class ListaDeChamadaMapper {

    public static ListaDePresencaDTOResponse mapToFaltaDTOResponseListaChamada(Falta falta) {
        return new ListaDePresencaDTOResponse(
                falta.getMotivo(),
                falta.getJustificada() ? ValoresPadraoListaDeChamada.FALTA_JUSTIFICADA : ValoresPadraoListaDeChamada.FALTA,
                falta.getData()
        );
    }

    public static ListaDePresencaDTOResponse mapToAlunoPresenteDTOResponseListaChamada(LocalDate data) {
        return new ListaDePresencaDTOResponse(
                null,
                ValoresPadraoListaDeChamada.PRESENTE,
                DateUtil.formatarData(data)
        );
    }

    public static AlunosTurmaListaDeChamadaDTOResponse mapToAlunosTurmaListaChamadaDTOResponseRefactor(TurmaAlunoDTOResponse aluno, Graduacao graduacaoAtual) {
        return new AlunosTurmaListaDeChamadaDTOResponse(
                aluno.getMatricula(),
                aluno.getNome(),
                graduacaoAtual.getFrequencia(),
                new ArrayList<>()
        );
    }
}