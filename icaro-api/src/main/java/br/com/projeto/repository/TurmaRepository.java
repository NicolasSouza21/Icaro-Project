package br.com.projeto.repository;

import br.com.projeto.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ✨ ALTERAÇÃO AQUI: Importamos a List
import java.util.List; 

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    
    // ✨ ALTERAÇÃO AQUI: Descomentamos e definimos o método
    /**
     * Busca todas as turmas associadas a um professor específico,
     * usando o ID do professor. O Spring Data JPA cria a query automaticamente
     * com base no nome do método ("findBy" + "Professor" + "Id").
     */
    List<Turma> findByProfessorId(Long professorId);
    
    // (Opcional, mas útil no futuro): Listar todas as turmas de uma disciplina
    // List<Turma> findByDisciplinaId(Long disciplinaId);
}