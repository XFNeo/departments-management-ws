package ru.xfneo.departmentsmanagement.service;

import feign.Response;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeClient employeeClient;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, EmployeeClient employeeClient) {
        this.departmentRepository = departmentRepository;
        this.employeeClient = employeeClient;
    }

    /**
     * Get all existing departments from repository
     *
     * @return List of all existing departments from repository.
     */
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    /**
     * Get the department from repository.
     *
     * @param id  ID of the department you want to receive
     * @return ResponseEntity with code 200 and department in body,
     * or ResponseEntity with code 404 and body "Department Not Found" if department with that id does not exist.
     */
    public ResponseEntity<?> find(Long id) {
        Optional<Department> departmentOptional = departmentRepository.findById(id);
        if (departmentOptional.isPresent()) {
            return ResponseEntity.ok(departmentOptional.get());
        }
        return ResponseEntity.status(404).body("Department Not Found");
    }

    /**
     * Create and save the department to repository.
     *
     * @param department  department to save
     * @return ResponseEntity with code 201 and saved department in body.
     */
    public ResponseEntity<?> create(Department department) {
        return ResponseEntity.status(201).body(departmentRepository.save(department));
    }

    /**
     * Update the department and save to repository.
     *
     * @param originalDepartmentId  id of the department to change
     * @param editedDepartment  updated department's data to save
     * @return ResponseEntity with code 200 and updated department in body,
     * or ResponseEntity with code 404 and body "Department Not Found" if original department does not exist,
     * or ResponseEntity with code 400 and body "Department data is not valid!" if updated department is null.
     */
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

    /**
     * Delete the department from repository and transfer all employees from this department to another department" .
     * (call external service employee-management-ws).
     *
     * @param departmentIdForDelete id of the department to delete
     * @param departmentForReplacement id of the department to transfer employees from the department to be removed
     * @return ResponseEntity with code 200 and body "%d employee(s) are affected"
     * (how many employees have been transferred from deleted department to new one),
     * or ResponseEntity with code 400 and body "Parameter departmentIdForReplacement must be a number"
     * if parameter departmentIdForReplacement is not valid,
     * or ResponseEntity with code 400 and body "Department Not Found" if deleted or swap department not found,
     * or ResponseEntity with another code if response from service employee-management-ws is not Ok.
     */
    public ResponseEntity<?> delete(Long departmentIdForDelete, String departmentForReplacement) {
        long departmentIdForReplacement;
        try {
            departmentIdForReplacement = Long.parseLong(departmentForReplacement);
        } catch (NumberFormatException e) {
            log.error("Called delete method for department {} with wrong param departmentIdForReplacement = {}", departmentIdForDelete, departmentForReplacement);
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
