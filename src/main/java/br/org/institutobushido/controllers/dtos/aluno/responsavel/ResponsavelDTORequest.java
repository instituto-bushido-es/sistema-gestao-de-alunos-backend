package br.org.institutobushido.controllers.dtos.aluno.responsavel;

import br.org.institutobushido.providers.enums.aluno.FiliacaoResposavel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Valid
@Builder(setterPrefix = "with")
public record ResponsavelDTORequest(
                @NotNull(message = "Nome do responsavel é obrigatório") @NotEmpty(message = "Nome do responsavel é obrigatório") String nome,

               // @NotNull(message = "Cpf do responsavel é obrigatório") @NotEmpty(message = "Cpf do responsavel é obrigatório") @Pattern(regexp = "^\\d{11}$", message = "Cpf inválido! Ex: 12345678910")
                String cpf,

                String telefone,

                String email,

                @NotNull(message = "Filiacao é obrigatório") FiliacaoResposavel filiacao) {
}
