package com.clustering.clustering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clustering.clustering.service.StudentPreferencesService;
import com.clustering.clustering.service.TeacherPreferencesService;

@RestController
@RequestMapping("/users")
public class UserSyncController {

    @Autowired
    private StudentPreferencesService alunoService;

    @Autowired
    private TeacherPreferencesService professorService;

    @DeleteMapping("/remove-preferences/{userId}")
    public ResponseEntity<Void> removeUserPreferences(@PathVariable Long userId) {
        alunoService.deleteByUserId(userId);
        professorService.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/sync")
    public ResponseEntity<Void> syncUser(@RequestBody UserDTO user) {
        alunoService.updateUserName(user.getId(), user.getName());
        alunoService.updateUserEmail(user.getId(), user.getEmail());

        professorService.updateUserName(user.getId(), user.getName());
        professorService.updateUserEmail(user.getId(), user.getEmail());

        return ResponseEntity.ok().build();
    }

    public static class UserDTO {
        private Long id;
        private String name;
        private String oldName;
        private String email;
        private String role;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getOldName() { return oldName; }
        public void setOldName(String oldName) { this.oldName = oldName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

}