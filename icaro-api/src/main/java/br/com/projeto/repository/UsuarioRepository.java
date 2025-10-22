package br.com.projeto.repository;

import br.com.projeto.model.Usuario; // ✨ ALTERAÇÃO AQUI
import org.springframework.data.jpa.repository.JpaRepository; // ✨ ALTERAÇÃO AQUI
import org.springframework.stereotype.Repository; // ✨ ALTERAÇÃO AQUI

import java.util.Optional; // ✨ ALTERAÇÃO AQUI

// ✨ ALTERAÇÃO AQUI: Mudamos de "class" para "interface" e estendemos JpaRepository
@Repository // ✨ ALTERAÇÃO AQUI: Indica ao Spring que esta é uma interface de Repositório (Bean)
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ✨ ALTERAÇÃO AQUI: Método de busca customizado.
    // O Spring Data JPA vai entender esse nome de método e automaticamente
    // criar uma query SQL (ex: "SELECT * FROM usuarios WHERE email = ?")
    // Isso é o que o Spring Security usará para o login.
    Optional<Usuario> findByEmail(String email);
    
}