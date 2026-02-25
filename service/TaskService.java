package com.taskmanager.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.taskmanager.model.Prioridad;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.repository.TaskRepository;

public class TaskService {
    private final TaskRepository repository;

    public TaskService() {
        this.repository = new TaskRepository();
    }

    
    //Crea una nueva tarea con prioridad
    
    public Task createTask(String title, String description, Prioridad priority) {
        Task task = new Task(title, description, priority);
        repository.save(task);
        return task;
    }

    
    //Crea una nueva tarea (prioridad por defecto: MEDIO)
    
    public Task createTask(String title, String description) {
        return createTask(title, description, Prioridad.MEDIUM);
    }

    
    //Obtiene todas las tareas
    
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    
    //Obtiene una tarea por ID completo
    public Task getTaskById(String id) {
        Optional<Task> task = repository.findById(id);
        return task.orElse(null);
    }

    
    //Obtiene una tarea por ID con los primeros digitos
    
    public Task getTaskByIdPrefix(String idPrefix) {
        Optional<Task> task = repository.findByIdStartsWith(idPrefix);
        return task.orElse(null);
    }

    
    //Actualiza una tarea
    
    public boolean updateTask(String id, String title, String description) {
        Optional<Task> taskOpt = repository.findById(id);
        
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setTitle(title);
            task.setDescription(description);
            return repository.update(task);
        }
        
        return false;
    }

    
     //Elimina una tarea
     
    public boolean deleteTask(String id) {
        return repository.delete(id);
    }

    
     //Marca una tarea como completada
     
    public boolean markAsCompleted(String id) {
        Optional<Task> taskOpt = repository.findById(id);
        
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(TaskStatus.COMPLETED);
            return repository.update(task);
        }
        
        return false;
    }

    
     //Cambia el estado de una tarea
     
    public boolean changeStatus(String id, TaskStatus status) {
        Optional<Task> taskOpt = repository.findById(id);
        
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(status);
            return repository.update(task);
        }
        
        return false;
    }

    
     //Cambia la prioridad de una tarea
     
    public boolean changePriority(String id, Prioridad priority) {
        Optional<Task> taskOpt = repository.findById(id);
        
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setPriority(priority);
            return repository.update(task);
        }
        
        return false;
    }

    
      //Filtra tareas por estado
     
    public List<Task> filterByStatus(TaskStatus status) {
        return repository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    
     //Filtra tareas por prioridad
    
    public List<Task> filterByPriority(Prioridad priority) {
        return repository.findAll().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }
}
