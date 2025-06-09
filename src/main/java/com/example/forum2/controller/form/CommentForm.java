// サーバー側：formの作成
// Viewへの入出力時に使用するJavaBeansのような入れ物
package com.example.forum2.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentForm {

    private int id;
    private String comment;
    private int messageId;
    private Date createdDate;
    private Date updatedDate;
}