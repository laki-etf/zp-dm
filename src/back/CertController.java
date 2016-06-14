package back;

import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import front.CertificateInfo;

public class CertController {

    private CertCore cc;
    public final String aliasCA = "tamara";
    private HashMap<String, PKCS10CertificationRequest> setCSRs;
    private String pathDefault;
    private String passwordDefault;

    // kontroler se instancira sa zapamcenim stanjem, ako ne postoji on pravi novo
    public CertController(String pathToDefaultKeyStore,
            String passwordDefaultKeyStore) {
        cc = new CertCore();
        pathDefault = pathToDefaultKeyStore;
        passwordDefault = passwordDefaultKeyStore;
        
        setCSRs = new HashMap<String, PKCS10CertificationRequest>();
        File defaultKSFile = new File(pathToDefaultKeyStore);
        if(!defaultKSFile.exists()) {
            cc.generateDefaultKeyStore(pathToDefaultKeyStore,
                    passwordDefaultKeyStore, aliasCA);
        }

        cc.loadDefaultKeyStore(pathToDefaultKeyStore, passwordDefaultKeyStore,
                aliasCA);
    }

    // pri zatvaranju aplikacije treba zapamtiti stanje ! ! ! !
    public void saveChangesToDefaultKeyStore() {
        cc.exportToPKCS12WithAES(pathDefault, passwordDefault);
    }
    
    // stvara par kljuceva (tj. sertifikat; SVE JE SERTIFIKAT)
    public void generatePairOfKeys(String alias, Integer keySize,
            Date dateFrom, Date dateTo, BigInteger serialNumber,
            String commonName, String organizationalUnit,
            String organizationalName, String localityName, String stateName,
            String countryName, String emailAddress) {
        cc.generatePairOfKeys(alias, keySize, dateFrom, dateTo, serialNumber,
                commonName, organizationalUnit, organizationalName,
                localityName, stateName, countryName, emailAddress);
    }

    // postoji neki magacin sertifikata koji ja odrzavam
    // on moze da ima nula, jedan ili n sertifikata
    
    // ucitava zapamceni magacin u vidu fajla i dodaje trenutnom stanju
    public void importKeyStoreWithAES(String path, String password) {
        cc.importKeyStoreWithAES(path, password);
    }

    // isto to bez dekriptovanja
    // sifra ipak treba jer se magacin uvek zakljucava pri pamcenju u vidu fajla
    public void importKeyPairNoAES(String alias, String path, String password) {
        cc.importKeyPairNoAES(alias, path, password);
    }

    // ucitava zapamceni magacin koji podrazumevano ima jedan sertifikat
    public void importKeyPairWithAES(String alias, String path, String password) {
        cc.importKeyPairWithAES(alias, path, password);
    }

    // isto to samo bez dekriptovanja
    public void importKeyStoreNoAES(String path, String password) {
        cc.importKeyStoreNoAES(path, password);
    }

    // pamti magacin sertifikata 
    public void exportToPKCS12WithAES(String path, String password) {
        cc.exportToPKCS12WithAES(path, password);
    }

    // isto to bez kriptovanja
    public void exportToPKCS12NoAES(String path, String password) {
        cc.exportToPKCS12NoAES(path, password);
    }

    // pamti magacin koji ima samo jedan izabrani sertifikat
    public void exportKeyPairToPKCS12WithAES(String alias, String path, String password) {
        cc.exportToPKCS12WithAES(alias, path, password);
    }

    // isto to bez kriptovanja
    public void exportKeyPairToPKCS12NoAES(String alias, String path, String password) {
        cc.exportToPKCS12NoAES(alias, path, password);
    }

    // stvara zahtev za potpisivanje sertifikata
    public void generateCSR(String alias) {
        PKCS10CertificationRequest generatedCSR = cc.generateCSR(alias);
        setCSRs.put(alias, generatedCSR);
    }

    // ispis zahteva za potpisivanje sertifikata kao dokaz da postoji
    public String previewCSR(String alias) {
        String stringCSR = null;
        try {
            PKCS10CertificationRequest request = setCSRs.get(alias);

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

    // potpisivanje sertifikata
    public void signX509Certificate(String alias) {
        //potpisivanje na osnovu zahteva
        X509Certificate certificate = cc
                .signX509Certificate(setCSRs.get(alias));

        try {
            KeyStore keyStore = cc.getKeyStore();
            Key privateKey = keyStore.getKey(alias, null);
            //cuvanje novog potpisanog sertifikata na mesto starog
            keyStore.deleteEntry(alias);
            keyStore.setCertificateEntry(alias, certificate);
            keyStore.setKeyEntry(alias, privateKey, null,
                    new X509Certificate[] { certificate });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // izvoz sertifikata
    public void exportX509Certificate(String alias, String path) {
        cc.exportX509Certificate(alias, path);
    }
    
    // uvoz sertifikata pod nekim aliasom
    public void importX509Certificate(String alias, String path) {
        cc.importX509Certificate(alias, path);
    }
    
    // ovo je tebi najbitnije
    // daje ti listu sa podacima o sertifikatima koji su trenutni
    // kad pozoves neku funkciju pozovi ponovo ovo da ti vrati svezu listu
    // ako ti ova funkcija nije ddovoljno prakticna mogu da dodam jos nesto
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

    // za test potrebe; ispisuje listu u konzolu
    public void ispis(List<CertificateInfo> list) {
        for (CertificateInfo ci : list) {
            System.out.println("CERT");
            System.out.println(ci.ispis());
        }
    }

    
    // util stvari
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
