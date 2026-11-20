
package com.fatec.beassi.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // TRATAR ERROS DE VALIDAÇÃO DOS DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException ex) {

        Map<String, String> erros = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage())
        );

        Map<String, Object> resposta = criarResposta(
                HttpStatus.BAD_REQUEST,
                "Erro de validação dos dados."
        );

        resposta.put("erros", erros);

        return ResponseEntity.badRequest().body(resposta);
    }

    // TRATAR ERROS DE VALIDAÇÃO DE PARÂMETROS
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarViolacao(
            ConstraintViolationException ex) {

        Map<String, String> erros = new LinkedHashMap<>();

        ex.getConstraintViolations().forEach(erro ->
                erros.put(
                        erro.getPropertyPath().toString(),
                        erro.getMessage()
                )
        );

        Map<String, Object> resposta = criarResposta(
                HttpStatus.BAD_REQUEST,
                "Os parâmetros informados são inválidos."
        );

        resposta.put("erros", erros);

        return ResponseEntity.badRequest().body(resposta);
    }

    // TRATAR ERROS DE NEGÓCIO E REGISTROS NÃO ENCONTRADOS
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> tratarStatus(
            ResponseStatusException ex) {

        HttpStatus status = HttpStatus.valueOf(
                ex.getStatusCode().value()
        );

        String mensagem = ex.getReason() != null
                ? ex.getReason()
                : "Não foi possível processar a solicitação.";

        return ResponseEntity.status(status)
                .body(criarResposta(status, mensagem));
    }

    // TRATAR VIOLAÇÕES DE INTEGRIDADE DO BANCO
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarIntegridade(
            DataIntegrityViolationException ex) {

        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status).body(
                criarResposta(
                        status,
                        "A operação viola uma restrição dos dados."
                )
        );
    }

    // TRATAR JSON INVÁLIDO
   @ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<Map<String, Object>> tratarJson(
        HttpMessageNotReadableException ex) {

    logger.error("Erro ao converter JSON recebido: ", ex);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    return ResponseEntity.badRequest().body(
            criarResposta(
                    status,
                    "O formato dos dados enviados é inválido."
            )
    );
}

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> tratarErroGeral(Exception ex) {

        logger.error("Erro interno não tratado:", ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.internalServerError().body(
                criarResposta(status, "Ocorreu um erro interno no servidor.")
        );
        }

    // MONTAR RESPOSTA PADRONIZADA
    private Map<String, Object> criarResposta(
            HttpStatus status,
            String mensagem) {

        Map<String, Object> resposta = new LinkedHashMap<>();

        resposta.put("dataHora", LocalDateTime.now());
        resposta.put("status", status.value());
        resposta.put("erro", status.getReasonPhrase());
        resposta.put("mensagem", mensagem);

        return resposta;
    }
}
