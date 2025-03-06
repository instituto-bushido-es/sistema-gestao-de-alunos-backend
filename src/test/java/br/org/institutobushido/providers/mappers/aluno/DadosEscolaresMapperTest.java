package br.org.institutobushido.providers.mappers.aluno;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.org.institutobushido.providers.enums.aluno.Turno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import br.org.institutobushido.controllers.dtos.aluno.dados_escolares.DadosEscolaresDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.dados_escolares.DadosEscolaresDTOResponse;
import br.org.institutobushido.controllers.dtos.aluno.dados_escolares.UpdateDadosEscolaresDTORequest;
import br.org.institutobushido.models.aluno.Aluno;
import br.org.institutobushido.models.aluno.dados_escolares.DadosEscolares;

@SpringBootTest
class DadosEscolaresMapperTest {

    private DadosEscolares dadosEscolares;
    private DadosEscolaresDTORequest dadosEscolaresDTORequest;
    private DadosEscolaresDTOResponse dadosEscolaresDTOResponse;
    private UpdateDadosEscolaresDTORequest updateDadosEscolaresDTORequest;

    private Aluno aluno = new Aluno();
    
    @BeforeEach
    void setUp() {
        dadosEscolares = new DadosEscolares("ESCOLA", Turno.MANHA, "1");

        dadosEscolaresDTORequest = new DadosEscolaresDTORequest("escola", Turno.MANHA, "1");

        dadosEscolaresDTOResponse = new DadosEscolaresDTOResponse("escola", Turno.MANHA, "1");

        updateDadosEscolaresDTORequest = new UpdateDadosEscolaresDTORequest("escola", Turno.MANHA, "1");
    }

    @Test
    void deveMapearAlunoParaDTOResponse() {

        dadosEscolaresDTOResponse = DadosEscolaresMapper
            .mapToDadosEscolaresDTOResponse(dadosEscolares);

        assertEquals(dadosEscolares.getEscola(), dadosEscolaresDTOResponse.escola());
        assertEquals(dadosEscolares.getTurno(), dadosEscolaresDTOResponse.turno());
        assertEquals(dadosEscolares.getSerie(), dadosEscolaresDTOResponse.serie());
    }

    @Test
    void deveMapearAlunoDTORequestDeAluno(){
        dadosEscolares = DadosEscolaresMapper.mapToDadosEscolares(dadosEscolaresDTORequest);

        assertEquals(dadosEscolaresDTORequest.escola(), dadosEscolares.getEscola());
        assertEquals(dadosEscolaresDTORequest.turno(), dadosEscolares.getTurno());
        assertEquals(dadosEscolaresDTORequest.serie(), dadosEscolares.getSerie());
    }

    @Test
    void deveMapearDadosEscolaresDeUpdateDadosEscolares(){
        dadosEscolares = DadosEscolaresMapper.updateDadosEscolares(updateDadosEscolaresDTORequest, aluno);
        assertEquals(updateDadosEscolaresDTORequest.escola(), dadosEscolares.getEscola());
        assertEquals(updateDadosEscolaresDTORequest.turno(), dadosEscolares.getTurno());
        assertEquals(updateDadosEscolaresDTORequest.serie(), dadosEscolares.getSerie());
    }
}
