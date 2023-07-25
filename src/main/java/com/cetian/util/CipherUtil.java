/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright [2014] [zangrong CetianTech]
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.KeySpec;

/**
 * @Description
 *
 *
 *
 * @author zangrong
 * @Date 2020-01-20 06:02
 */
@Slf4j
public class CipherUtil {

    public static final String AES = "AES";
    public static final int AES_LENGTH = 128;
    public static final String AES_ECB_PKCS_5_PADDING = "AES/ECB/PKCS5Padding";
    public static final String AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5Padding";
    public static final String AES_CBC_NOPADDING = "AES_256/CBC/NoPadding";
    public static final String MD5 = "MD5";
    public static final String ENCODING_UTF8 = "utf-8";
    private static final String DES_KEY_ALGORITHM = "DESede";

    // AES 密钥 BASE64 只是个样例
    private static String AES_KEY_BASE64 = "KmXwkXWTC2Ky6s/ernuSRA==";
    // AES 密钥
    private static Key AES_KEY = aesKey(AES_KEY_BASE64);

    /**
     * 根据输入的base64编码文本，生成AES密钥
     * @param encodingKey base64编码后的AES密钥
     * @return
     */
    public static Key aesKey(String encodingKey) {
        // 1、通过base64把密钥解码回字节数组
        byte[] keyString = new Base64().decode(encodingKey);
        // 2、根据字节数组生成AES密钥
        Key key = new SecretKeySpec(keyString, AES);
        return key;
    }

    /**
     * 随机生成一个AES密钥，长度128位，生成后用base64编码后返回
     * @return
     */
    public static String aesGenerateKey() {
        String keyString = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            //生成一个128位的随机源,根据传入的字节数组
            keyGenerator.init(AES_LENGTH);
            //产生原始对称密钥
            SecretKey secretKey = keyGenerator.generateKey();
            //获得原始对称密钥的字节数组
            byte[] keyBytes = secretKey.getEncoded();
            Base64 coder = new Base64();
            keyString = coder.encodeAsString(keyBytes);
        }catch (Exception e){
            log.warn("生成AES密钥异常", e);
        }
        return keyString;
    }

    @Slf4j
    public static class AesCipher{

        private Base64 base64 = new Base64();
        private Key key;// 密钥
        private String transformation;
        private IvParameterSpec iv;//偏移量

        public AesCipher transformation(String transformation){
            this.transformation = transformation;
            return this;
        }

        public AesCipher key(Key key){
            this.key = key;
            return this;
        }
        public AesCipher key(String key){
            byte[] keyBytes = base64.decode(key);
            this.key = new SecretKeySpec(keyBytes, AES);
            return this;
        }
        public AesCipher key(byte[] key){
            this.key = new SecretKeySpec(key, AES);
            return this;
        }

        public AesCipher iv(IvParameterSpec iv){
            this.iv = iv;
            return this;
        }
        public AesCipher iv(String iv){
            byte[] ivBytes = base64.decode(iv);
            this.iv = new IvParameterSpec(ivBytes);
            return this;
        }
        public AesCipher iv(byte[] iv){
            this.iv = new IvParameterSpec(iv);
            return this;
        }

        public String decrypt(String cipherText){
            byte[] cipherTextBytes = base64.decode(cipherText);
            return decrypt(cipherTextBytes);
        }
        public String decrypt(byte[] cipherText) {
            try {
                Cipher cipher = Cipher.getInstance(transformation);
                if (this.iv != null){
                    cipher.init(Cipher.DECRYPT_MODE, this.key, this.iv);
                }else{
                    cipher.init(Cipher.DECRYPT_MODE, this.key);
                }
                //将加密并编码后的内容解码成字节数组
                byte[] plainTextBytes = cipher.doFinal(cipherText);
                return new String(plainTextBytes);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        public String encrypt(String plainText){
            return encrypt(plainText.getBytes());
        }
        public String encrypt(byte[] plainText) {
            try {
                Cipher cipher = Cipher.getInstance(transformation);
                if (this.iv != null){
                    cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
                }else{
                    cipher.init(Cipher.ENCRYPT_MODE, this.key);
                }
                // 将明文加密成密文
                byte[] cipherBytes = cipher.doFinal(plainText);
                String cipherText = base64.encodeAsString(cipherBytes);
                return cipherText;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    public static AesCipher aes(){
        return new AesCipher();
    }

    public static String aesDecrypt(String algorithm, String key, IvParameterSpec iv, String cipherText) {
        String plainText = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            Base64 base64 = new Base64();
            byte[] cipherTextBytes = base64.decode(cipherText);
            byte[] keyBytes = base64.decode(key);
            Key decryptKey = new SecretKeySpec(keyBytes, AES);

            //初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY，第三个参数iv
            cipher.init(Cipher.DECRYPT_MODE, decryptKey, iv);
            byte[] plainTextBytes = cipher.doFinal(cipherTextBytes);
            plainText = new String(plainTextBytes);
        } catch (Exception e) {
            log.warn("", e);
        }
        return plainText;
    }

    public static String md5(String text) {
        String cipherText = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            md5.update(text.getBytes());
            byte[] digest = md5.digest();
            cipherText = new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            log.warn("加密异常", e);
            throw new RuntimeException("加密异常");
        }
        return cipherText;
    }

    public static String encrypt3Des(String plainText, String key) {
        String cipherText = null;
        try {
            Cipher cipher = Cipher.getInstance(DES_KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(key));
            byte[] bytes = plainText.getBytes(ENCODING_UTF8);
            byte[] encryptedBytes = cipher.doFinal(bytes);
            cipherText = new String(Base64.encodeBase64(encryptedBytes));
        } catch (Exception e) {
            log.warn("", e);
            throw new RuntimeException("3DES加密异常");
        }
        return cipherText;
    }

    public static byte[] decrypt3Des(String cipherText, String key) {
        byte[] decryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(DES_KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(key));
            byte[] encryptedBytes = Base64.decodeBase64(cipherText);
            decryptedBytes = cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            log.warn("", e);
            throw new RuntimeException("3DES解密异常");
        }
        return decryptedBytes;
    }

    public static SecretKey generateKey(String key) throws Exception{
        KeySpec ks = new DESedeKeySpec(key.getBytes(ENCODING_UTF8));
        SecretKeyFactory skf = SecretKeyFactory.getInstance(DES_KEY_ALGORITHM);
        SecretKey secretKey = skf.generateSecret(ks);
        return secretKey;
    }

}
