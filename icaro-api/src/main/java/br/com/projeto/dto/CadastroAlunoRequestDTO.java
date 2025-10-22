package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroAlunoRequestDTO {

    private String nome;
    private String email;
    private String senha;
    private String matriculaRa;
    private Long turmaId; // ID da Turma onde o aluno será matriculado

}