package pl.edu.pw.elka.phrasalwebservice;

public class PhrasalServerStart {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Nalezy przekazac 5 parametrow - englishFilePath, foreignFilePath, onlyEnglishCorpusFilePath, tunerModelWeightsFilePath, serverPort");
            System.exit(0);
        }

        String englishFilePath = args[0];
        String foreignFilePath = args[1];
        String onlyEnglishCorpusFilePath = args[2];
        String tunerModelWeightsFilePath = args[3];
        int serverPort = Integer.valueOf(args[4]);

        PhrasalServer server = new PhrasalServer(englishFilePath, foreignFilePath, onlyEnglishCorpusFilePath, tunerModelWeightsFilePath);
        server.runTranslationService(serverPort);
    }

}
