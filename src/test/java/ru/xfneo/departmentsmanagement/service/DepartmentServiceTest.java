package ru.xfneo.departmentsmanagement.service;

import feign.Request;
import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import ru.xfneo.departmentsmanagement.client.EmployeeClient;
import ru.xfneo.departmentsmanagement.domain.Department;
import ru.xfneo.departmentsmanagement.repository.DepartmentRepository;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeClient employeeClient;
    @Mock
    Response mockResponse;
    @InjectMocks
    private DepartmentService sut;
    private Department department1, department2;

    @Before
    public void setUp() {
        department1 = new Department(1, "IT Department");
        department2 = new Department(2, "Development Department");
    }

    @Test
    public void testFindAll() {
        List<Department> expectedList = Arrays.asList(department1, department2);
        when(departmentRepository.findAll()).thenReturn(expectedList);
        List<Department> actualList = sut.findAll();
        verify(departmentRepository).findAll();
        assertEquals(expectedList, actualList);
        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    public void testFind_OkResponse() {
        when(departmentRepository.findById(department1.getId())).thenReturn(Optional.of(department1));
        ResponseEntity<?> expectedResponse = ResponseEntity.ok(department1);
        ResponseEntity<?> actualResponse = sut.find(department1.getId());
        assertEquals(expectedResponse, actualResponse);
        verify(departmentRepository).findById(department1.getId());
    }

    @Test
    public void testFind_NotFoundResponse() {
        ResponseEntity<?> expectedNotFoundResponse = ResponseEntity.status(404).body("Department Not Found");
        ResponseEntity<?> actualFailResponse = sut.find(null);
        assertEquals(expectedNotFoundResponse, actualFailResponse);
    }

    @Test
    public void testCreate_OkResponse() {
        when(departmentRepository.save(department1)).thenReturn(department1);
        ResponseEntity<?> expectedResponse = ResponseEntity.ok(department1);
        ResponseEntity<?> actualResponse = sut.create(department1);
        assertEquals(expectedResponse, actualResponse);
        verify(departmentRepository).save(department1);
    }

    @Test
    public void testUpdate_OkResponse() {
        when(departmentRepository.save(department1)).thenReturn(department1);
        when(departmentRepository.findById(department1.getId())).thenReturn(Optional.of(department1));
        ResponseEntity<?> expectedResponse = ResponseEntity.ok(department1);
        ResponseEntity<?> actualResponse = sut.update(department1.getId(), department2);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(department1.getId(), ((Department) Objects.requireNonNull(actualResponse.getBody())).getId());
        assertEquals(department2.getName(), ((Department) Objects.requireNonNull(actualResponse.getBody())).getName());
        verify(departmentRepository).save(department1);
        verify(departmentRepository).findById(department1.getId());
    }

    @Test
    public void testUpdate_NotFoundResponse() {
        when(departmentRepository.findById(department1.getId())).thenReturn(Optional.empty());
        ResponseEntity<?> expectedResponse = ResponseEntity.status(404).body("Department Not Found");
        ResponseEntity<?> actualResponse = sut.update(department1.getId(), department2);
        assertEquals(expectedResponse, actualResponse);
        verify(departmentRepository, times(0)).save(any(Department.class));
        verify(departmentRepository).findById(department1.getId());
    }

    @Test
    public void testDelete_OkResponse() {
        Request mockRequest = mock(Request.class);
        Response responseFromEmployeesService = Response.builder()
                .status(200)
                .body("2 employees are affected", StandardCharsets.UTF_8)
                .request(mockRequest)
                .build();
        when(employeeClient.replaceDepartmentId(anyMap())).thenReturn(responseFromEmployeesService);
        when(departmentRepository.findById(department1.getId())).thenReturn(Optional.of(department1));
        when(departmentRepository.findById(department2.getId())).thenReturn(Optional.of(department2));
        ResponseEntity<?> expectedResponse = ResponseEntity.status(200).body("2 employees are affected");
        ResponseEntity<?> actualResponse = sut.delete(department1.getId(), String.valueOf(department2.getId()));
        assertEquals(expectedResponse, actualResponse);
        verify(departmentRepository).deleteById(department1.getId());
    }

    @Test
    public void testDelete_NotFoundResponse() {
        ResponseEntity<?> expectedResponse = ResponseEntity.status(404).body("Department Not Found");
        ResponseEntity<?> actualResponse = sut.delete(null, String.valueOf(department2.getId()));
        assertEquals(expectedResponse, actualResponse);
        verify(departmentRepository, times(0)).delete(any(Department.class));
    }

    @Test
    public void testDelete_WrongRequestParamResponse() {
        ResponseEntity<?> expectedResponse = ResponseEntity.status(400).body("Parameter departmentIdForReplacement must be a number");
        ResponseEntity<?> actualResponse = sut.delete(department1.getId(), "WrongParam");
        assertEquals(expectedResponse, actualResponse);
        verify(departmentRepository, times(0)).delete(any(Department.class));
    }
}