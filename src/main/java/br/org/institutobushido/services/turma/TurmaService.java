package br.org.institutobushido.services.turma;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import br.org.institutobushido.controllers.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.*;
import br.org.institutobushido.controllers.dtos.turma.lista_de_chamada.*;
import br.org.institutobushido.models.aluno.graduacao.Graduacao;
import br.org.institutobushido.models.aluno.graduacao.falta.Falta;
import br.org.institutobushido.providers.mappers.aluno.AlunoMapper;
import br.org.institutobushido.providers.mappers.turma.ListaDeChamadaMapper;
import br.org.institutobushido.providers.utils.date_utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import br.org.institutobushido.models.admin.Admin;
import br.org.institutobushido.models.admin.turmas.TurmaResponsavel;
import br.org.institutobushido.models.aluno.Aluno;
import br.org.institutobushido.models.turma.Turma;
import br.org.institutobushido.models.turma.tutor.Tutor;
import br.org.institutobushido.providers.mappers.turma.TurmaMapper;
import br.org.institutobushido.providers.utils.resources.exceptions.AlreadyRegisteredException;
import br.org.institutobushido.providers.utils.resources.exceptions.EntityNotFoundException;
import br.org.institutobushido.providers.utils.resources.exceptions.LimitQuantityException;
import br.org.institutobushido.repositories.AdminRepositorio;
import br.org.institutobushido.repositories.TurmaRepositorio;

@Service
@Slf4j
public class TurmaService implements TurmaServiceInterface {

    private final TurmaRepositorio turmaRepositorio;
    private final MongoTemplate mongoTemplate;
    private final AdminRepositorio adminRepositorio;

    private static final String COLLECTION_TURMAS = "turmas";
    private static final String COLLECTION_ALUNO = "alunos";
    private static final String GRADUACAO = "graduacao";

    public TurmaService(TurmaRepositorio turmaRepositorio, MongoTemplate mongoTemplate,
            AdminRepositorio adminRepositorio) {
        this.mongoTemplate = mongoTemplate;
        this.turmaRepositorio = turmaRepositorio;
        this.adminRepositorio = adminRepositorio;
    }

    @CacheEvict(value = { "turma", "admin" }, allEntries = true)
    @Override
    public String criarNovaTurma(TurmaDTORequest turma) {

        if (this.verificaSeTurmaExiste(turma.nome())) {
            throw new AlreadyRegisteredException(" A Turma " + turma.nome() + " ja está registrada.");
        }

        Turma novaTurma = TurmaMapper.mapToTurma(turma);

        Admin admin = this.vinculrTurmaAoAdmin(turma.tutor().email(),
                new TurmaResponsavel(novaTurma.getEndereco(), novaTurma.getNome()));

        novaTurma.setTutor(
                new Tutor(admin.getNome(), admin.getEmail()));

        this.turmaRepositorio.save(novaTurma);
        return "Turma " + turma.nome() + " foi criada com sucesso!";
    }

    @CacheEvict(value = { "turma", "admin" }, allEntries = true)
    @Override
    public String deletarTurma(String emailAdmin, String nomeTurma) {

        if (!this.verificaSeTurmaExiste(nomeTurma)) {
            throw new EntityNotFoundException("Turma com esse nome não existe");
        }

        if (this.vefrificarSeExistemAlunosAtivosNaTurma(nomeTurma)) {
            throw new LimitQuantityException("Turma com alunos não pode ser deletada");
        }

        this.desvicularTurmaDoAdmin(emailAdmin, nomeTurma);

        this.turmaRepositorio.deleteByNome(nomeTurma);
        return "Turma " + nomeTurma + " deletada com sucesso";
    }

    @Override
    public List<TurmaDTOResponse> listarTurmas(long dataInicial, long dataFinal) {

        if (dataInicial > System.currentTimeMillis()) {
            throw new LimitQuantityException("Data inicial deve ser menor que data atual");
        }

        if (dataInicial == 0 && dataFinal == 0) {
            return TurmaMapper.mapToListTurmaDTOResponse(this.turmaRepositorio.findAll());
        }

        if (dataFinal == 0) {
            dataFinal = System.currentTimeMillis();
        }

        if (dataInicial >= dataFinal) {
            throw new LimitQuantityException("Data inicial deve ser menor que data final");
        }

        Criteria criteria = Criteria.where("dataCriacao")
                .gte(new Date(dataInicial))
                .lte(new Date(dataFinal));
        Query query = new Query();
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.ASC, "dataCriacao"));
        return TurmaMapper.mapToListTurmaDTOResponse(this.mongoTemplate.find(query, Turma.class));
    }

    @Override
    public TurmaDTOResponse buscarTurmaPorNome(String nomeTurma) {
        return TurmaMapper.mapToTurmaDTOResponse(this.encontrarTurmaPeloNome(nomeTurma));
    }

    @Override
    public DadosTurmaDTOResponse listarAlunosDaTurma(String nomeTurma) {
        Turma turma = this.encontrarTurmaPeloNome(nomeTurma);
        return new DadosTurmaDTOResponse(turma.getTutor().getEmail(), this.listarAlunosAtivosDaTurma(turma.getNome()));
    }

    protected Aluno encontrarAlunoPorMatricula(String matricula) {
        List<AlunoDTOResponse> alunoEncontrado = this.buscarAlunoPorMatricula(matricula);

        if (alunoEncontrado.isEmpty()) {
            throw new EntityNotFoundException("Aluno com a matricula " + matricula + " não encontrado!");
        }

        return AlunoMapper.mapToAluno(alunoEncontrado.get(0));
    }

    @Cacheable(value = "aluno", key = "#matricula")
    public List<AlunoDTOResponse> buscarAlunoPorMatricula(String matricula) {
        Query query = new Query(Criteria.where("_id").is(matricula));
        return AlunoMapper.mapToListAlunoDTOResponse(mongoTemplate.find(query, Aluno.class));
    }


    public ListaDeChamadaDTOResponse obterListaDeChamada(String nomeTurma, LocalDate dataInicial, LocalDate dataFinal) {
        List<TurmaAlunoDTOResponse> alunos = listarAlunosAtivosDaTurma(nomeTurma);
        List<AlunosTurmaListaDeChamadaDTOResponse> listaDeChamada = new ArrayList<>();

        Turma turma = encontrarTurmaPeloNome(nomeTurma);
        List<LocalDate> diasAula = turma.getDiasAula();

        boolean dataInicialValida = diasAula.contains(dataInicial);
        boolean dataFinalValida = diasAula.contains(dataFinal);

        if (!dataInicialValida) {
            dataInicial = encontrarDataMaisProxima(diasAula, dataInicial, true);
        }
        if (!dataFinalValida) {
            dataFinal = encontrarDataMaisProxima(diasAula, dataFinal, false);
        }

        for (TurmaAlunoDTOResponse aluno : alunos) {
            Aluno alunoEncontrado = encontrarAlunoPorMatricula(aluno.getMatricula());
            Graduacao graduacaoAtual = alunoEncontrado.getGraduacaoAtual();

            AlunosTurmaListaDeChamadaDTOResponse alunoListaChamada = ListaDeChamadaMapper.mapToAlunosTurmaListaChamadaDTOResponseRefactor(aluno, graduacaoAtual);
            List<ListaDePresencaDTOResponse> presencas = new ArrayList<>();

            for (LocalDate dia : diasAula) {
                if ((dataInicial.isEqual(dataFinal) && dia.isEqual(dataInicial)) ||
                        (dataInicial.isBefore(dataFinal) && (dia.isEqual(dataInicial) || dia.isEqual(dataFinal) || (dia.isAfter(dataInicial) && dia.isBefore(dataFinal))))) {

                        processarListaDeChamada( graduacaoAtual, dia, presencas);
                }
            }

            alunoListaChamada.setPresencas(presencas);
            listaDeChamada.add(alunoListaChamada);
        }

        String email = turma.getTutor().getEmail();
        return new ListaDeChamadaDTOResponse(email, listaDeChamada);
    }

    private List<TurmaAlunoDTOResponse> listarAlunosAtivosDaTurma(String nomeTurma) {
        AggregationOperation match = Aggregation.match(Criteria.where("nome").is(nomeTurma));

        AggregationOperation lookup = Aggregation.lookup(COLLECTION_ALUNO, "nome", "turma", "alunos_turma");

        AggregationOperation unwind = Aggregation.unwind("$alunos_turma");

        AggregationOperation project = Aggregation.project()
                .and("alunos_turma.nome").as("nome")
                .and("alunos_turma._id").as("matricula")
                .and("alunos_turma.genero").as("genero")
                .and("alunos_turma.dataNascimento").as("dataNascimento")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(match, lookup, unwind, project);
        return mongoTemplate.aggregate(aggregation, COLLECTION_TURMAS, TurmaAlunoDTOResponse.class).getMappedResults();
    }

    private boolean verificaSeTurmaExiste(String nomeTurma) {
        Query query = new Query(Criteria.where("nome").regex(nomeTurma, "si"));
        return this.mongoTemplate.exists(query, Turma.class);
    }

    private Admin vinculrTurmaAoAdmin(String emailAdmin, TurmaResponsavel turma) {
        Admin admin = this.encontrarAdminPeloEmail(emailAdmin);
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(emailAdmin));
        Update update = new Update();
        update.push(COLLECTION_TURMAS, admin.adicionarTurma(turma));
        this.mongoTemplate.updateFirst(query, update, Admin.class);
        return admin;
    }

    private void desvicularTurmaDoAdmin(String emailAdmin, String nomeTurma) {
        Admin admin = this.encontrarAdminPeloEmail(emailAdmin);
        String turma = admin.removerTurma(nomeTurma);
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(emailAdmin));
        Update update = new Update();
        update.pull(COLLECTION_TURMAS, Query.query(Criteria.where("nome").is(turma)));
        this.mongoTemplate.updateFirst(query, update, Admin.class);
    }

    private boolean vefrificarSeExistemAlunosAtivosNaTurma(String nomeTurma) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where("turma").is(nomeTurma).and("graduacao").elemMatch(Criteria.where("status").is(true)));
        return this.mongoTemplate.exists(query, Aluno.class);
    }

    @Cacheable(value = "turma", key = "#nomeTurma")
    public Turma encontrarTurmaPeloNome(String nomeTurma) {
        Query query = new Query(Criteria.where("nome").regex(nomeTurma, "si"));
        Turma turma = this.mongoTemplate.findOne(query, Turma.class);

        if (turma == null) {
            throw new EntityNotFoundException("Turma com esse nome não existe");
        }

        return turma;
    }

    @Cacheable(value = "admin", key = "#email")
    public Admin encontrarAdminPeloEmail(String email) {
        return this.adminRepositorio.findByEmailAdmin(email).orElseThrow(
                () -> new EntityNotFoundException("Admin com esse email não existe"));
    }


    public void validarDatas(String nomeTurma, LocalDate dataInicialParsed, LocalDate dataFinalParsed) {

        if (dataInicialParsed.isEqual(dataFinalParsed)) {
            Turma turma = encontrarTurmaPeloNome(nomeTurma);
            List<LocalDate> diasAula = turma.getDiasAula();
            if (!diasAula.contains(dataInicialParsed)) {
                throw new NoSuchElementException("Dia não encontrado");
            }
        } else {
            if (dataInicialParsed.isAfter(dataFinalParsed)) {
                throw new IllegalArgumentException("Data inicial não pode ser maior que data final");
            }
        }
    }

    @Override
    public String registrarPresenca(String nomeTurma, ListaDeChamadaDTORequest listaDeChamadaDTORequest) {
        Turma turma = this.encontrarTurmaPeloNome(nomeTurma);
        LocalDate diaAula = listaDeChamadaDTORequest.getDiaAula();

        if (turma.getDiasAula().contains(diaAula)) {
            return "O dia da aula já está registrado." + diaAula;
        }

        this.adicionarDiaAula(nomeTurma, diaAula);
        log.info("Dia de aula adicionado: {}", diaAula);


        for (AlunosTurmaListaDeChamadaDTORequest aluno : listaDeChamadaDTORequest.getListaDeChamada()) {

            FaltaListaDeChamadaDTORequest falta = aluno.getFalta();
            try {
                adicionarFaltaDoAlunoListaChamada(
                        aluno.getMatricula(),
                        new FaltaListaDeChamadaDTORequest(
                                falta.getMotivo(),
                                falta.isJustificada(),
                                falta.getDataFalta()
                        )
                );
                log.info("Adicionando faltas");
            } catch (EntityNotFoundException e) {
                log.error("falha ao adicionar falta aluno {}", aluno);
                continue;
            }
        }
        log.info("Sucesso");

        return "Faltas registradas para todos os alunos encontrados.";
    }

    public String adicionarDiaAula(String nomeTurma, LocalDate diaAula) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nome").is(nomeTurma));
        Update update = new Update().addToSet("diasAula", diaAula);
        mongoTemplate.updateFirst(query, update, Turma.class);

        return "Dia de aula adicionado com sucesso.";
    }

    public String adicionarFaltaDoAlunoListaChamada(String matricula, FaltaListaDeChamadaDTORequest falta) {
        log.info("Falta recebida para matricula: {}, dia: {}", matricula, falta.getDataFalta());

        try {
            // 1. Buscar o aluno completo
            Aluno aluno = this.encontrarAlunoPorMatricula(matricula);

            // 2. Adicionar a falta na graduação - isso modifica o objeto em memória
            Falta novaFalta = aluno.getGraduacaoAtual().adicionarFalta(
                    falta.getMotivo(),
                    falta.getDataFalta(),
                    falta.isJustificada()
            );

            // 3. Abordagem para Cosmos DB - salvar o documento inteiro
            // Esta é a chave para resolução do problema
            mongoTemplate.save(aluno);

            log.info("Falta adicionada para matricula: {}, dia: {}", matricula, falta.getDataFalta());

            return String.valueOf(aluno.getGraduacaoAtual().getFaltas().size());
        } catch (Exception e) {
            log.error("Erro ao adicionar falta para matricula: {}, erro: {}", matricula, e.getMessage());
            throw e;
        }
    }
    private void processarListaDeChamada(Graduacao graduacaoAtual, LocalDate data,
                                         List<ListaDePresencaDTOResponse> listaDeChamada) {
        boolean faltaEncontrada = false;

        if (graduacaoAtual.getInicioGraduacao() != null && graduacaoAtual.getInicioGraduacao().isAfter(data)) {
            return;
        }

        for (Falta falta : graduacaoAtual.getFaltas()) {
            LocalDate faltaConvertida = DateUtil.getDataAsLocalDate(falta.getData());

            if (faltaConvertida.isEqual(data)) {
                adicionarAlunoAusente(falta, listaDeChamada);
                faltaEncontrada = true;
                break;
            }
        }

        if (!faltaEncontrada) {
            adicionarAlunoPresente(data, listaDeChamada);
        }
    }

    private void adicionarAlunoAusente(Falta falta, List<ListaDePresencaDTOResponse> listaDeChamada) {
        ListaDePresencaDTOResponse faltaDTO = ListaDeChamadaMapper.mapToFaltaDTOResponseListaChamada(falta);
        listaDeChamada.add(faltaDTO);
    }

    private void adicionarAlunoPresente(LocalDate data, List<ListaDePresencaDTOResponse> listaDeChamada) {
        ListaDePresencaDTOResponse faltaDTO = ListaDeChamadaMapper.mapToAlunoPresenteDTOResponseListaChamada(data);
        listaDeChamada.add(faltaDTO);
    }

    private LocalDate encontrarDataMaisProxima(List<LocalDate> diasAula, LocalDate data, boolean procurandoFuturo) {
        LocalDate dataMaisProxima = null;

        for (LocalDate dia : diasAula) {
            if (procurandoFuturo && dia.isAfter(data)) {
                return dia;
            }
            if (!procurandoFuturo && dia.isBefore(data)) {
                dataMaisProxima = dia;
            }
        }

        return (procurandoFuturo) ? (diasAula.isEmpty() ? null : diasAula.get(diasAula.size() - 1)) : dataMaisProxima;
    }


}
