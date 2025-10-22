package br.com.projeto.repository;

import br.com.projeto.model.Aula; // ✨ ALTERAÇÃO AQUI: Importa a entidade Aula
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // ✨ ALTERAÇÃO AQUI: Import para listas

// ✨ ALTERAÇÃO AQUI: Mudamos de "class" para "interface"
@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    // (O Spring Data JPA nos dará save(), findById(), findAll(), etc.)

    // --- Métodos de busca customizados que serão úteis ---

    /**
     * Busca todas as aulas de uma turma específica.
     * Útil para o professor ver o histórico de aulas da turma.
     */
    List<Aula> findByTurmaIdOrderByDataHoraAulaDesc(Long turmaId); // Ordena da mais recente para a mais antiga

    /**
     * Busca aulas de uma turma que estão em um estado específico (Ex: "ABERTA").
     * Útil para encontrar a aula atual onde a chamada está ativa.
     */
    List<Aula> findByTurmaIdAndEstado(Long turmaId, String estado);

}