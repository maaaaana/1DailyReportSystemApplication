package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

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
    public String list(Model model) {
        List<Report> reportList = reportService.findAll();  // ← データ取得してる？
        model.addAttribute("reportList", reportList);       // ← modelに渡してる？
        model.addAttribute("listSize", reportList.size());  // ← 件数も渡してる？

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
    public String add(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail) {
        // ログイン中の従業員を取得してセット
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);

        // 保存処理
        reportService.save(report);

        return "redirect:/report";  // 登録後は一覧画面へ
    }

    //日報詳細画面
    // 従業員詳細画面
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("report", reportService.findById(id));
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

    //更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute Report report,
                         @AuthenticationPrincipal UserDetail userDetail) {

        Employee loginEmployee = userDetail.getEmployee();
        report.setEmployee(loginEmployee);
        reportService.update(id, report);

        return "redirect:/report/" + id;
    }

}

