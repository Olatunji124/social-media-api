package com.assessment.socialmedia.utils;



import java.util.regex.Pattern;

public class JStringUtils {
    private static final Pattern alphaNumericPattern = Pattern.compile("^[a-zA-Z0-9]*$");
    private static final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    private static final Pattern alphanumericAndSpecialCharacterMatchPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])[\\p{ASCII}&&[\\S]]{8,}$");
    private static final Pattern specialCharacterMatchPattern = Pattern.compile("[^A-Za-z0-9]");

    public static boolean isAlphaNumeric(String s) {
        return alphaNumericPattern.matcher(s).find();
    }
    public static boolean isEmailValid(String email) {
        return emailPattern.matcher(email).matches();
    }
    public static boolean isAlphaNumericAndSpecialCharacter(String value) {
        return alphanumericAndSpecialCharacterMatchPattern.matcher(value).matches();
    }
    public static boolean hasSpecialCharacter(String value) {
        return specialCharacterMatchPattern.matcher(value).find();
    }

}
