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
        controller.ispis(controller.getCertificateInfoList(true));
        
        controller.generateCSR(alias1);
        controller.previewCSR(alias1);
        controller.signX509Certificate(alias1);
        
        
        
        
        controller.ispis(controller.getCertificateInfoList(true));
        controller.exportToPKCS12WithAES(pathExport, password);
        // ovo bi trebalo uvek zvati pri zatvaranju
        // sad ne treba jer ce pokvariti test
        // controller.saveChangesToDefaultKeyStore();
        
    }

}
