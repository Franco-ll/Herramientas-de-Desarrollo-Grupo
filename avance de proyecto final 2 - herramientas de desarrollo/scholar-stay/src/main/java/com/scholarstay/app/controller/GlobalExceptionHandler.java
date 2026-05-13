package com.scholarstay.app.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de excepciones para prevenir pantallas blancas (Error 500)
 * y asegurar que cualquier falla se presente de manera amigable al usuario.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura excepciones de validación de negocio y reglas de dominio
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleBusinessExceptions(RuntimeException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // Redirigimos a la página de donde vino el usuario con un mensaje de error
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    // Captura errores de validación de Jakarta Validation (entidades mal formateadas)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public String handleValidationExceptions(jakarta.validation.ConstraintViolationException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        // Extraemos solo el mensaje del primer error para no abrumar al usuario
        String errorMessage = ex.getConstraintViolations().iterator().next().getMessage();
        redirectAttributes.addFlashAttribute("error", errorMessage);
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    // Captura errores de parseo numérico o de fecha que no hayan sido capturados
    @ExceptionHandler({java.time.format.DateTimeParseException.class, NumberFormatException.class})
    public String handleParseExceptions(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("error", "Los datos ingresados tienen un formato inválido.");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    // Captura cualquier otro error interno inesperado (Error 500 general)
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        // En un entorno de producción, esto debería logear el error 'ex.getMessage()' en un archivo
        model.addAttribute("error", "Ha ocurrido un error inesperado en el servidor. Por favor, intenta nuevamente.");
        // Si tuvieramos una vista error.html la mostraríamos.
        // Como no la tenemos de momento y el front está en desarrollo, podemos usar una vista existente
        // o retornar a dashboard con un modelo de error.
        return "redirect:/dashboard?error=internal_error";
    }
}
