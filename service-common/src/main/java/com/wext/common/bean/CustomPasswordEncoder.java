package com.wext.common.bean;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        try {
            return new String(MessageDigest.getInstance("SHA-256")
                    .digest(charSequence.toString().getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        try {
            String pw = new String(MessageDigest.getInstance("SHA-256")
                    .digest(charSequence.toString().getBytes()));
            return pw.equals(s);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }

    }
}
