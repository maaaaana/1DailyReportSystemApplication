package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

    @Service
    public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void save(Report report) {
        LocalDateTime now = LocalDateTime.now();

        // 作成日時が未設定なら設定する
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(now);
        }

        report.setUpdatedAt(now);
        report.setDeleteFlg(false); // 論理削除フラグ（true = 削除）

        reportRepository.save(report);
    }

    // 一覧取得
    public List<Report> findAll(UserDetail userDetail) {
        Employee employee = userDetail.getEmployee();

        if (employee.getRole().toString().equals("ADMIN")) {
            // 管理者：削除されてない全件
            return reportRepository.findByDeleteFlgFalse();
        } else {
            // 一般ユーザー：自分が書いたものだけ
            return reportRepository.findByEmployeeCodeAndDeleteFlgFalse(employee.getCode());
        }
    }

    //1件取得
    public Report findById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report != null) {
            report.setDeleteFlg(true);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
        }
    }

    @Transactional
    public void update(Long id, Report updatedReport) {
        Report report = reportRepository.findById(id).orElseThrow();

        report.setReportDate(updatedReport.getReportDate());
        report.setTitle(updatedReport.getTitle());
        report.setContent(updatedReport.getContent());
        report.setUpdatedAt(LocalDateTime.now());

        reportRepository.save(report);  // ← 必須！
    }


    public List<Report> getAllReports() {
        return reportRepository.findByDeleteFlgFalse(); // 削除されてない全件
    }

    public List<Report> getReportsByEmployeeCode(String code) {
        return reportRepository.findByEmployeeCodeAndDeleteFlgFalse(code);
    }

    public boolean existsByDateAndEmployee(LocalDate date, Employee employee) {
        return reportRepository.existsByReportDateAndEmployee(date, employee);
    }

    public boolean isDuplicateForUpdate(Long id, LocalDate date, Employee employee) {
        List<Report> reports = reportRepository.findByDeleteFlgFalse();

        return reports.stream()
            .filter(r -> !r.getId().equals(id)) // ←型が一致して安心！
            .anyMatch(r ->
                r.getReportDate().equals(date) &&
                r.getEmployee().getCode().equals(employee.getCode())
            );
    }

}
