import os

def fix_encoding_issues():
    templates_dir = r"c:\Users\sanan\Downloads\interfaces\avance 1 - herramientas de desarrollo\scholar-stay\src\main\resources\templates"
    replacements = {
        "Ã¡": "á",
        "Ã©": "é",
        "Ã³": "ó",
        "Ã­": "í",
        "Ãº": "ú",
        "Ã±": "ñ",
        "Ã": "í", # Ã sometimes is í when not followed by another char, but let's be careful
        "Â¿": "¿",
        "DiseÃ±o": "Diseño",
        "SesiÃ³n": "Sesión",
        "AnfitriÃ³n": "Anfitrión",
        "ContraseÃ±a": "Contraseña",
        "â€¢": "•",
        "AÃºn": "Aún",
        "prÃ³ximo": "próximo",
        "AcadÃ©mico": "Académico",
        "reseÃ±as": "reseñas",
        "calificaciÃ³n": "calificación",
        "CalificaciÃ³n": "Calificación",
        "ubicaciÃ³n": "ubicación",
        "UbicaciÃ³n": "Ubicación",
        "descripciÃ³n": "descripción",
        "DescripciÃ³n": "Descripción",
        "informaciÃ³n": "información",
        "InformaciÃ³n": "Información",
        "confirmaciÃ³n": "confirmación",
        "ConfirmaciÃ³n": "Confirmación",
        "polÃ­tica": "política",
        "PolÃ­tica": "Política",
        "cancelaciÃ³n": "cancelación",
        "CancelaciÃ³n": "Cancelación",
        "tÃ©rminos": "términos",
        "TÃ©rminos": "Términos",
        "condiciones": "condiciones",
        "Condiciones": "Condiciones",
        "habitación": "habitación",
        "Habitación": "Habitación",
        "baÃ±o": "baño",
        "BaÃ±o": "Baño",
        "televisiÃ³n": "televisión",
        "TelevisiÃ³n": "Televisión",
        "estÃ¡ndar": "estándar",
        "EstÃ¡ndar": "Estándar",
        "bÃ¡sico": "básico",
        "BÃ¡sico": "Básico",
        "mÃ¡s": "más",
        "MÃ¡s": "Más",
        "aquÃ­": "aquí",
        "AquÃ­": "Aquí",
        "dÃ­a": "día",
        "DÃ­a": "Día",
        "mes": "mes",
        "aÃ±o": "año",
        "AÃ±o": "Año",
        "fotografÃ­as": "fotografías",
        "FotografÃ­as": "Fotografías",
        "imÃ¡genes": "imágenes",
        "ImÃ¡genes": "Imágenes",
        "sÃ­": "sí",
        "SÃ­": "Sí",
        "no": "no",
        "No": "No",
        "Ã¡rea": "área",
        "Ãrea": "Área",
        "Ãºltimo": "último",
        "Ãšltimo": "Último",
        "Ãºnico": "único",
        "Ãšnico": "Único",
        "Ã©xito": "éxito",
        "Ã‰xito": "Éxito",
        "Ãndice": "índice",
        "Ãndice": "Índice",
        "Ã³rden": "órden",
        "Ã“rden": "Órden",
        "Ãºtil": "útil",
        "Ãštil": "Útil",
        "Ã±": "ñ",
        "Ã‘": "Ñ",
        "Ã¡": "á",
        "Ã©": "é",
        "Ã­": "í",
        "Ã³": "ó",
        "Ãº": "ú",
        "Ã": "Á",
        "Ã‰": "É",
        "Ã": "Í",
        "Ã“": "Ó",
        "Ãš": "Ú",
        "Â¡": "¡",
        "Â¿": "¿"
    }

    # Iterate through templates
    for filename in os.listdir(templates_dir):
        if filename.endswith(".html"):
            filepath = os.path.join(templates_dir, filename)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            # Fix charset
            if '<meta charset="utf-8"' in content.lower():
                content = content.replace('<meta charset="utf-8"/>', '<meta charset="UTF-8">')
                content = content.replace('<meta charset="utf-8">', '<meta charset="UTF-8">')
            elif '<meta charset="UTF-8"' not in content:
                content = content.replace('<head>', '<head>\n<meta charset="UTF-8">')

            # Replace broken characters (do specific ones first, then single letters to avoid double replacement)
            for old, new in replacements.items():
                content = content.replace(old, new)

            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)

    print("HTML templates fixed.")

if __name__ == "__main__":
    fix_encoding_issues()
