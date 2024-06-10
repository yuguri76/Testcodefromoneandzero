package com.sparta.oneandzerobest.newsfeed.entity;

import com.sparta.oneandzerobest.newsfeed_like.entity.NewsfeedLike;
import com.sparta.oneandzerobest.s3.entity.Image;
import com.sparta.oneandzerobest.timestamp.TimeStamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
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
    private int likeCount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> imageList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsfeedLike> newsfeedLikeList = new ArrayList<>();

    public Newsfeed(Long userid, String content) {
        this.userid = userid;
        this.content = content;
    }

    public void setImage(Image image) {
        this.imageList.add(image);

    }

    public void setNewsfeedLike(NewsfeedLike newsfeedLike) {
        this.newsfeedLikeList.add(newsfeedLike);
        this.likeCount++;
    }

    public void removeNewsfeedLike(NewsfeedLike newsfeedLike) {
        this.newsfeedLikeList.remove(newsfeedLike);
        this.likeCount--;
    }
}
