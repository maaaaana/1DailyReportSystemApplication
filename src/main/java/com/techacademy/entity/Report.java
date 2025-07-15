package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 日付（必須）
    @NotNull(message = "値を入力してください")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    // タイトル（必須、100文字以内）
    @NotBlank(message = "値を入力してください")
    @Size(max = 100, message = "100文字以下で入力してください")
    @Column(length = 100, nullable = false)
    private String title;

    // 内容（必須、600文字以内）
    @NotBlank(message = "値を入力してください")
    @Size(max = 600, message = "600文字以下で入力してください")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 従業員（外部キー）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // 削除フラグ
    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
