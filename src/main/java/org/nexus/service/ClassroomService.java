package org.nexus.service;

import jakarta.transaction.Transactional;
import org.nexus.entity.classroom.Classroom;
import org.nexus.repository.ClassroomRepoistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClassroomService {

    private final ClassroomRepoistory classroomRepoistory;

    @Autowired
    public ClassroomService(ClassroomRepoistory classroomRepoistory) {
        this.classroomRepoistory = classroomRepoistory;
    }

    public void addClassroom(Classroom classroom) {
        classroomRepoistory.save(classroom);
    }

    public List<Classroom> findAllClassrooms() {
        return classroomRepoistory.findAll();
    }

    public Optional<Classroom> findClassroomById(int id) {
        return classroomRepoistory.findById(id);
    }
    public Classroom findClassroomByIdcode(Integer id){
        return classroomRepoistory.findById(id).orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    public Optional<Classroom> findClassroomByNumber(String classroomNumber) {
        return classroomRepoistory.findByClassroomNumber(classroomNumber);
    }

    public List<Classroom> findclassroomsByName(String classroomName) {
        return classroomRepoistory.findByClassroomNameContainingIgnoreCase(classroomName);
    }

    public List<Classroom> findclassroomsByDepartmentId(int departmentId) {
        return classroomRepoistory.findByDepartmentId(departmentId);
    }

    public Classroom createclassroom(Classroom classroom) {
        if (classroomRepoistory.existsByClassroomNumber(classroom.getClassroomNumber())) {
            throw new IllegalArgumentException("Classroom number already exists");
        }
        return classroomRepoistory.save(classroom);
    }

    public Classroom updateClassroom(Integer id, Classroom classroom) {
        return classroomRepoistory.findById(id).map(classroom1 -> {
            classroom1.setClassroomNumber(classroom.getClassroomNumber());
            classroom1.setClassroomName(classroom.getClassroomName());
            classroom1.setClassroomTeacher(classroom.getClassroomTeacher());
            classroom1.setCost(classroom.getCost());
            classroom1.setDepartment(classroom.getDepartment());
            classroom1.setCapacity(classroom.getCapacity());
            classroom1.setArea(classroom.getArea());
            classroom1.setDescription(classroom.getDescription());
            return classroomRepoistory.save(classroom1);
        }).orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    @Transactional
    public void deleteById(Integer id) {
        if (!classroomRepoistory.existsById(id)) {
            throw new RuntimeException("Classroom not found with id: " + id);
        }
        classroomRepoistory.deleteById(id);
    }

    public long countclassrooms() {
        return classroomRepoistory.count();
    }
}
