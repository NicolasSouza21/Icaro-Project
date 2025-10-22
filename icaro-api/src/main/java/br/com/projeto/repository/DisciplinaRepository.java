// ✨ ALTERAÇÃO AQUI: Pacote correto
package br.com.projeto.repository;

import br.com.projeto.model.Disciplina; // ✨ ALTERAÇÃO AQUI: Importa o modelo que criamos
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ✨ ALTERAÇÃO AQUI: Mudamos de "class" para "interface"
@Repository // ✨ ALTERAÇÃO AQUI: Indica ao Spring que é um Repositório (Bean)
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    
    // (O Spring Data JPA nos dará os métodos save(), findById(), findAll(), etc. automaticamente)
    
    // (Opcional, mas útil): Encontrar disciplina pelo código
    // Optional<Disciplina> findByCodigo(String codigo);
}