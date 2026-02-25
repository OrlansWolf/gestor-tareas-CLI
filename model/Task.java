package com.taskmanager.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private Prioridad priority; // 🆕 NUEVO CAMPO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor completo
    public Task(String id, String title, String description, TaskStatus status, 
                Prioridad priority, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor para crear nueva tarea
    public Task(String title, String description) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.priority = Prioridad.MEDIUM; // 🆕 Prioridad por defecto MEDIO
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor con prioridad personalizada
    public Task(String title, String description, Prioridad priority) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public Prioridad getPriority() {
        return priority;
    }

    public void setPriority(Prioridad priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
