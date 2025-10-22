package br.com.projeto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob; // ✨ ALTERAÇÃO AQUI: Import para Large Object
import jakarta.persistence.Table;

import br.com.projeto.model.enums.Role;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "matricula_ra", unique = true)
    private String matriculaRa;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private Role role;

    // --- ✨ ALTERAÇÃO AQUI: Novo campo para armazenar o template da digital ---
    /**
     * Armazena o template biométrico do usuário, geralmente em formato Base64.
     * A anotação @Lob indica que este pode ser um campo de dados grande.
     * columnDefinition="TEXT" garante que o PostgreSQL use um tipo de coluna
     * adequado para textos longos, sem limite de caracteres.
     */
    @Lob
    @Column(name = "digital_template", columnDefinition = "TEXT")
    private String digitalTemplate;
    // --- FIM DA ALTERAÇÃO ---


    // --- IMPLEMENTAÇÃO DOS MÉTODOS DO USERDETAILS ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.senhaHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}