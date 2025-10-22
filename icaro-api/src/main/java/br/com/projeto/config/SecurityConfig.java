package br.com.projeto.config;

import br.com.projeto.config.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// ✨ ALTERAÇÃO AQUI: Importa HttpMethod
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Mantemos, pois pode ser útil para regras mais complexas no futuro
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ✨ ALTERAÇÃO AQUI: Refinamos as regras de autorização
                .authorizeHttpRequests(authorize -> authorize
                        // Libera login e registro (se houver)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Regras para TURMAS:
                        // GET /api/v1/turmas/minhas -> Exige PROFESSOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/turmas/minhas").hasAuthority("ROLE_PROFESSOR")
                        // POST /api/v1/turmas -> Exige PROFESSOR
                        .requestMatchers(HttpMethod.POST, "/api/v1/turmas").hasAuthority("ROLE_PROFESSOR")
                        // Outros métodos em /api/v1/turmas/** (ex: PUT, DELETE) -> Exige PROFESSOR (ajustar se necessário)
                        .requestMatchers("/api/v1/turmas/**").hasAuthority("ROLE_PROFESSOR")

                        // Regras para DISCIPLINAS:
                        // GET /api/v1/disciplinas -> Exige PROFESSOR (por enquanto)
                        .requestMatchers(HttpMethod.GET, "/api/v1/disciplinas").hasAuthority("ROLE_PROFESSOR")
                        // Outros métodos em /api/v1/disciplinas/** -> Talvez exigir ADMIN no futuro? Por agora, PROFESSOR.
                        .requestMatchers("/api/v1/disciplinas/**").hasAuthority("ROLE_PROFESSOR")

                        // Regras para AULAS:
                        // POST /api/v1/aulas (Abrir Chamada) -> Exige PROFESSOR
                        .requestMatchers(HttpMethod.POST, "/api/v1/aulas").hasAuthority("ROLE_PROFESSOR")
                        // Outros métodos em /api/v1/aulas/** (GET presenças, PUT fechar) -> Exige PROFESSOR (ajustar se necessário)
                        .requestMatchers("/api/v1/aulas/**").hasAuthority("ROLE_PROFESSOR")


                        // TODO: Adicionar regras para Alunos (GET /api/v1/matriculas/minhas, etc.)
                        // TODO: Adicionar regras para Admin

                        // Qualquer outra requisição não definida acima exige autenticação (mas não role específica)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}