package io.github.milyor.qrcodeservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_code_records")
public class QRCodeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 1024)
    private String content;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false, length = 1)
    private String correctionLevel;

    @Column(nullable = false, length = 10)
    private String type;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public QRCodeRecord() {
    }

    public QRCodeRecord(String content, Integer size, String correctionLevel, String type, LocalDateTime createdAt) {
        this.content = content;
        this.size = size;
        this.correctionLevel = correctionLevel;
        this.type = type;
        this.createdAt = createdAt;
    }



    // Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getCorrectionLevel() {
        return correctionLevel;
    }

    public void setCorrectionLevel(String correctionLevel) {
        this.correctionLevel = correctionLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
