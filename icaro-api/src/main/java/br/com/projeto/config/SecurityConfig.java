package br.com.projeto.config; // Mantendo seu pacote

import br.com.projeto.config.filter.JwtAuthFilter; // ✨ ALTERAÇÃO AQUI: Importa nosso filtro
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// ✨ ALTERAÇÃO AQUI: Importa o filtro padrão de autenticação do Spring
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ✨ ALTERAÇÃO AQUI: Injeção de dependência do nosso filtro JWT
    private final JwtAuthFilter jwtAuthFilter;

    // ✨ ALTERAÇÃO AQUI: Construtor para injetar o filtro
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Bean para criptografar senhas (como antes)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para expor o AuthenticationManager (como antes)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configuração principal do Spring Security (como antes)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Libera os endpoints de autenticação
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        
                        // Protege todo o resto
                        .anyRequest().authenticated()
                )
                // ✨ ALTERAÇÃO AQUI: "Instalando" nosso porteiro (o filtro JWT)
                // Dizemos ao Spring Security: "Execute o 'jwtAuthFilter' ANTES
                // do filtro padrão 'UsernamePasswordAuthenticationFilter'."
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuração do CORS (como antes)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Frontend React
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}