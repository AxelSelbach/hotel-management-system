import at.favre.lib.crypto.bcrypt.BCrypt;

public class GenerateHash {
    public static void main(String[] args) {
        String senhaAdmin = "admin123";
        String senhaRecep = "123456";

        String hashAdmin = BCrypt.withDefaults().hashToString(12, senhaAdmin.toCharArray());
        String hashRecep = BCrypt.withDefaults().hashToString(12, senhaRecep.toCharArray());

        System.out.println("Hash Admin: " + hashAdmin);
        System.out.println("Hash Recep: " + hashRecep);
    }
}