package br.com.projeto.model;

// ✨ ALTERAÇÃO AQUI (Imports do Lombok)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ✨ ALTERAÇÃO AQUI (Imports do Spring Security Core)
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// ✨ ALTERAÇÃO AQUI (Imports do Jakarta Persistence - JPA)
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// ✨ ALTERAÇÃO AQUI (Import do nosso Enum de Role, que criaremos a seguir)
import br.com.projeto.model.enums.Role;

import java.util.Collection;
import java.util.List;

@Data // ✨ ALTERAÇÃO AQUI: Gera Getters, Setters, equals(), hashCode() e toString()
@Builder // ✨ ALTERAÇÃO AQUI: Habilita o "Builder pattern"
@NoArgsConstructor // ✨ ALTERAÇÃO AQUI: Gera construtor sem argumentos (requerido pelo JPA)
@AllArgsConstructor // ✨ ALTERAÇÃO AQUI: Gera construtor com todos os argumentos
@Entity // ✨ ALTERAÇÃO AQUI: Marca esta classe como uma entidade JPA (um modelo de tabela)
@Table(name = "usuarios") // ✨ ALTERAÇÃO AQUI: Mapeia esta classe para a tabela "usuarios" no SQL
public class Usuario implements UserDetails { // ✨ ALTERAÇÃO AQUI: Implementa UserDetails
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✨ ALTERAÇÃO AQUI: ID auto-incrementável
    private Long id;

    @Column(nullable = false) // ✨ ALTERAÇÃO AQUI: Coluna não pode ser nula
    private String nome;

    @Column(nullable = false, unique = true) // ✨ ALTERAÇÃO AQUI: Email é a chave de login e não pode repetir
    private String email;

    @Column(name = "senha_hash", nullable = false) // ✨ ALTERAÇÃO AQUI: Mapeia para a coluna 'senha_hash'
    private String senhaHash;

    @Column(name = "matricula_ra", unique = true) // ✨ ALTERAÇÃO AQUI: Matrícula/RA (pode ser nulo para admin/prof)
    private String matriculaRa;

    // ✨ ALTERAÇÃO AQUI: Armazena o "Role" como String no banco (ALUNO, PROFESSOR, ADMIN)
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private Role role;


    // --- IMPLEMENTAÇÃO DOS MÉTODOS DO USERDETAILS ---
    // O Spring Security usará estes métodos internamente para autenticação e autorização

    @Override
    // ✨ ALTERAÇÃO AQUI: Define quais são as "permissões" (Roles) do usuário
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // O Spring Security espera um prefixo "ROLE_" (embora possamos mudar, é uma boa prática)
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    // ✨ ALTERAÇÃO AQUI: Informa ao Spring Security qual campo é a *senha hasheada*
    public String getPassword() {
        return this.senhaHash;
    }

    @Override
    // ✨ ALTERAÇÃO AQUI: Informa ao Spring Security qual campo é o "username" (o login)
    public String getUsername() {
        return this.email;
    }

    // ✨ ALTERAÇÃO AQUI: Para o nosso projeto, vamos manter as contas sempre ativas.
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