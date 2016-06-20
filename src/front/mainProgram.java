package front;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import back.CertController;

public class mainProgram {

    public static void main(String[] args) {

        String aliasCA = "tamara";
        String alias1 = "makise";
        String path = "resources/defaultKeyStore.p12";
        String pathExport = "resources/exportKeyStore.p12";
        String pathCertImport = "resources/importCertificate.cer";
        String pathCertExport = "resources/exportCertificate.cer";
        String password = "password";
        CertController controller = new CertController(path, password);
        System.out.println("ISPIS POCETNOG STANJA");
        controller.ispis(controller.getCertificateInfoList(true));

        System.out.println("UCITAVANJE FAJLA");
        controller.importKeyPairNoAES("ispravan", "resources/ispravan.p12", "kljuc");
        
        System.out.println("DODAVANJE PARA KLJUCEVA SA EKSTENZIAJAMA");
        List<String> listAN = new ArrayList<String>();
        listAN.add("makise.etf.rs");
        controller.generatePairOfKeys(alias1, 1024,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000),
                BigInteger.TEN, "makise chris Server", "Racunski centar",
                "ETF", "Beograd", "Republika Srbija", "Republic of Serbia",
                "makise@etf.rs", true, true, new Integer(10), true, true, listAN,
                true, true, true, 1, 0, 0, 0, 0, 0, 0, 0, 1);
        System.out.println("ISPIS NOVOG STANJA");
        controller.ispis(controller.getCertificateInfoList(true));
        
        System.out.println("GENERISANJE CSR");
        controller.generateCSR(alias1);
        System.out.println("ISPIS CSR");
        controller.previewCSR(alias1);
        System.out.println("POTPISIVANJE SERTIFIKATA");
        controller.signX509Certificate(alias1);
         

        System.out.println("ISPIS KRAJNJEG STANJA");
        controller.ispis(controller.getCertificateInfoList(true));
        System.out.println("PAMCENJE U DRUGI STORE");
        controller.exportToPKCS12WithAES(pathExport, password);
        // ovo bi trebalo uvek zvati pri zatvaranju
        // sad ne treba jer ce pokvariti test
        // controller.saveChangesToDefaultKeyStore();

    }

}
