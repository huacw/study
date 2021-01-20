package net.sea.study.algorithm;

/**
 * @Description: 判断字符串是否为回文字符串
 * @Author: hcw
 * @Date: 2021/1/20 16:43
 */
public class Palindrome {
    public static void main(String[] args) {
        System.out.println(isPalindrome("A man, a plan, a canal: Panama"));
        System.out.println(isPalindrome("race a car"));
        System.out.println(isPalindrome("abc123321cba"));
    }

    public static boolean isPalindrome(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        //剔除非字母、数字的字符
        String newStr = str.replaceAll("[^0-9|a-z|A-Z]", "");
        if (str.isEmpty()) {
            return false;
        }
        char[] chars = newStr.toCharArray();
        int size = chars.length;
        boolean flag = true;
        for (int i = 0, j = size - 1; i < size / 2; i++, j--) {
            flag = flag && Character.toLowerCase(chars[i]) == Character.toLowerCase(chars[j]);
        }
        return flag;
    }
}
