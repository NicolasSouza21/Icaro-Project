package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ✨ ALTERAÇÃO AQUI: Anotações do Lombok
@Data // Gera Getters, Setters, toString, etc.
@Builder // Padrão Builder
@NoArgsConstructor // Construtor vazio
@AllArgsConstructor // Construtor com todos os argumentos
public class AuthRequestDTO {

    // ✨ ALTERAÇÃO AQUI: Campos que o frontend (React) deve enviar no JSON
    
    private String email;
    private String senha;
    
}