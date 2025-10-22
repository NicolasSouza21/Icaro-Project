package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaRequestDTO {

    // ID da Turma onde o aluno será matriculado
    private Long turmaId;

    // ID do Aluno a ser matriculado
    private Long alunoId;

    // Poderíamos também permitir a busca por RA ou email, mas por ID é mais direto.
    // private String alunoRa;
}