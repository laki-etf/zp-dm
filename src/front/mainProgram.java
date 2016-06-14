package front;

import java.io.File;

import back.CertController;

public class mainProgram {

    public static void main(String[] args) {
        
        String path = "resources/defaultKeyStore.p12";
        String password = "password";
        CertController controller = new CertController(path, password);
        
    }

}
