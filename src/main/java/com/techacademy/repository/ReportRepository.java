package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 削除されてない全件
    List<Report> findByDeleteFlgFalse();

    // 削除されてない & 指定従業員コードの報告のみ
    List<Report> findByEmployeeCodeAndDeleteFlgFalse(String code);

    // 登録時の重複チェック（完全一致）
    boolean existsByReportDateAndEmployee(LocalDate reportDate, Employee employee);

    // 更新時の重複チェック（自分自身は除く）
    @Query("SELECT r FROM Report r WHERE r.reportDate = :reportDate AND r.employee = :employee AND r.id <> :id AND r.deleteFlg = false")
    Report findByReportDateAndEmployeeAndIdNot(@Param("reportDate") LocalDate reportDate,
                                                @Param("employee") Employee employee,
                                                @Param("id") Long id);
}
