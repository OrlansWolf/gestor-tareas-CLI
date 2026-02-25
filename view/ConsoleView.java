package com.taskmanager.view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.taskmanager.model.Prioridad;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;

public class ConsoleView {
    private final Scanner scanner;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú principal
     */
    public void showMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      GESTOR DE TAREAS - MENÚ          ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Crear nueva tarea                  ║");
        System.out.println("║ 2. Listar todas las tareas            ║");
        System.out.println("║ 3. Ver detalles de una tarea          ║");
        System.out.println("║ 4. Actualizar tarea                   ║");
        System.out.println("║ 5. Eliminar tarea                     ║");
        System.out.println("║ 6. Marcar tarea como completada       ║");
        System.out.println("║ 7. Cambiar estado de tarea            ║");
        System.out.println("║ 8. Filtrar tareas por estado          ║");
        System.out.println("║ 9. Cambiar prioridad de tarea         ║");
        System.out.println("║ 10. Filtrar tareas por prioridad      ║");
        System.out.println("║ 0. Salir                              ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    /**
     * Muestra una lista de tareas
     */
    public void showTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showMessage("📭 No hay tareas para mostrar.");
            return;
        }

        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                         LISTA DE TAREAS                                 │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────┤");
        
        for (Task task : tasks) {
            String statusIcon = getStatusIcon(task.getStatus());
            String priorityIcon = task.getPriority().getIcon();
            
            System.out.printf("│ %s %s [%s] %-48s │%n", 
                    statusIcon,
                    priorityIcon,
                    task.getId(), 
                    truncate(task.getTitle(), 48));
            System.out.printf("│    Estado: %-20s Prioridad: %-18s │%n", 
                    task.getStatus().getDisplayName(),
                    task.getPriority().getDisplayName());
        }
        
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘");
        System.out.println("Total de tareas: " + tasks.size());
    }

    /**
     * Muestra una tarea individual con detalles
     */
    public void showTaskDetails(Task task) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DETALLES DE LA TAREA                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ ID:          %-49s ║%n", task.getId());
        System.out.printf("║ Título:      %-49s ║%n", task.getTitle());
        System.out.printf("║ Descripción: %-49s ║%n", truncate(task.getDescription(), 49));
        System.out.printf("║ Estado:      %-49s ║%n", task.getStatus().getDisplayName());
        System.out.printf("║ Prioridad:   %s %-44s ║%n", 
                task.getPriority().getIcon(), 
                task.getPriority().getDisplayName());
        System.out.printf("║ Creada:      %-49s ║%n", task.getCreatedAt().format(formatter));
        System.out.printf("║ Actualizada: %-49s ║%n", task.getUpdatedAt().format(formatter));
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    /**
     * Muestra un mensaje genérico
     */
    public void showMessage(String message) {
        System.out.println("\n✓ " + message);
    }

    /**
     * Muestra un mensaje de error
     */
    public void showError(String error) {
        System.err.println("\n✗ ERROR: " + error);
    }

    /**
     * Lee la entrada del usuario con un prompt
     */
    public String readInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    /**
     * Lee un número entero
     */
    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                showError("Por favor, ingrese un número válido.");
            }
        }
    }

    /**
     * Muestra opciones de estado y retorna la selección
     */
    public TaskStatus selectStatus() {
        System.out.println("\n┌─────────────────────────────┐");
        System.out.println("│  SELECCIONE ESTADO          │");
        System.out.println("├─────────────────────────────┤");
        System.out.println("│ 1. Pendiente                │");
        System.out.println("│ 2. En Progreso              │");
        System.out.println("│ 3. Completada               │");
        System.out.println("└─────────────────────────────┘");
        
        int option = readInt("Opción");
        
        switch (option) {
            case 1:
                return TaskStatus.PENDING;
            case 2:
                return TaskStatus.IN_PROGRESS;
            case 3:
                return TaskStatus.COMPLETED;
            default:
                showError("Opción inválida. Se seleccionará 'Pendiente'.");
                return TaskStatus.PENDING;
        }
    }

    /**
     * Muestra opciones de prioridad y retorna la selección
     */
    public Prioridad selectPriority() {
        System.out.println("\n┌─────────────────────────────┐");
        System.out.println("│  SELECCIONE PRIORIDAD       │");
        System.out.println("├─────────────────────────────┤");
        System.out.println("│ 1. 🟢 Baja                  │");
        System.out.println("│ 2. 🟡 Media                 │");
        System.out.println("│ 3. 🔴 Alta                  │");
        System.out.println("│ 4. 🔥 Urgente               │");
        System.out.println("└─────────────────────────────┘");
        
        int option = readInt("Opción");
        
        switch (option) {
            case 1:
                return Prioridad.LOW;
            case 2:
                return Prioridad.MEDIUM;
            case 3:
                return Prioridad.HIGH;
            case 4:
                return Prioridad.URGENT;
            default:
                showError("Opción inválida. Se seleccionará 'Media'.");
                return Prioridad.MEDIUM;
        }
    }

    /**
     * Obtiene el ícono según el estado
     */
    private String getStatusIcon(TaskStatus status) {
        switch (status) {
            case PENDING:
                return "⏳";
            case IN_PROGRESS:
                return "🔄";
            case COMPLETED:
                return "✅";
            default:
                return "❓";
        }
    }

    /**
     * Trunca un texto si es muy largo
     */
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Limpia la consola
     */
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Pausa hasta que el usuario presione Enter
     */
    public void pause() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Cierra el scanner
     */
    public void close() {
        scanner.close();
    }
}
