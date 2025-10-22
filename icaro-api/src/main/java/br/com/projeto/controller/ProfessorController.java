package br.com.projeto.controller;

import br.com.projeto.dto.CadastroAlunoRequestDTO;
import br.com.projeto.model.Matricula;
import br.com.projeto.model.Turma;
import br.com.projeto.model.Usuario;
import br.com.projeto.model.enums.Role;
import br.com.projeto.repository.MatriculaRepository;
import br.com.projeto.repository.TurmaRepository;
import br.com.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/alunos")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    @Transactional // Garante que ou tudo funciona, ou nada é salvo no banco
    public ResponseEntity<?> cadastrarEmatricularAluno(@RequestBody CadastroAlunoRequestDTO requestDTO) {

        // 1. Pega o professor logado para validação
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario professorLogado = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Professor não autenticado"));

        // 2. Valida se a turma existe e se pertence ao professor logado
        Turma turma = turmaRepository.findById(requestDTO.getTurmaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        // --- ✨ ALTERAÇÃO AQUI: LOGS PARA DEBUG ---
        System.out.println("--- [DEBUG] TENTATIVA DE MATRICULAR ALUNO ---");
        System.out.println("ID do Professor Logado: " + professorLogado.getId());
        System.out.println("ID do Professor associado à Turma " + turma.getId() + " selecionada: " + turma.getProfessor().getId());
        // --- FIM DOS LOGS ---

        if (!turma.getProfessor().getId().equals(professorLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para cadastrar alunos nesta turma.");
        }

        // 3. Valida se o email ou RA do aluno já existem
        usuarioRepository.findByEmail(requestDTO.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O e-mail informado já está em uso.");
        });
        // (Adicionar validação para RA se ele for obrigatório e único)

        // 4. Cria a nova entidade Usuario para o aluno
        Usuario novoAluno = Usuario.builder()
                .nome(requestDTO.getNome())
                .email(requestDTO.getEmail())
                .senhaHash(passwordEncoder.encode(requestDTO.getSenha())) // Criptografa a senha
                .matriculaRa(requestDTO.getMatriculaRa())
                .role(Role.ALUNO) // Define o papel como ALUNO
                .build();

        Usuario alunoSalvo = usuarioRepository.save(novoAluno);

        // 5. Cria a nova entidade Matricula
        Matricula novaMatricula = Matricula.builder()
                .aluno(alunoSalvo)
                .turma(turma)
                .build();

        matriculaRepository.save(novaMatricula);

        // 6. Retorna uma resposta de sucesso
        return ResponseEntity.status(HttpStatus.CREATED).body("Aluno cadastrado e matriculado com sucesso!");
    }
}