//package uz.alex.its.beverlee.view.views;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.drawable.GradientDrawable;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import androidx.core.content.ContextCompat;
//
//import uz.alex.its.beverlee.R;
//
//public class ProgressButton extends androidx.appcompat.widget.AppCompatButton {
//    private enum State {
//        PROGRESS, IDLE
//    }
//
//    private void init(Context context) {
//        mGradientDrawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.button_shape);
//
//        setBackground(mGradientDrawable);
//    }
//
//
//    public ProgressButton(Context context) {
//        super(context);
//    }
//
//    public ProgressButton(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    public void startAnimation() {
//        if(mState != State.IDLE){
//            return;
//        }
//
//        int initialWidth = getWidth();
//        int initialHeight = getHeight();
//
//        int initialCornerRadius = 0;
//        int finalCornerRadius = 1000;
//
//        mState = State.PROGRESS;
//        mIsMorphingInProgress = true;
//        this.setText(null);
//        setClickable(false);
//
//        int toWidth = 300; //some random value...
//        int toHeight = toWidth; //make it a perfect circle
//
//        ObjectAnimator cornerAnimation = ObjectAnimator.ofFloat(mGradientDrawable, "cornerRadius", initialCornerRadius, finalCornerRadius);
//
//        ValueAnimator widthAnimation = ValueAnimator.ofInt(initialWidth, toWidth);
//        widthAnimation.addUpdateListener(valueAnimator -> {
//            int val = (Integer) valueAnimator.getAnimatedValue();
//            ViewGroup.LayoutParams layoutParams = getLayoutParams();
//            layoutParams.width = val;
//            setLayoutParams(layoutParams);
//        });
//
//        ValueAnimator heightAnimation = ValueAnimator.ofInt(initialHeight, toHeight);
//        heightAnimation.addUpdateListener(valueAnimator -> {
//            int val = (Integer) valueAnimator.getAnimatedValue();
//            ViewGroup.LayoutParams layoutParams = getLayoutParams();
//            layoutParams.height = val;
//            setLayoutParams(layoutParams);
//        });
//
//        mMorphingAnimatorSet = new AnimatorSet();
//        mMorphingAnimatorSet.setDuration(300);
//        mMorphingAnimatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
//        mMorphingAnimatorSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mIsMorphingInProgress = false;
//            }
//        });
//        mMorphingAnimatorSet.start();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (mState == State.PROGRESS && !mIsMorphingInProgress) {
//            drawIndeterminateProgress(canvas);
//        }
//    }
//
//    private void drawIndeterminateProgress(Canvas canvas) {
//        if (mAnimatedDrawable == null || !mAnimatedDrawable.isRunning()) {
//
//            int arcWidth = 15;
//
//            mAnimatedDrawable = new CircularAnimatedDrawable(this, arcWidth, Color.WHITE);
//
//            int offset = (getWidth() - getHeight()) / 2;
//
//            int left = offset;
//            int right = getWidth() - offset;
//            int bottom = getHeight();
//            int top = 0;
//
//            mAnimatedDrawable.setBounds(left, top, right, bottom);
//            mAnimatedDrawable.setCallback(this);
//            mAnimatedDrawable.start();
//        } else {
//            mAnimatedDrawable.draw(canvas);
//        }
//    }
//}
