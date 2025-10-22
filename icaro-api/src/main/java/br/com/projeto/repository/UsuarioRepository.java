package br.com.projeto.repository;

import br.com.projeto.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para o Spring Security usar no login.
    Optional<Usuario> findByEmail(String email);

    // --- ✨ ALTERAÇÃO AQUI: Novo método para o Totem ---
    /**
     * Busca um usuário pela sua matrícula (RA).
     * Usado pelo TotemController para identificar o aluno via biometria.
     */
    Optional<Usuario> findByMatriculaRa(String matriculaRa);

}