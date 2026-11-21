package semana18.ejercicios;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class VentanaConsulta2 extends JFrame {

    private JButton btnConsultar;
    private JLabel lblEstado;
    private JTextArea areaDatos;

    private JProgressBar barra = new JProgressBar();

    private final String URL = "http://localhost:8080/socios";

    public VentanaConsulta2() {

        setTitle("Consulta de datos");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        btnConsultar = new JButton("Consultar");
        lblEstado = new JLabel("Listo");
        areaDatos = new JTextArea();

        JPanel panelSuperior = new JPanel(new FlowLayout());

        panelSuperior.add(btnConsultar);
        panelSuperior.add(lblEstado);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(areaDatos), BorderLayout.CENTER);

        barra.setIndeterminate(false);
        barra.setVisible(false);

        add(barra, BorderLayout.SOUTH);

        btnConsultar.addActionListener(e -> consultar());

        setVisible(true);
    }

    private void consultar() {

        btnConsultar.setEnabled(false);
        lblEstado.setText("Consultando...");
        areaDatos.setText("");

        SwingWorker<String, Void> trabajador = new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {

                HttpClient cliente = HttpClient.newHttpClient();

                HttpRequest peticion =
                        HttpRequest.newBuilder(URI.create(URL)).build();

                HttpResponse<String> respuesta =
                        cliente.send(peticion,
                                HttpResponse.BodyHandlers.ofString());

                return respuesta.body();
            }

            @Override
            protected void done() {

                try {

                    String json = get();

                    areaDatos.setText(
                            json.replace("}, {", "},\n{")
                    );

                    lblEstado.setText("Datos recibidos");

                } catch (InterruptedException | ExecutionException ex) {

                    lblEstado.setText("Error");

                    JOptionPane.showMessageDialog(
                            VentanaConsulta2.this,
                            "No se pudo conectar con el servidor",
                            "Error de conexión",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {

                    btnConsultar.setEnabled(true);

                    barra.setIndeterminate(false);
                    barra.setVisible(false);
                }
            }
        };

        barra.setVisible(true);
        barra.setIndeterminate(true);

        trabajador.execute();
    }

    public static void main(String[] args) {

        new VentanaConsulta2();
    }
}