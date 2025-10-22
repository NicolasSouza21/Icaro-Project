package br.com.projeto.controller;

import br.com.projeto.dto.MatriculaRequestDTO;
import br.com.projeto.model.Matricula;
import br.com.projeto.model.Turma;
import br.com.projeto.model.Usuario;
import br.com.projeto.repository.MatriculaRepository;
import br.com.projeto.repository.TurmaRepository;
import br.com.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaRepository matriculaRepository;
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<?> matricularAluno(@RequestBody MatriculaRequestDTO requestDTO) {

        // 1. Validações de Segurança e de Negócio
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario professorLogado = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Professor não autenticado"));

        Turma turma = turmaRepository.findById(requestDTO.getTurmaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        // O professor logado é o dono da turma?
        if (!turma.getProfessor().getId().equals(professorLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para matricular alunos nesta turma.");
        }

        Usuario aluno = usuarioRepository.findById(requestDTO.getAlunoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno com o ID informado não foi encontrado."));

        // O aluno já está matriculado nesta turma?
        matriculaRepository.findByAlunoIdAndTurmaId(aluno.getId(), turma.getId()).ifPresent(m -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este aluno já está matriculado nesta turma.");
        });

        // 2. Cria a nova matrícula
        Matricula novaMatricula = Matricula.builder()
                .aluno(aluno)
                .turma(turma)
                .build();

        // 3. Salva no banco
        matriculaRepository.save(novaMatricula);

        // 4. Retorna uma resposta de sucesso sem corpo (201 Created)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}