package br.org.institutobushido.models.aluno.dados_escolares;

import br.org.institutobushido.providers.enums.aluno.Turno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class DadosEscolaresTest {

    private DadosEscolares dadosEscolares;

    @BeforeEach
    void setUp() {
        dadosEscolares = new DadosEscolares(
                "ESCOLA",
                Turno.MANHA,
                "1"
        );
    }

    @Test
    void deveSetarValorDeEscola() {
        dadosEscolares.setEscola("ESCOLA1");
        dadosEscolares.setTurno(Turno.MANHA);
        dadosEscolares.setSerie("1");
        assertEquals("ESCOLA1", dadosEscolares.getEscola());
        assertEquals(Turno.MANHA, dadosEscolares.getTurno());
        assertEquals("1", dadosEscolares.getSerie());
    }

    @Test
    void deveManterOsMesmosValoresSeEstiverVazio() {
        dadosEscolares = new DadosEscolares();
        dadosEscolares.setEscola("");
        assertNull(dadosEscolares.getEscola());
    }
}
