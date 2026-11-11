package com.canvia.ats_gestion_talento.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.InputStream;

@Slf4j
@Service
public class PdfService {

    /**
     * Extrae el texto de un archivo PDF usando su ruta física.
     */
    public String extraerTextoDePdf(String rutaArchivo) {
        try (PDDocument document = PDDocument.load(new File(rutaArchivo))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).toLowerCase(); // Convertimos a minúsculas para facilitar la búsqueda
        } catch (Exception e) {
            log.error("Error leyendo el PDF en la ruta {}: {}", rutaArchivo, e.getMessage());
            return "";
        }
    }

    /**
     * Extrae el texto de un PDF directamente desde un InputStream (útil cuando se sube el archivo desde la web).
     */
    public String extraerTextoDePdf(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).toLowerCase();
        } catch (Exception e) {
            log.error("Error procesando el flujo del PDF: {}", e.getMessage());
            return "";
        }
    }
}