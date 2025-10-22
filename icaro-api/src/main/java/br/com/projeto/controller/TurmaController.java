package br.com.projeto.controller;

import br.com.projeto.dto.AlunoMatriculadoDTO;
import br.com.projeto.dto.TurmaRequestDTO;
import br.com.projeto.dto.TurmaResponseDTO;
import br.com.projeto.model.Disciplina;
import br.com.projeto.model.Matricula;
import br.com.projeto.model.Turma;
import br.com.projeto.model.Usuario;
import br.com.projeto.repository.DisciplinaRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaRepository matriculaRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<TurmaResponseDTO> criarTurma(@RequestBody TurmaRequestDTO requestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailProfessor = authentication.getName();
        Usuario professor = usuarioRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));
        Disciplina disciplina = disciplinaRepository.findById(requestDTO.getDisciplinaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));
        Turma novaTurma = Turma.builder()
                .nomeTurma(requestDTO.getNomeTurma())
                .semestre(requestDTO.getSemestre())
                .professor(professor)
                .disciplina(disciplina)
                .build();
        Turma turmaSalva = turmaRepository.save(novaTurma);
        TurmaResponseDTO responseDTO = mapToTurmaResponseDTO(turmaSalva);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<List<TurmaResponseDTO>> listarMinhasTurmas() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailProfessor = authentication.getName();
        Usuario professor = usuarioRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));
        List<Turma> turmasDoProfessor = turmaRepository.findByProfessorId(professor.getId());
        List<TurmaResponseDTO> responseDTOs = turmasDoProfessor.stream()
                .map(this::mapToTurmaResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{turmaId}/alunos")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<List<AlunoMatriculadoDTO>> listarAlunosDaTurma(@PathVariable Long turmaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario professor = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Professor não autenticado"));

        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada"));
        if (!turma.getProfessor().getId().equals(professor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta turma não pertence a você");
        }

        List<Matricula> matriculas = matriculaRepository.findByTurmaId(turmaId);

        // --- ✨ ALTERAÇÃO AQUI: Linha corrigida ---
        // A conversão (stream) é feita na lista 'matriculas' e o resultado é ATRIBUÍDO a 'alunosDTO'
        List<AlunoMatriculadoDTO> alunosDTO = matriculas.stream()
                .map(matricula -> new AlunoMatriculadoDTO(
                        matricula.getAluno().getId(),
                        matricula.getAluno().getNome(),
                        matricula.getAluno().getMatriculaRa(),
                        matricula.getAluno().getEmail()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(alunosDTO);
    }

    private TurmaResponseDTO mapToTurmaResponseDTO(Turma turma) {
        return new TurmaResponseDTO(
            turma.getId(),
            turma.getNomeTurma(),
            turma.getSemestre(),
            turma.getProfessor().getNome(),
            turma.getDisciplina().getNome(),
            turma.getDisciplina().getCodigo()
        );
    }
}