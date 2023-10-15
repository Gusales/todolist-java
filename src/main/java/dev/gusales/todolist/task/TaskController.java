package dev.gusales.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gusales.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @GetMapping("")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var taskList = this.taskRepository.findByIdUser((UUID) idUser);

        return taskList;
    }

    @PostMapping("")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(400).body("A data de inicio / término da tarefa deve ser depois da data atual.");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(400).body("A data de término deve ser maior que a data de início");
        }

        var createdTask = this.taskRepository.save(taskModel);
        return ResponseEntity.status(201).body(createdTask);

    }

    @PutMapping("/{taskId}")
    public ResponseEntity updateTask(@RequestBody TaskModel taskModel, @PathVariable UUID taskId, HttpServletRequest request) {
        var task = this.taskRepository.findById(taskId).orElse(null);

        if (task == null) {
            return ResponseEntity.status(400).body("Task not exists.");
        }

        var idUser = request.getAttribute("idUser");
        
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(401).body("This user cannot be modify this task.");
        }
        
        Utils.copyNonNullProperties(taskModel, task);
        var updatedTask = this.taskRepository.save(task);

        return ResponseEntity.ok().body(updatedTask); 
    }
}
