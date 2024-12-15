// Nome: Matheus Angelo de Souza Santos - CB3025489

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static int currentIndex = 0;
    private static List<String[]> results = new ArrayList<>();
    private static int currentCodFunc;

    public static void main(String[] args) {
        // Criação do frame principal
        JFrame frame = new JFrame("Gestão de Funcionários");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Painel principal com layout vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Painel superior para Nome e Pesquisar
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblNome = new JLabel("Nome:");
        JTextField txtNome = new JTextField(15);
        JButton btnPesquisar = new JButton("Pesquisar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(lblNome, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(txtNome, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(btnPesquisar, gbc);

        // Painel de entrada de dados
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel lblSalario = new JLabel("Salário:");
        JTextField txtSalario = new JTextField(10);

        JLabel lblCargo = new JLabel("Cargo:");
        JTextField txtCargo = new JTextField(10);

        // Adicionando componentes ao painel de entrada
        inputPanel.add(lblSalario);
        inputPanel.add(txtSalario);

        inputPanel.add(lblCargo);
        inputPanel.add(txtCargo);

        // Painel para os botões de navegação
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAnterior = new JButton("Anterior");
        JButton btnProximo = new JButton("Próximo");

        // Adicionando botões ao painel
        buttonPanel.add(btnAnterior);
        buttonPanel.add(btnProximo);

        // Adicionando painéis ao painel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adicionando o painel principal ao frame
        frame.add(mainPanel);

        // Função para exibir registro atual
        ActionListener updateFields = e -> {
            if (!results.isEmpty()) {
                String[] currentRecord = results.get(currentIndex);
                txtSalario.setText(currentRecord[1]);
                txtCargo.setText(currentRecord[2]);
            }
        };

        // Configurando ação para o botão "Pesquisar"
        btnPesquisar.addActionListener(e -> {
            String nome = txtNome.getText();
            results.clear();
            currentIndex = 0;
            
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://aulajava.mssql.somee.com;databaseName=aulajava;encrypt=true;trustServerCertificate=true", 
                    "matheus007_SQLLogin_1", 
                    "sbqfmszryw")) {
                        String sql = "SELECT f.nome_func, f.sal_func, c.ds_cargo " +
                        "FROM dbo.tbfuncs f " +
                        "INNER JOIN dbo.tbcargos c ON f.cod_cargo = c.cd_cargo " +
                        "WHERE f.nome_func LIKE ?";
                      
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, "%" + nome + "%");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    results.add(new String[]{
                        rs.getString("nome_func"), 
                        rs.getString("sal_func"),  
                        rs.getString("ds_cargo")
                    });
                }
                                

                if (!results.isEmpty()) {
                    updateFields.actionPerformed(null);
                    JOptionPane.showMessageDialog(frame, "Resultados encontrados: " + results.size());
                } else {
                    JOptionPane.showMessageDialog(frame, "Nenhum resultado encontrado.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao realizar a pesquisa.");
            }
        });

        btnAnterior.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://aulajava.mssql.somee.com;databaseName=aulajava;encrypt=true;trustServerCertificate=true", 
                    "matheus007_SQLLogin_1", 
                    "sbqfmszryw")) {
                
                // Busca o registro anterior com base no cod_func atual
                String sql = "SELECT TOP 1 f.cod_func, f.nome_func, f.sal_func, c.ds_cargo " +
                             "FROM dbo.tbfuncs f " +
                             "INNER JOIN dbo.tbcargos c ON f.cod_cargo = c.cd_cargo " +
                             "WHERE f.cod_func < ? " +
                             "ORDER BY f.cod_func DESC";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, currentCodFunc);
                ResultSet rs = stmt.executeQuery();
        
                if (rs.next()) {
                    // Atualiza o registro atual
                    currentCodFunc = rs.getInt("cod_func");
                    txtSalario.setText(rs.getString("sal_func"));
                    txtCargo.setText(rs.getString("ds_cargo"));
                    txtNome.setText(rs.getString("nome_func"));
                } else {
                    JOptionPane.showMessageDialog(frame, "Você já está no primeiro registro.");
                }
        
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao carregar registro anterior.");
            }
        });        
        

        btnProximo.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://aulajava.mssql.somee.com;databaseName=aulajava;encrypt=true;trustServerCertificate=true", 
                    "matheus007_SQLLogin_1", 
                    "sbqfmszryw")) {
                
                // Busca o próximo registro com base no cod_func atual
                String sql = "SELECT TOP 1 f.cod_func, f.nome_func, f.sal_func, c.ds_cargo " +
                             "FROM dbo.tbfuncs f " +
                             "INNER JOIN dbo.tbcargos c ON f.cod_cargo = c.cd_cargo " +
                             "WHERE f.cod_func > ? " +
                             "ORDER BY f.cod_func ASC";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, currentCodFunc);
                ResultSet rs = stmt.executeQuery();
        
                if (rs.next()) {
                    // Atualiza o registro atual
                    currentCodFunc = rs.getInt("cod_func");
                    txtSalario.setText(rs.getString("sal_func"));
                    txtCargo.setText(rs.getString("ds_cargo"));
                    txtNome.setText(rs.getString("nome_func"));
                } else {
                    JOptionPane.showMessageDialog(frame, "Você já está no último registro.");
                }
        
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao carregar próximo registro.");
            }
        });
        
        
        frame.setVisible(true);
    }
}
