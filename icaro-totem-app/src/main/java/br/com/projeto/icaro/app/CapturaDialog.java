package br.com.projeto.icaro.app; // ✨ ALTERAÇÃO AQUI: Pacote correto

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.UUID;

// ✨ ALTERAÇÃO AQUI: A classe agora é um JDialog (um popup modal)
public class CapturaDialog extends JDialog {

    private final JLabel statusLabel;
    private final JLabel imageLabel;
    private final String raAluno;
    private String resultado; // Para guardar o resultado final (sucesso ou erro)

    public CapturaDialog(Frame owner, String raAluno) {
        super(owner, "Registrando Biometria", true); // 'true' o torna modal
        this.raAluno = raAluno;

        // --- Configuração do Diálogo ---
        setSize(350, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Impede o fechamento pelo "X"

        // --- Componentes Visuais ---
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Tenta carregar a imagem do classpath (da pasta src/main/resources)
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/fingerprint.png"));
            // Redimensiona a imagem para caber no label
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            imageLabel.setText("Imagem não encontrada");
        }

        statusLabel = new JLabel("Aguardando início...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Adiciona os componentes ao diálogo
        add(imageLabel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Inicia o processo de captura e comunicação com o backend.
     * @return A mensagem de sucesso ou erro do servidor.
     */
    public String iniciarCaptura() {
        // Usamos SwingWorker para rodar a tarefa demorada (rede) em segundo plano
        CadastroWorker worker = new CadastroWorker();
        worker.execute(); // Inicia o SwingWorker

        setVisible(true); // Mostra o diálogo e bloqueia a janela principal

        return resultado; // Retorna o resultado após o diálogo ser fechado
    }

    // SwingWorker é a forma correta no Swing de fazer tarefas de fundo sem travar a UI
    private class CadastroWorker extends SwingWorker<String, String> {

        @Override
        protected String doInBackground() throws Exception {
            // --- CÓDIGO QUE RODA EM SEGUNDO PLANO ---

            // 1. Simula a captura (um pequeno delay)
            publish("Posicione o dedo..."); // 'publish' envia atualizações para a UI
            Thread.sleep(1500); // Espera 1.5 segundos

            // 2. Simula a geração do template
            publish("Processando digital...");
            String templateSimulado = UUID.randomUUID().toString();
            String templateBase64 = Base64.getEncoder().encodeToString(templateSimulado.getBytes());
            Thread.sleep(1000); // Espera 1 segundo

            // 3. Envia para o servidor
            publish("Enviando para o servidor...");
            String jsonBody = new Gson().toJson(new BiometriaCadastroRequest(raAluno, templateBase64));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/biometria/cadastrar"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Retorna o resultado final
            if (response.statusCode() == 201) {
                return "SUCESSO: " + response.body();
            } else {
                return "ERRO: " + response.statusCode() + " - " + response.body();
            }
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            // --- CÓDIGO QUE ATUALIZA A UI EM TEMPO REAL ---
            // Pega a última mensagem enviada pelo 'publish'
            String latestStatus = chunks.get(chunks.size() - 1);
            statusLabel.setText(latestStatus);
        }

        @Override
        protected void done() {
            // --- CÓDIGO QUE EXECUTA QUANDO TUDO TERMINA ---
            try {
                resultado = get(); // Pega o resultado do 'doInBackground'
                if (resultado.startsWith("SUCESSO")) {
                    statusLabel.setForeground(new Color(0, 128, 0)); // Verde
                    statusLabel.setText("Biometria Registrada!");
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Falha no Cadastro!");
                }
                // Espera 2 segundos antes de fechar para o usuário ver a mensagem final
                Timer timer = new Timer(2000, e -> dispose()); // 'dispose' fecha o diálogo
                timer.setRepeats(false);
                timer.start();
            } catch (Exception e) {
                resultado = "ERRO FATAL: " + e.getMessage();
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Erro de comunicação!");
                // Fecha após 2 segundos mesmo em caso de erro
                Timer timer = new Timer(2000, evt -> dispose());
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    // Classe auxiliar interna para o corpo da requisição JSON
    private static class BiometriaCadastroRequest {
        final String matriculaRa;
        final String templateBiometrico;

        BiometriaCadastroRequest(String matriculaRa, String templateBiometrico) {
            this.matriculaRa = matriculaRa;
            this.templateBiometrico = templateBiometrico;
        }
    }
}