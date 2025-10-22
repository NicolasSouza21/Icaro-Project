package br.com.projeto.repository;

import br.com.projeto.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // ✨ ALTERAÇÃO AQUI: Import para Optional

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    /**
     * Busca todas as aulas de uma turma específica.
     */
    List<Aula> findByTurmaIdOrderByDataHoraAulaDesc(Long turmaId);

    /**
     * Busca aulas de uma turma que estão em um estado específico.
     */
    List<Aula> findByTurmaIdAndEstado(Long turmaId, String estado);

    // --- ✨ ALTERAÇÃO AQUI: Novo método para o Totem ---
    /**
     * Busca a primeira (mais recente) aula de uma turma que está em um estado específico.
     * Otimizado para o totem, que só precisa encontrar uma aula aberta, não todas.
     */
    Optional<Aula> findFirstByTurmaIdAndEstadoOrderByDataHoraAulaDesc(Long turmaId, String estado);

}