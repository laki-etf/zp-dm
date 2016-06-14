package back;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.io.CipherOutputStream;
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

public class CertCore {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private KeyStore keyStore;
    private String aliasCA;

    public CertCore() {
        try {
            keyStore = null;
            aliasCA = null;
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

    //TODO ovo ne valja plus extenzije
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

    public void generateDefaultKeyStore(String path, String password,
            String aliasCA) {
        try {
            //stvaranje sertifikata kojim potpisujemo
            Date referenceDate = new Date(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.setTime(referenceDate);
            c.add(Calendar.MONTH, -6);
            Date dateFrom = c.getTime();
            c.add(Calendar.MONTH, 36);
            Date dateTo = c.getTime();
            generatePairOfKeys(aliasCA, 1024, dateFrom, dateTo, BigInteger.ONE,
                    aliasCA + "Name", "organizationalUnit",
                    "organizationalName", "localityName", "stateName",
                    "countryName", aliasCA + "@etf.rs");
            this.aliasCA = aliasCA;

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

    public void loadDefaultKeyStore(String path, String password, String aliasCA) {
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

            boolean existsCA = false;
            while (enumeration.hasMoreElements()) {
                String alias = (String) enumeration.nextElement();
                //postoji li sertifikat kojim bi potpisivali
                if(alias.equals(aliasCA)) {
                    existsCA = true;
                    this.aliasCA = aliasCA;
                }
            }
            //ako ne postoji sertifikat kojim potpisujemo
            if(!existsCA) {
                //stvaranje sertifikata kojim potpisujemo
                Date referenceDate = new Date(System.currentTimeMillis());
                Calendar c = Calendar.getInstance();
                c.setTime(referenceDate);
                c.add(Calendar.MONTH, -6);
                Date dateFrom = c.getTime();
                c.add(Calendar.MONTH, 12);
                Date dateTo = c.getTime();
                generatePairOfKeys(aliasCA, 1024, dateFrom, dateTo,
                        BigInteger.ONE, aliasCA + "Name", "organizationalUnit",
                        "organizationalName", "localityName", "stateName",
                        "countryName", aliasCA + "@etf.rs");
                this.aliasCA = aliasCA;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO extenzije
    public void generatePairOfKeys(String alias, Integer keySize,
            Date dateFrom, Date dateTo, BigInteger serialNumber,
            String commonName, String organizationalUnit,
            String organizationalName, String localityName, String stateName,
            String countryName, String emailAddress) {
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

            keyStore.setCertificateEntry(alias, certificate);
            keyStore.setKeyEntry(alias, keyPair.getPrivate(), null,
                    new X509Certificate[] { certificate });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                String alias = (String) enumeration.nextElement();
                X509Certificate certificate = (X509Certificate) importKeyStore
                        .getCertificate(alias);
                PrivateKey privateKey = (PrivateKey) importKeyStore.getKey(
                        alias, null);

                if(keyStore.containsAlias(alias)) {
                    keyStore.deleteEntry(alias);
                    System.out.println("Alias: " + alias
                            + " pregazen novim pri import.");
                }
                //dodavanje u defaultKeyStore
                keyStore.setCertificateEntry(alias, certificate);
                keyStore.setKeyEntry(alias, privateKey, null,
                        new X509Certificate[] { certificate });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importKeyStoreNoAES(String path, String password) {
        try {
            KeyStore importKeyStore = KeyStore.getInstance("PKCS12", "BC");

            File file = new File(path);

            FileInputStream fileInputStream = new FileInputStream(file);

            importKeyStore.load(fileInputStream, password.toCharArray());

            fileInputStream.close();

            Enumeration<String> enumeration = importKeyStore.aliases();

            while (enumeration.hasMoreElements()) {
                String alias = (String) enumeration.nextElement();
                X509Certificate certificate = (X509Certificate) importKeyStore
                        .getCertificate(alias);
                PrivateKey privateKey = (PrivateKey) importKeyStore.getKey(
                        alias, null);

                if(keyStore.containsAlias(alias)) {
                    keyStore.deleteEntry(alias);
                    System.out.println("Alias: " + alias
                            + " pregazen novim pri import.");
                }
                //dodavanje u defaultKeyStore
                keyStore.setCertificateEntry(alias, certificate);
                keyStore.setKeyEntry(alias, privateKey, null,
                        new X509Certificate[] { certificate });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToPKCS12WithAES(String path, String password) {
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

    public void exportToPKCS12NoAES(String path, String password) {
        try {
            File file = new File(path);

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            keyStore.store(fileOutputStream, password.toCharArray());

            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importKeyPairWithAES(String alias, String path, String password) {
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

            if(enumeration.hasMoreElements()) {
                String oldAlias = (String) enumeration.nextElement();
                X509Certificate certificate = (X509Certificate) importKeyStore
                        .getCertificate(oldAlias);
                PrivateKey privateKey = (PrivateKey) importKeyStore.getKey(
                        oldAlias, null);

                if(keyStore.containsAlias(alias)) {
                    keyStore.deleteEntry(alias);
                    System.out.println("Alias: " + alias
                            + " pregazen novim pri import.");
                }
                //dodavanje u defaultKeyStore
                keyStore.setCertificateEntry(alias, certificate);
                keyStore.setKeyEntry(alias, privateKey, null,
                        new X509Certificate[] { certificate });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importKeyPairNoAES(String alias, String path, String password) {
        try {
            KeyStore importKeyStore = KeyStore.getInstance("PKCS12", "BC");

            File file = new File(path);

            FileInputStream fileInputStream = new FileInputStream(file);

            importKeyStore.load(fileInputStream, password.toCharArray());

            fileInputStream.close();

            Enumeration<String> enumeration = importKeyStore.aliases();

            if(enumeration.hasMoreElements()) {
                String oldAlias = (String) enumeration.nextElement();
                X509Certificate certificate = (X509Certificate) importKeyStore
                        .getCertificate(oldAlias);
                PrivateKey privateKey = (PrivateKey) importKeyStore.getKey(
                        oldAlias, null);

                if(keyStore.containsAlias(alias)) {
                    keyStore.deleteEntry(alias);
                    System.out.println("Alias: " + alias
                            + " pregazen novim pri import.");
                }
                //dodavanje u defaultKeyStore
                keyStore.setCertificateEntry(alias, certificate);
                keyStore.setKeyEntry(alias, privateKey, null,
                        new X509Certificate[] { certificate });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToPKCS12WithAES(String alias, String path, String password) {
        try {
            // novi keyStore koji ima samo odabrani sertifikat
            KeyStore exportKeyStore = KeyStore.getInstance("PKCS12", "BC");
            exportKeyStore.load(null, null);

            X509Certificate certificate = (X509Certificate) keyStore
                    .getCertificate(alias);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
            exportKeyStore.setCertificateEntry(alias, certificate);
            exportKeyStore.setKeyEntry(alias, privateKey, null,
                    new X509Certificate[] { certificate });

            File file = new File(path);

            SecretKeySpec secretKeySpec = buildSecretKeySpec(password);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    new FileOutputStream(file), cipher);

            exportKeyStore.store(cipherOutputStream, password.toCharArray());

            cipherOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToPKCS12NoAES(String alias, String path, String password) {
        try {
         // novi keyStore koji ima samo odabrani sertifikat
            KeyStore exportKeyStore = KeyStore.getInstance("PKCS12", "BC");
            exportKeyStore.load(null, null);

            X509Certificate certificate = (X509Certificate) keyStore
                    .getCertificate(alias);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
            exportKeyStore.setCertificateEntry(alias, certificate);
            exportKeyStore.setKeyEntry(alias, privateKey, null,
                    new X509Certificate[] { certificate });
            
            File file = new File(path);

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            exportKeyStore.store(fileOutputStream, password.toCharArray());

            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PKCS10CertificationRequest generateCSR(String alias) {
        try {

            X509Certificate certificate = (X509Certificate) keyStore
                    .getCertificate(alias);
            PublicKey publicKey = certificate.getPublicKey();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
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

    public X509Certificate signX509Certificate(
            PKCS10CertificationRequest request) {
        try {
            RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory
                    .createKey(request.getSubjectPublicKeyInfo());
            RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(),
                    rsa.getExponent());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey rsaPublicKeyOfSubject = keyFactory
                    .generatePublic(rsaSpec);

            ContentVerifierProvider contentVerifierProvider = new JcaContentVerifierProviderBuilder()
                    .setProvider("BC").build(rsaPublicKeyOfSubject);

            if(!request.isSignatureValid(contentVerifierProvider)) {
                throw new Exception("CSR signature invalid!");
            }

            X509Certificate caCertificate = (X509Certificate) keyStore
                    .getCertificate(aliasCA);
            X500Name issuer = new JcaX509CertificateHolder(caCertificate)
                    .getSubject();
            PrivateKey privateKeyCA = (PrivateKey) keyStore.getKey(aliasCA,
                    null);

            //davanje perioda validnosti sertifikatu
            Date referenceDate = new Date(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.setTime(referenceDate);
            c.add(Calendar.MONTH, -1);
            Date dateFrom = c.getTime();
            c.add(Calendar.MONTH, 12);
            Date dateTo = c.getTime();

            X509v3CertificateBuilder v3CertificateBuilder = new JcaX509v3CertificateBuilder(
                    issuer, BigInteger.ONE, dateFrom, dateTo,
                    request.getSubject(), rsaPublicKeyOfSubject);

            ContentSigner signerCA = new JcaContentSignerBuilder("SHA1WithRSA")
                    .build(privateKeyCA);

            X509CertificateHolder certificateHolder = v3CertificateBuilder
                    .build(signerCA);

            return new JcaX509CertificateConverter().setProvider("BC")
                    .getCertificate(certificateHolder);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void exportX509Certificate(String alias, String path) {
        try {
            X509Certificate certificate = (X509Certificate) keyStore
                    .getCertificate(alias);

            X509Certificate caCertificate = (X509Certificate) keyStore
                    .getCertificate(aliasCA);
            certificate.verify(caCertificate.getPublicKey());

            PrintWriter writer = new PrintWriter(path);
            JcaPEMWriter pem = new JcaPEMWriter(writer);
            pem.writeObject(certificate);
            pem.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO dodavanje u keyStore
    public X509Certificate importX509Certificate(String alias, String path) {
        try {
            File testFile = new File(path);

            if(!testFile.exists()) {
                throw new Exception("File does not exists!");
            }

            FileReader reader = new FileReader(path);

            PEMParser parser = new PEMParser(reader);
            X509CertificateHolder x509CertificateHolder = (X509CertificateHolder) parser
                    .readObject();
            parser.close();

            X509Certificate certificate = new JcaX509CertificateConverter()
                    .setProvider("BC").getCertificate(x509CertificateHolder);

            return certificate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

}
