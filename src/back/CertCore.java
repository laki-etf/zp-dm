package back;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

public class CertCore {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private KeyStore keyStore;

    public CertCore() {
        try {
            keyStore = null;
            keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private KeyPair generateRSAKeyPair(int keySize)
            throws NoSuchProviderException, NoSuchAlgorithmException {
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");

        kpGen.initialize(keySize, new SecureRandom());

        return kpGen.generateKeyPair();
    }

    private X500Name buildX500Name(String commonName,
            String organizationalUnit, String organizationalName,
            String localityName, String stateName, String countryName,
            String emailAddress) {
        X500NameBuilder caBuilder = new X500NameBuilder();

        caBuilder.addRDN(BCStyle.CN, commonName);
        caBuilder.addRDN(BCStyle.OU, organizationalUnit);
        caBuilder.addRDN(BCStyle.O, organizationalName);
        caBuilder.addRDN(BCStyle.L, localityName);
        caBuilder.addRDN(BCStyle.ST, stateName);
        caBuilder.addRDN(BCStyle.C, countryName);
        caBuilder.addRDN(BCStyle.E, emailAddress);

        return caBuilder.build();
    }
    
    public HashMap<String, String> readX500Name(X500Name x500name) {
        HashMap<String, String> r = new HashMap<String, String>();
        
        r.put("commonName", x500name.getRDNs(BCStyle.CN)[0].toString());
        r.put("organizationalUnit", x500name.getRDNs(BCStyle.OU)[0].toString());
        r.put("organizationalName", x500name.getRDNs(BCStyle.O)[0].toString());
        r.put("localityName", x500name.getRDNs(BCStyle.L)[0].toString());
        r.put("stateName", x500name.getRDNs(BCStyle.ST)[0].toString());
        r.put("countryName", x500name.getRDNs(BCStyle.C)[0].toString());
        r.put("emailAddress", x500name.getRDNs(BCStyle.E)[0].toString());

        return r;
    }

    //generisanje para kljuceva za kriptovanje pri import/export
    private SecretKeySpec buildSecretKeySpec(String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(),
                "someSaltyString123".getBytes(), 65536, 128);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public void generateEmptyKeyStore(String path, String password) {
        try {
            File file = new File(path);

            SecretKeySpec secretKeySpec = buildSecretKeySpec(password);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    new FileOutputStream(file), cipher);

            keyStore.store(cipherOutputStream, password.toCharArray());

            cipherOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDefaultKeyStore(String path, String password) {
        try {
            File file = new File(path);

            SecretKeySpec secretKeySpec = buildSecretKeySpec(password);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new FileInputStream(file), cipher);

            keyStore.load(cipherInputStream, password.toCharArray());

            cipherInputStream.close();

            Enumeration<String> enumeration = keyStore.aliases();

            while (enumeration.hasMoreElements()) {
                String commonName = (String) enumeration.nextElement();
                X509Certificate certificate = (X509Certificate) keyStore
                        .getCertificate(commonName);
                PublicKey publicKey = certificate.getPublicKey();
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                        commonName, null);
                X500Name x500name = new JcaX509CertificateHolder(certificate)
                        .getSubject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO extenzije
    public void generatePairOfKeys(Integer keySize, Date dateFrom, Date dateTo,
            BigInteger serialNumber, String commonName,
            String organizationalUnit, String organizationalName,
            String localityName, String stateName, String countryName,
            String emailAddress) {
        try {
            KeyPair keyPair = generateRSAKeyPair(keySize);
            ContentSigner selfSigner = new JcaContentSignerBuilder(
                    "SHA1WithRSA").build(keyPair.getPrivate());

            X500Name x500Name = buildX500Name(commonName, organizationalUnit,
                    organizationalName, localityName, stateName, countryName,
                    emailAddress);

            X509v3CertificateBuilder v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                    x500Name, serialNumber, dateFrom, dateTo, x500Name,
                    keyPair.getPublic());

            X509CertificateHolder certificateHolder = v3CertificateBuilder
                    .build(selfSigner);
            X509Certificate certificate = new JcaX509CertificateConverter()
                    .setProvider("BC").getCertificate(certificateHolder);

            keyStore.setCertificateEntry(commonName, certificate);
            keyStore.setKeyEntry(commonName, keyPair.getPrivate(), null,
                    new X509Certificate[] { certificate });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PKCS10CertificationRequest generateCSR(String commonName) {
        try {

            X509Certificate certificate = (X509Certificate) keyStore
                    .getCertificate(commonName);
            PublicKey publicKey = certificate.getPublicKey();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(commonName,
                    null);
            X500Name subject = new JcaX509CertificateHolder(certificate)
                    .getSubject();
            PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(
                    subject, publicKey);

            ContentSigner contentSigner = new JcaContentSignerBuilder(
                    "SHA1WithRSA").build(privateKey);

            return csrBuilder.build(contentSigner);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void importKeyStoreWithAES(String path, String password) {
        try {
            KeyStore importKeyStore = KeyStore.getInstance("PKCS12", "BC");

            SecretKeySpec secretKeySpec = buildSecretKeySpec(password);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

            File file = new File(path);

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new FileInputStream(file), cipher);

            importKeyStore.load(cipherInputStream, password.toCharArray());

            cipherInputStream.close();

            Enumeration<String> enumeration = importKeyStore.aliases();

            while (enumeration.hasMoreElements()) {
                String commonName = (String) enumeration.nextElement();
                X509Certificate certificate = (X509Certificate) importKeyStore
                        .getCertificate(commonName);
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                        commonName, null);

                //dodavanje u defaultKeyStore
                keyStore.setCertificateEntry(commonName, certificate);
                keyStore.setKeyEntry(commonName, privateKey, null,
                        new X509Certificate[] { certificate });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO proveriti pamcenje keyStore bez sifre
    public void importKeyStoreNoAES(String path, String password) {
        try {
            KeyStore importKeyStore = KeyStore.getInstance("PKCS12", "BC");

            File file = new File(path);

            FileInputStream fileInputStream = new FileInputStream(file);

            importKeyStore.load(fileInputStream, password.toCharArray());

            fileInputStream.close();

            Enumeration<String> enumeration = importKeyStore.aliases();

            while (enumeration.hasMoreElements()) {
                String commonName = (String) enumeration.nextElement();
                X509Certificate certificate = (X509Certificate) importKeyStore
                        .getCertificate(commonName);
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                        commonName, null);

                //dodavanje u defaultKeyStore
                keyStore.setCertificateEntry(commonName, certificate);
                keyStore.setKeyEntry(commonName, privateKey, null,
                        new X509Certificate[] { certificate });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

}