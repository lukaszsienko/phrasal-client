package pl.edu.pw.elka.phrasalwebservice;

public class PhrasalServerStart {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Nalezy przekazac 4 parametry - englishFilePath, foreignFilePath, onlyEnglishCorpusFilePath, serverPort");
            System.exit(0);
        }

        String englishFilePath = args[0];
        String foreignFilePath = args[1];
        String onlyEnglishCorpusFilePath = args[2];
        int serverPort = Integer.valueOf(args[3]);

        PhrasalServer server = new PhrasalServer(englishFilePath, foreignFilePath, onlyEnglishCorpusFilePath);
        server.runTranslationService(serverPort);
    }

}
