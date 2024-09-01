/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.module.Module;
import java.lang.invoke.LambdaMetafactory;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class StringUtils {


    public static String getLongestModeName(List<String> listOfWords) {
        String longestWord = null;
        for (String word : listOfWords) {
            if (longestWord != null && word.length() <= longestWord.length()) continue;
            longestWord = word;
        }
        return longestWord != null ? longestWord : "";
    }

    public static String b64(Object o) {
        return Base64.getEncoder().encodeToString(String.valueOf(o).getBytes());
    }

    private static /* synthetic */ Integer lambda$findLongestModuleName$0(Module module) {
        return (module.name + (module.getTag() != null ? " " + module.getTag() : "")).length();
    }
}

