package br.com.projeto.icaro.app; // ✨ ALTERAÇÃO AQUI: Pacote correto

import javax.swing.*;
import java.awt.*;

public class CadastroBiometriaApp extends JFrame {

    private final JTextField raField;
    private final JTextArea statusArea;
    private final JButton cadastrarButton;

    public CadastroBiometriaApp() {
        // --- Configuração da Janela (praticamente inalterado) ---
        setTitle("ÍCARO - Cadastro Biométrico");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Inicialização de componentes ---
        raField = new JTextField();
        statusArea = new JTextArea("Aguardando ação...");
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        cadastrarButton = new JButton("Iniciar Cadastro da Digital");

        // --- Montagem da UI (praticamente inalterado) ---
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.add(new JLabel("RA do Aluno:"), BorderLayout.WEST);
        topPanel.add(raField, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Status"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomPanel.add(cadastrarButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- ✨ ALTERAÇÃO AQUI: Ação do Botão foi simplificada ---
        cadastrarButton.addActionListener(e -> iniciarProcessoDeCadastro());
    }

    private void iniciarProcessoDeCadastro() {
        String ra = raField.getText().trim();
        if (ra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o RA do aluno.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Limpa a área de status
        statusArea.setText("Iniciando processo para o RA: " + ra + "\n");
        cadastrarButton.setEnabled(false);

        // 2. Cria e exibe o diálogo de captura
        CapturaDialog capturaDialog = new CapturaDialog(this, ra);
        String resultado = capturaDialog.iniciarCaptura(); // O código vai pausar aqui até o diálogo fechar

        // 3. Exibe o resultado final na janela principal
        statusArea.append("\nProcesso finalizado.\n");
        statusArea.append("Resultado: " + resultado + "\n");
        cadastrarButton.setEnabled(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CadastroBiometriaApp().setVisible(true);
        });
    }
}