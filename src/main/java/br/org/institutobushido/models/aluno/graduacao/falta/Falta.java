package br.org.institutobushido.models.aluno.graduacao.falta;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.org.institutobushido.providers.utils.resources.exceptions.LimitQuantityException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Falta implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    public Falta(String motivo, Date data, Boolean justificada) {
        if (data.getTime() > new Date().getTime()) {
            throw new LimitQuantityException("A data deve ser menor ou igual a data atual");
        }
        this.data = new SimpleDateFormat("dd-MM-yyyy").format(data);
        this.motivo = motivo;
        this.justificada = justificada;
    }

    private String data;
    private String motivo;
    private Boolean justificada;

    public void setData(String data) {
        this.data = data;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setJustificada(Boolean justificada) {
        this.justificada = justificada;
    }

}