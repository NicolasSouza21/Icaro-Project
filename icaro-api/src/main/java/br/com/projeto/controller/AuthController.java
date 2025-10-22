package br.com.projeto.controller;

import br.com.projeto.dto.AuthRequestDTO;
import br.com.projeto.dto.AuthResponseDTO;
import br.com.projeto.model.Usuario; // ✨ ALTERAÇÃO AQUI
import br.com.projeto.service.JwtService;
import lombok.RequiredArgsConstructor; // ✨ ALTERAÇÃO AQUI
import org.springframework.http.ResponseEntity; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.authentication.AuthenticationManager; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.core.Authentication; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.core.userdetails.UsernameNotFoundException; // ✨ ALTERAÇÃO AQUI
import org.springframework.web.bind.annotation.PostMapping; // ✨ ALTERAÇÃO AQUI
import org.springframework.web.bind.annotation.RequestBody; // ✨ ALTERAÇÃO AQUI
import org.springframework.web.bind.annotation.RequestMapping; // ✨ ALTERAÇÃO AQUI
import org.springframework.web.bind.annotation.RestController; // ✨ ALTERAÇÃO AQUI

@RestController // ✨ ALTERAÇÃO AQUI: Marca esta classe como um Controller REST
@RequestMapping("/api/v1/auth") // ✨ ALTERAÇÃO AQUI: Define o prefixo da URL para este controller
@RequiredArgsConstructor // ✨ ALTERAÇÃO AQUI: Cria um construtor com os campos 'final'
public class AuthController {

    // ✨ ALTERAÇÃO AQUI: Injeção dos "cérebros" da autenticação
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    // Não precisamos do CustomUserDetailsService aqui, pois o AuthManager já o utiliza

    /**
     * Endpoint de Login.
     * Recebe um JSON (AuthRequestDTO) e retorna um JSON (AuthResponseDTO) com o token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) {
        
        // 1. Tenta autenticar o usuário
        // O AuthenticationManager vai chamar internamente o nosso CustomUserDetailsService
        // e vai usar o PasswordEncoder para comparar as senhas.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getSenha()
                )
        );

        // 2. Se a autenticação foi SUCESSO (não lançou exceção)
        // Pegamos o 'Usuario' principal que o Spring Security autenticou
        Object principal = authentication.getPrincipal();
        Usuario usuario;

        if (principal instanceof Usuario) {
            usuario = (Usuario) principal;
        } else {
            // Isso não deve acontecer se nosso CustomUserDetailsService estiver correto
            throw new UsernameNotFoundException("Usuário não encontrado no contexto de segurança");
        }

        // 3. Gera o token JWT para este usuário
        String token = jwtService.generateToken(usuario);

        // 4. Cria o DTO de Resposta
        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .token(token)
                .email(usuario.getEmail())
                .role(usuario.getRole().name()) // Converte o Enum 'Role' para String
                .build();

        // 5. Retorna 200 OK com o DTO no corpo
        return ResponseEntity.ok(responseDTO);
        
        // ⚠️ Nota: Se a autenticação falhar (email errado, senha errada),
        // o 'authenticationManager.authenticate()' vai lançar uma exceção
        // (ex: BadCredentialsException). O Spring Boot vai capturar isso
        // automaticamente e retornar um erro 401 Unauthorized para o React.
        // (Mais tarde, podemos criar um @ControllerAdvice para customizar essa resposta)
    }
    
}