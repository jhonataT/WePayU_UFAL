import easyaccept.EasyAccept;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        String facade = "br.ufal.ic.p2.wepayu.Facade";
        EasyAccept.main(new String[]{facade, "tests/us1.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us1_1.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us2.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us2_1.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us3.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us3_1.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us4.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us4_1.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us5.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us5_1.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us6.txt"}); // ok
        EasyAccept.main(new String[]{facade, "tests/us6_1.txt"}); // ok
//        EasyAccept.main(new String[]{facade, "tests/us7.txt"});

//        EasyAccept.main(new String[]{facade, "tests/us5_2.txt"});
//
//        EasyAccept.main(new String[]{facade, "tests/us6_2.txt"});
//        EasyAccept.main(new String[]{facade, "tests/us7_1.txt"});
//        EasyAccept.main(new String[]{facade, "tests/us7_2.txt"});
//        EasyAccept.main(new String[]{facade, "tests/us8_1.txt"});
//        EasyAccept.main(new String[]{facade, "tests/us8_2.txt"});
//        EasyAccept.main(new String[]{facade, "tests/us9_1.txt"});
//        EasyAccept.main(new String[]{facade, "tests/us9_2.txt"});



        // EasyAccept.main(new String[]{facade, "tests/us3_2.txt"}); <- Arquivo não existe
    }
}


