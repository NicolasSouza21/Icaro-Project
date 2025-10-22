package br.com.projeto.model;

import jakarta.persistence.*; // Importa anotações JPA
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Para notas com casas decimais

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matriculas", uniqueConstraints = {
    // Garante que um aluno só pode se matricular UMA VEZ na mesma turma
    @UniqueConstraint(columnNames = {"aluno_id", "turma_id"})
})
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relacionamentos ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Usuario aluno; // O aluno matriculado (DEVE ter Role ALUNO)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma; // A turma em que o aluno está matriculado

    // --- Notas ---
    // Usamos BigDecimal para precisão decimal, scale=2 significa 2 casas decimais

    @Column(name = "nota_np1", precision = 4, scale = 2) // Ex: 10.00
    private BigDecimal notaNp1;

    @Column(name = "nota_np2", precision = 4, scale = 2)
    private BigDecimal notaNp2;

    @Column(name = "nota_exame", precision = 4, scale = 2)
    private BigDecimal notaExame;

    // --- Campos Calculados (Regras de Negócio UNIP) ---
    // Estes campos NÃO são colunas no banco. São calculados em tempo real.
    // Usamos @Transient para indicar ao JPA para IGNORAR estes campos.

    @Transient
    public BigDecimal getMediaParcial() {
        if (notaNp1 == null || notaNp2 == null) {
            return null; // Não calcula se faltar nota
        }
        // (NP1 + NP2) / 2
        return notaNp1.add(notaNp2).divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
    }

    @Transient
    public String getSituacaoParcial() {
        BigDecimal mediaParcial = getMediaParcial();
        if (mediaParcial == null) {
            return "Indefinida";
        }
        // Se Média Parcial >= 7.0, Situação = "Aprovado". Senão, "Em Exame".
        return mediaParcial.compareTo(BigDecimal.valueOf(7.0)) >= 0 ? "Aprovado" : "Em Exame";
    }

    @Transient
    public BigDecimal getMediaFinal() {
        String situacaoParcial = getSituacaoParcial();
        if ("Aprovado".equals(situacaoParcial)) {
            return getMediaParcial(); // Se já aprovado, média final é a parcial
        }
        if ("Em Exame".equals(situacaoParcial) && notaExame != null) {
            // (Média Parcial + Exame) / 2
            return getMediaParcial().add(notaExame).divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        }
        return null; // Não calcula se não fez exame ou situação indefinida
    }

    @Transient
    public String getSituacaoFinal() {
        String situacaoParcial = getSituacaoParcial();
        if ("Aprovado".equals(situacaoParcial)) {
            return "Aprovado";
        }
        if ("Em Exame".equals(situacaoParcial)) {
            BigDecimal mediaFinal = getMediaFinal();
            if (mediaFinal == null) {
                return "Em Exame"; // Ainda não tem nota do exame
            }
            // Se Média Final >= 5.0, Situação = "Aprovado". Senão, "Reprovado".
            return mediaFinal.compareTo(BigDecimal.valueOf(5.0)) >= 0 ? "Aprovado (Exame)" : "Reprovado";
        }
        return "Indefinida"; // Se situação parcial for Indefinida
    }

    // (Mais tarde, adicionaremos o relacionamento @OneToMany para a lista de Presenca)
}