package com.example.forum2.controller;

import com.example.forum2.controller.form.CommentForm;
import com.example.forum2.controller.form.ReportForm;
import com.example.forum2.service.CommentService;
import com.example.forum2.service.ReportService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;
    @Autowired
    HttpSession session;

    /*
     * ⑤投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name="startDate", required=false) String startDate,
                            @RequestParam(name="endDate", required=false) String endDate) throws ParseException  {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findAllReport(startDate, endDate);
        // コメントを全件取得
        List<CommentForm> commentData = commentService.findAllComment();
        // 画面遷移先(「現在のURL」/top )を指定
        mav.setViewName("top");
        // 投稿データオブジェクト(contentData)を保管
        mav.addObject("contents", contentData);
        // コメントデータオブジェクト(commentData)を保管
        mav.addObject("comments", commentData);
        // セッションにデータがあれば、エラーフォームを渡す（なければ新規）
        Object commentFormObj = session.getAttribute("commentModel");
        if (commentFormObj != null) {
            mav.addObject("commentModel", commentFormObj);
            session.removeAttribute("commentModel");
        } else {
            mav.addObject("commentModel", new CommentForm());
        }
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
    public ModelAndView addContent(@Validated @ModelAttribute("formModel") ReportForm reportForm,
                                   BindingResult result) throws ParseException {
        if(result.hasErrors()) {
            //エラー処理
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/new");
            mav.addObject("formModel", reportForm); // 入力内容を保持
            return mav;
        }
        // ReportForm型の変数reportFormを引数として、ReportServiceのsaveReportを実行(投稿をテーブルに格納)
        reportService.saveReport(reportForm);
        // root(⑤)へリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除
     */
    @DeleteMapping("/delete/{id}")
    // @PathVariableでformタグ内のaction属性に記述されてる{}内で指定されたURLパラメータを取得
    // th:action="@{/delete/{id}(id=${content.id})" の{id}のところ
    public ModelAndView deleteContent(@PathVariable Integer id) {
        // reportServiceにidを引数にして渡す。➡ReportRepositoryに渡してDELETE文実行
        reportService.deleteReport(id);
        // root（⑤）へリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 編集画面に遷移
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // テーブルから編集する投稿を取得してくる（idと投稿内容を変数reportへ格納）
        ReportForm report = reportService.editReport(id);
        // 編集する投稿をmavにセット
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * 編集処理
     */
    @PutMapping("/update/{id}")
    // 編集画面から、id および formModel の変数名で入力された投稿内容を受け取る
    public ModelAndView updateContent (@PathVariable Integer id,
                                       @Validated @ModelAttribute("formModel") ReportForm reportForm,
                                       BindingResult result) throws ParseException{
        if(result.hasErrors()) {
            //エラー処理
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/edit");
            mav.addObject("formModel", reportForm); // 入力内容を保持
            return mav;
        }
        // UrlParameterのidを更新するentityにセット
        reportForm.setId(id);
        // 編集した投稿を更新
        reportService.saveReport(reportForm);
        // root⑤へリダイレクト（編集が終わったら、最新の状態を画面表示）
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント投稿処理
     */
    @PostMapping("/addComment")
    public ModelAndView addComment(@Validated @ModelAttribute("commentModel") CommentForm commentForm,
                                   BindingResult result) throws ParseException {
        if(result.hasErrors()) {
            // セッションにエラー情報と入力値を入れる
            // session.setAttribute("commentErrors", result);
            session.setAttribute("commentModel", commentForm);
            return new ModelAndView("redirect:/");
        }
        commentService.saveComment(commentForm);
        return new ModelAndView("redirect:/");
    }


    /*
     * コメント編集画面を表示
     */
    @GetMapping("/editComment/{id}")
    public ModelAndView editComment(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        CommentForm comment = commentService.editComment(id);
        // 編集する投稿をセット
        mav.addObject("commentModel", comment);
        // 画面遷移先を指定
        mav.setViewName("/editComment");
        return mav;
    }

    /*
     * コメント編集処理
     */
    @PutMapping("/updateComment/{id}/{messageId}")
    // 編集画面から、id および formModel の変数名で入力された投稿内容を受け取る
    public ModelAndView updateComment (@PathVariable Integer id, @PathVariable Integer messageId,
                                       @Validated @ModelAttribute("commentModel") CommentForm commentForm,
                                       BindingResult result) throws ParseException {
        if(result.hasErrors()) {
            //エラー処理
            ModelAndView mav = new ModelAndView("/editcomment");
            mav.addObject("commentModel", commentForm); // フォーム内容を表示
            return mav;
        }
        // UrlParameterのidを更新するentityにセット
        commentForm.setId(id);
        // 編集した投稿を更新
        commentService.saveComment(commentForm);

        // 投稿のupdatedDateを更新
        commentForm.setMessageId(messageId);
        reportService.saveReportOnlyUpdatedDate(commentForm);

        // root⑤へリダイレクト（編集が終わったら、最新の状態を画面表示）
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント削除
     */
    @DeleteMapping("/deleteComment/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return new ModelAndView("redirect:/");
    }

}
