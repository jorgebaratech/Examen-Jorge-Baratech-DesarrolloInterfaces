package com.example.examenjorge;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class HelloController {

    @FXML
    private ChoiceBox<String> tipotalla;
    @FXML
    private TextField textopeso;
    @FXML
    private Button botoncalcular;
    @FXML
    private TextField textoedad;
    @FXML
    private RadioButton botonhombre;
    @FXML
    private RadioButton botonmujer;
    @FXML
    private TextArea observacionestexto;
    @FXML
    private ChoiceBox<String> tipoactividad;
    @FXML
    private Button botondescargar;
    @FXML
    private Label Pesoresultado;

    @FXML
    public void initialize() {
        tipotalla.getItems().addAll("Talla l", "Talla xl", "Talla xxl");
        tipotalla.setValue("Talla 1");

        tipoactividad.getItems().addAll("Actividad baja", "Actividad media", "Actividad alta");
        tipoactividad.setValue("Actividad baja");

        ToggleGroup toggleGroup = new ToggleGroup();
        botonhombre.setToggleGroup(toggleGroup);
        botonmujer.setToggleGroup(toggleGroup);

        botoncalcular.setOnAction(event -> calcularMetabolismoBasal());

        botondescargar.setOnAction(event -> descargarInformacion());
    }

    private void calcularMetabolismoBasal() {
        try {
            double peso = Double.parseDouble(textopeso.getText());
            int edad = Integer.parseInt(textoedad.getText());

            double factorActividad = obtenerFactorActividad();

            double metabolismoBasal;
            if (botonhombre.isSelected()) {
                metabolismoBasal = 10 * peso + 6.25 * edad - 5 * edad + 5;
            } else if (botonmujer.isSelected()) {
                metabolismoBasal = 10 * peso + 6.25 * edad - 5 * edad - 161;
            } else {
                throw new IllegalStateException("Debe seleccionar un sexo.");
            }

            metabolismoBasal *= factorActividad;

            Pesoresultado.setText("Tu metabolismo basal es: " + metabolismoBasal);
        } catch (NumberFormatException e) {
            Pesoresultado.setText("Por favor ingresa valores válidos en los campos de peso y edad.");
        } catch (IllegalStateException e) {
            Pesoresultado.setText(e.getMessage());
        }
    }


    private double obtenerFactorActividad() {
        String actividadSeleccionada = tipoactividad.getValue();
        double factorActividad;
        switch (actividadSeleccionada) {
            case "Actividad baja":
                factorActividad = 1.2;
                break;
            case "Actividad media":
                factorActividad = 1.375;
                break;
            case "Actividad alta":
                factorActividad = 1.55;
                break;
            default:
                factorActividad = 1.2;
        }
        return factorActividad;
    }

    private void descargarInformacion() {
        String resourcePath = getClass().getClassLoader().getResource("reporte1.jasper").getFile();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("reporte1.jasper");
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            try {
                Files.copy(new File(resourcePath).toPath(), selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                mostrarAlerta("Descarga exitosa", "El archivo se ha descargado correctamente.");
            } catch (IOException e) {
                mostrarAlerta("Error al descargar", "No se pudo descargar el archivo: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
