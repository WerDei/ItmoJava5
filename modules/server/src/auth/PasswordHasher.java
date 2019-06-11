package net.werdei.talechars.server.auth;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordHasher
{
    static final int hashLength = 512;

    public static String hash(String password)
    {
        String hash;
        ByteBuffer buf = ByteBuffer.allocate(hashLength/8);
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(md.digest());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
