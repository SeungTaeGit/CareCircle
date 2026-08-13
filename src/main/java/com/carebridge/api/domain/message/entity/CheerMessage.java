package com.carebridge.api.domain.message.entity;

import com.carebridge.api.domain.senior.entity.Senior;
import com.carebridge.api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheerMessage extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @Column(nullable = false, length = 50)
    private String senderName;

    @Column(nullable = false, length = 20)
    private String messageType;

    @Column(length = 500)
    private String content;

    private String audioUrl;
    private String imageUrl;

    @Column(nullable = false)
    private boolean isRead;

    @Builder
    public CheerMessage(Senior senior, String senderName, String messageType, String content, String audioUrl, String imageUrl) {
        this.senior = senior;
        this.senderName = senderName;
        this.messageType = messageType;
        this.content = content;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}