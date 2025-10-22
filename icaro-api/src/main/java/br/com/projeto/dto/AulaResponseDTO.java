package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Importa LocalDateTime

// ✨ ALTERAÇÃO AQUI: Anotações Lombok
@Data
@Builder // Adiciona o Builder pattern
@NoArgsConstructor
@AllArgsConstructor
public class AulaResponseDTO {

    // ✨ ALTERAÇÃO AQUI: Campos que o Spring vai devolver ao React
    private Long id; // ID da Aula criada/listada
    private Long turmaId; // ID da Turma a que pertence
    private String nomeDisciplina; // Nome da disciplina (para exibição)
    private String nomeTurma; // Nome da turma (para exibição)
    private LocalDateTime dataHoraAula; // Data e hora exatas
    private String estado; // Estado atual (Ex: "ABERTA")

}