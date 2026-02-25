package com.taskmanager;

import java.util.List;

import com.taskmanager.model.Prioridad;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.service.TaskService;
import com.taskmanager.view.ConsoleView;

public class main {
    private static final TaskService taskService = new TaskService();
    private static final ConsoleView view = new ConsoleView();

    public static void main(String[] args) {
        boolean running = true;

        view.showMessage("¡Bienvenido al Gestor de Tareas!");

        while (running) {
            view.showMenu();
            int option = view.readInt("Seleccione una opción");

            switch (option) {
                case 1:
                    createTask();
                    break;
                case 2:
                    listAllTasks();
                    break;
                case 3:
                    viewTaskById();
                    break;
                case 4:
                    updateTask();
                    break;
                case 5:
                    deleteTask();
                    break;
                case 6:
                    markTaskAsCompleted();
                    break;
                case 7:
                    changeTaskStatus();
                    break;
                case 8:
                    filterTasksByStatus();
                    break;
                case 9:
                    changePriority();
                    break;
                case 10:
                    filterTasksByPriority();
                    break;
                case 0:
                    view.showMessage("¡Hasta luego! 👋");
                    running = false;
                    break;
                default:
                    view.showError("Opción no válida. Intente nuevamente.");
            }

            if (running && option != 0) {
                view.pause();
            }
        }

        view.close();
    }

    private static void createTask() {
        view.showMessage("=== CREAR NUEVA TAREA ===");
        
        String title = view.readInput("Título");
        if (title.isEmpty()) {
            view.showError("El título no puede estar vacío.");
            return;
        }

        String description = view.readInput("Descripción");
        
        // Preguntar por la prioridad
        Prioridad priority = view.selectPriority();
        
        Task task = taskService.createTask(title, description, priority);
        view.showMessage("✓ Tarea creada exitosamente con ID: " + task.getId());
    }

    private static void listAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        view.showTasks(tasks);
    }

    private static void viewTaskById() {
        String id = view.readInput("Ingrese el ID de la tarea");
        
        Task task = taskService.getTaskByIdPrefix(id);
        
        if (task != null) {
            view.showTaskDetails(task);
        } else {
            view.showError("No se encontró ninguna tarea con ese ID.");
        }
    }

    private static void updateTask() {
        String id = view.readInput("Ingrese el ID de la tarea a actualizar");
        
        Task existingTask = taskService.getTaskByIdPrefix(id);
        
        if (existingTask == null) {
            view.showError("No se encontró ninguna tarea con ese ID.");
            return;
        }
        
        view.showTaskDetails(existingTask);
        
        String newTitle = view.readInput("Nuevo título (Enter para mantener actual)");
        String newDescription = view.readInput("Nueva descripción (Enter para mantener actual)");
        
        String title = newTitle.isEmpty() ? existingTask.getTitle() : newTitle;
        String description = newDescription.isEmpty() ? existingTask.getDescription() : newDescription;
        
        if (taskService.updateTask(existingTask.getId(), title, description)) {
            view.showMessage("✓ Tarea actualizada exitosamente.");
        } else {
            view.showError("No se pudo actualizar la tarea.");
        }
    }

    private static void deleteTask() {
        String id = view.readInput("Ingrese el ID de la tarea a eliminar");
        
        Task task = taskService.getTaskByIdPrefix(id);
        
        if (task == null) {
            view.showError("No se encontró ninguna tarea con ese ID.");
            return;
        }
        
        view.showTaskDetails(task);
        String confirm = view.readInput("¿Está seguro? (s/n)");
        
        if (confirm.equalsIgnoreCase("s")) {
            if (taskService.deleteTask(task.getId())) {
                view.showMessage("✓ Tarea eliminada exitosamente.");
            } else {
                view.showError("No se pudo eliminar la tarea.");
            }
        } else {
            view.showMessage("Operación cancelada.");
        }
    }

    private static void markTaskAsCompleted() {
        String id = view.readInput("Ingrese el ID de la tarea a completar");
        
        Task task = taskService.getTaskByIdPrefix(id);
        
        if (task == null) {
            view.showError("No se encontró ninguna tarea con ese ID.");
            return;
        }
        
        if (taskService.markAsCompleted(task.getId())) {
            view.showMessage("✓ Tarea marcada como completada.");
        } else {
            view.showError("No se pudo marcar la tarea como completada.");
        }
    }

    private static void changeTaskStatus() {
        String id = view.readInput("Ingrese el ID de la tarea");
        
        Task task = taskService.getTaskByIdPrefix(id);
        
        if (task == null) {
            view.showError("No se encontró ninguna tarea con ese ID.");
            return;
        }
        
        TaskStatus newStatus = view.selectStatus();
        
        if (taskService.changeStatus(task.getId(), newStatus)) {
            view.showMessage("✓ Estado actualizado a: " + newStatus.getDisplayName());
        } else {
            view.showError("No se pudo cambiar el estado de la tarea.");
        }
    }

    private static void filterTasksByStatus() {
        TaskStatus status = view.selectStatus();
        List<Task> filteredTasks = taskService.filterByStatus(status);
        
        view.showMessage("Tareas con estado: " + status.getDisplayName());
        view.showTasks(filteredTasks);
    }

    private static void changePriority() {
        String id = view.readInput("Ingrese el ID de la tarea");
        
        Task task = taskService.getTaskByIdPrefix(id);
        
        if (task == null) {
            view.showError("No se encontró ninguna tarea con ese ID.");
            return;
        }
        
        view.showTaskDetails(task);
        Prioridad newPriority = view.selectPriority();
        
        if (taskService.changePriority(task.getId(), newPriority)) {
            view.showMessage("✓ Prioridad actualizada a: " + newPriority.getDisplayName());
        } else {
            view.showError("No se pudo cambiar la prioridad de la tarea.");
        }
    }

    private static void filterTasksByPriority() {
        Prioridad priority = view.selectPriority();
        List<Task> filteredTasks = taskService.filterByPriority(priority);
        
        view.showMessage("Tareas con prioridad: " + priority.getDisplayName());
        view.showTasks(filteredTasks);
    }
}
