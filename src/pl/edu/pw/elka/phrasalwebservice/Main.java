package pl.edu.pw.elka.phrasalwebservice;

import org.apache.commons.cli.*;
import pl.edu.pw.elka.phrasalwrapper.model_persistence.ModelsPersistence;

import java.io.IOException;

public class Main {
    private static boolean runTraining;
    private static boolean runServer;
    private static int translationWebservicePortOnLocalhost;
    private static String foreignFilePath;
    private static String englishFilePath;
    private static String englishOnlyCorpusFilePath;
    private static String modelsOutputDirPath;
    private static String modelName;
    private static boolean useGizaWordAligner;

    public static void main(String[] args) throws Exception {
        // 1) Collect wrapper run parameters
        loadProgramParameters(args);

        // 2) Train translation model if needed or just load
        ModelsPersistence modelsPersistence;
        if (runTraining) {
            modelsPersistence = trainTranslationModel(foreignFilePath, englishFilePath, englishOnlyCorpusFilePath, modelsOutputDirPath, modelName,useGizaWordAligner);
        } else {
            modelsPersistence = loadTranslationModel(modelsOutputDirPath, modelName);
        }

        // 3) Start translation webservice if needed
        if (runServer) {
            PhrasalServer server = new PhrasalServer(modelsPersistence);
            server.runTranslationService(translationWebservicePortOnLocalhost);
        }
    }

    private static void loadProgramParameters(String[] args) {
        // 1) Specify options
        Option t = new Option("t", "run-training", false, "Run generation of translation model.");
        Option s = new Option("s", "run-server", true, "Run translation service on specified port of localhost machine.");
        Option f = new Option("f", "foreign-file", true, "Path to file of foreign-lang part of parallel corpus.");
        Option e = new Option("e", "english-file", true, "Path to file of english-lang part of parallel corpus.");
        Option e2 = new Option("e2", "english-monolingual-file", true, "Path to file of english monolingual corpus, used to enrich language model.");
        Option d = new Option("d", "models-dir", true, "Path to directory where model folder is/will be stored. Each training run creates model folder here.");
        d.setRequired(true);
        Option n = new Option("n", "model-name", true, "Name of the model. Corresponds to model folder name stored in "+d.getLongOpt()+" directory.");
        n.setRequired(true);
        Option g = new Option("g", "use-giza-aligner", false, "Change from default Berkeley Word Aligner to GIZA++ word aligner.");

        Options options = new Options();
        options.addOption(t);
        options.addOption(s);
        options.addOption(f);
        options.addOption(e);
        options.addOption(e2);
        options.addOption(d);
        options.addOption(n);
        options.addOption(g);

        // 2) Parse command line parameters and validate it
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("phrasal-start", options);
            System.exit(1);
        }

        boolean specifiedModelsDirAndModelName = cmd.hasOption(d.getOpt()) && cmd.hasOption(n.getOpt());
        boolean trainRunCorrect = cmd.hasOption(t.getOpt()) && cmd.hasOption(f.getOpt()) && cmd.hasOption(e.getOpt()) && specifiedModelsDirAndModelName;
        boolean loadRunCorrect = cmd.hasOption(s.getOpt()) && specifiedModelsDirAndModelName;

        boolean validParams = trainRunCorrect || loadRunCorrect;
        if (!validParams) {
            if (!trainRunCorrect) {
                System.out.println("When specifying "+t.getLongOpt()+" option, please specify also: "+f.getLongOpt()+", "+e.getLongOpt()+", "+d.getLongOpt()+", "+e.getLongOpt());
            } else if (!loadRunCorrect) {
                System.out.println("When specifying "+s.getLongOpt()+" option, please specify also: "+d.getLongOpt()+", "+e.getLongOpt());
            }
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("phrasal-start", options);
            System.exit(1);
        }

        // 3) Initialize program parameters
        runTraining = cmd.hasOption(t.getOpt());
        runServer = cmd.hasOption(s.getOpt());
        if (runServer) {
            try {
                translationWebservicePortOnLocalhost = Integer.valueOf(cmd.getOptionValue(s.getOpt()));
            } catch (NumberFormatException exp) {
                System.out.println("Cannot parse the port number specified after option "+s.getLongOpt());
            }
        }
        foreignFilePath = cmd.getOptionValue(f.getOpt());
        englishFilePath = cmd.getOptionValue(e.getOpt());
        englishOnlyCorpusFilePath = cmd.getOptionValue(e2.getOpt());
        modelsOutputDirPath = cmd.getOptionValue(d.getOpt());
        modelName = cmd.getOptionValue(n.getOpt());
        useGizaWordAligner = cmd.hasOption(g.getOpt());
    }

    private static ModelsPersistence trainTranslationModel(String foreignFilePath, String englishFilePath, String englishOnlyCorpusFilePath, String modelOutputDirPath, String modelName, boolean useGizaWordAligner) throws Exception {
        TranslationPreparation preparation = new TranslationPreparation();
        if (useGizaWordAligner) {
            preparation.trainTranslationModelUsingGizaAligner(foreignFilePath, englishFilePath, englishOnlyCorpusFilePath, modelOutputDirPath, modelName);
        } else { // use Berkeley Word Aligner
            preparation.trainTranslationModelUsingBerkeleyAligner(foreignFilePath, englishFilePath, englishOnlyCorpusFilePath, modelOutputDirPath, modelName);
        }

        return preparation.getModelsPersistence();
    }

    private static ModelsPersistence loadTranslationModel(String modelOutputDirPath, String modelName) throws IOException {
        TranslationPreparation preparation = new TranslationPreparation();
        preparation.loadTransationModelFromDirectory(modelOutputDirPath, modelName);
        return preparation.getModelsPersistence();
    }

}
