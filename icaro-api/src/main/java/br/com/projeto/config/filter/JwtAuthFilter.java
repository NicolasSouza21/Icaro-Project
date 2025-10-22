package br.com.projeto.config.filter; // ✨ ALTERAÇÃO AQUI: Pacote correto

import br.com.projeto.service.JwtService; // ✨ ALTERAÇÃO AQUI: Importa nosso serviço
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull; // ✨ ALTERAÇÃO AQUI
import lombok.RequiredArgsConstructor; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // ✨ ALTERAÇÃO AQUI
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // ✨ ALTERAÇÃO AQUI
import org.springframework.web.filter.OncePerRequestFilter; // ✨ ALTERAÇÃO AQUI

import java.io.IOException;

@Component // ✨ ALTERAÇÃO AQUI: Torna este filtro um Spring Bean (gerenciado pelo Spring)
@RequiredArgsConstructor // ✨ ALTERAÇÃO AQUI: Cria um construtor com os campos 'final'
public class JwtAuthFilter extends OncePerRequestFilter { // ✨ ALTERAÇÃO AQUI: O filtro base do Spring

    // ✨ ALTERAÇÃO AQUI: Injeção dos nossos serviços
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // O Spring vai injetar nosso CustomUserDetailsService

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Pega o cabeçalho 'Authorization' da requisição
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Validação rápida: Se não houver header ou não começar com "Bearer ", ignora.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Passa para o próximo filtro
            return; // Encerra a execução deste filtro
        }

        // 3. Extrai o token (remove o "Bearer ")
        jwt = authHeader.substring(7); // "Bearer " tem 7 caracteres

        try {
            // 4. Extrai o email (subject) de dentro do token
            userEmail = jwtService.extractUsername(jwt);

            // 5. Se temos o email E o usuário ainda não está autenticado no contexto do Spring
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 6. Carrega o usuário do banco de dados (usando nosso CustomUserDetailsService)
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 7. Valida o token (compara com o usuário do banco e verifica expiração)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    
                    // 8. ✅ SUCESSO! Cria a autenticação
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credenciais (senha) são nulas, pois estamos usando token
                            userDetails.getAuthorities() // As roles (ALUNO, PROFESSOR)
                    );
                    
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9. Coloca o usuário autenticado no Contexto de Segurança do Spring
                    // A partir daqui, o Spring sabe que o usuário está logado para esta requisição.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            // 10. Passa a requisição (autenticada ou não) para o próximo filtro
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // ⚠️ Se o token for inválido (expirado, assinatura errada), uma exceção será lançada.
            // Aqui podemos tratar o erro, mas por enquanto vamos apenas
            // passar para o próximo filtro (o Spring vai barrar por falta de autenticação).
            // Em produção, poderíamos customizar a resposta de "token inválido" aqui.
            filterChain.doFilter(request, response);
        }
    }
}