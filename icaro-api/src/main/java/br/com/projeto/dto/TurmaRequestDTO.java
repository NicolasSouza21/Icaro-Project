package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ✨ ALTERAÇÃO AQUI: Anotações do Lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurmaRequestDTO {

    // ✨ ALTERAÇÃO AQUI: Estes são os campos que o React deve enviar no JSON
    
    private String nomeTurma; // Ex: "A", "B", "Noturno"
    private String semestre;  // Ex: "2025.2"
    private Long disciplinaId; // O ID da Disciplina (ex: "Engenharia de Software")
    
    // O 'professorId' não é necessário aqui, pois o Controller
    // vai pegá-lo automaticamente do token JWT do usuário logado.
}