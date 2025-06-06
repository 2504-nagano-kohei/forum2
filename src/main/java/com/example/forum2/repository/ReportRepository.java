package com.example.forum2.repository;

import com.example.forum2.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// ReportRepository が JpaRepository を継承しており、
// findAllメソッドを実行しているため、こちらで特に何か記載する必要はない
// JpaRepositryにはあらかじめいくつかのメソッドが定義されており、SQL文を打つ必要がない。
// findAllで実行されている処理はSQL文の「select * from report;」のようなイメージ
public interface ReportRepository extends JpaRepository<Report, Integer> {
}
