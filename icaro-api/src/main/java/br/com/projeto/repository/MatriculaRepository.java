package br.com.projeto.repository;

import br.com.projeto.model.Matricula; // ✨ ALTERAÇÃO AQUI: Importa a entidade Matricula
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ✨ ALTERAÇÃO AQUI: Imports para métodos de busca customizados
import java.util.List;
import java.util.Optional;

// ✨ ALTERAÇÃO AQUI: Mudamos de "class" para "interface"
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    // (O Spring Data JPA nos dará save(), findById(), findAll(), etc.)

    // --- Métodos de busca customizados que serão úteis ---

    /**
     * Busca todas as matrículas de uma turma específica.
     * Útil para o professor ver a lista de alunos da sua turma.
     */
    List<Matricula> findByTurmaId(Long turmaId);

    /**
     * Busca todas as matrículas de um aluno específico.
     * Útil para o aluno ver seu histórico ou notas.
     */
    List<Matricula> findByAlunoId(Long alunoId);

    /**
     * Busca uma matrícula específica pela combinação de aluno e turma.
     * Útil para verificar se um aluno já está matriculado ou para buscar
     * as notas/situação de um aluno específico numa turma.
     */
    Optional<Matricula> findByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

}