package front;

import back.CertController;

public class mainProgram {

    public static void main(String[] args) {
        
        String aliasCA = "tamara";
        String alias1 = "tatatamara";
        String path = "resources/defaultKeyStore.p12";
        String pathExport = "resources/exportKeyStore.p12";
        String pathCertImport = "resources/importCertificate.cer";
        String pathCertExport = "resources/exportCertificate.cer";
        String password = "password";
        CertController controller = new CertController(path, password);
        System.out.println("ISPIS POCETNOG STANJA");
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
