package semana18.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class VentanaConsulta extends JFrame {

 private static final String URL = "http://localhost:8080/socios";

    private final JButton btnConsultar;
    private final JLabel lblEstado;
    private final JTextArea areaDatos;

    public VentanaConsulta() {
        setTitle("Consulta al servidor");
        setSize(520, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        btnConsultar = new JButton("Consultar socios");
        lblEstado = new JLabel("Listo para consultar");
        areaDatos = new JTextArea();
        areaDatos.setEditable(false);
        areaDatos.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(btnConsultar);
        panelSuperior.add(lblEstado);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(areaDatos), BorderLayout.CENTER);

        btnConsultar.addActionListener(e -> consultar());
    }

    private void consultar() {
        btnConsultar.setEnabled(false);
        lblEstado.setText("Consultando...");
        areaDatos.setText("");

        SwingWorker<String, Void> trabajador = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                HttpClient cliente = HttpClient.newHttpClient();
                HttpRequest peticion = HttpRequest.newBuilder(URI.create(URL)).build();
                HttpResponse<String> respuesta = cliente.send(
                        peticion,
                        HttpResponse.BodyHandlers.ofString()
                );
                return respuesta.body();
            }

            @Override
            protected void done() {
                try {
                    String json = get();
                    areaDatos.setText(json.replace("}, {", "},\n{"));
                    lblEstado.setText("Datos recibidos");
                } catch (InterruptedException | ExecutionException ex) {
                    lblEstado.setText("Error");
                    JOptionPane.showMessageDialog(
                            VentanaConsulta.this,
                            "No se pudo conectar con el servidor",
                            "Error de conexion",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    btnConsultar.setEnabled(true);
                }
            }
        };

        trabajador.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaConsulta ventana = new VentanaConsulta();
            ventana.setVisible(true);
        });
    }
}