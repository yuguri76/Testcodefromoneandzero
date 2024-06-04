package com.sparta.oneandzerobest.newsfeed.entity;

import com.sparta.oneandzerobest.timestamp.TimeStamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Newsfeed extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userid;
    private String content;

    public Newsfeed(Long userid, String content) {
        this.userid = userid;
        this.content = content;
    }

}
