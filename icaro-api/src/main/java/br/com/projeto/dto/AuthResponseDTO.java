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
public class AuthResponseDTO {

    // ✨ ALTERAÇÃO AQUI: O token JWT que será enviado ao React
    private String token;
    
    // ✨ ALTERAÇÃO AQUI: (Opcional, mas boa prática)
    // Envia o email e o "role" do usuário de volta.
    // Isso economiza ao React o trabalho de ter que decodificar o token
    // imediatamente para saber quem está logado (ex: "Olá, professor@email.com").
    private String email;
    private String role;
    
}