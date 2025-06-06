package com.example.forum2.controller;

import com.example.forum2.controller.form.ReportForm;
import com.example.forum2.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;

    /*
     * ⑤投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top() {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findAllReport();
        // 画面遷移先(「現在のURL」/top )を指定
        mav.setViewName("/top");
        // 投稿データオブジェクト(contentData)を保管
        mav.addObject("contents", contentData);
        return mav;
    }


    /*
     * ⑦新規投稿画面表示
     */
    @GetMapping("/new")
    // 画面遷移先を指定して、空のFormを保管。戻り値mavを返す流れ
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }


    /*
     * ⑧新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") ReportForm reportForm){
        // Report型の変数reportを引数として、ReportServiceのsaveReportを実行(投稿をテーブルに格納)
        reportService.saveReport(reportForm);
        // root(⑤)へリダイレクト
        return new ModelAndView("redirect:/");
    }
}
