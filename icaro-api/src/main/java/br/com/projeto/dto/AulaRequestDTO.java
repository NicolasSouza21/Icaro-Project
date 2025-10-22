package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ✨ ALTERAÇÃO AQUI: Anotações Lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AulaRequestDTO { // ✨ ALTERAÇÃO AQUI: Nome da classe correto

    // ✨ ALTERAÇÃO AQUI: Campo que o frontend enviará
    // ID da Turma para a qual o professor quer abrir a aula/chamada
    private Long turmaId;

    // Não precisamos enviar data/hora, pois o backend usará a hora atual
    // Não precisamos enviar estado, pois o backend definirá como "ABERTA"
}