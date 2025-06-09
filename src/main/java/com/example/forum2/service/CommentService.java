package com.example.forum2.service;

import com.example.forum2.controller.form.CommentForm;
import com.example.forum2.repository.CommentRepository;
import com.example.forum2.repository.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    /*
     * ⑥レコード全件取得処理
     */
    public List<CommentForm> findAllComment() {
        // commentRepositoryのfindAllを実行
        List<Comment> results = commentRepository.findAllByOrderByIdDesc();
        // その値をsetReportFormメソッドでEntity→Formに詰め直し
        List<CommentForm> comments = setCommentForm(results);
        // Controllerに戻している。
        // Entityはデータアクセス時の入れ物、FormはViewへの入出力時に使用する入れ物と役割を分けているため
        return comments;
    }

    /*
     * ⑥DBから取得したデータをFormに設定
     */
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentForm comment = new CommentForm();
            Comment result = results.get(i);
            comment.setId(result.getId());
            comment.setComment(result.getComment());
            comment.setMessageId(result.getMessageId());
            comments.add(comment);
        }
        return comments;
    }

    /*
     * ⑧レコード追加
     */
    public void saveComment(CommentForm reqComment) {
        Comment saveComment = setCommentEntity(reqComment);
        // CommentRepositoryのsaveメソッドはテーブルに新規投稿をinsertするような処理になっている
        // その他にもsaveメソッドには、update文のような処理も兼ね備えている
        // 「id が既に存在するかどうかをDBから確認➡存在する id であればmergeメソッド（SQL文でいうupdateの処理）が行われて、
        // 存在しないidであればpersistメソッド（SQL文でいう insert の処理）が行われる」
        commentRepository.save(saveComment);
    }

    /*
     * ⑧リクエストから取得した情報をForm（view側）->Entity(DB側)に詰め替え
     */
    private Comment setCommentEntity(CommentForm reqComment) {
        Comment comment = new Comment();
        comment.setId(reqComment.getId());
        comment.setComment(reqComment.getComment());
        comment.setMessageId(reqComment.getMessageId());
        return comment;
    }

    /*
     * レコード1件取得 コメント編集
     */
    public CommentForm editComment(Integer id) {
        List<Comment> results = new ArrayList<>();
        results.add((Comment) commentRepository.findById(id).orElse(null));
        List<CommentForm> comments = setCommentForm(results);
        return comments.get(0);
    }
}
