package com.gestionplanillas.application.views.boleta;

import com.gestionplanillas.application.data.Empleado;
import com.gestionplanillas.application.data.RegistroPlanilla;
import com.gestionplanillas.application.services.PlanillaService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/boleta-pdf")
public class BoletaPDFView {

    @Autowired
    private PlanillaService planillaService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void generarBoletaPdf(@PathVariable Long id, HttpServletResponse response) {
        Optional<RegistroPlanilla> registroOpt = planillaService.findRegistroById(id);
        if (registroOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        RegistroPlanilla registro = registroOpt.get();
        Empleado empleado = registro.getContratoEmpleado().getEmpleado();

        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=boleta_" + id + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font netoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

            // Título centrado
            Paragraph title1 = new Paragraph("Technova Solutions S.A. de C.V.", titleFont);
            title1.setAlignment(Element.ALIGN_CENTER);
            document.add(title1);
            Paragraph title = new Paragraph("Boleta de Pago", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Tabla de datos personales y salario
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(10);

            infoTable.addCell(new Phrase("Nombre:", labelFont));
            infoTable.addCell(new Phrase(empleado.getNombres() + " " + empleado.getApellidos(), valueFont));

            infoTable.addCell(new Phrase("Salario Mensual:", labelFont));
            infoTable.addCell(new Phrase("$" + registro.getContratoEmpleado().getSalarioBaseMensual(), valueFont));

            infoTable.addCell(new Phrase("Salario Base Periodo:", labelFont));
            infoTable.addCell(new Phrase("$" + registro.getSalarioBasePeriodo(), valueFont));

            infoTable.addCell(new Phrase("Fecha de Pago:", labelFont));
            infoTable.addCell(new Phrase(String.valueOf(registro.getPlanilla().getFecha_fin_corte()), valueFont));

            infoTable.addCell(new Phrase("Fecha de Impresión:", labelFont));
            infoTable.addCell(new Phrase(String.valueOf(LocalDate.now()), valueFont));

            document.add(infoTable);

            // Tabla de ingresos y deducciones lado a lado
            PdfPTable dobleTabla = new PdfPTable(2);
            dobleTabla.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            dobleTabla.setWidthPercentage(100);
            dobleTabla.setSpacingBefore(10);
            dobleTabla.setSpacingAfter(10);
            dobleTabla.setWidths(new int[]{1, 1});

            // Columna ingresos
            PdfPTable ingresos = new PdfPTable(1);
            ingresos.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            ingresos.addCell(new Phrase("--- INGRESOS ---", labelFont));
            ingresos.addCell(new Phrase("Monto H.E. Diurnas: $" + registro.getMontoHoraExtrasDiurnas(), valueFont));
            ingresos.addCell(new Phrase("Monto H.E. Nocturnas: $" + registro.getMontoHorasExtrasNocturnas(), valueFont));
            ingresos.addCell(new Phrase("Total Devengado: $" + registro.getTotalDevengado(), valueFont));

            // Columna deducciones
            PdfPTable deducciones = new PdfPTable(1);
            deducciones.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            deducciones.addCell(new Phrase("--- DEDUCCIONES ---", labelFont));
            deducciones.addCell(new Phrase("ISSS Empleado: $" + registro.getIsssEmpleado(), valueFont));
            deducciones.addCell(new Phrase("AFP Empleado: $" + registro.getAfpEmpleado(), valueFont));
            deducciones.addCell(new Phrase("Renta: $" + registro.getRenta(), valueFont));
            deducciones.addCell(new Phrase("Total Deducciones: $" + registro.getTotalDeducciones(), valueFont));

            PdfPCell ingresosCell = new PdfPCell(ingresos);
            ingresosCell.setBorder(Rectangle.NO_BORDER);
            PdfPCell deduccionesCell = new PdfPCell(deducciones);
            deduccionesCell.setBorder(Rectangle.NO_BORDER);

            dobleTabla.addCell(ingresosCell);
            dobleTabla.addCell(deduccionesCell);
            document.add(dobleTabla);

            // Neto + firma (una misma fila)
            document.add(new Paragraph("\n\n"));
            PdfPTable netoYfirma = new PdfPTable(2);
            netoYfirma.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            netoYfirma.setWidthPercentage(100);
            netoYfirma.setSpacingBefore(10);
            netoYfirma.setWidths(new int[]{1, 1});

            netoYfirma.addCell(new Phrase("NETO A PAGAR: $" + registro.getSalarioNeto(), netoFont));
            PdfPCell firma = new PdfPCell(new Phrase("Recibí conforme: __________________", valueFont));
            firma.setBorder(Rectangle.NO_BORDER);
            firma.setHorizontalAlignment(Element.ALIGN_LEFT);
            netoYfirma.addCell(firma);

            document.add(netoYfirma);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
