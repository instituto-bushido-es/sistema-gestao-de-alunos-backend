package br.org.institutobushido.providers.mappers.aluno;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTOResponse;
import br.org.institutobushido.models.aluno.responsaveis.Responsavel;
import br.org.institutobushido.providers.enums.aluno.FiliacaoResposavel;

@Component
public class ResponsavelMapper {

    private ResponsavelMapper() {
    }

    public static Responsavel mapToResponsavel(ResponsavelDTORequest responsavelDTORequest) {
        if (responsavelDTORequest == null) {
            return null;
        }

        return new Responsavel(
                responsavelDTORequest.nome(),
                responsavelDTORequest.cpf(),
                responsavelDTORequest.telefone(),
                responsavelDTORequest.email(),
                responsavelDTORequest.filiacao());
    }

    public static Responsavel mapToResponsavel(ResponsavelDTOResponse responsavelDTOResponse) {
        if (responsavelDTOResponse == null) {
            return null;
        }

        return new Responsavel(
                responsavelDTOResponse.nome(),
                responsavelDTOResponse.cpf(),
                responsavelDTOResponse.email(),
                responsavelDTOResponse.telefone(),
                FiliacaoResposavel.valueOf(responsavelDTOResponse.filiacao())
        );
    }

    public static ResponsavelDTOResponse mapToResponsavelDTOResponse(Responsavel responsavel) {
        if (responsavel == null) {
            return null;
        }

        return ResponsavelDTOResponse.builder()
                .withNome(responsavel.getNome())
                .withCpf(responsavel.getCpf())
                .withEmail(responsavel.getEmail())
                .withTelefone(responsavel.getTelefone())
                .withFiliacao(responsavel.getFiliacao().name())
                .build();
    }

    public static List<ResponsavelDTOResponse> mapToResponsaveisDTOResponse(List<Responsavel> responsaveis) {
        if (responsaveis == null) {
            return Collections.emptyList();
        }

        return responsaveis.stream().map(ResponsavelMapper::mapToResponsavelDTOResponse)
                .collect(Collectors.toList());
    }

    public static List<Responsavel> mapToResponsaveis(List<ResponsavelDTORequest> responsaveis) {
        if (responsaveis == null) {
            return Collections.emptyList();
        }

        return responsaveis.stream().map(ResponsavelMapper::mapToResponsavel)
                .collect(Collectors.toList());
    }

    public static List<Responsavel> mapToListResponsaveis(List<ResponsavelDTOResponse> responsaveis) {
        if (responsaveis == null) {
            return Collections.emptyList();
        }

        return responsaveis.stream().map(ResponsavelMapper::mapToResponsavel)
                .collect(Collectors.toList());
    }
}
