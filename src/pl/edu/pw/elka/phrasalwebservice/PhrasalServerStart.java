package pl.edu.pw.elka.phrasalwebservice;

public class PhrasalServerStart {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Nalezy przekazac 3 parametry - englishFilePath, foreignFilePath, serverPort");
            System.exit(0);
        }

        String englishFilePath = args[0];
        String foreignFilePath = args[1];
        int serverPort = Integer.valueOf(args[2]);

        PhrasalServer server = new PhrasalServer(englishFilePath, foreignFilePath);
        server.runTranslationService(serverPort);
    }

}
