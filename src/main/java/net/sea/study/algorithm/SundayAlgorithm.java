package net.sea.study.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 字符串匹配算法Sunday
 * @Author: hcw
 * @Date: 2021/1/20 14:18
 */
public class SundayAlgorithm {
    public static class MatchResult {
        private int start;
        private int end;

        @Override
        public String toString() {
            return "MatchResult{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    /**
     * 字符串匹配
     *
     * @param source
     * @param pattern
     * @return
     */
    public List<MatchResult> search(String source, String pattern) {
        char[] destChars = source.toCharArray();
        char[] patternChars = pattern.toCharArray();

        int i = 0;
        int j = 0;
        List<MatchResult> results = new ArrayList<>();
        int sourceLength = source.length();
        int patternLength = pattern.length();

        while (i <= (sourceLength - patternLength + j)) {
            if (destChars[i] != patternChars[j]) {
                if (i == (sourceLength - patternLength + j)) {
                    break;
                }
                int pos = contains(patternChars, destChars[i + patternLength - j]);
                if (pos == -1) {
                    i = i + patternLength + 1 - j;
                } else {
                    i = i + patternLength - pos - j;
                }
                j = 0;
            } else {
                if (j == (patternLength - 1)) {
                    System.out.println("the start pos is " + (i - j) + " the end pos is " + i);
                    MatchResult result = new MatchResult();
                    result.start = i - j;
                    result.end = i;
                    results.add(result);
                    i = i - j + 1;
                    j = 0;
                } else {
                    i++;
                    j++;
                }
            }
        }
        return results;
    }

    private int contains(char[] chars, char target) {
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == target) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SundayAlgorithm sunday = new SundayAlgorithm();
        List<MatchResult> search = sunday.search("asdqwe12sad21wqqwe", "qwe");
        search.forEach(System.out::println);
    }

}
