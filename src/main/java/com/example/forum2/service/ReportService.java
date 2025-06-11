package com.example.forum2.service;

import com.example.forum2.controller.form.CommentForm;
import com.example.forum2.controller.form.ReportForm;
import com.example.forum2.repository.ReportRepository;
import com.example.forum2.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
//     * ⑥レコード全件取得処理
     */
    public List<ReportForm> findAllReport(String startDate, String endDate) throws ParseException {
        //ここでデフォルトのstartDateとendDateを設定したい
        Date nowDate = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dafaultStartDate = "2020-01-01 00:00:00";
        String dafaultEndDate = sdFormat.format(nowDate);

        //startDateが未入力なら2020-01-01 00:00:00を代入、endDateが未入力なら現在日時を代入
        if(hasText(startDate)) {
            startDate += " 00:00:00";
        } else {
            startDate = dafaultStartDate;
        }
        if (hasText(endDate)){
            endDate += " 23:59:59";
        } else {
            endDate = dafaultEndDate;
        }

        //Date型➡String型に変換
        Date start = sdFormat.parse(startDate);
        Date end = sdFormat.parse(endDate);

        // reportRepositoryのfindAllを実行
        List<Report> results = reportRepository.findByCreatedDateBetweenOrderByUpdatedDateDesc(start, end);
        // その値をsetReportFormメソッドでEntity→Formに詰め直し
        List<ReportForm> reports = setReportForm(results);
        // Controllerに戻している。
        // Entityはデータアクセス時の入れ物、FormはViewへの入出力時に使用する入れ物と役割を分けているため
        return reports;
    }
    /*
     * ⑥DBから取得したデータをFormに設定
     */
    private List<ReportForm> setReportForm(List<Report> results) {
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            Report result = results.get(i);
            report.setId(result.getId());
            report.setContent(result.getContent());
            report.setCreatedDate(result.getCreatedDate());
            report.setUpdatedDate(result.getUpdatedDate());
            reports.add(report);
        }
        return reports;
    }


    /*
     * ⑧レコード追加
     */
    public void saveReport(ReportForm reqReport) throws ParseException{
        Report saveReport = setReportEntity(reqReport);
        // ReportRepositoryのsaveメソッドはテーブルに新規投稿をinsertするような処理や、update文のような処理も兼ね備えている
        // 「id が既に存在するかどうかをDBから確認➡存在する id であればmergeメソッド（SQL文でいうupdateの処理）が行われて、
        // 存在しないidであればpersistメソッド（SQL文でいう insert の処理）が行われる」
        reportRepository.save(saveReport);
    }

    /*
     * ⑧リクエストから取得した情報をForm（view側）->Entity(DB側)に詰め替え
     */
    private Report setReportEntity(ReportForm reqReport) throws ParseException{
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        report.setCreatedDate(reqReport.getCreatedDate());
        //updatedDateに現在日をセットしてCommentRepositoryでUPDATE文を発行したときにnullにならないようにする
        // 現在日時の取得
        Date nowDate = new Date();
        // フォーマットを決める
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String uDate = sdFormat.format(nowDate);
        Date updatedDate = sdFormat.parse(uDate);
        report.setUpdatedDate(updatedDate);
        return report;
    }

    /*
     * 投稿削除
     */
    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }


    /*
     * 編集する投稿のレコードを1件取得
     */
    public ReportForm editReport(Integer id) {
        // Report型のリストを用意し
        List<Report> results = new ArrayList<>();
        // reportRepositoryのfindById(id)メソッドで抽出しで詰める
        results.add((Report) reportRepository.findById(id).orElse(null));
        // 詰めたものをsetReportFormメソッドでEntityに詰め替え、ReportForm型のリストに詰める
        List<ReportForm> reports = setReportForm(results);
        return reports.get(0);
    }

    // コメントの編集と共にreportテーブルのupdated_dateのみ更新　saveReportOnlyUpdatedDate
    public void saveReportOnlyUpdatedDate(CommentForm comment) throws ParseException{
        Report saveReport = setReportEntityOnlyUpdatedDate(comment);
        reportRepository.updateUpdatedDate(saveReport.getUpdatedDate(), saveReport.getId());
    }

    // CommentFormをRepotのEntityに詰めている
    private Report setReportEntityOnlyUpdatedDate(CommentForm comment) throws ParseException{
        Report report = new Report();
        report.setId(comment.getMessageId());

        Date nowDate = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String uDate = sdFormat.format(nowDate);
        Date updatedDate = sdFormat.parse(uDate);
        report.setUpdatedDate(updatedDate);
        return report;
    }

}
