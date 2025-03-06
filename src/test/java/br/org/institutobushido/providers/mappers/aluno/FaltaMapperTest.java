package br.org.institutobushido.providers.mappers.aluno;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.org.institutobushido.controllers.dtos.aluno.graduacao.faltas.FaltaDTOResponse;
import br.org.institutobushido.models.aluno.graduacao.falta.Falta;

@SpringBootTest
class FaltaMapperTest {
    private Falta falta;
    private FaltaDTOResponse faltaDTO;

    @BeforeEach
    void setUp() {
        falta = new Falta(
                "motivo",
                new Date(new Date().getTime() - 2000 * 60 * 60 * 24 * 4),
                true);
        faltaDTO = new FaltaDTOResponse(
                falta.getData(),
                falta.getMotivo(),
                falta.getJustificada()
        );
    }

    @Test
    void deveRetornarFaltaDeFaltaDTOResponse() {
        falta = FaltaMapper.mapToFalta(faltaDTO);
        assertEquals(faltaDTO.data(), falta.getData());
        assertEquals(faltaDTO.motivo(), falta.getMotivo());
        assertEquals(faltaDTO.justificada(), falta.getJustificada());
    }

    @Test
    void deveRetornarFaltaDTOResponseDeFalta() {
        faltaDTO = FaltaMapper.mapToFaltaDTOResponse(falta);
        assertEquals(falta.getData(), faltaDTO.data());
        assertEquals(falta.getMotivo(), faltaDTO.motivo());
        assertEquals(falta.getJustificada(), faltaDTO.justificada());
    }

}
