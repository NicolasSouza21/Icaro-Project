package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotemCheckinRequestDTO {

    // O RA do aluno que foi identificado pela biometria
    private String matriculaRa;

    // Poderíamos adicionar um ID do totem para saber de onde veio o registro
    // private String totemId;
}