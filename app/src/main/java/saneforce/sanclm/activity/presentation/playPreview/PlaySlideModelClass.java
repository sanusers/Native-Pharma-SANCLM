package saneforce.sanclm.activity.presentation.playPreview;

public class PlaySlideModelClass {

    private int featured_image;

    public PlaySlideModelClass (int hero) {
        this.featured_image = hero;
    }

    public int getFeatured_image() {
        return featured_image;
    }

    public void setFeatured_image(int featured_image) {
        this.featured_image = featured_image;
    }

}