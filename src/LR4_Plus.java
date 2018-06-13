public class LR4_Plus {
    public static void main(String[] args) {


        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        StackC stackC = new StackC();
        String input = "int h; h=0; int i; set a; for (i=1; i < 8 ; i=i+2) {h = (h^i)/2; int f; f = i+9+h; a add f;} a remove 12;";

        //1+         set a; int i; i=3; int h; h = (3^i)+7; int f; f = i+9+h; a add f;;
        //2+         int h; h=0; int i; set a; for (i=1; i < 8 ; i=i+2) {h = (h^i)/2; int f; f = i+9+h; a add f;}
        //3+         set a; if (a contains 15) {int b; b=8%3; a add b;} else {int c; c = 6^4; a add c;}


        /*System.out.println('\n');
        for (Token token : tokens) {
            System.out.println(token);
        }*/
        System.out.println('\n');
        System.out.println("[" + input + "]");
        System.out.println('\n');
        System.out.println(parser.lang(lexer.recognize(input)));
        System.out.println('\n');
        System.out.println(parser.printTOV(stackC.stackC(parser)));
    }
}
