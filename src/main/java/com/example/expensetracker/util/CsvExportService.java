package com.example.expensetracker.util;

import com.example.expensetracker.model.Expense;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvExportService {

    private static final String[] CSV_HEADERS = {
            "ID", "Name", "Amount", "Date", "Category", "Sub Category", "Location", "Card Used"
    };

    public byte[] exportExpensesToCsv(List<Expense> expenses) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(
                     new OutputStreamWriter(out, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.withHeader(CSV_HEADERS))) {

            for (Expense expense : expenses) {
                csvPrinter.printRecord(
                        expense.getId(),
                        expense.getName(),
                        expense.getAmount(),
                        expense.getDate(),
                        expense.getCategory(),
                        expense.getSubCategory(),
                        expense.getLocation(),
                        expense.getCardUsed()
                );
            }

            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export expenses to CSV", e);
        }
    }
}
