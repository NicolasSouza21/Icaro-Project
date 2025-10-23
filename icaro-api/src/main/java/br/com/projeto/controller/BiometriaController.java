package br.com.projeto.controller;

import br.com.projeto.dto.BiometriaCadastroRequestDTO;
import br.com.projeto.model.Usuario;
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

@RestController
@RequestMapping("/api/v1/biometria")
@RequiredArgsConstructor
public class BiometriaController {

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/cadastrar")
    @Transactional
    // Este endpoint é público (permitAll no SecurityConfig), pois será acessado
    // pelo software do totem/secretaria, que não tem login.
    // A segurança pode ser feita futuramente com uma API Key.
    public ResponseEntity<String> cadastrarBiometria(@RequestBody BiometriaCadastroRequestDTO requestDTO) {

        // 1. Encontrar o aluno pelo RA fornecido
        Usuario aluno = usuarioRepository.findByMatriculaRa(requestDTO.getMatriculaRa())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno com o RA informado não foi encontrado."));

        // 2. Verificar se o aluno já não possui uma digital cadastrada
        if (aluno.getDigitalTemplate() != null && !aluno.getDigitalTemplate().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este aluno já possui uma biometria cadastrada.");
        }

        // 3. Salvar o template biométrico no campo do usuário
        aluno.setDigitalTemplate(requestDTO.getTemplateBiometrico());
        usuarioRepository.save(aluno);

        // 4. Retornar uma resposta de sucesso
        return ResponseEntity.status(HttpStatus.CREATED).body("Biometria cadastrada com sucesso para o aluno: " + aluno.getNome());
    }
}