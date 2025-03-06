package br.org.institutobushido.providers.mappers.aluno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.org.institutobushido.providers.enums.aluno.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import br.org.institutobushido.controllers.dtos.aluno.AlunoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.aluno.dados_escolares.DadosEscolaresDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.dados_sociais.DadosSociaisDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.endereco.EnderecoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.graduacao.GraduacaoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.historico_de_saude.HistoricoSaudeDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.models.aluno.Aluno;
import br.org.institutobushido.models.aluno.dados_escolares.DadosEscolares;
import br.org.institutobushido.models.aluno.dados_sociais.DadosSociais;
import br.org.institutobushido.models.aluno.endereco.Endereco;
import br.org.institutobushido.models.aluno.graduacao.Graduacao;
import br.org.institutobushido.models.aluno.historico_de_saude.HistoricoSaude;
import br.org.institutobushido.models.aluno.historico_de_saude.informacoes_saude.Alergia;
import br.org.institutobushido.models.aluno.historico_de_saude.informacoes_saude.Cirurgia;
import br.org.institutobushido.models.aluno.historico_de_saude.informacoes_saude.DoencaCronica;
import br.org.institutobushido.models.aluno.historico_de_saude.informacoes_saude.UsoMedicamentoContinuo;
import br.org.institutobushido.models.aluno.responsaveis.Responsavel;

@SpringBootTest
class AlunoMapperTest {
        private AlunoDTORequest alunoDTORequest;
        private Aluno aluno;
        private AlunoDTOResponse alunoDTOResponse;

        private MockMultipartFile imagemAluno;

        @BeforeEach
        void setUp() {
                alunoDTORequest = AlunoDTORequest.builder()
                                .withNome("NOME")
                                .withCorDePele(CorDePele.AMARELO.name())
                                .withCpf("11111111111")
                                .withCartaoSus("11111111111")
                                .withDadosEscolares(
                                                new DadosEscolaresDTORequest("ESCOLA", Turno.MANHA, "1"))
                                .withDadosSociais(
                                                new DadosSociaisDTORequest(false, false, Imovel.PROPRIO, 4, 2, false,
                                                                0))
                                .withEndereco(
                                                new EnderecoDTORequest("CIDADE", "ESTADO", "06140140", "100",
                                                                "LOGRADOURO"))
                                .withGenero(Genero.M.name())
                                .withGraduacao(
                                                new GraduacaoDTORequest(1, 2))
                                .withHistoricoSaude(
                                                HistoricoSaudeDTORequest.builder().build())
                                .withDataNascimento(new Date().getTime() - 2000 * 60 * 60 * 24 * 4)
                                .withResponsaveis(ResponsavelDTORequest.builder()
                                                .withCpf("11111111111")
                                                .withEmail("email@email.com")
                                                .withFiliacao(FiliacaoResposavel.OUTRO)
                                                .withNome("NOME")
                                                .withTelefone("11111111111")
                                                .build())
                                .build();

                aluno = new Aluno();

                aluno.setNome("NOME");
                aluno.setCpf("11111111111");
                aluno.setDataNascimento(new Date().getTime());
                aluno.setGenero(Genero.M.name());
                aluno.setDadosEscolares(
                                new DadosEscolares("ESCOLA", Turno.MANHA, "1"));

                aluno.setEndereco(
                                new Endereco(
                                                "CIDADE",
                                                "ESTADO",
                                                "CEP",
                                                "100",
                                                "LOGRADOURO"));

                aluno.setDadosSociais(
                                new DadosSociais(Imovel.PROPRIO,
                                                4,
                                                2,
                                                2000,
                                                false,
                                                false,
                                                false));

                aluno.setHistoricoSaude(
                                new HistoricoSaude(
                                                TipoSanguineo.O_POSITIVO,
                                                new UsoMedicamentoContinuo("Tipo"),
                                                new DoencaCronica("Doenca"),
                                                new Alergia("Alergia"),
                                                new Cirurgia("Cirurgia"),
                                                List.of("Deficiencia"),
                                                List.of("Acompanhamento")));

                aluno.adicionarGraduacao(
                                new Graduacao(7, new ArrayList<>(), true, 100, LocalDate.now().minusMonths(3),
                                                LocalDate.now().plusMonths(3), false, 80, 1, 10));

                aluno.adicionarResponsavel(
                                new Responsavel("Nome", "12345678901", "Email", "Telefone", FiliacaoResposavel.OUTRO));

                alunoDTOResponse = AlunoDTOResponse.builder().build();
        }

        @Test
        void deveMapearAlunoDTORequestToAlunoComImagem() throws IOException {
                imagemAluno = new MockMultipartFile("imagem", "imagem".getBytes());
                aluno = AlunoMapper.mapToAluno(alunoDTORequest, imagemAluno);
                
                assertEquals(alunoDTORequest.nome(), aluno.getNome());
                assertEquals(alunoDTORequest.cpf(), aluno.getCpf());
                assertEquals(new Date(alunoDTORequest.dataNascimento()), aluno.getDataNascimento());
                assertEquals(alunoDTORequest.genero(), aluno.getGenero());
                assertEquals(alunoDTORequest.dadosSociais().auxilioBrasil(),
                                aluno.getDadosSociais().isAuxilioBrasil());
                assertEquals(alunoDTORequest.dadosEscolares().escola(), aluno.getDadosEscolares().getEscola());
                assertEquals(alunoDTORequest.endereco().estado(), aluno.getEndereco().getEstado());
                assertEquals(alunoDTORequest.historicoSaude().alergia().tipo(),
                                aluno.getHistoricoSaude().getAlergia().getTipo());
                assertEquals(alunoDTORequest.cartaoSus(), aluno.getCartaoSus());
                assertEquals(alunoDTORequest.corDePele(), aluno.getCorDePele());
                assertEquals(alunoDTORequest.rg(), aluno.getRg());
                assertNotNull(aluno.getImagemAluno());
        }

        @Test
        void deveMapearAlunoDTORequestToAluno() {
                aluno = AlunoMapper.mapToAluno(alunoDTORequest);
                assertEquals(alunoDTORequest.nome(), aluno.getNome());
                assertEquals(alunoDTORequest.cpf(), aluno.getCpf());
                assertEquals(new Date(alunoDTORequest.dataNascimento()), aluno.getDataNascimento());
                assertEquals(alunoDTORequest.genero(), aluno.getGenero());
                assertEquals(alunoDTORequest.dadosSociais().auxilioBrasil(),
                                aluno.getDadosSociais().isAuxilioBrasil());
                assertEquals(alunoDTORequest.dadosEscolares().escola(), aluno.getDadosEscolares().getEscola());
                assertEquals(alunoDTORequest.endereco().estado(), aluno.getEndereco().getEstado());
                assertEquals(alunoDTORequest.historicoSaude().alergia().tipo(),
                                aluno.getHistoricoSaude().getAlergia().getTipo());
                assertEquals(alunoDTORequest.cartaoSus(), aluno.getCartaoSus());
                assertEquals(alunoDTORequest.corDePele(), aluno.getCorDePele());
                assertEquals(alunoDTORequest.rg(), aluno.getRg());
        }

        @Test
        void deveMapearAlunoDTOResponseDeAluno() {
                alunoDTOResponse = AlunoMapper.mapToAlunoDTOResponse(aluno);
                assertEquals(aluno.getNome(), alunoDTOResponse.nome());
                assertEquals(aluno.getCpf(), alunoDTOResponse.cpf());
                assertEquals(aluno.getDataNascimento(), alunoDTOResponse.dataNascimento());
                assertEquals(aluno.getGenero(), alunoDTOResponse.genero());
                assertEquals(aluno.getDadosSociais().isAuxilioBrasil(),
                                alunoDTOResponse.dadosSociais().auxilioBrasil());
                assertEquals(aluno.getDadosEscolares().getEscola(), alunoDTOResponse.dadosEscolares().escola());
                assertEquals(aluno.getEndereco().getEstado(), alunoDTOResponse.endereco().estado());
                assertEquals(aluno.getHistoricoSaude().getAlergia().getTipo(),
                                alunoDTOResponse.historicoSaude().alergia().tipo());
                assertEquals(aluno.getCartaoSus(), alunoDTOResponse.cartaoSus());
                assertEquals(aluno.getCorDePele(), alunoDTOResponse.corDePele());
                assertEquals(aluno.getRg(), alunoDTOResponse.rg());
        }

        @Test
        void deveMapearUmaListaAlunoParaUmaListaAlunoDTOResponse() {
                List<Aluno> alunos = List.of(aluno);
                List<AlunoDTOResponse> alunosDTOResponses = AlunoMapper.mapToListAlunoDTOResponse(alunos);
                assertEquals(alunos.get(0).getNome(), alunosDTOResponses.get(0).nome());
                assertEquals(alunos.get(0).getCpf(), alunosDTOResponses.get(0).cpf());
                assertEquals(alunos.get(0).getDataNascimento(), alunosDTOResponses.get(0).dataNascimento());
                assertEquals(alunos.get(0).getGenero(), alunosDTOResponses.get(0).genero());
        }

}