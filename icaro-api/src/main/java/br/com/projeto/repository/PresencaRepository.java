package br.com.projeto.repository;

import br.com.projeto.model.Presenca; // ✨ ALTERAÇÃO AQUI: Importa a entidade Presenca
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // ✨ ALTERAÇÃO AQUI: Import para listas
import java.util.Optional; // ✨ ALTERAÇÃO AQUI: Import para Optional

// ✨ ALTERAÇÃO AQUI: Mudamos de "class" para "interface"
@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {

    // (O Spring Data JPA nos dará save(), findById(), findAll(), etc.)

    // --- Métodos de busca customizados que serão úteis ---

    /**
     * Busca todos os registros de presença para uma aula específica.
     * Útil para o professor ver quem esteve presente naquela aula.
     */
    List<Presenca> findByAulaId(Long aulaId);

    /**
     * Busca todos os registros de presença de um aluno específico em uma turma.
     * (Requereria buscar as aulas da turma primeiro ou um JOIN mais complexo).
     * Alternativa: buscar todas as presenças do aluno e filtrar.
     */
    List<Presenca> findByAlunoId(Long alunoId);

    /**
     * Busca o registro de presença específico de um aluno em uma aula.
     * Útil para verificar se o aluno já fez check-in ou para adicionar o check-out.
     */
    Optional<Presenca> findByAlunoIdAndAulaId(Long alunoId, Long aulaId);

}