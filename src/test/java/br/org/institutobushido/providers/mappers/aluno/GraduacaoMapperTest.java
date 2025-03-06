package br.org.institutobushido.providers.mappers.aluno;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.org.institutobushido.controllers.dtos.aluno.graduacao.GraduacaoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.graduacao.GraduacaoDTOResponse;
import br.org.institutobushido.models.aluno.graduacao.Graduacao;

@SpringBootTest
class GraduacaoMapperTest {

    private Graduacao graduacao;

    private GraduacaoDTOResponse graduacaoDTOResponse;

    private GraduacaoDTORequest graduacaoDTORequest;

    @BeforeEach
    void setUp() {
        graduacao = new Graduacao(7, new ArrayList<>(), true, 100, LocalDate.now().minusMonths(3),
                LocalDate.now().plusMonths(3), false, 80, 1, 10);
        graduacaoDTORequest = new GraduacaoDTORequest(7, 1);
        graduacaoDTOResponse = new GraduacaoDTOResponse(
                7,
                null,
                true,
                null,
                null,
                0,
                true,
                0,
                0,
                10);
    }

    @Test
    void deveRetornarGraduacaoDeGraduacaoDTORequest() {
        graduacao = GraduacaoMapper.mapToGraduacao(graduacaoDTORequest);
        assertEquals(graduacaoDTORequest.kyu(), graduacao.getKyu());
    }

    @Test
    void deveRetornarGraduacaoDTOResponseDeGraduacao() {
        graduacaoDTOResponse = GraduacaoMapper.mapToGraduacaoDTOResponse(graduacao);
        assertEquals(graduacao.getKyu(), graduacaoDTOResponse.kyu());
        assertEquals(graduacao.isAprovado(), graduacaoDTOResponse.aprovado());
        assertEquals(graduacao.getCargaHoraria(), graduacaoDTOResponse.cargaHoraria());
        assertEquals(graduacao.getDan(), graduacaoDTOResponse.dan());
        assertEquals(graduacao.getFrequencia(), graduacaoDTOResponse.frequencia());
        assertEquals(graduacao.getFaltas(), graduacaoDTOResponse.faltas());
        assertEquals(graduacao.isStatus(), graduacaoDTOResponse.status());
        assertEquals(graduacao.getFimGraduacao(), graduacaoDTOResponse.fimGraduacao());
        assertEquals(graduacao.getInicioGraduacao(), graduacaoDTOResponse.inicioGraduacao());
    }
}
