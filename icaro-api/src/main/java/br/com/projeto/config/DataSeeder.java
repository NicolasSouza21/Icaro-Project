package br.com.projeto.config;

import br.com.projeto.model.Disciplina; // ✨ ALTERAÇÃO AQUI
import br.com.projeto.model.Usuario;
import br.com.projeto.model.enums.Role;
import br.com.projeto.repository.DisciplinaRepository; // ✨ ALTERAÇÃO AQUI
import br.com.projeto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    // ✨ ALTERAÇÃO AQUI: Injetamos o novo repositório
    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- [DataSeeder] Iniciando verificação de dados...");

        // --- Seed de Usuários (como antes) ---
        Optional<Usuario> alunoOpt = usuarioRepository.findByEmail("aluno@icaro.com");
        Optional<Usuario> profOpt = usuarioRepository.findByEmail("professor@icaro.com");

        if (alunoOpt.isEmpty()) {
            System.out.println("--- [DataSeeder] Criando usuário ALUNO de teste...");
            Usuario aluno = Usuario.builder()
                    .nome("Aluno de Teste")
                    .email("aluno@icaro.com")
                    .senhaHash(passwordEncoder.encode("senha123")) 
                    .matriculaRa("123456")
                    .role(Role.ALUNO)
                    .build();
            usuarioRepository.save(aluno);
            System.out.println("--- [DataSeeder] Usuário ALUNO criado.");
        } else {
             System.out.println("--- [DataSeeder] Usuário ALUNO já existe.");
        }

        if (profOpt.isEmpty()) {
            System.out.println("--- [DataSeeder] Criando usuário PROFESSOR de teste...");
            Usuario professor = Usuario.builder()
                    .nome("Professor de Teste")
                    .email("professor@icaro.com")
                    .senhaHash(passwordEncoder.encode("senha123")) 
                    .matriculaRa(null) 
                    .role(Role.PROFESSOR)
                    .build();
            usuarioRepository.save(professor);
            System.out.println("--- [DataSeeder] Usuário PROFESSOR criado.");
        } else {
             System.out.println("--- [DataSeeder] Usuário PROFESSOR já existe.");
        }

        // --- ✨ ALTERAÇÃO AQUI: Seed de Disciplina ---
        if (disciplinaRepository.count() == 0) { // Se não houver NENHUMA disciplina
            System.out.println("--- [DataSeeder] Criando disciplina de teste 'Engenharia de Software'...");
            Disciplina disciplina = Disciplina.builder()
                    .nome("Engenharia de Software III")
                    .codigo("CC7261")
                    .build();
            disciplinaRepository.save(disciplina);
            System.out.println("--- [DataSeeder] Disciplina de teste criada (ID: 1).");
        } else {
            System.out.println("--- [DataSeeder] Disciplinas já existem.");
        }

        System.out.println("--- [DataSeeder] Verificação concluída.");
    }
}