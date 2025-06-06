// ③サーバー側：formの作成
// Viewへの入出力時に使用するJavaBeansのような入れ物
package com.example.forum2.controller.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportForm {

    private int id;
    private String content;
}
