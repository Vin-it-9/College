package org.nexus.service;


import jakarta.transaction.Transactional;
import org.nexus.entity.Request;
import org.nexus.entity.User;
import org.nexus.repository.RequestRepository;
import org.nexus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Request createRequest(String subject, String message) {

        User currentUser = getCurrentAuthenticatedUser();

        Request request = new Request();
        request.setSubject(subject);
        request.setMessage(message);
        request.setUser(currentUser);
        request.setStatus(Request.RequestStatus.PENDING);

        return requestRepository.save(request);
    }



    public Request getRequestById(Integer id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
    }

    public List<Request> getCurrentUserRequests() {
        User currentUser = getCurrentAuthenticatedUser();
        return requestRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    public List<Request> getRequestsByDepartment(Integer departmentId) {
        return requestRepository.findByDepartmentIdOrderByCreatedAtDesc(departmentId);
    }


    public List<Request> getPendingRequestsByDepartment(Integer departmentId) {
        return requestRepository.findByDepartmentIdOrderByCreatedAtDesc(departmentId)
                .stream()
                .filter(request -> request.getStatus() == Request.RequestStatus.PENDING)
                .toList();
    }

    public List<Request> getPendingRequests() {
        return requestRepository.findAll().stream().filter(request -> request.getStatus() == Request.RequestStatus.PENDING).toList();
    }


    public int countPendingRequestsByDepartment(Integer departmentId) {
        return requestRepository.countPendingRequestsByDepartmentId(departmentId);
    }


    @Transactional
    public Request approveRequest(Integer requestId, String comments) {
        Request request = getRequestById(requestId);
        User currentUser = getCurrentAuthenticatedUser();

        request.setStatus(Request.RequestStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }


    @Transactional
    public Request rejectRequest(Integer requestId, String comments) {
        Request request = getRequestById(requestId);
        User currentUser = getCurrentAuthenticatedUser();

        request.setStatus(Request.RequestStatus.REJECTED);
        request.setApprovedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }


    private User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.getUserByEmail(email);
    }
}