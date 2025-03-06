package br.org.institutobushido.controllers.routes.turma;

import br.org.institutobushido.controllers.dtos.turma.DadosTurmaDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.TurmaAlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.TurmaDTORequest;
import br.org.institutobushido.controllers.dtos.turma.TurmaDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.ListaDeChamadaDTORequest;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.ListaDeChamadaDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.tutor.TutorDTORequest;
import br.org.institutobushido.controllers.dtos.turma.tutor.TutorDTOResponse;
import br.org.institutobushido.controllers.response.success.SuccessDeleteResponse;
import br.org.institutobushido.controllers.response.success.SuccessPostResponse;
import br.org.institutobushido.providers.enums.aluno.Genero;
import br.org.institutobushido.providers.utils.date_utils.DateUtil;
import br.org.institutobushido.services.turma.TurmaServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TurmaControllerTest {

    String nomeTurma = "Nome Turma";

    private TurmaController turmaController;

    private TurmaDTORequest turmaDTORequest;

    @Mock
    private TurmaServiceInterface turmaService;

    @BeforeEach
    public void setup() {
        turmaController = new TurmaController(turmaService);

        turmaDTORequest = new TurmaDTORequest(
                nomeTurma,
                new TutorDTORequest(
                        "nome tutor",
                        "email tutor"
                ),
                "Endereco Turma"
        );
    }

    @Test
    void deveCriarNovaTurma() throws URISyntaxException {
        when(turmaService.criarNovaTurma(turmaDTORequest)).thenReturn("Turma " + nomeTurma + " foi criada com sucesso!");

        ResponseEntity<SuccessPostResponse> responseEntity = turmaController.criarNovaTurma(turmaDTORequest);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void deveDeletarTurma() {
        when(turmaService.deletarTurma(anyString(), anyString())).thenReturn("Turma " + nomeTurma + " foi deletada com sucesso!");
        ResponseEntity<SuccessDeleteResponse> responseEntity = turmaController.deletarTurma(turmaDTORequest.nome(), turmaDTORequest.tutor().email());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deveListarTurmas() {
        when(turmaService.listarTurmas(any(Long.class), any(Long.class))).thenReturn(
                List.of(
                        new TurmaDTOResponse(
                                "nome",
                                new TutorDTOResponse(
                                        "nome tutor",
                                        "email tutor"
                                ),
                                "Endereco Turma",
                                LocalDate.now()
                        )
                )
        );
        ResponseEntity<List<TurmaDTOResponse>> responseEntity = turmaController.listarTurmas(0L, 0L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    void deveListarTurmasPorNome() {
        when(turmaService.buscarTurmaPorNome(anyString())).thenReturn(
                new TurmaDTOResponse(
                        "nome",
                        new TutorDTOResponse(
                                "nome tutor",
                                "email tutor"
                        ),
                        "Endereco Turma",
                        LocalDate.now()
                )
        );
        ResponseEntity<TurmaDTOResponse> responseEntity = turmaController.buscarTurmaPorNome("nome");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deveListarAlunosPorTurma() {
        when(turmaService.listarAlunosDaTurma(anyString())).thenReturn(
                new DadosTurmaDTOResponse(
                        "email@email.com.br",
                        List.of(
                                new TurmaAlunoDTOResponse(
                                    "nome",
                                    "matricula",
                                    Genero.F,
                                    new Date()
                                )
                        )
                )
        );
        DadosTurmaDTOResponse responseEntity = turmaController.listarAlunoPorTurma("nome");
        assertEquals(1, responseEntity.alunos().size());
    }

    @Test
    void deveRegistrarPresencaComSucesso() {
        ListaDeChamadaDTORequest listaDeChamadaDTORequest = new ListaDeChamadaDTORequest();
        String nomeTurma = "Nome Turma";
        String requestId = "request-id-teste-123";

        when(turmaService.registrarPresenca(nomeTurma, listaDeChamadaDTORequest))
                .thenReturn("Presença registrada com sucesso!");

        ResponseEntity<SuccessPostResponse> responseEntity = turmaController.registrarPresenca(nomeTurma, listaDeChamadaDTORequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deveRetornarConflitoAoRegistrarPresencaQuandoDiaJaRegistrado() {
        ListaDeChamadaDTORequest listaDeChamadaDTORequest = new ListaDeChamadaDTORequest();
        String nomeTurma = "Nome Turma";
        String requestId = "request-id-teste-123";

        when(turmaService.registrarPresenca(nomeTurma, listaDeChamadaDTORequest))
                .thenReturn("O dia da aula já está registrado.");

        ResponseEntity<SuccessPostResponse> responseEntity = turmaController.registrarPresenca(nomeTurma, listaDeChamadaDTORequest);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());

    }

    @Test
    void deveCarregarListaDeChamadaComSucesso() {
        String nomeTurma = "Nome Turma";
        String dataInicial = "2023-10-01";
        String dataFinal = "2023-10-31";

        when(turmaService.obterListaDeChamada(nomeTurma,
                DateUtil.getDataAsLocalDate(dataInicial),
                DateUtil.getDataAsLocalDate(dataFinal)))
                .thenReturn(new ListaDeChamadaDTOResponse());

        ResponseEntity<?> responseEntity = turmaController.carregarListaDeChamada(nomeTurma, dataInicial, dataFinal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deveRetornarNotFoundAoCarregarListaDeChamadaComTurmaInexistente() {
        String nomeTurma = "Nome Turma";
        String dataInicial = "2023-10-01";
        LocalDate dataInicialParsed = DateUtil.getDataAsLocalDate(dataInicial);

        String dataFinal = null;

        when(turmaService.obterListaDeChamada(nomeTurma, dataInicialParsed, dataInicialParsed))
                .thenThrow(new NoSuchElementException("Turma não encontrada"));

        ResponseEntity<?> responseEntity = turmaController.carregarListaDeChamada(nomeTurma, dataInicial, dataFinal);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Turma não encontrada", responseEntity.getBody());
    }

    @Test
    void deveRetornarBadRequestAoCarregarListaDeChamadaComDatasInvalidas() {
        String nomeTurma = "Nome Turma";
        String dataInicial = "data-invalida";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            turmaService.validarDatas(nomeTurma,
                    DateUtil.getDataAsLocalDate(dataInicial),
                    null);
        });

        assertTrue(exception.getMessage().contains("Data inválida"));

        ResponseEntity<?> responseEntity = turmaController.carregarListaDeChamada(nomeTurma, dataInicial, null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Data inválida: data-invalida", responseEntity.getBody());
    }
}
