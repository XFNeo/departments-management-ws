package ru.xfneo.departmentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xfneo.departmentsmanagement.domain.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
