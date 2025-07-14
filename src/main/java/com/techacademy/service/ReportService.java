package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Report> findAll() {
        return reportRepository.findAll();
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
    }

}
