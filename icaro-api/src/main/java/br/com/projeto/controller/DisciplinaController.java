package br.com.projeto.controller;

// ✨ ALTERAÇÃO AQUI: Importa o DTO e o Modelo
import br.com.projeto.dto.DisciplinaResponseDTO;
import br.com.projeto.model.Disciplina;
import br.com.projeto.repository.DisciplinaRepository; // ✨ ALTERAÇÃO AQUI: Importa o Repositório
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✨ ALTERAÇÃO AQUI: Importa a segurança
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ✨ ALTERAÇÃO AQUI: Imports para a lista
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/disciplinas") // ✨ ALTERAÇÃO AQUI: URL base para disciplinas
@RequiredArgsConstructor
public class DisciplinaController {

    // ✨ ALTERAÇÃO AQUI: Injeta o repositório
    private final DisciplinaRepository disciplinaRepository;

    // --- ✨ ALTERAÇÃO AQUI: Novo Endpoint GET ---
    /**
     * Endpoint para listar TODAS as disciplinas.
     * Protegido para que apenas Professores possam acessar (pois só eles precisam
     * escolher disciplinas ao criar turmas no momento).
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<List<DisciplinaResponseDTO>> listarDisciplinas() {
        
        // 1. Busca todas as disciplinas do banco
        List<Disciplina> disciplinas = disciplinaRepository.findAll();

        // 2. Converte a lista de Disciplina (Entidade) para DisciplinaResponseDTO
        List<DisciplinaResponseDTO> responseDTOs = disciplinas.stream()
                .map(this::mapToDisciplinaResponseDTO) // Usa o helper para converter
                .collect(Collectors.toList());

        // 3. Retorna a lista com status OK
        return ResponseEntity.ok(responseDTOs);
    }

    // --- ✨ ALTERAÇÃO AQUI: Método Helper ---
    /**
     * Converte uma entidade Disciplina para o DTO DisciplinaResponseDTO.
     */
    private DisciplinaResponseDTO mapToDisciplinaResponseDTO(Disciplina disciplina) {
        return new DisciplinaResponseDTO(
            disciplina.getId(),
            disciplina.getNome(),
            disciplina.getCodigo()
        );
    }

    // (Aqui poderíamos adicionar @PostMapping para criar disciplinas, @PutMapping, @DeleteMapping, etc.
    // mas por enquanto, o DataSeeder cria a de teste)
}