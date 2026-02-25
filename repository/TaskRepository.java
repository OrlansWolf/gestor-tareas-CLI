package com.taskmanager.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.taskmanager.model.Task;
import com.taskmanager.util.FileManager;

public class TaskRepository {
    private List<Task> tasks;
    private static final String FILE_PATH = "tasks.json";
    private int nextId;

    public TaskRepository() {
        this.tasks = new ArrayList<>();
        loadFromFile();
        this.nextId = calculateNextId();
    }


    //Calcula el siguiente ID basado en las tareas existentes
    
    private int calculateNextId() {
        if (tasks.isEmpty()) {
            return 1;
        }
        
        int maxId = 0;
        for (Task task : tasks) {
            try {
                int currentId = Integer.parseInt(task.getId());
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                // Si hay un ID no numerico se ignora
            }
        }
        
        return maxId + 1;
    }


    //Genera un ID formateado (000001, 000002, etc.)
    
    private String generateId() {
        String id = String.format("%06d", nextId);
        nextId++;
        return id;
    }


    //Guarda una tarea
    
    public void save(Task task) {
        // Si la tarea no tiene ID se asigna uno
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(generateId());
            tasks.add(task);
        } else {
            // Si ya tiene ID se actualiza
            Optional<Task> existingTask = findById(task.getId());
            if (existingTask.isPresent()) {
                update(task);
            } else {
                tasks.add(task);
            }
        }
        
        saveToFile();
    }

    
    //Obtener todas las tareas
    
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    
    //Busca una tarea por ID
    
    public Optional<Task> findById(String id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    
    //Busca una tarea por ID parcial (permite buscar con los primeros dígitos)
    
    public Optional<Task> findByIdStartsWith(String idPrefix) {
        return tasks.stream()
                .filter(task -> task.getId().startsWith(idPrefix))
                .findFirst();
    }

    
    //Elimina una tarea por ID
    
    public boolean delete(String id) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        
        if (removed) {
            saveToFile();
        }
        
        return removed;
    }

    
    //Actualiza una tarea existente
    
    public boolean update(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    
    //Carga las tareas desde el archivo
    
    public void loadFromFile() {
        tasks = FileManager.loadTasks(FILE_PATH);
    }

    
    //Guarda las tareas en el archivo
    
    public void saveToFile() {
        FileManager.saveTasks(tasks, FILE_PATH);
    }

    
    //Obtiene el siguiente ID que se asignará
    
    public int getNextId() {
        return nextId;
    }
}
