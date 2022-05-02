package nl.mwsoft.www.chatster.modelLayer.helper.groupE2E;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ChatsterGroupE2EHelper {

    public ChatsterGroupE2EHelper() {
    }

    public List<Curve25519KeyPair> generateOneTimeGroupKeys(int amount){
        List<Curve25519KeyPair> oneTimeKeys = new ArrayList<>();

        for(int i = 0; i < amount; i++){
            Curve25519KeyPair oneTimeKey = Curve25519.getInstance(Curve25519.BEST).generateKeyPair();
            oneTimeKeys.add(oneTimeKey);
        }

        return oneTimeKeys;
    }

    public List<String> generateUUIDS(int amount){
        List<String> uuids = new ArrayList<>();

        for(int i = 0; i < amount; i++){
            String uuid = UUID.randomUUID().toString().replace("-","");
            uuids.add(uuid);
        }

        return uuids;
    }

    public String jsonifyOneTimeGroupKeys(List<Curve25519KeyPair> keyPairs, List<String> uuids, long userId, String groupChatId) {
        JsonArray oneTimeGroupPreKeys = new JsonArray();

        int counter = 0;

        for (Curve25519KeyPair keyPair: keyPairs) {
            Gson gson = new Gson();
            JsonElement u_id = gson.toJsonTree(userId);
            JsonElement g_id = gson.toJsonTree(groupChatId);
            JsonElement uuid = gson.toJsonTree(uuids.get(counter));
            JsonElement otpk = gson.toJsonTree(keyPair.getPublicKey());
            JsonObject oneTimeGroupPreKey = new JsonObject();
            oneTimeGroupPreKey.add("user_id", u_id);
            oneTimeGroupPreKey.add("group_id", g_id);
            oneTimeGroupPreKey.add("group_one_time_pre_key_pair_pbk", otpk);
            oneTimeGroupPreKey.add("group_one_time_pre_key_pair_uuid", uuid);
            oneTimeGroupPreKeys.add(oneTimeGroupPreKey);

            counter++;
        }

        JsonObject objMain = new JsonObject();
        objMain.add("oneTimeGroupPreKeyPairPbks",oneTimeGroupPreKeys);

        return objMain.toString();
    }

    public byte[] generateSignature(byte[] senderPrivateKey, byte[] encryptedMessage){
        return Curve25519.getInstance(Curve25519.BEST).
                calculateSignature(senderPrivateKey, encryptedMessage);
    }

    public boolean verifySignature(byte[] senderPublicKey, byte[] encryptedMessage, byte[] signature){
        return Curve25519.getInstance(Curve25519.BEST).
                verifySignature(senderPublicKey, encryptedMessage, signature);
    }

    public byte[] generateEncryptSharedSecret(byte[] receiverPublicKey, byte[] senderPrivateKey){
        return Curve25519.getInstance(Curve25519.BEST).
                calculateAgreement(receiverPublicKey, senderPrivateKey);
    }

    public byte[] generateDecryptSharedSecret(byte[] senderPublicKey, byte[] receiverPrivateKey){
        return Curve25519.getInstance(Curve25519.BEST).
                calculateAgreement(senderPublicKey, receiverPrivateKey);
    }

    public byte[] encrypt(byte[] message, byte[] sharedSecret){
        SecureRandom secureRandom = new SecureRandom();

        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);

        SecretKey secretKey = new SecretKeySpec(sharedSecret, "AES");

        final Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] cipherText = cipher.doFinal(message);

            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            byte[] cipherMessage = byteBuffer.array();
            return cipherMessage;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (java.security.InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] decrypt(byte[] cipherMessage, byte[] sharedSecret){
        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
        int ivLength = byteBuffer.getInt();
        if(ivLength < 12 || ivLength >= 16) { // check input parameter
            throw new IllegalArgumentException("invalid iv length");
        }
        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        final Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(sharedSecret, "AES"),
                    new GCMParameterSpec(128, iv));
            byte[] plainText= cipher.doFinal(cipherText);
            return plainText;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (java.security.InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] appendHMACSignatureAndPKTo(byte[] encryptedMessage, byte[] hmac, byte[] signature, byte[] userPublicKey){
        ByteBuffer byteBuffer = ByteBuffer.allocate(encryptedMessage.length + hmac.length + signature.length + userPublicKey.length);
        byteBuffer.put(encryptedMessage);
        byteBuffer.put(hmac);
        byteBuffer.put(signature);
        byteBuffer.put(userPublicKey);
        byte[] messageWithSignature = byteBuffer.array();

        return messageWithSignature;
    }

    public byte[] getMessageFrom(byte[] messageWithHMACSignatureAndPK){
        return Arrays.copyOfRange(messageWithHMACSignatureAndPK, 0,
                ((messageWithHMACSignatureAndPK.length) - 160));
    }

    public byte[] getHMACFrom(byte[] messageWithHMACSignatureAndPK){
        return Arrays.copyOfRange(messageWithHMACSignatureAndPK, ((messageWithHMACSignatureAndPK.length) - 160),
                (messageWithHMACSignatureAndPK.length - 96));
    }

    public byte[] getSignatureFrom(byte[] messageWithHMACSignatureAndPK){
        return Arrays.copyOfRange(messageWithHMACSignatureAndPK, ((messageWithHMACSignatureAndPK.length) - 96),
                (messageWithHMACSignatureAndPK.length - 32));
    }

    public byte[] getPublicKeyFrom(byte[] messageWithHMACSignatureAndPK){
        return Arrays.copyOfRange(messageWithHMACSignatureAndPK, ((messageWithHMACSignatureAndPK.length) - 32),
                (messageWithHMACSignatureAndPK.length));
    }

    public String messageWithSignatureHMACAndPKToString(byte[] messageWithSignature){
        return new String(Base64.encode(messageWithSignature,0));
    }

    public byte[] messageWithSignatureHMACAndPKToByteArray(String messageWithSignatureHMACAndPK){
        return Base64.decode(messageWithSignatureHMACAndPK.getBytes(),0);
    }

    public String bytesToHex(byte[] bytes) {
        final  char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] messageToHMAC(byte[] message, byte[] sharedSecret){
        try{
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(sharedSecret, "HmacSHA512");
            sha512_HMAC.init(keySpec);

            return sha512_HMAC.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        } finally{
            System.out.println("Done");
        }

        return null;
    }
}
