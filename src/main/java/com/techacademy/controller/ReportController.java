package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

import jakarta.validation.Valid;

@Controller
@RequestMapping("report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        String loginUserCode = userDetail.getEmployee().getCode();
        boolean isAdmin = userDetail.getEmployee().getRole().toString().equals("ADMIN");

        List<Report> reports;
        if (isAdmin) {
            reports = reportService.getAllReports(); // 管理者は全件
        } else {
            reports = reportService.getReportsByEmployeeCode(loginUserCode); // 一般は自分のみ
        }

        model.addAttribute("reportList", reports);
        model.addAttribute("listSize", reports.size()); // 件数も追加で安心
        return "report/list";
    }

    // 日報新規登録画面
    @GetMapping("/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail) {
        // ログイン中の従業員を取得してセット
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);

        return "report/new";
    }

    // 日報登録処理（POST）
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Report report,
                      BindingResult result,
                      @AuthenticationPrincipal UserDetail userDetail,
                      Model model) {

        // 入力チェック（バリデーション）でエラーがあれば入力画面に戻す
        if (result.hasErrors()) {
            return "report/new";
        }

        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);

        // ✅ 業務チェック：同一従業員・同一日付で既に登録されていないか
        if (reportService.existsByDateAndEmployee(report.getReportDate(), employee)) {
            model.addAttribute("dateError", "既に登録されている日付です");
            return "report/new";
        }

        reportService.save(report);
        return "redirect:/report";
    }

    //日報詳細画面
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         @AuthenticationPrincipal UserDetail userDetail,
                         Model model) {
        Report report = reportService.findById(id);
        Employee loginEmployee = userDetail.getEmployee();

        // 管理者でなければ、本人のデータ以外は拒否
        if (!loginEmployee.getRole().toString().equals("ADMIN") &&
            !report.getEmployee().getCode().equals(loginEmployee.getCode())) {
            return "error/403"; // またはエラーページにリダイレクトなど
        }

        model.addAttribute("report", report);
        return "report/detail";
    }


    //日報削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        reportService.delete(id);
        return "redirect:/report";
    }

    //更新画面表示（GET）
    @GetMapping("/{id}/update")
    public String showUpdate(@PathVariable("id") Long id, Model model) {
        Report report = reportService.findById(id); // データ取得
        model.addAttribute("report", report);
        return "report/update"; // update.html に遷移
    }

    // 更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute Report report,
                         BindingResult result,
                         @AuthenticationPrincipal UserDetail userDetail,
                         Model model) {

        Employee loginEmployee = userDetail.getEmployee();
        report.setEmployee(loginEmployee);  // ここで必ずセット

        if (result.hasErrors()) {
            model.addAttribute("employee", loginEmployee);
            return "report/update";
        }

        // ✅ ここを追加！重複チェック
        if (reportService.isDuplicateForUpdate(id, report.getReportDate(), loginEmployee)) {
            result.rejectValue("reportDate", "", "既に登録されている日付です");
            model.addAttribute("employee", loginEmployee);
            return "report/update";
        }

        // 更新実行
        reportService.update(id, report);
        return "redirect:/report";
    }
}

