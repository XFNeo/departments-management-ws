package ru.xfneo.departmentsmanagement.controller;

import feign.Request;
import feign.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import ru.xfneo.departmentsmanagement.client.EmployeeClient;
import ru.xfneo.departmentsmanagement.domain.Department;
import ru.xfneo.departmentsmanagement.repository.DepartmentRepository;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DepartmentsControllerIT {
    @MockBean
    private EmployeeClient employeeClient;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DataSource dataSource;
    private Department department1;
    private Department department2;
    private Department department3;
    private static final String DEPARTMENTS_URI = "/api/departments";
    private static final String DEPARTMENTS_ID_URI = "/api/departments/{id}";

    @Before
    public void setUp() {
        department1 = new Department(1, "IT Department");
        department2 = new Department(2,"QA Department");
        department3 = new Department(3,"Development Department");
        departmentRepository.save(department1);
        departmentRepository.save(department2);
        departmentRepository.save(department3);
    }

    @After
    public void resetDb() {
        departmentRepository.deleteAll();
        new JdbcTemplate(dataSource).update("ALTER SEQUENCE hibernate_sequence RESTART WITH 1");
    }

    @Test
    public void testGetListOfDepartments() {
        ResponseEntity<List<Department>> response = restTemplate.exchange(DEPARTMENTS_URI, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Department>>() {
                });
        List<Department> departments = response.getBody();
        assertThat(departments, hasSize(3));
        assertThat(departments.get(0).getName(), is("IT Department"));
    }

    @Test
    public void testGetDepartment() {
        long id = department2.getId();
        Department actualDepartment = restTemplate.getForObject(DEPARTMENTS_ID_URI, Department.class, id);
        assertEquals(department2, actualDepartment);
    }

    @Test
    public void testCreateDepartment() {
        Department expectedDepartment = new Department(4, "Accounting Department");
        ResponseEntity<Department> response = restTemplate.postForEntity(DEPARTMENTS_URI, expectedDepartment, Department.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody().getId(), notNullValue());
        assertThat(response.getBody().getName(), is(expectedDepartment.getName()));
        Department actualDepartment = restTemplate.getForObject(DEPARTMENTS_ID_URI, Department.class, expectedDepartment.getId());
        assertEquals(expectedDepartment, actualDepartment);
    }

    @Test
    public void updateDepartment() {
        Department expectedDepartment = new Department(999, "Platform Development Department");
        HttpEntity<Department> entity = new HttpEntity<>(expectedDepartment);
        ResponseEntity<Department> response =
                restTemplate.exchange(DEPARTMENTS_ID_URI, HttpMethod.PUT, entity, Department.class, department3.getId());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getId(), is(department3.getId()));
        assertThat(response.getBody().getName(), is(expectedDepartment.getName()));
        Department actualDepartment = restTemplate.getForObject(DEPARTMENTS_ID_URI, Department.class, department3.getId());
        assertEquals(actualDepartment.getName(), expectedDepartment.getName());
    }

    @Test
    public void deleteDepartment() {
        Request mockRequest = mock(Request.class);
        Response responseFromEmployeesService = Response.builder()
                .status(200)
                .body("2 employees are affected", StandardCharsets.UTF_8)
                .request(mockRequest)
                .build();
        when(employeeClient.replaceDepartmentId(anyMap())).thenReturn(responseFromEmployeesService);
        long id = department1.getId();
        ResponseEntity<String> deleteResponse =
                restTemplate.exchange(DEPARTMENTS_ID_URI + "?departmentForReplacement=3", HttpMethod.DELETE, null, String.class, id);
        assertThat(deleteResponse.getStatusCodeValue(), is(200));
        assertThat(deleteResponse.getBody(), is(responseFromEmployeesService.body().toString()));
        ResponseEntity<Void> getDeletedDepartmentResponse =
                restTemplate.exchange(DEPARTMENTS_ID_URI, HttpMethod.GET, null, Void.class, id);
        assertThat(getDeletedDepartmentResponse.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}