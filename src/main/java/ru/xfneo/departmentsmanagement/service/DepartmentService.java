package ru.xfneo.departmentsmanagement.service;

import feign.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.xfneo.departmentsmanagement.client.EmployeeClient;
import ru.xfneo.departmentsmanagement.domain.Department;
import ru.xfneo.departmentsmanagement.repository.DepartmentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeClient employeeClient;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, EmployeeClient employeeClient) {
        this.departmentRepository = departmentRepository;
        this.employeeClient = employeeClient;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public ResponseEntity<?> find(Long id) {
        Optional<Department> departmentOptional = departmentRepository.findById(id);
        if (departmentOptional.isPresent()) {
            return ResponseEntity.ok(departmentOptional.get());
        }
        return ResponseEntity.status(404).body("Department Not Found");
    }

    public ResponseEntity<?> create(Department department) {
        return ResponseEntity.ok(departmentRepository.save(department));
    }

    public ResponseEntity<?> update(Long originalDepartmentId, Department editedDepartment) {
        Optional<Department> departmentOptional = departmentRepository.findById(originalDepartmentId);
        if (!departmentOptional.isPresent()) {
            return ResponseEntity.status(404).body("Department Not Found");
        }
        if (editedDepartment == null) return ResponseEntity.status(400).body("Department data is not valid!");
        Department departmentToSave = departmentOptional.get();
        BeanUtils.copyProperties(editedDepartment, departmentToSave, "id");
        return ResponseEntity.ok(departmentRepository.save(departmentToSave));
    }

    public ResponseEntity<?> delete(Long departmentIdForDelete, String departmentForReplacement) {
        long departmentIdForReplacement;
        try {
            departmentIdForReplacement = Long.parseLong(departmentForReplacement);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .status(400)
                    .body("Parameter departmentIdForReplacement must be a number");
        }
        if (!departmentRepository.findById(departmentIdForReplacement).isPresent() ||
                !departmentRepository.findById(departmentIdForDelete).isPresent()) {
            return ResponseEntity.status(404).body("Department Not Found");
        }
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("oldDepartmentID", departmentIdForDelete);
        requestBody.put("newDepartmentID", departmentIdForReplacement);
        Response responseFromEmployeeService = employeeClient.replaceDepartmentId(requestBody);
        if (responseFromEmployeeService.status() >= 200 && responseFromEmployeeService.status() < 300) {
            departmentRepository.deleteById(departmentIdForDelete);
        }
        return ResponseEntity.status(responseFromEmployeeService.status()).body(responseFromEmployeeService.body().toString());
    }
}
