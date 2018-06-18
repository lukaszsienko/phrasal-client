package pl.edu.pw.elka.phrasalwebservice;

import pl.edu.pw.elka.phrasalwrapper.*;
import pl.edu.pw.elka.phrasalwrapper.model_persistence.ModelsPersistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class PhrasalServer {

    private Decoder decoder;

    public PhrasalServer(ModelsPersistence modelsPersistence) {
        try {
            decoder = new Decoder(modelsPersistence);
        } catch (IOException e) {
            e.printStackTrace();
        }

        decoder.loadDecodingModel();
    }

    public void runTranslationService(int serverPort) {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(serverPort);
            while (true) {
                Socket socket = listener.accept();
                TranslateRequestHandler requestHandlerThread = new TranslateRequestHandler(socket);
                requestHandlerThread.start();
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

    private class TranslateRequestHandler extends Thread {

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

                            String translatedSentence = decoder.translateSentence(input);
                            out.println(translatedSentence);
                        }
                    } catch (SocketTimeoutException e) {
                        //do nothing, when timeout happened just finish handling thread
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


























