package ru.xfneo.departmentsmanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.xfneo.departmentsmanagement.domain.Department;
import ru.xfneo.departmentsmanagement.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Api(value = "/api", tags = "Departments API")
public class DepartmentsController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentsController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @ApiOperation(value = "Retrieve a list of all departments", response = Department.class, responseContainer="List")
    @ApiResponse(code = 200, message = "Successfully retrieved list")
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Department> getListOfDepartments() {
        return departmentService.findAll();
    }

    @ApiOperation(value = "Retrieve department", response = Department.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved department"),
            @ApiResponse(code = 404, message = "Department not found")
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getDepartment(@PathVariable("id") Long id) {
        return departmentService.find(id);
    }

    @ApiOperation(value = "Create department", code = 201, response = Department.class)
    @ApiResponse(code = 201, message = "Successfully created department")
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        return departmentService.create(department);
    }

    @ApiOperation(value = "Update department", response = Department.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated department"),
            @ApiResponse(code = 400, message = "Department data is not valid"),
            @ApiResponse(code = 404, message = "Department for update not found")
    })
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> updateDepartment(
            @PathVariable("id") Long originalDepartmentId,
            @RequestBody Department editedDepartment
    ) {
        return departmentService.update(originalDepartmentId, editedDepartment);
    }

    @ApiOperation(value = "Delete department and transfer all employee from this department to another department", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted department, returns information about how many employees have been transferred to new department"),
            @ApiResponse(code = 400, message = "Invalid \"departmentIdForReplacement\" parameter"),
            @ApiResponse(code = 404, message = "Department not found")
    })
    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deleteDepartment(
            @RequestParam String departmentForReplacement,
            @PathVariable("id") Long departmentIdForDelete
    ) {
        return departmentService.delete(departmentIdForDelete, departmentForReplacement);
    }
}
