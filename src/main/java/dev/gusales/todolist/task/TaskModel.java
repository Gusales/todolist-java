package dev.gusales.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * id
 * user
 * title
 * description
 * createdAt
 * startAt
 * endAt
 * Priority
 * completed
 */

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

     @Id
     @GeneratedValue(generator = "UUID")
     private UUID id;
     
     @Column(length = 50)
     private String title;


     private String description;
     private LocalDateTime startAt;
     private LocalDateTime endAt;
     private String priority;

     @CreationTimestamp
     private LocalDateTime createdAt;

    private UUID idUser;


    public void setTitle(String title) throws Exception{
        if (title.length() > 50) {
            throw new Exception("The field 'title' cannot be longer than 50 characters");
        }

        this.title = title;
    }

}
