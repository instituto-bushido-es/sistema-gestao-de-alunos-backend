package br.org.institutobushido.controllers.dtos.aluno.endereco;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record EnderecoDTORequest(
        String cidade,
        String estado,
        @Pattern(regexp = "^\\d{8}$", message = "Cep inválido") String cep,
        String numero,
        String logradouro) {
}
