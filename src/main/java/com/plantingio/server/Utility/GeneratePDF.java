package com.plantingio.server.Utility;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class GeneratePDF {

    private static void addTableHeader(PdfPTable table) {
        Stream.of("#", "Item", "Unit Cost", "Quantity", "Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.WHITE);
                    header.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void addRows(PdfPTable table) {
        Stream.of("1", "Areca Palm Plant", "499.00", "1", "499.00")
                .forEach(cellItem -> {
                    PdfPCell cell = new PdfPCell();
                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    cell.setPhrase(new Phrase(cellItem));
                    table.addCell(cell);
                });

    }

    public static ByteArrayInputStream generate() throws IOException, DocumentException, URISyntaxException {

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA);

        Path path = Paths.get(ClassLoader.getSystemResource("plantingio-logo.png").toURI());
        Image img = Image.getInstance(path.toAbsolutePath().toString());
        document.add(img);

        font.setSize(14);
        font.setColor(58, 58, 58);
        Chunk chunk = new Chunk("Order Date: 03/04/22 \nOrder ID: #50012", font);
        Paragraph para = new Paragraph(chunk);
        para.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(para);

        font.setColor(30, 30, 30);
        Paragraph shippingAddress = new Paragraph("202, Kanak Residency,\nPritamnagar, Paldi,\nAhmedabad, Gujarat - 380007", font);
        shippingAddress.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(shippingAddress);

        PdfPTable table = new PdfPTable(5);
        addTableHeader(table);
        addRows(table);
        document.add(table);

        document.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
