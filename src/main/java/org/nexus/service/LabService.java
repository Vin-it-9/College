package org.nexus.service;

import org.nexus.entity.Lab;
import org.nexus.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LabService {

    private final LabRepository labRepository;

    @Autowired
    public LabService(LabRepository labRepository) {
        this.labRepository = labRepository;
    }

    public List<Lab> findAllLabs() {
        return labRepository.findAll();
    }

    public Optional<Lab> findLabById(Integer id) {
        return labRepository.findById(id);
    }

    public Optional<Lab> findLabByLabNumber(String labNumber) {
        return labRepository.findByLabNumber(labNumber);
    }

    public List<Lab> findLabsByLabName(String labName) {
        return labRepository.findByLabNameContainingIgnoreCase(labName);
    }

    @Transactional
    public Lab createLab(Lab lab) {
        // Additional validation logic can go here
        if (labRepository.existsByLabNumber(lab.getLabNumber())) {
            throw new IllegalArgumentException("Lab number already exists");
        }
        return labRepository.save(lab);
    }

    @Transactional
    public Lab updateLab(Integer id, Lab labDetails) {
        return labRepository.findById(id).map(lab -> {
            lab.setLabNumber(labDetails.getLabNumber());
            lab.setLabName(labDetails.getLabName());
            lab.setLabAssistant(labDetails.getLabAssistant());
            lab.setLabTeacher(labDetails.getLabTeacher());
            lab.setCost(labDetails.getCost());
            lab.setDepartment(labDetails.getDepartment());
            lab.setCapacity(labDetails.getCapacity());
            lab.setArea(labDetails.getArea());
            lab.setDescription(labDetails.getDescription());
            lab.setIsActive(labDetails.getIsActive());
            lab.setAdditionalFacility(labDetails.getAdditionalFacility());
            return labRepository.save(lab);
        }).orElseThrow(() -> new RuntimeException("Lab not found with id: " + id));
    }

    @Transactional
    public void deleteLab(Integer id) {
        if (!labRepository.existsById(id)) {
            throw new RuntimeException("Lab not found with id: " + id);
        }
        labRepository.deleteById(id);
    }

    public long countLabs() {
        return labRepository.count();
    }
}