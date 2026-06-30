package com.scholarstay.app.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.scholarstay.app.model.Reserva;
import com.scholarstay.app.repository.ReservaRepository;

@Service
public class ReporteService {

    private final ReservaRepository reservaRepository;

    public ReporteService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public byte[] generarExcel(int mes, int anio, String nombreAdmin) throws Exception {
        List<Reserva> reservas = reservaRepository.findAll().stream()
                .filter(r -> r.getFechaInicio().getMonthValue() == mes
                        && r.getFechaInicio().getYear() == anio)
                .sorted(Comparator.comparing(Reserva::getFechaInicio))
                .collect(Collectors.toList());

        XSSFWorkbook workbook = new XSSFWorkbook();

        // ===== PALETA =====
        byte[] azulPrimario = {(byte) 61, (byte) 99, (byte) 126};
        byte[] azulOscuro = {(byte) 41, (byte) 80, (byte) 106};
        byte[] blanco = {(byte) 255, (byte) 255, (byte) 255};
        byte[] grisTexto = {(byte) 89, (byte) 96, (byte) 97};
        byte[] verdeClaro = {(byte) 199, (byte) 236, (byte) 201};
        byte[] verdeOscuro = {(byte) 57, (byte) 89, (byte) 63};
        byte[] bandaClara = {(byte) 245, (byte) 248, (byte) 248};

        // ===== ESTILOS =====
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(azulPrimario, null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(new XSSFColor(blanco, null));
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        XSSFCellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setColor(new XSSFColor(azulOscuro, null));
        titleStyle.setFont(titleFont);

        XSSFCellStyle subtitleStyle = workbook.createCellStyle();
        XSSFFont subtitleFont = workbook.createFont();
        subtitleFont.setFontHeightInPoints((short) 11);
        subtitleFont.setColor(new XSSFColor(grisTexto, null));
        subtitleStyle.setFont(subtitleFont);

        XSSFCellStyle metaStyle = workbook.createCellStyle();
        XSSFFont metaFont = workbook.createFont();
        metaFont.setFontHeightInPoints((short) 9);
        metaFont.setItalic(true);
        metaFont.setColor(new XSSFColor(grisTexto, null));
        metaStyle.setFont(metaFont);

        XSSFCellStyle montoStyle = workbook.createCellStyle();
        montoStyle.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat format = workbook.createDataFormat();
        montoStyle.setDataFormat(format.getFormat("#,##0.00"));

        XSSFCellStyle montoBandaStyle = workbook.createCellStyle();
        montoBandaStyle.setAlignment(HorizontalAlignment.RIGHT);
        montoBandaStyle.setDataFormat(format.getFormat("#,##0.00"));
        montoBandaStyle.setFillForegroundColor(new XSSFColor(bandaClara, null));
        montoBandaStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setFillForegroundColor(new XSSFColor(verdeClaro, null));
        totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setDataFormat(format.getFormat("#,##0.00"));
        XSSFFont totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalFont.setFontHeightInPoints((short) 12);
        totalFont.setColor(new XSSFColor(verdeOscuro, null));
        totalStyle.setFont(totalFont);

        XSSFCellStyle totalLabelStyle = workbook.createCellStyle();
        totalLabelStyle.setFillForegroundColor(new XSSFColor(verdeClaro, null));
        totalLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalLabelStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont totalLabelFont = workbook.createFont();
        totalLabelFont.setBold(true);
        totalLabelFont.setFontHeightInPoints((short) 12);
        totalLabelFont.setColor(new XSSFColor(verdeOscuro, null));
        totalLabelStyle.setFont(totalLabelFont);

        XSSFCellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle centerBandaStyle = workbook.createCellStyle();
        centerBandaStyle.setAlignment(HorizontalAlignment.CENTER);
        centerBandaStyle.setFillForegroundColor(new XSSFColor(bandaClara, null));
        centerBandaStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle textoStyle = workbook.createCellStyle();
        textoStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCellStyle textoBandaStyle = workbook.createCellStyle();
        textoBandaStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textoBandaStyle.setFillForegroundColor(new XSSFColor(bandaClara, null));
        textoBandaStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtGenerado = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");
        String nombreMes = LocalDate.of(anio, mes, 1)
                .format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "PE")));
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);

        // ===== HOJA 1: TRANSACCIONES =====
        XSSFSheet sheet1 = workbook.createSheet("Transacciones");
        sheet1.setColumnWidth(0, 1200);
        sheet1.setColumnWidth(1, 5500);
        sheet1.setColumnWidth(2, 6500);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 3500);
        sheet1.setColumnWidth(5, 3500);
        sheet1.setColumnWidth(6, 3500);
        sheet1.setColumnWidth(7, 3200);
        sheet1.setColumnWidth(8, 4200);

        int totalColumnas = 9;

        // Título
        Row rowTitulo = sheet1.createRow(0);
        rowTitulo.setHeight((short) 750);
        Cell cellTitulo = rowTitulo.createCell(0);
        cellTitulo.setCellValue("Scholar Stay — Reporte de Ingresos");
        cellTitulo.setCellStyle(titleStyle);
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumnas - 1));

        Row rowSubtitulo = sheet1.createRow(1);
        rowSubtitulo.setHeight((short) 350);
        Cell cellSub = rowSubtitulo.createCell(0);
        cellSub.setCellValue("Período: " + nombreMes + "   |   Total de transacciones: " + reservas.size());
        cellSub.setCellStyle(subtitleStyle);
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 0, totalColumnas - 1));

        Row rowMeta = sheet1.createRow(2);
        Cell cellMeta = rowMeta.createCell(0);
        String generadoPor = (nombreAdmin != null && !nombreAdmin.isBlank()) ? nombreAdmin : "Administrador";
        cellMeta.setCellValue("Generado por " + generadoPor + " el " + LocalDate.now().format(fmt));
        cellMeta.setCellStyle(metaStyle);
        sheet1.addMergedRegion(new CellRangeAddress(2, 2, 0, totalColumnas - 1));

        sheet1.createRow(3); // espacio

        // Headers
        Row rowHeader = sheet1.createRow(4);
        rowHeader.setHeight((short) 550);
        String[] headers = {"#", "Residente", "Email", "Propiedad", "Fecha Inicio", "Fecha Fin", "Duración", "Estudiantes", "Monto (S/)"};
        for (int i = 0; i < headers.length; i++) {
            Cell c = rowHeader.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }
        sheet1.createFreezePane(0, 5);

        // Datos
        int rowNum = 5;
        double totalGeneral = 0;

        for (int i = 0; i < reservas.size(); i++) {
            Reserva r = reservas.get(i);
            boolean banda = i % 2 == 1;
            Row row = sheet1.createRow(rowNum++);
            row.setHeight((short) 420);

            Cell cNum = row.createCell(0);
            cNum.setCellValue(i + 1);
            cNum.setCellStyle(banda ? centerBandaStyle : centerStyle);

            Cell cNombre = row.createCell(1);
            cNombre.setCellValue(r.getUsuario().getNombre());
            cNombre.setCellStyle(banda ? textoBandaStyle : textoStyle);

            Cell cEmail = row.createCell(2);
            cEmail.setCellValue(r.getUsuario().getEmail() != null ? r.getUsuario().getEmail() : "—");
            cEmail.setCellStyle(banda ? textoBandaStyle : textoStyle);

            Cell cProp = row.createCell(3);
            cProp.setCellValue(r.getAlojamiento().getTitulo());
            cProp.setCellStyle(banda ? textoBandaStyle : textoStyle);

            Cell cFI = row.createCell(4);
            cFI.setCellValue(r.getFechaInicio().format(fmt));
            cFI.setCellStyle(banda ? centerBandaStyle : centerStyle);

            Cell cFF = row.createCell(5);
            cFF.setCellValue(r.getFechaFin().format(fmt));
            cFF.setCellStyle(banda ? centerBandaStyle : centerStyle);

            long dias = java.time.temporal.ChronoUnit.DAYS.between(r.getFechaInicio(), r.getFechaFin());
            long mesesDur = java.time.temporal.ChronoUnit.MONTHS.between(r.getFechaInicio(), r.getFechaFin());
            String duracion = mesesDur >= 1 ? dias + " días (" + mesesDur + (mesesDur == 1 ? " mes)" : " meses)") : dias + " días";
            Cell cDur = row.createCell(6);
            cDur.setCellValue(duracion);
            cDur.setCellStyle(banda ? centerBandaStyle : centerStyle);

            Cell cCap = row.createCell(7);
            Integer capacidad = r.getAlojamiento().getCapacidadEstudiantes();
            cCap.setCellValue(capacidad != null ? capacidad : 0);
            if (capacidad == null) cCap.setBlank();
            cCap.setCellStyle(banda ? centerBandaStyle : centerStyle);

            Cell cMonto = row.createCell(8);
            cMonto.setCellValue(r.getPrecioTotal());
            cMonto.setCellStyle(banda ? montoBandaStyle : montoStyle);

            totalGeneral += r.getPrecioTotal();
        }

        if (reservas.isEmpty()) {
            Row rowVacio = sheet1.createRow(rowNum++);
            Cell cVacio = rowVacio.createCell(0);
            cVacio.setCellValue("No se registraron transacciones en este período.");
            cVacio.setCellStyle(metaStyle);
            sheet1.addMergedRegion(new CellRangeAddress(rowVacio.getRowNum(), rowVacio.getRowNum(), 0, totalColumnas - 1));
        }

        // Fila vacía
        sheet1.createRow(rowNum++);

        // Total general
        Row rowTotal = sheet1.createRow(rowNum);
        rowTotal.setHeight((short) 500);
        Cell labelTotal = rowTotal.createCell(6);
        labelTotal.setCellValue("TOTAL GENERAL:");
        labelTotal.setCellStyle(totalLabelStyle);
        Cell capTotalVacia = rowTotal.createCell(7);
        capTotalVacia.setCellStyle(totalLabelStyle);
        Cell valorTotal = rowTotal.createCell(8);
        valorTotal.setCellValue(totalGeneral);
        valorTotal.setCellStyle(totalStyle);

        // ===== HOJA 2: DESGLOSE POR PROPIEDAD =====
        XSSFSheet sheet2 = workbook.createSheet("Desglose por Propiedad");
        sheet2.setColumnWidth(0, 8000);
        sheet2.setColumnWidth(1, 3000);
        sheet2.setColumnWidth(2, 3500);
        sheet2.setColumnWidth(3, 4200);

        int totalColumnas2 = 4;

        Row rTit2 = sheet2.createRow(0);
        rTit2.setHeight((short) 750);
        Cell cTit2 = rTit2.createCell(0);
        cTit2.setCellValue("Desglose por Propiedad");
        cTit2.setCellStyle(titleStyle);
        sheet2.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumnas2 - 1));

        Row rSub2 = sheet2.createRow(1);
        Cell cSub2 = rSub2.createCell(0);
        cSub2.setCellValue("Período: " + nombreMes);
        cSub2.setCellStyle(subtitleStyle);
        sheet2.addMergedRegion(new CellRangeAddress(1, 1, 0, totalColumnas2 - 1));

        sheet2.createRow(2);

        Row rHead2 = sheet2.createRow(3);
        rHead2.setHeight((short) 550);
        String[] h2 = {"Propiedad", "Reservas", "Capacidad", "Ingresos (S/)"};
        for (int i = 0; i < h2.length; i++) {
            Cell c = rHead2.createCell(i);
            c.setCellValue(h2[i]);
            c.setCellStyle(headerStyle);
        }

        Map<String, List<Reserva>> porPropiedad = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getAlojamiento().getTitulo()));

        int r2 = 4;
        double totalGeneral2 = 0;
        int idxFila = 0;
        for (Map.Entry<String, List<Reserva>> entry : porPropiedad.entrySet().stream()
                .sorted((a, b) -> Double.compare(
                        b.getValue().stream().mapToDouble(Reserva::getPrecioTotal).sum(),
                        a.getValue().stream().mapToDouble(Reserva::getPrecioTotal).sum()))
                .collect(Collectors.toList())) {

            boolean banda = idxFila % 2 == 1;
            double total = entry.getValue().stream().mapToDouble(Reserva::getPrecioTotal).sum();
            totalGeneral2 += total;
            Integer capacidad = entry.getValue().get(0).getAlojamiento().getCapacidadEstudiantes();

            Row row = sheet2.createRow(r2++);
            row.setHeight((short) 420);

            Cell cNombre = row.createCell(0);
            cNombre.setCellValue(entry.getKey());
            cNombre.setCellStyle(banda ? textoBandaStyle : textoStyle);

            Cell cCant = row.createCell(1);
            cCant.setCellValue(entry.getValue().size());
            cCant.setCellStyle(banda ? centerBandaStyle : centerStyle);

            Cell cCap = row.createCell(2);
            cCap.setCellValue(capacidad != null ? capacidad + " estudiantes" : "—");
            cCap.setCellStyle(banda ? centerBandaStyle : centerStyle);

            Cell cTot = row.createCell(3);
            cTot.setCellValue(total);
            cTot.setCellStyle(banda ? montoBandaStyle : montoStyle);

            idxFila++;
        }

        sheet2.createRow(r2++);
        Row rTotal2 = sheet2.createRow(r2);
        rTotal2.setHeight((short) 500);
        Cell lTot2 = rTotal2.createCell(0);
        lTot2.setCellValue("TOTAL GENERAL");
        lTot2.setCellStyle(totalLabelStyle);
        Cell capTotal2 = rTotal2.createCell(2);
        capTotal2.setCellStyle(totalLabelStyle);
        Cell vTot2 = rTotal2.createCell(3);
        vTot2.setCellValue(totalGeneral2);
        vTot2.setCellStyle(totalStyle);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}