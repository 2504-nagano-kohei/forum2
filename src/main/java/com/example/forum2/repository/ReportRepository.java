package com.example.forum2.repository;

import com.example.forum2.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
// ReportRepository が JpaRepository を継承しており、
// findAllメソッドを実行しているため、こちらで特に何か記載する必要はない
// JpaRepositryにはあらかじめいくつかのメソッドが定義されており、SQL文を打つ必要がない。
// findAllで実行されている処理はSQL文の「select * from report;」のようなイメージ
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByCreatedDateBetweenOrderByUpdatedDateDesc(Date start, Date end);

    // updatedDateのみ更新
    @Modifying
    @Transactional
    @Query(value = "UPDATE Report r SET r.updatedDate = :updatedDate WHERE r.id = :id")
    void updateUpdatedDate(@Param("updatedDate") Date updatedDate, @Param("id") Integer id);
}
