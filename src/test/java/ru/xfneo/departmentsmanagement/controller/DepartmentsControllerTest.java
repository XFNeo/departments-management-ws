package ru.xfneo.departmentsmanagement.controller;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.xfneo.departmentsmanagement.domain.Department;
import ru.xfneo.departmentsmanagement.service.DepartmentService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(MockitoJUnitRunner.class)
public class DepartmentsControllerTest {
    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentsController sut;

    private MockMvc mockMvc;
    private Department department1;
    private Department department2;
    private Department department1UpdatedName;

    private static final String GET_ALL_DEPARTMENTS_URI = "/api/departments";
    private static final String POST_DEPARTMENT_URI = "/api/departments";
    private static final String GET_PUT_DEPARTMENT_URI = "/api/departments/1";
    private static final String DELETE_DEPARTMENT_URI = "/api/departments/1?departmentForReplacement=2";
    private static final String CREATE_DEPARTMENT1_JSON =
            "{\"name\":\"New Department\"}";
    private static final String UPDATE_DEPARTMENT1_JSON =
            "{\"name\":\"Updated Department\"}";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        department1 = new Department(1, "IT Department");
        department2 = new Department(2, "Development Department");
        department1UpdatedName = new Department(1, "Updated Department");
        List<Department> findAllList = Arrays.asList(department1, department2);
        doReturn(findAllList).when(departmentService).findAll();
        doReturn(ResponseEntity.ok(department1)).when(departmentService).find(department1.getId());
        doReturn(ResponseEntity.ok(department1)).when(departmentService).create(any(Department.class));
        doReturn(ResponseEntity.ok(department1UpdatedName)).when(departmentService).update(eq(department1.getId()), any(Department.class));
        doReturn(ResponseEntity.ok().build()).when(departmentService).delete(department1.getId(), String.valueOf(department2.getId()));
    }

    @Test
    @SneakyThrows
    public void testGetListOfDepartments() {
        mockMvc.perform(get(GET_ALL_DEPARTMENTS_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].id", is((int) department1.getId())))
                .andExpect(jsonPath("$[0].name", is(department1.getName())))
                .andExpect(jsonPath("$[1].id", is((int) department2.getId())))
                .andExpect(jsonPath("$[1].name", is(department2.getName())));
        verify(departmentService).findAll();
    }

    @Test
    @SneakyThrows
    public void testGetDepartment() {
        mockMvc.perform(get(GET_PUT_DEPARTMENT_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is((int) department1.getId())))
                .andExpect(jsonPath("$.name", is(department1.getName())));
        verify(departmentService).find(department1.getId());
    }

    @Test
    @SneakyThrows
    public void testCreateDepartment() {
        mockMvc.perform(post(POST_DEPARTMENT_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(CREATE_DEPARTMENT1_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is((int) department1.getId())))
                .andExpect(jsonPath("$.name", is(department1.getName())));
        verify(departmentService).create(any(Department.class));
    }

    @Test
    @SneakyThrows
    public void testUpdateDepartment() {
        mockMvc.perform(put(GET_PUT_DEPARTMENT_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(UPDATE_DEPARTMENT1_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is((int) department1UpdatedName.getId())))
                .andExpect(jsonPath("$.name", is( department1UpdatedName.getName())));
        verify(departmentService).update(eq(department1.getId()), any(Department.class));
    }

    @Test
    @SneakyThrows
    public void testDeleteDepartment() {
        mockMvc.perform(delete(DELETE_DEPARTMENT_URI))
                .andDo(print())
                .andExpect(status().isOk());
        verify(departmentService).delete(department1.getId(), String.valueOf(department2.getId()));
    }
}