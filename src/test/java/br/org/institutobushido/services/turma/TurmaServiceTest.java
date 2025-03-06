package br.org.institutobushido.services.turma;

import br.org.institutobushido.controllers.dtos.turma.TurmaDTORequest;
import br.org.institutobushido.controllers.dtos.turma.TurmaDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.ListaDeChamadaDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.tutor.TutorDTORequest;
import br.org.institutobushido.controllers.dtos.turma.tutor.TutorDTOResponse;
import br.org.institutobushido.models.admin.Admin;
import br.org.institutobushido.models.admin.turmas.TurmaResponsavel;
import br.org.institutobushido.models.aluno.Aluno;
import br.org.institutobushido.models.turma.Turma;
import br.org.institutobushido.models.turma.tutor.Tutor;
import br.org.institutobushido.providers.enums.admin.UserRole;
import br.org.institutobushido.providers.utils.resources.exceptions.AlreadyRegisteredException;
import br.org.institutobushido.providers.utils.resources.exceptions.EntityNotFoundException;
import br.org.institutobushido.providers.utils.resources.exceptions.LimitQuantityException;
import br.org.institutobushido.repositories.AdminRepositorio;
import br.org.institutobushido.repositories.TurmaRepositorio;
import br.org.institutobushido.services.aluno.AlunoServices;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TurmaServiceTest {

    @Mock
    private TurmaRepositorio turmaRepositorio;

    @Mock
    private AdminRepositorio adminRepositorio;

    @Mock
    private MongoTemplate mongoTemplate;

    private TurmaService turmaServices;

    private final TurmaResponsavel turmaResponsavel = new TurmaResponsavel("Rua A", "Turma A");

    private TurmaDTORequest turmaDTORequest;
    private TurmaDTOResponse turmaDTOResponse;
    private Turma turma;
    private Admin admin;

    @BeforeEach
    void setUp() {

        turmaServices = new TurmaService(turmaRepositorio, mongoTemplate, adminRepositorio);

        admin = new Admin("admin", "admin@email.com", "admin", "admin", UserRole.ADMIN);
        admin.adicionarTurma(turmaResponsavel);

        turmaDTORequest = new TurmaDTORequest(
                "Turma A",
                new TutorDTORequest("Tutor", "Tutor@email.com"),
                "Endereço I");

        turmaDTOResponse = new TurmaDTOResponse(
                "Turma A",
                new TutorDTOResponse("Tutor", "Tutor@email.com"),
                "Endereço I",
                LocalDate.now());

        turma = new Turma("Endereço I",
                "Turma A",
                new Tutor("Tutor", "Tutor@email.com"));
    }

    @Test
    void deveCriarNovaTurma() {
        when(turmaRepositorio.save(any(Turma.class))).thenReturn(turma);
        when(adminRepositorio.findByEmailAdmin(anyString())).thenReturn(Optional.of(admin));

        TurmaDTORequest novaTurma = new TurmaDTORequest("Endereço", new TutorDTORequest("Tutor", "Tutor@email.com"),
                "Tutor");

        String result = turmaServices.criarNovaTurma(novaTurma);
        assertEquals("Turma " + novaTurma.nome() + " foi criada com sucesso!", result);
    }

    @Test
    void deveLancarExcecaoQuandoTurmaJaExiste() {
        when(mongoTemplate.exists(any(Query.class), eq(Turma.class))).thenReturn(true);
        assertThrows(AlreadyRegisteredException.class, () -> turmaServices.criarNovaTurma(turmaDTORequest));
    }

    @Test
    void deveLancarExcecaoQuandoAdminNaoExiste() {
        when(mongoTemplate.exists(any(Query.class), eq(Admin.class))).thenReturn(false);
        when(adminRepositorio.findByEmailAdmin(anyString())).thenReturn(Optional.empty());
        TurmaDTORequest novaTurma = new TurmaDTORequest("Turma B", new TutorDTORequest("Tutor", "Tutor@email.com"),
                "Endereço II");
        assertThrows(EntityNotFoundException.class, () -> turmaServices.criarNovaTurma(novaTurma));
    }

    @Test
    void deveDeletarTurma() {
        // Arrange
        String nomeTurma = "Turma A";

        when(mongoTemplate.exists(any(Query.class), eq(Turma.class))).thenReturn(true);
        when(mongoTemplate.exists(any(Query.class), eq(Aluno.class))).thenReturn(false);
        when(adminRepositorio.findByEmailAdmin(anyString())).thenReturn(Optional.of(admin));

        // Act
        String result = turmaServices.deletarTurma("admin@email.com", turma.getNome());

        // Assert
        assertEquals("Turma " + nomeTurma + " deletada com sucesso", result);
    }

    @Test
    void deveLancarQuandoTurmaNaoExiste() {
        String nomeTurma = "Turma A";
        String email = "admin@email.com";
        when(turmaRepositorio.findByNome(anyString())).thenReturn(Optional.empty());
        when(adminRepositorio.findByEmailAdmin(anyString())).thenReturn(Optional.of(admin));
        assertThrows(EntityNotFoundException.class,
                () -> turmaServices.deletarTurma(email, nomeTurma));
    }

    @Test
    void deveLancarExcecaoQuandoExisteAlunosVinculadosATurma() {
        String nomeTurma = "Turma A";
        String email = "admin@email.com";
        when(turmaRepositorio.findByNome(anyString())).thenReturn(Optional.of(turma));
        when(mongoTemplate.exists(any(Query.class), any(Class.class))).thenReturn(true);
        assertThrows(
                LimitQuantityException.class, () -> turmaServices.deletarTurma(
                        email,
                        nomeTurma));
    }

    @Test
    void deveListarTurmas() {

        when(turmaRepositorio.findAll()).thenReturn(List.of(
                turma,
                new Turma("Turma B", "Nome", new Tutor("Tutor", "Tutor@email.com")),
                new Turma("Turma C", "Nome", new Tutor("Tutor", "Tutor@email.com"))));

        // Act
        List<TurmaDTOResponse> result = turmaServices.listarTurmas(0L, 0L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void deveRetornarExcecaoQuandoDataInicialForMaiorQueHoje() {
        assertThrows(LimitQuantityException.class, () -> turmaServices.listarTurmas(System.currentTimeMillis() + 1, 0L));
    }


    @Test
    void deveRetornarListaTotalQuandoDataFinalDataInicialForZero() {
        when(turmaRepositorio.findAll()).thenReturn(List.of(turma));
        List<TurmaDTOResponse> result = turmaServices.listarTurmas(0L, 0L);
        assertEquals(1, result.size());
    }

    @Test
    void deveRetornarExcecaoQuandoDataInicialForMaiorQueDataFinal() {
        assertThrows(LimitQuantityException.class, () -> turmaServices.listarTurmas(System.currentTimeMillis() - 10000000 + 1, System.currentTimeMillis() - 10000000 - 1));
    }

    @Test
    void deveRetornarExcecaoQuandoDataInicialForIgualQueDataFinal() {
        assertThrows(LimitQuantityException.class, () -> turmaServices.listarTurmas(System.currentTimeMillis() + 1, System.currentTimeMillis() + 1));
    }

    @Test
    void deveBuscarTurmaPorNome() {
        // Arrange
        String nomeTurma = "Turma A";

        when(mongoTemplate.findOne(any(Query.class), eq(Turma.class))).thenReturn(turma);

        // Act
        TurmaDTOResponse actualResponse = turmaServices.buscarTurmaPorNome(nomeTurma);

        // Assert
        assertEquals(turmaDTOResponse, actualResponse);
    }

    @Test
    void deveLancarExcecaoQuandoBuscarTurmaPorNomeNaoEncontrada() {
        // Arrange
        String nomeTurma = "NotFoundTurma";
        when(turmaRepositorio.findByNome(nomeTurma)).thenReturn(Optional.empty());

        // Act
        assertThrows(EntityNotFoundException.class, () -> turmaServices.buscarTurmaPorNome(nomeTurma));
    }

    @Test
    void deveLancarExcecaoQuandoTurmaNaoExiste() {
        // Arrange
        String nomeTurma = "Turma A";
        TurmaService turmaService = new TurmaService(turmaRepositorio, mongoTemplate, adminRepositorio);
        when(turmaRepositorio.findByNome(nomeTurma)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> turmaService.buscarTurmaPorNome(nomeTurma));
    }

    @Test
    void deveAdicionarDiaAula() {
        LocalDate diaAula = LocalDate.of(2024, 11, 27);

        when(mongoTemplate.findOne(any(Query.class), eq(Turma.class))).thenReturn(turma);

        String result = turmaServices.adicionarDiaAula("Turma A", diaAula);

        assertEquals("Dia de aula adicionado com sucesso.", result);
        assertTrue(turma.getDiasAula().contains(diaAula));

        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(Turma.class));
    }


    @Test
    void naoDeveLancarExcecaoComDataCorreta() {
        LocalDate dataInicial = LocalDate.of(2023, 11, 1);
        LocalDate dataFinal = LocalDate.of(2023, 11, 3);

        turma.setDiaAula(dataInicial);

        when(turmaRepositorio.findByNome("Turma A")).thenReturn(Optional.of(turma));

        assertDoesNotThrow(() -> {
            turmaServices.validarDatas("Turma A", dataInicial, dataFinal);
        });
    }

    @Test
    void deveLancarExcecaoComRangeDeDataInvalido() {
        LocalDate dataInicial = LocalDate.of(2023, 11, 5);
        LocalDate dataFinal = LocalDate.of(2023, 11, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            turmaServices.validarDatas("Turma A", dataInicial, dataFinal);
        });
    }

    @Test
    void deveLancarExecaoParaDataNaoEncontrada() {
        LocalDate data = LocalDate.of(2023, 11, 2);

        Turma turmaSemDiasAula = new Turma("Endereço I", "Turma A", new Tutor("Tutor", "Tutor@email.com"));

        when(turmaRepositorio.findByNome("Turma A")).thenReturn(Optional.of(turmaSemDiasAula));

        assertThrows(EntityNotFoundException.class, () -> {
            turmaServices.validarDatas("Turma A", data, data);
        });
    }

}
