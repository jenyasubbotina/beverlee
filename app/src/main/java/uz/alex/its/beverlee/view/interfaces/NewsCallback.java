package uz.alex.its.beverlee.view.interfaces;

import uz.alex.its.beverlee.model.news.NewsModel.News;

public interface NewsCallback {
    void expandNewsItem(final int position);
    void onNewsSelected(final int position, final News news);
}
