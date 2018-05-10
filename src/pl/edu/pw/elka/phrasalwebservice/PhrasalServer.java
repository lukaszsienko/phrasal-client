package pl.edu.pw.elka.phrasalwebservice;

import pl.edu.pw.elka.phrasalwrapper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class PhrasalServer {

    private ParallerCorpus corpus;
    private WordAlignmentModel alignmentModel;
    private LanguageModel languageModel;
    private TranslationModel translationModel;
    private Decoder decoder;

    public PhrasalServer(String englishFilePath, String foreignFilePath) {
        try {
            corpus = new ParallerCorpus(englishFilePath, foreignFilePath);
            alignmentModel = new WordAlignmentModel(corpus);
            languageModel = new LanguageModel(5, corpus);
            translationModel = new TranslationModel(alignmentModel, corpus);
            decoder = new Decoder(languageModel, translationModel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        decoder.loadModelWithDefaultConfigInServerMode();
    }

    public void runTranslationService(int serverPort) {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(serverPort);
            while (true) {
                Socket socket = listener.accept();
                TranslateRequestHandler requestHandlerThread = new TranslateRequestHandler(socket);
                requestHandlerThread.run();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class TranslateRequestHandler implements Runnable {

        private static final int SOCKET_READ_TIMEOUT_MS = 15*1000;
        private Socket socket;

        public TranslateRequestHandler(Socket socket) {
            this.socket = socket;
            try {
                this.socket.setSoTimeout(SOCKET_READ_TIMEOUT_MS);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    try {
                        while (true) {
                            String input = in.readLine();
                            if (input == null) {
                                break;
                            }

                            String translatedSentence = decoder.translateSentenceInServerMode(input);
                            out.println(translatedSentence);
                        }
                    } catch (SocketTimeoutException e) {
                        out.println("Socket timeout. Connection finished.");
                    }

                    in.close();
                    out.close();
                } finally {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


























