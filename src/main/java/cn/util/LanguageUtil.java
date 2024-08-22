package cn.util;

/**
 * @Description: language
 * @Author haodd
 * @Date 2024/8/9 17:35
 * @Version 1.0
 */
public class LanguageUtil {

    /**
     * 判断给定的字符串是否包含中文字符。
     *
     * 这个方法遍历字符串中的每个字符，并判断其是否属于
     * Unicode CJK（中日韩统一表意文字）字符块。如果字符串中
     * 存在一个或多个这样的字符，则认为该字符串包含中文。
     *
     * @param text 要检查的字符串
     * @return 如果字符串包含中文字符，则返回 true；否则返回 false
     */
    public static boolean isChinese(String text) {
        for (char c : text.toCharArray()) {
            // 判断字符是否属于CJK（中日韩统一表意文字）字符块
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                return true;  // 如果找到中文字符，返回 true
            }
        }
        return false;  // 如果未找到中文字符，返回 false
    }


}
