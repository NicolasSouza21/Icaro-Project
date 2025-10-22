package br.com.projeto.controller;

import br.com.projeto.dto.PresencaResponseDTO;
import br.com.projeto.dto.TotemCheckinRequestDTO;
import br.com.projeto.model.Aula;
import br.com.projeto.model.Matricula;
import br.com.projeto.model.Presenca;
import br.com.projeto.model.Usuario;
import br.com.projeto.repository.AulaRepository;
import br.com.projeto.repository.MatriculaRepository;
import br.com.projeto.repository.PresencaRepository;
import br.com.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/totem")
@RequiredArgsConstructor
public class TotemController {

    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;
    private final AulaRepository aulaRepository;
    private final PresencaRepository presencaRepository;

    @PostMapping("/checkin")
    @Transactional
    // Este endpoint não precisará de autenticação de professor,
    // pois o totem fará a chamada. Mais tarde, podemos protegê-lo com uma API Key.
    public ResponseEntity<PresencaResponseDTO> registrarPresencaViaTotem(@RequestBody TotemCheckinRequestDTO requestDTO) {

        // 1. Encontrar o aluno pelo RA fornecido
        Usuario aluno = usuarioRepository.findByMatriculaRa(requestDTO.getMatriculaRa())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno com RA não encontrado."));

        // 2. Encontrar todas as matrículas (turmas) deste aluno
        List<Matricula> matriculasDoAluno = matriculaRepository.findByAlunoId(aluno.getId());
        if (matriculasDoAluno.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não está matriculado em nenhuma turma.");
        }

        // 3. Procurar por uma aula ABERTA em qualquer uma das turmas do aluno
        Aula aulaAberta = null;
        for (Matricula matricula : matriculasDoAluno) {
            Long turmaId = matricula.getTurma().getId();
            // O método findFirst... retorna a primeira aula aberta que encontrar para a turma
            Optional<Aula> aulaOpt = aulaRepository.findFirstByTurmaIdAndEstadoOrderByDataHoraAulaDesc(turmaId, "ABERTA");
            if (aulaOpt.isPresent()) {
                aulaAberta = aulaOpt.get();
                break; // Encontrou a aula, pode parar de procurar
            }
        }

        // 4. Se nenhuma aula aberta foi encontrada para nenhuma das turmas do aluno
        if (aulaAberta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma chamada aberta encontrada para as turmas deste aluno no momento.");
        }

        // 5. Verificar se o aluno já não fez check-in para esta aula
        final Long aulaAbertaId = aulaAberta.getId(); // 'final' para usar dentro do lambda
        presencaRepository.findByAlunoIdAndAulaId(aluno.getId(), aulaAbertaId).ifPresent(p -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Presença já registrada para esta aula.");
        });

        // 6. Tudo certo! Criar e salvar o registro de presença
        Presenca novaPresenca = Presenca.builder()
                .aluno(aluno)
                .aula(aulaAberta)
                .dataHoraCheckin(LocalDateTime.now())
                .build();

        Presenca presencaSalva = presencaRepository.save(novaPresenca);

        // 7. Retornar uma resposta de sucesso para o totem
        PresencaResponseDTO responseDTO = PresencaResponseDTO.builder()
                .id(presencaSalva.getId())
                .alunoId(presencaSalva.getAluno().getId())
                .nomeAluno(presencaSalva.getAluno().getNome())
                .aulaId(presencaSalva.getAula().getId())
                .dataHoraCheckin(presencaSalva.getDataHoraCheckin())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}