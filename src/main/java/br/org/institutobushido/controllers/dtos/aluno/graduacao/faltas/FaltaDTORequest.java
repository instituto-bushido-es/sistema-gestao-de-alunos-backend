package br.org.institutobushido.controllers.dtos.aluno.graduacao.faltas;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FaltaDTORequest(
                @NotNull(message = "Motivo é obrigatório") @NotEmpty(message = "Motivo é obrigatório") String motivo,
                Boolean justificada) {
}
