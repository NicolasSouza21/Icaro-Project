package br.com.projeto.controller;

// ✨ ALTERAÇÃO AQUI: Imports necessários
import br.com.projeto.dto.AulaRequestDTO;
import br.com.projeto.dto.AulaResponseDTO;
import br.com.projeto.model.Aula;
import br.com.projeto.model.Turma;
import br.com.projeto.model.Usuario;
import br.com.projeto.repository.AulaRepository;
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

import java.time.LocalDateTime;
import java.util.List; // Para validação

@RestController
@RequestMapping("/api/v1/aulas") // ✨ ALTERAÇÃO AQUI: URL base
@RequiredArgsConstructor
public class AulaController {

    // ✨ ALTERAÇÃO AQUI: Injeta os repositórios necessários
    private final AulaRepository aulaRepository;
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository; // Para verificar se a turma pertence ao professor

    /**
     * Endpoint para um PROFESSOR "abrir a chamada" (criar uma nova Aula)
     * para uma de suas turmas.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<AulaResponseDTO> abrirAula(@RequestBody AulaRequestDTO requestDTO) {

        // 1. Pega o professor logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailProfessor = authentication.getName();
        Usuario professor = usuarioRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Professor não autenticado"));

        // 2. Busca a turma pelo ID fornecido no DTO
        Turma turma = turmaRepository.findById(requestDTO.getTurmaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada"));

        // 3. ✨ VALIDAÇÃO IMPORTANTE: Verifica se a turma pertence ao professor logado
        if (!turma.getProfessor().getId().equals(professor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta turma não pertence a você");
        }

        // 4. ✨ VALIDAÇÃO ADICIONAL: Verifica se já existe uma aula ABERTA para esta turma
        List<Aula> aulasAbertas = aulaRepository.findByTurmaIdAndEstado(turma.getId(), "ABERTA");
        if (!aulasAbertas.isEmpty()) {
            // Se já existe, retorna a aula existente em vez de criar uma nova
            // (Ou poderia retornar um erro 409 Conflict, dependendo da regra de negócio)
            Aula aulaExistente = aulasAbertas.get(0); // Pega a primeira (deve haver só uma)
            AulaResponseDTO dtoExistente = mapToAulaResponseDTO(aulaExistente);
            return ResponseEntity.ok(dtoExistente); // Retorna 200 OK com os dados da aula já aberta
            // throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma chamada aberta para esta turma.");
        }


        // 5. Cria a nova Aula
        Aula novaAula = Aula.builder()
                .turma(turma) // Associa a turma encontrada
                .dataHoraAula(LocalDateTime.now()) // Define a hora atual como início da aula
                .estado("ABERTA") // Define o estado inicial
                .build();

        // 6. Salva no banco
        Aula aulaSalva = aulaRepository.save(novaAula);

        // 7. Retorna o DTO de Resposta
        AulaResponseDTO responseDTO = mapToAulaResponseDTO(aulaSalva);

        // Retorna 201 Created com os dados da nova aula
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // (Aqui adicionaremos GET para listar alunos da turma, GET para presenças da aula, PUT para fechar aula)


    // --- Método Helper para Mapeamento ---
    private AulaResponseDTO mapToAulaResponseDTO(Aula aula) {
        // Assume que turma e disciplina não são nulos ao chegar aqui
        Turma turma = aula.getTurma();
        // Acesso seguro aos nomes, tratando possível nulidade (embora não devesse ocorrer)
        String nomeDisciplina = (turma.getDisciplina() != null) ? turma.getDisciplina().getNome() : "Disciplina não encontrada";
        String nomeTurma = turma.getNomeTurma() != null ? turma.getNomeTurma() : "Nome não encontrado";

        return AulaResponseDTO.builder()
                .id(aula.getId())
                .turmaId(turma.getId())
                .nomeDisciplina(nomeDisciplina)
                .nomeTurma(nomeTurma)
                .dataHoraAula(aula.getDataHoraAula())
                .estado(aula.getEstado())
                .build();
    }

}