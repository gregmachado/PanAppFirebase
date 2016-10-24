package gregmachado.com.panappfirebase.util;

import java.util.HashMap;

/**
 * Created by gregmachado on 24/10/16.
 */
public class Encryption extends Object{

    private char[] alfa = new char[52];
    private char[] nume = new char[10];

    private String letters = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ";
    private String numbers = "0123456789";

    private HashMap<Character, String> values = new HashMap<Character, String>();
    private String password = "password";

    private static Encryption encryption = new Encryption();

    public static Encryption getInstance(String password) {
        encryption.setOriginalPassword(password);
        return encryption;
    }

    public String getEncryptPassword() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < password.length(); i++) {
            sb.append(values.get(password.charAt(i)));
        }
        return sb.toString();
    }

    private void setOriginalPassword(String password) {
        this.password = password;

        for (int i = 0; i < 52; i++) {
            if ((i % 2) == 0) { // minúsculas
                values.put(alfa[i], String.format("%02X", logic((password.length() * i) % 2014)));
            }
            else if ((i % 2) != 0) { // maiúsculas
                values.put(alfa[i], String.format("%02x", logic((password.length() * i) % 2013)));
            }
        }
        for (int i = 0; i < 10; i++) {
            if ((i % 2) == 0) { // pares
                values.put(nume[i], String.format("%02X", logic((password.length() * i) % 2012)));
            }
            else if ((i % 2) != 0) { // ímpares
                values.put(nume[i], String.format("%02x", logic((password.length() * i) % 2011)));
            }
        }
    }

    // Método com a lógica pra gerar os valores
    private long logic(long n) {
        long cube = n * n * n;

        long x = cube + 157;
        long y = (cube * n) * (21 * 2007);

        return 2 + x + y;
    }

    private Encryption() {
        for (int i = 0; i < 52; i++) {
            alfa[i] = letters.charAt(i);
        }
        for (int i = 0; i < 10; i ++) {
            nume[i] = numbers.charAt(i);
        }
    }
}
