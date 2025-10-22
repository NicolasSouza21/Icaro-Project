package br.com.projeto.model;

import jakarta.persistence.*; // Importa anotações JPA
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Para data e hora do registro

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "presencas", uniqueConstraints = {
    // Garante que um aluno só pode ter UM registro de presença por aula
    @UniqueConstraint(columnNames = {"aluno_id", "aula_id"})
})
public class Presenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relacionamentos ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Usuario aluno; // O aluno que registrou a presença (DEVE ter Role ALUNO)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula; // A aula em que a presença foi registrada

    // --- Timestamps de Check-in/Check-out ---

    // Momento em que o aluno registrou a entrada (ex: via biometria no totem)
    @Column(name = "data_hora_checkin", nullable = false)
    private LocalDateTime dataHoraCheckin;

    // Momento em que o aluno registrou a saída (pode ser nulo se ele não sair)
    @Column(name = "data_hora_checkout")
    private LocalDateTime dataHoraCheckout;

    // (Opcional) Poderíamos adicionar um campo para o método de registro
    // Ex: BIOMETRIA, MANUAL_PROFESSOR, etc.
    // @Column(name = "metodo_registro")
    // private String metodoRegistro;

}