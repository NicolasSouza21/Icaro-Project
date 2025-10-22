package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresencaResponseDTO {

    private Long id;
    private Long alunoId;
    private String nomeAluno;
    private Long aulaId;
    private LocalDateTime dataHoraCheckin;

}