package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 削除されてない全件
    List<Report> findByDeleteFlgFalse();
    // 削除されてない & 指定従業員コードの報告のみ
    List<Report> findByEmployeeCodeAndDeleteFlgFalse(String code);
    boolean existsByReportDateAndEmployee(LocalDate reportDate, Employee employee);
    }

