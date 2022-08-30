package com.github.jiuzhuan.browser.tab.manager.common.repository;

import com.intellij.ide.util.PropertiesComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Õ¯’æµº∫Ω¿∏≈‰÷√≥÷æ√ªØ
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

    public static void deleteMainTab(String title) {
        String[] values = PropertiesComponent.getInstance().getValues(NAVKEY + MAINKEY + TITLEKEY);
        PropertiesComponent.getInstance().setValues(NAVKEY + MAINKEY + TITLEKEY, ArrayUtils.removeElement(values, title));
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + title, null);
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + title, null);
    }

    public static void deleteSalveTab(String mainTitle, int index) {
        String[] titles = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle);
        String[] urls = PropertiesComponent.getInstance().getValues(NAVKEY + SALVEKEY + URLKEY + mainTitle);
        if (titles == null || urls == null) {
            return;
        }
        // …æ≥˝indexŒª÷√µƒtitle∫Õurl
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + TITLEKEY + mainTitle, ArrayUtils.remove(titles, index));
        PropertiesComponent.getInstance().setValues(NAVKEY + SALVEKEY + URLKEY + mainTitle, ArrayUtils.remove(urls, index));
    }
}
