package com.fluxo.fluxoapp.shared.web;

import org.springframework.http.HttpStatus;

/** Erro de negócio com status HTTP e código estável que o app pode tratar. */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() { return status; }
    public String code() { return code; }
}
