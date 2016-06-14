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
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import front.CertificateInfo;

public class CertController {

    private CertCore cc;
    public final String aliasCA = "tamara";
    private HashMap<String, PKCS10CertificationRequest> setCSRs;

    public CertController(String pathToDefaultKeyStore,
            String passwordDefaultKeyStore) {
        cc = new CertCore();
        setCSRs = new HashMap<String, PKCS10CertificationRequest>();
        File defaultKSFile = new File(pathToDefaultKeyStore);
        if(!defaultKSFile.exists()) {
            cc.generateDefaultKeyStore(pathToDefaultKeyStore,
                    passwordDefaultKeyStore, aliasCA);
        }

        cc.loadDefaultKeyStore(pathToDefaultKeyStore, passwordDefaultKeyStore,
                aliasCA);
    }

    public void generatePairOfKeys(String alias, Integer keySize,
            Date dateFrom, Date dateTo, BigInteger serialNumber,
            String commonName, String organizationalUnit,
            String organizationalName, String localityName, String stateName,
            String countryName, String emailAddress) {
        cc.generatePairOfKeys(alias, keySize, dateFrom, dateTo, serialNumber,
                commonName, organizationalUnit, organizationalName,
                localityName, stateName, countryName, emailAddress);
    }

    public void importKeyStoreWithAES(String path, String password) {
        cc.importKeyStoreWithAES(path, password);
    }

    public void importKeyStoreNoAES(String path, String password) {
        cc.importKeyStoreNoAES(path, password);
    }

    public void exportToPKCS12WithAES(String path, String password) {
        cc.exportToPKCS12WithAES(path, password);
    }

    public void exportToPKCS12NoAES(String path, String password) {
        cc.exportToPKCS12NoAES(path, password);
    }

    public void signX509Certificate(String alias) {
        //potpisivanje na osnovu zahteva
        X509Certificate certificate = cc
                .signX509Certificate(setCSRs.get(alias));

        try {
            KeyStore keyStore = cc.getKeyStore();
            Key privateKey = keyStore.getKey(alias, null);
            //cuvanje novog potpisanog sertifikata na mesto starog
            keyStore.setCertificateEntry(alias, certificate);
            keyStore.setKeyEntry(alias, privateKey, null,
                    new X509Certificate[] { certificate });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateCSR(String alias) {
        PKCS10CertificationRequest generatedCSR = cc.generateCSR(alias);
        setCSRs.put(alias, generatedCSR);
    }

    public String previewCSR(String commonName) {
        String stringCSR = null;
        try {
            PKCS10CertificationRequest request = setCSRs.get(commonName);

            StringWriter writer = new StringWriter();
            JcaPEMWriter pem = new JcaPEMWriter(writer);
            pem.writeObject(request);
            pem.close();

            stringCSR = writer.getBuffer().toString();
            System.out.println(stringCSR);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringCSR;
    }

    public List<CertificateInfo> getCertificateInfoList(boolean fgCAappears) {
        List<CertificateInfo> list = new ArrayList<CertificateInfo>();

        KeyStore keyStore = cc.getKeyStore();

        try {
            X509Certificate caCertificate = (X509Certificate) keyStore
                    .getCertificate(aliasCA);
            Enumeration<String> enumeration = keyStore.aliases();

            while (enumeration.hasMoreElements()) {
                String alias = (String) enumeration.nextElement();
                if(alias.equals(aliasCA) && !fgCAappears)
                    continue;

                X509Certificate certificate = (X509Certificate) keyStore
                        .getCertificate(alias);

                PublicKey publicKey = certificate.getPublicKey();
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias,
                        null);
                X500Name x500name = new JcaX509CertificateHolder(certificate)
                        .getSubject();

                HashMap<String, String> attributes = cc.readX500Name(x500name);

                boolean isPotpisan = true;
                try {
                    certificate.verify(caCertificate.getPublicKey());
                } catch (InvalidKeyException|SignatureException e) {
                    isPotpisan = false;
                }

                RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory
                        .createKey(publicKey.getEncoded());

                CertificateInfo certificateInfo = new CertificateInfo(alias,
                        rsa.getModulus().bitLength(),
                        certificate.getNotAfter(), certificate.getNotBefore(),
                        certificate.getSerialNumber(),
                        attributes.get("commonName"),
                        attributes.get("organizationalUnit"),
                        attributes.get("organizationalName"),
                        attributes.get("localityName"),
                        attributes.get("stateName"),
                        attributes.get("countryName"),
                        attributes.get("emailAddress"),
                        bytesToHex(publicKey.getEncoded()),
                        bytesToHex(privateKey.getEncoded()), isPotpisan);
                list.add(certificateInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void ispis(List<CertificateInfo> list) {
        for (CertificateInfo ci : list) {
            System.out.println("CERT");
            System.out.println(ci.toString());
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
