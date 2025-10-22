package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoMatriculadoDTO {

    private Long id; // ID do usuário (aluno)
    private String nome;
    private String matriculaRa;
    private String email;

}