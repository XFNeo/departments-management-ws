package ru.xfneo.departmentsmanagement.client;

import feign.Response;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface EmployeeClient {

    @PostMapping(value = "/api/employees/replaceDepartment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response replaceDepartmentId(@RequestBody Map<String, Long> oldAndNewDepartmentIds);
}