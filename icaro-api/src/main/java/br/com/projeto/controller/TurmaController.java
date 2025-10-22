package br.com.projeto.controller;

import br.com.projeto.dto.TurmaRequestDTO;
import br.com.projeto.dto.TurmaResponseDTO;
import br.com.projeto.model.Disciplina;
import br.com.projeto.model.Turma;
import br.com.projeto.model.Usuario;
import br.com.projeto.repository.DisciplinaRepository;
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

// ✨ ALTERAÇÃO AQUI: Imports para a lista
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisciplinaRepository disciplinaRepository;

    /**
     * Endpoint para um PROFESSOR criar uma nova Turma.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')") 
    public ResponseEntity<TurmaResponseDTO> criarTurma(@RequestBody TurmaRequestDTO requestDTO) {

        // 1. Pega o usuário (Professor) que está logado (autenticado)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailProfessor = authentication.getName();
        
        Usuario professor = usuarioRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));

        // 2. Busca a disciplina pelo ID informado no DTO
        Disciplina disciplina = disciplinaRepository.findById(requestDTO.getDisciplinaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));

        // 3. Cria a nova Turma
        Turma novaTurma = Turma.builder()
                .nomeTurma(requestDTO.getNomeTurma())
                .semestre(requestDTO.getSemestre())
                .professor(professor) // Associa o professor logado
                .disciplina(disciplina) // Associa a disciplina encontrada
                .build();

        // 4. Salva no banco
        Turma turmaSalva = turmaRepository.save(novaTurma);

        // 5. Retorna um DTO de Resposta
        TurmaResponseDTO responseDTO = mapToTurmaResponseDTO(turmaSalva); // ✨ ALTERAÇÃO AQUI: Usa o helper

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // --- ✨ ALTERAÇÃO AQUI: Novo Endpoint GET ---
    /**
     * Endpoint para o PROFESSOR logado listar SUAS turmas.
     */
    @GetMapping("/minhas") // URL: /api/v1/turmas/minhas
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<List<TurmaResponseDTO>> listarMinhasTurmas() {
        
        // 1. Pega o usuário (Professor) logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailProfessor = authentication.getName();
        Usuario professor = usuarioRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));

        // 2. Usa o método do repository que criamos
        List<Turma> turmasDoProfessor = turmaRepository.findByProfessorId(professor.getId());

        // 3. Converte a lista de Turma (Entidade) para uma lista de TurmaResponseDTO
        List<TurmaResponseDTO> responseDTOs = turmasDoProfessor.stream()
                .map(this::mapToTurmaResponseDTO) // Usa o helper para converter cada turma
                .collect(Collectors.toList());

        // 4. Retorna a lista com status OK
        return ResponseEntity.ok(responseDTOs);
    }

    // --- ✨ ALTERAÇÃO AQUI: Método Helper ---
    /**
     * Converte uma entidade Turma para o DTO TurmaResponseDTO.
     * Evita repetição de código entre criarTurma e listarMinhasTurmas.
     */
    private TurmaResponseDTO mapToTurmaResponseDTO(Turma turma) {
        return new TurmaResponseDTO(
            turma.getId(),
            turma.getNomeTurma(),
            turma.getSemestre(),
            turma.getProfessor().getNome(), // Assume que professor não é nulo
            turma.getDisciplina().getNome(), // Assume que disciplina não é nula
            turma.getDisciplina().getCodigo()
        );
    }
}