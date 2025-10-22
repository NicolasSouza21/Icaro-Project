package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ✨ ALTERAÇÃO AQUI: Anotações do Lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaResponseDTO {

    // ✨ ALTERAÇÃO AQUI: Campos que o Spring vai devolver ao React
    private Long id;
    private String nome;
    private String codigo;

}