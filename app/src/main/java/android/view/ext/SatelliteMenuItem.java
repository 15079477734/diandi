package android.view.ext;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Menu Item.
 * <p/>
 * TODO: tell about usage
 *
 * @author Siyamed SINIR
 */
public class SatelliteMenuItem {
    private int id;
    private int imgResourceId;
    private Drawable imgDrawable;
    private ImageView view;
    private ImageView cloneView;
    private Animation outAnimation;
    private Animation inAnimation;
    private Animation clickAnimation;
    private int finalX;
    private int finalY;

    public SatelliteMenuItem(int id, int imgResourceId) {
        this.imgResourceId = imgResourceId;
        this.id = id;
    }

    public SatelliteMenuItem(int id, Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImgResourceId() {
        return imgResourceId;
    }

    public void setImgResourceId(int imgResourceId) {
        this.imgResourceId = imgResourceId;
    }

    public Drawable getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    ImageView getView() {
        return view;
    }

    void setView(ImageView view) {
        this.view = view;
    }

    Animation getInAnimation() {
        return inAnimation;
    }

    void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
    }

    Animation getOutAnimation() {
        return outAnimation;
    }

    void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
    }

    int getFinalX() {
        return finalX;
    }

    void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    int getFinalY() {
        return finalY;
    }

    void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    ImageView getCloneView() {
        return cloneView;
    }

    void setCloneView(ImageView cloneView) {
        this.cloneView = cloneView;
    }

    Animation getClickAnimation() {
        return clickAnimation;
    }

    void setClickAnimation(Animation clickAnim) {
        this.clickAnimation = clickAnim;
    }
}
