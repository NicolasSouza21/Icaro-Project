package br.com.projeto.service;

import br.com.projeto.repository.UsuarioRepository; // ✨ ALTERAÇÃO AQUI
import org.springframework.beans.factory.annotation.Autowired; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.core.userdetails.UserDetails; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.core.userdetails.UserDetailsService; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.core.userdetails.UsernameNotFoundException; // ✨ ALTERAÇÃO AQUI
import org.springframework.stereotype.Service; // ✨ ALTERAÇÃO AQUI

@Service // ✨ ALTERAÇÃO AQUI: Marca esta classe como um "Serviço" (um Spring Bean)
public class CustomUserDetailsService implements UserDetailsService { // ✨ ALTERAÇÃO AQUI

    // ✨ ALTERAÇÃO AQUI: Injeção de dependência do nosso repositório
    private final UsuarioRepository usuarioRepository;

    @Autowired // ✨ ALTERAÇÃO AQUI: Spring injeta o repositório no construtor (melhor prática)
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ✨ ALTERAÇÃO AQUI: Este é o método que o Spring Security chama *exatamente* no momento do login.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Como definimos em Usuario.java, nosso "username" é o email.
        
        // 1. Busca o usuário no banco de dados pelo email
        return usuarioRepository.findByEmail(username)
                // 2. Se não encontrar, lança uma exceção padrão do Spring Security
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + username)
                );
        
        // 3. Se encontrar, o método retorna o objeto 'Usuario'.
        // Como 'Usuario' implementa 'UserDetails', o Spring Security sabe
        // como extrair a senha (getPassword()) e as roles (getAuthorities()) dele.
    }
}