package br.com.projeto.model;

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

public class CadastroBiometriaApp extends JFrame {

    private final JTextField raField;
    private final JTextArea statusArea;
    private final JButton cadastrarButton;
    private final HttpClient httpClient;
    private final Gson gson;

    public CadastroBiometriaApp() {
        // --- Configuração da Janela ---
        setTitle("ÍCARO - Cadastro Biométrico");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela
        setLayout(new BorderLayout(10, 10));

        // --- Inicialização de componentes ---
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
        raField = new JTextField();
        statusArea = new JTextArea("Aguardando ação...");
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        cadastrarButton = new JButton("Iniciar Cadastro da Digital");

        // --- Painel Superior (para o RA) ---
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.add(new JLabel("RA do Aluno:"), BorderLayout.WEST);
        topPanel.add(raField, BorderLayout.CENTER);

        // --- Painel Central (para o status) ---
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Status"));

        // --- Painel Inferior (para o botão) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomPanel.add(cadastrarButton);

        // --- Adicionar painéis à janela principal ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Ação do Botão ---
        cadastrarButton.addActionListener(e -> cadastrarDigital());
    }

    private void cadastrarDigital() {
        String ra = raField.getText().trim();
        if (ra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o RA do aluno.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- SIMULAÇÃO DA CAPTURA BIOMÉTRICA ---
        statusArea.setText("Simulando captura da digital...\n");
        // 1. O SDK do leitor geraria o template aqui. Estamos simulando com um texto aleatório.
        String templateSimulado = UUID.randomUUID().toString();
        // 2. Templates biométricos são binários, então são geralmente enviados como Base64.
        String templateBase64 = Base64.getEncoder().encodeToString(templateSimulado.getBytes());
        statusArea.append("Template simulado gerado: " + templateBase64 + "\n");
        // --- FIM DA SIMULAÇÃO ---

        // Desabilita o botão para evitar cliques duplos
        cadastrarButton.setEnabled(false);
        statusArea.append("Enviando para o servidor...\n");

        // Executa a chamada de rede em uma thread separada para não travar a UI
        SwingUtilities.invokeLater(() -> {
            try {
                // Monta o corpo da requisição
                String jsonBody = gson.toJson(new BiometriaCadastroRequest(ra, templateBase64));

                // Cria a requisição HTTP para o nosso backend
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/biometria/cadastrar"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                // Envia a requisição e recebe a resposta
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Processa a resposta
                if (response.statusCode() == 201) { // 201 Created
                    statusArea.append("\nSUCESSO: Biometria cadastrada no servidor!");
                    JOptionPane.showMessageDialog(this, "Biometria cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    statusArea.append("\nERRO: " + response.statusCode() + " - " + response.body());
                    JOptionPane.showMessageDialog(this, "Erro do servidor: " + response.body(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException | InterruptedException ex) {
                statusArea.append("\nERRO DE CONEXÃO: Não foi possível conectar ao servidor.\n" + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Falha de conexão com a API.", "Erro de Rede", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Habilita o botão novamente, independentemente do resultado
                cadastrarButton.setEnabled(true);
            }
        });
    }

    // Classe auxiliar para montar o JSON da requisição
    private static class BiometriaCadastroRequest {
        final String matriculaRa;
        final String templateBiometrico;

        BiometriaCadastroRequest(String matriculaRa, String templateBiometrico) {
            this.matriculaRa = matriculaRa;
            this.templateBiometrico = templateBiometrico;
        }
    }

    public static void main(String[] args) {
        // Garante que a UI seja criada na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            new CadastroBiometriaApp().setVisible(true);
        });
    }
}