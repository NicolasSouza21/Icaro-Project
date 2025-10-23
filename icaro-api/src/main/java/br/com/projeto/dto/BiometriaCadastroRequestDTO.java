package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para receber os dados de um novo cadastro de biometria vindo do totem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiometriaCadastroRequestDTO {

    /**
     * A matrícula (RA) do aluno ao qual a biometria pertence.
     * O totem enviará este campo para que o backend saiba de quem é a digital.
     * Ex: "123456"
     */
    private String matriculaRa;

    /**
     * O template biométrico (a "impressão digital" em si) em formato de string.
     * Geralmente, o leitor biométrico gera um template em Base64, que é um formato
     * de texto seguro para ser enviado via JSON/HTTP.
     */
    private String templateBiometrico; // Em formato Base64

}