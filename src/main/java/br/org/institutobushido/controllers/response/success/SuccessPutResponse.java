package br.org.institutobushido.controllers.response.success;

import java.io.Serializable;
import org.springframework.http.HttpStatus;
import lombok.Data;

@Data
public class SuccessPutResponse implements Serializable {
    private String id;
    private int status;
    private String message;
    private String entity;

    public SuccessPutResponse(String id, String message) {
        this.id = id;
        this.status = HttpStatus.OK.value();
        this.message = message;
    }

    public SuccessPutResponse(String id, String message, String entity) {
        this.id = id;
        this.entity = entity;
        this.status = HttpStatus.OK.value();
        this.message = message;
    }
}
