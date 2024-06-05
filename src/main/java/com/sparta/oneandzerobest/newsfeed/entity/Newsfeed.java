package com.sparta.oneandzerobest.newsfeed.entity;

import com.sparta.oneandzerobest.s3.entity.Image;
import com.sparta.oneandzerobest.timestamp.TimeStamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.List;
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

    @OneToMany
    @JoinColumn(name = "imageid",referencedColumnName = "id")
    private List<Image> imageList;

    public Newsfeed(Long userid, String content) {
        this.userid = userid;
        this.content = content;
    }

    public void setImage(Image image) {
        this.imageList.add(image);
    }

}
