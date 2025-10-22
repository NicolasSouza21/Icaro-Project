package br.com.projeto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ex: "2025.2" ou "2026.1"
    @Column(nullable = false)
    private String semestre;

    // Ex: "A", "B", "Noturno"
    @Column(nullable = false)
    private String nomeTurma; 

    // --- RELACIONAMENTOS ---

    // Relacionamento 1: Muitas turmas podem ter UM professor
    @ManyToOne(fetch = FetchType.LAZY) // LAZY = Só carrega o professor se pedirmos
    @JoinColumn(name = "professor_id", nullable = false) // Chave estrangeira no banco
    private Usuario professor;

    // Relacionamento 2: Muitas turmas podem ser da MESMA disciplina
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false) // Chave estrangeira no banco
    private Disciplina disciplina;
    
    // (Mais tarde, adicionaremos aqui o @OneToMany para a lista de Alunos (Matricula) 
    // e para a lista de Presença (Frequencia))
}