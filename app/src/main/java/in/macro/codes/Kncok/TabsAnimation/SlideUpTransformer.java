package in.macro.codes.Kncok.TabsAnimation;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class SlideUpTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (-1 < position && position < 0) {
            float scaleFactor = 1 - Math.abs(position) * 0.1f;
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horizontalMargin - verticalMargin / 2);
            } else {
                view.setTranslationX(-horizontalMargin + verticalMargin / 2);
            }
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }

        view.setTranslationX(view.getWidth() * -position);

        if (position > 0) {
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);
        }
    }
}