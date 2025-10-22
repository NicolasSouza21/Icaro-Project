package br.com.projeto.model;

import jakarta.persistence.*; // Importa anotações JPA
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Para data e hora da aula

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "aulas")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relacionamento ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma; // A turma a que esta aula pertence

    // --- Data e Hora ---
    // Representa o momento exato em que a aula ocorreu ou está agendada
    @Column(name = "data_hora_aula", nullable = false)
    private LocalDateTime dataHoraAula;

    // --- Estado da Aula (para controle da chamada) ---
    // Ex: 'AGENDADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA'
    // Usaremos um Enum para isso mais tarde, por agora uma String simples.
    // @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private String estado; // Ex: ABERTA, FECHADA

    // (Mais tarde, adicionaremos o relacionamento @OneToMany para a lista de Presenca)

}