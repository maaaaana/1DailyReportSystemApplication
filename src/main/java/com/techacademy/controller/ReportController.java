package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("report")
public class ReportController {

    // 日報一覧画面へ遷移
    @GetMapping
    public String list(Model model) {
        return "report/list";  // templates/reports/list.html を表示
    }

    // 日報詳細画面

    //日報新規登録画面

    // 必要に応じて、詳細・新規作成画面などへの遷移用GETメソッドを追加
    @GetMapping("/add")
    public String add() {
        return "report/new";
    }

    @GetMapping("/{id}")
    public String detail() {
        return "report/detail";
    }
}