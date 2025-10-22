package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ✨ ALTERAÇÃO AQUI: Anotações do Lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurmaResponseDTO {

    // ✨ ALTERAÇÃO AQUI: Estes são os campos que o Spring vai devolver ao React
    
    private Long id; // O ID da Turma que acabou de ser criada
    private String nomeTurma;
    private String semestre;
    
    // ✨ ALTERAÇÃO AQUI: Enviamos dados "achatados" (planificados)
    // Em vez de enviar o objeto 'Professor' e 'Disciplina' inteiros,
    // enviamos apenas os nomes, que é o que o React precisa exibir.
    private String nomeProfessor;
    private String nomeDisciplina;
    private String codigoDisciplina;

}