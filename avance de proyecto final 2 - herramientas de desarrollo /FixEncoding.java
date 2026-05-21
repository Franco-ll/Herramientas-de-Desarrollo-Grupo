import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FixEncoding {
    public static void main(String[] args) throws IOException {
        String templatesDir = "src/main/resources/templates";
        File dir = new File(templatesDir);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("Ã¡", "á");
        replacements.put("Ã©", "é");
        replacements.put("Ã³", "ó");
        replacements.put("Ã­", "í");
        replacements.put("Ãº", "ú");
        replacements.put("Ã±", "ñ");
        replacements.put("Â¿", "¿");
        replacements.put("DiseÃ±o", "Diseño");
        replacements.put("SesiÃ³n", "Sesión");
        replacements.put("AnfitriÃ³n", "Anfitrión");
        replacements.put("ContraseÃ±a", "Contraseña");
        replacements.put("â€¢", "•");
        replacements.put("AÃºn", "Aún");
        replacements.put("prÃ³ximo", "próximo");
        replacements.put("AcadÃ©mico", "Académico");
        replacements.put("reseÃ±as", "reseñas");
        
        // General replacements as fallback, in specific order
        replacements.put("Ã ", "Á");
        replacements.put("Ã‰", "É");
        replacements.put("Ã ", "Í");
        replacements.put("Ã“", "Ó");
        replacements.put("Ãš", "Ú");
        replacements.put("Ã‘", "Ñ");

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".html")) {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                
                // Fix charset
                if (content.toLowerCase().contains("<meta charset=\"utf-8\"/>")) {
                    content = content.replace("<meta charset=\"utf-8\"/>", "<meta charset=\"UTF-8\">");
                } else if (!content.contains("<meta charset=\"UTF-8\">")) {
                    content = content.replace("<head>", "<head>\n<meta charset=\"UTF-8\">");
                }

                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    content = content.replace(entry.getKey(), entry.getValue());
                }

                // Handle standalone Ã which translates to í usually when not paired in my dictionary
                content = content.replace("Ã ", "í "); 

                Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
                System.out.println("Processed " + file.getName());
            }
        }
        System.out.println("All files processed.");
    }
}
