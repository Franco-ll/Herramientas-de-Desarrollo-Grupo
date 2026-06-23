package com.scholarstay.app.service;

import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.repository.ReservaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final ReservaRepository reservaRepository;

    public ReporteService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public byte[] generarExcel(int mes, int anio) throws Exception {
        List<Reserva> reservas = reservaRepository.findAll().stream()
                .filter(r -> r.getFechaInicio().getMonthValue() == mes
                        && r.getFechaInicio().getYear() == anio)
                .sorted(Comparator.comparing(Reserva::getFechaInicio))
                .collect(Collectors.toList());

        XSSFWorkbook workbook = new XSSFWorkbook();

        // ===== ESTILOS =====
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)61, (byte)99, (byte)126}, null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        XSSFCellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);

        XSSFCellStyle subtitleStyle = workbook.createCellStyle();
        XSSFFont subtitleFont = workbook.createFont();
        subtitleFont.setFontHeightInPoints((short) 11);
        subtitleFont.setColor(new XSSFColor(new byte[]{(byte)89, (byte)96, (byte)97}, null));
        subtitleStyle.setFont(subtitleFont);

        XSSFCellStyle montoStyle = workbook.createCellStyle();
        montoStyle.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat format = workbook.createDataFormat();
        montoStyle.setDataFormat(format.getFormat("#,##0.00"));

        XSSFCellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)199, (byte)236, (byte)201}, null));
        totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setDataFormat(format.getFormat("#,##0.00"));
        XSSFFont totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);

        XSSFCellStyle totalLabelStyle = workbook.createCellStyle();
        totalLabelStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)199, (byte)236, (byte)201}, null));
        totalLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont totalLabelFont = workbook.createFont();
        totalLabelFont.setBold(true);
        totalLabelStyle.setFont(totalLabelFont);

        XSSFCellStyle estadoConfStyle = workbook.createCellStyle();
        estadoConfStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)199, (byte)236, (byte)201}, null));
        estadoConfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estadoConfStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle estadoPendStyle = workbook.createCellStyle();
        estadoPendStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)254, (byte)236, (byte)206}, null));
        estadoPendStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estadoPendStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle estadoCancStyle = workbook.createCellStyle();
        estadoCancStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)250, (byte)219, (byte)216}, null));
        estadoCancStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estadoCancStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String nombreMes = LocalDate.of(anio, mes, 1)
                .format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "PE")));
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);

        // ===== HOJA 1: TRANSACCIONES =====
        XSSFSheet sheet1 = workbook.createSheet("Transacciones");
        sheet1.setColumnWidth(0, 1200);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 6000);
        sheet1.setColumnWidth(3, 3500);
        sheet1.setColumnWidth(4, 3500);
        sheet1.setColumnWidth(5, 3500);
        sheet1.setColumnWidth(6, 4000);

        // Título
        Row rowTitulo = sheet1.createRow(0);
        rowTitulo.setHeight((short) 700);
        Cell cellTitulo = rowTitulo.createCell(0);
        cellTitulo.setCellValue("Scholar Stay — Reporte de Ingresos");
        cellTitulo.setCellStyle(titleStyle);
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        Row rowSubtitulo = sheet1.createRow(1);
        Cell cellSub = rowSubtitulo.createCell(0);
        cellSub.setCellValue("Período: " + nombreMes + "   |   Generado: " + LocalDate.now().format(fmt));
        cellSub.setCellStyle(subtitleStyle);
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

        sheet1.createRow(2); // espacio

        // Headers
        Row rowHeader = sheet1.createRow(3);
        rowHeader.setHeight((short) 500);
        String[] headers = {"#", "Residente", "Propiedad", "Fecha Inicio", "Fecha Fin", "Duración", "Monto (S/)", "Estado"};
        sheet1.setColumnWidth(7, 3500);
        for (int i = 0; i < headers.length; i++) {
            Cell c = rowHeader.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 4;
        double totalConfirmado = 0;
        double totalPendiente = 0;
        double totalCancelado = 0;

        for (int i = 0; i < reservas.size(); i++) {
            Reserva r = reservas.get(i);
            Row row = sheet1.createRow(rowNum++);
            row.setHeight((short) 400);

            row.createCell(0).setCellValue(i + 1);
            row.getCell(0).getCellStyle();
            row.createCell(1).setCellValue(r.getUsuario().getNombre());
            row.createCell(2).setCellValue(r.getAlojamiento().getTitulo());

            Cell cFI = row.createCell(3);
            cFI.setCellValue(r.getFechaInicio().format(fmt));
            cFI.setCellStyle(centerStyle);

            Cell cFF = row.createCell(4);
            cFF.setCellValue(r.getFechaFin().format(fmt));
            cFF.setCellStyle(centerStyle);

            long dias = java.time.temporal.ChronoUnit.DAYS.between(r.getFechaInicio(), r.getFechaFin());
            long meses = java.time.temporal.ChronoUnit.MONTHS.between(r.getFechaInicio(), r.getFechaFin());
            String duracion = meses >= 1 ? dias + " días (" + meses + (meses == 1 ? " mes)" : " meses)") : dias + " días";
            Cell cDur = row.createCell(5);
            cDur.setCellValue(duracion);
            cDur.setCellStyle(centerStyle);

            Cell cMonto = row.createCell(6);
            cMonto.setCellValue(r.getPrecioTotal());
            cMonto.setCellStyle(montoStyle);

            Cell cEstado = row.createCell(7);
            if ("CONFIRMADA".equals(r.getEstado())) {
                cEstado.setCellValue("Pagado");
                cEstado.setCellStyle(estadoConfStyle);
                totalConfirmado += r.getPrecioTotal();
            } else if ("PENDIENTE".equals(r.getEstado())) {
                cEstado.setCellValue("Pendiente");
                cEstado.setCellStyle(estadoPendStyle);
                totalPendiente += r.getPrecioTotal();
            } else {
                cEstado.setCellValue("Cancelado");
                cEstado.setCellStyle(estadoCancStyle);
                totalCancelado += r.getPrecioTotal();
            }
        }

        // Fila vacía
        sheet1.createRow(rowNum++);

        // Totales
        String[][] totales = {
            {"Total confirmado:", String.format("%.2f", totalConfirmado)},
            {"Total pendiente:", String.format("%.2f", totalPendiente)},
            {"Total cancelado:", String.format("%.2f", totalCancelado)},
            {"TOTAL GENERAL:", String.format("%.2f", totalConfirmado + totalPendiente + totalCancelado)},
        };

        for (String[] t : totales) {
            Row row = sheet1.createRow(rowNum++);
            Cell label = row.createCell(5);
            label.setCellValue(t[0]);
            label.setCellStyle(totalLabelStyle);
            Cell valor = row.createCell(6);
            valor.setCellValue(Double.parseDouble(t[1]));
            valor.setCellStyle(totalStyle);
        }

        // ===== HOJA 2: DESGLOSE POR PROPIEDAD =====
        XSSFSheet sheet2 = workbook.createSheet("Desglose por Propiedad");
        sheet2.setColumnWidth(0, 8000);
        sheet2.setColumnWidth(1, 3000);
        sheet2.setColumnWidth(2, 4000);

        Row rTit2 = sheet2.createRow(0);
        rTit2.setHeight((short) 700);
        Cell cTit2 = rTit2.createCell(0);
        cTit2.setCellValue("Desglose por Propiedad — " + nombreMes);
        cTit2.setCellStyle(titleStyle);
        sheet2.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        sheet2.createRow(1);

        Row rHead2 = sheet2.createRow(2);
        rHead2.setHeight((short) 500);
        String[] h2 = {"Propiedad", "Reservas", "Ingresos (S/)"};
        for (int i = 0; i < h2.length; i++) {
            Cell c = rHead2.createCell(i);
            c.setCellValue(h2[i]);
            c.setCellStyle(headerStyle);
        }

        Map<String, List<Reserva>> porPropiedad = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getAlojamiento().getTitulo()));

        int r2 = 3;
        double totalGeneral2 = 0;
        for (Map.Entry<String, List<Reserva>> entry : porPropiedad.entrySet().stream()
                .sorted((a, b) -> Double.compare(
                        b.getValue().stream().mapToDouble(Reserva::getPrecioTotal).sum(),
                        a.getValue().stream().mapToDouble(Reserva::getPrecioTotal).sum()))
                .collect(Collectors.toList())) {

            double total = entry.getValue().stream().mapToDouble(Reserva::getPrecioTotal).sum();
            totalGeneral2 += total;
            Row row = sheet2.createRow(r2++);
            row.createCell(0).setCellValue(entry.getKey());
            Cell cCant = row.createCell(1);
            cCant.setCellValue(entry.getValue().size());
            cCant.setCellStyle(centerStyle);
            Cell cTot = row.createCell(2);
            cTot.setCellValue(total);
            cTot.setCellStyle(montoStyle);
        }

        sheet2.createRow(r2++);
        Row rTotal2 = sheet2.createRow(r2);
        Cell lTot2 = rTotal2.createCell(0);
        lTot2.setCellValue("TOTAL");
        lTot2.setCellStyle(totalLabelStyle);
        Cell vTot2 = rTotal2.createCell(2);
        vTot2.setCellValue(totalGeneral2);
        vTot2.setCellStyle(totalStyle);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}