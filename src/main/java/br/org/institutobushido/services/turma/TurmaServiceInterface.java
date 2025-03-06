package br.org.institutobushido.services.turma;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import br.org.institutobushido.controllers.dtos.turma.*;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.FaltaListaDeChamadaDTORequest;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.ListaDeChamadaDTORequest;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.ListaDeChamadaDTOResponse;

public interface TurmaServiceInterface {
    String criarNovaTurma(TurmaDTORequest turma);

    String deletarTurma(String emailAdmin, String nomeTurma);

    List<TurmaDTOResponse> listarTurmas(long dataInicial, long dataFinal);

    TurmaDTOResponse buscarTurmaPorNome(String nomeTurma);

    DadosTurmaDTOResponse listarAlunosDaTurma(String nomeTurma);

    String registrarPresenca(String nomeTurma, ListaDeChamadaDTORequest listaDeChamadaDTORequestRefactor);

    ListaDeChamadaDTOResponse obterListaDeChamada(String nomeTurma, LocalDate dataInicial, LocalDate dataFinal);

    String adicionarDiaAula(String nomeTurma, LocalDate diaAula);

    String adicionarFaltaDoAlunoListaChamada(String matricula, FaltaListaDeChamadaDTORequest falta);

    void validarDatas(String nomeTurma, LocalDate dataInicialParsed, LocalDate dataFinalParsed);

}
