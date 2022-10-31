package com.github.jiuzhuan.browser.tab.manager.common.repository;

import com.intellij.ide.util.PropertiesComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * 网站导航栏配置持久化
 *
 * @date 2022/7/1 14:55
 */
public class NavigationTabMap {

    private static final String MAINKEY = "main-";
    private static final String SALVEKEY = "salve-";
    private static final String NAVKEY = "browser-tab-manager-";
    private static final String TITLEKEY = "title-";
    private static final String URLKEY = "url-";

    public static void addMainTab(String title) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + MAINKEY + TITLEKEY);
        if (titles == null) {
            titles = new String[0];
        }
        PropertiesComponent.getInstance().setValues(NAVKEY + MAINKEY + TITLEKEY, ArrayUtils.add(titles, title));
    }

    public static void addSalveTab(String maintitle, String title, String url) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + TITLEKEY + maintitle);
        String[] urls = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + URLKEY + maintitle);
        if (titles == null || urls == null) {
            titles = new String[0];
            urls = new String[0];
        }
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + maintitle, ArrayUtils.add(titles, title));
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + maintitle, ArrayUtils.add(urls, url));
    }

    public static List<String> getMainTitleList() {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + MAINKEY + TITLEKEY);
        if (titles == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(titles);
    }

    public static void moveMainTitleList(Integer source, Integer target) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + MAINKEY + TITLEKEY);
        if (titles == null) return;
        titles = ArrayUtils.insert(target, titles, titles[source]);
        if (target < source) source++;
        titles = ArrayUtils.remove(titles, source);
        PropertiesComponent.getInstance().setValues(NAVKEY + MAINKEY + TITLEKEY, titles);
    }

    public static List<Pair<String, String>> getSalveTabList(String mainTitle) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle);
        String[] urls = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + URLKEY + mainTitle);
        if (titles == null || urls == null) {
            return Collections.emptyList();
        }
        List<Pair<String, String>> list = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            list.add(Pair.of(titles[i], urls[i]));
        }
        return list;
    }

    public static List<Pair<String, String>> moveSalveTabList(String mainTitle, Integer source, Integer target) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle);
        String[] urls = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + URLKEY + mainTitle);
        if (titles == null || urls == null) return Collections.emptyList();
        titles = ArrayUtils.insert(target, titles, titles[source]);
        urls = ArrayUtils.insert(target, urls, urls[source]);
        if (target < source) source++;
        titles = ArrayUtils.remove(titles, source);
        urls = ArrayUtils.remove(urls, source);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle, titles);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + mainTitle, urls);
        List<Pair<String, String>> list = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            list.add(Pair.of(titles[i], urls[i]));
        }
        return list;
    }

    public static void deleteMainTab(String title) {
        String[] values = PropertiesComponent.getInstance().getValues(NAVKEY + MAINKEY + TITLEKEY);
        PropertiesComponent.getInstance().setValues(NAVKEY + MAINKEY + TITLEKEY, ArrayUtils.removeElement(values, title));
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + title, null);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + title, null);
    }

    public static void updateMainTab(String before, String after) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + MAINKEY + TITLEKEY);
        for (int i = 0; i < titles.length; i++) {
            if (Objects.equals(titles[i], before)) titles[i] = after;
        }
        PropertiesComponent.getInstance().setValues(NAVKEY + MAINKEY + TITLEKEY, titles);
        String[] slaveTitles = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + TITLEKEY + before);
        String[] urls = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + URLKEY + before);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + after, slaveTitles);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + after, urls);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + before, null);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + before, null);
    }

    public static void deleteSalveTab(String mainTitle, int index) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle);
        String[] urls = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + URLKEY + mainTitle);
        if (titles == null || urls == null) {
            return;
        }
        // 删除index位置的title和url
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle, ArrayUtils.remove(titles, index));
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + mainTitle, ArrayUtils.remove(urls, index));
    }

    public static void updateSlaveTab(String mainTitle, String beforeTitle, String afterTitle, String afterUrl) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle);
        String[] urls = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + URLKEY + mainTitle);
        for (int i = 0; i < titles.length; i++) {
            if (Objects.equals(titles[i], beforeTitle)) {
                titles[i] = afterTitle;
                urls[i] = afterUrl;
            }
        }
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle, titles);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + mainTitle, urls);
    }
}
