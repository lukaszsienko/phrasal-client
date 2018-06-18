package pl.edu.pw.elka.phrasalwebservice;

import pl.edu.pw.elka.phrasalwrapper.model_persistence.ModelsPersistence;

public class PhrasalServerStart {

    public static void main(String[] args) throws Exception {

        // 1) Collect wrapper run parameters
        String foreignFilePath = "example_data/parallel.polish";
        String englishFilePath = "example_data/parallel.english";
        String englishOnlyCorpusFilePath = "example_data/mono.english";
        String modelsOutputDirPath = "~/my_translation_models";
        String modelName = "model_1";
        boolean useGizaWordAligner = false; //true then GIZA++; false then Berkeley Word Aligner
        int translationWebservicePortOnLocalhost = 5555;

        // 2) Train translation model
        ModelsPersistence modelsPersistence = prepareTranslationModel(foreignFilePath, englishFilePath, englishOnlyCorpusFilePath, modelsOutputDirPath, modelName,useGizaWordAligner);

        // 3) Start translation webservice
        PhrasalServer server = new PhrasalServer(modelsPersistence);
        server.runTranslationService(translationWebservicePortOnLocalhost);
    }

    private static ModelsPersistence prepareTranslationModel(String foreignFilePath, String englishFilePath, String englishOnlyCorpusFilePath, String modelOutputDirPath, String modelName, boolean useGizaWordAligner) throws Exception {
        TranslationPreparation preparation = new TranslationPreparation();
        if (useGizaWordAligner) {
            preparation.trainTranslationModelUsingGizaAligner(foreignFilePath, englishFilePath, englishOnlyCorpusFilePath, modelOutputDirPath, modelName);
        } else { // use Berkeley Word Aligner
            preparation.trainTranslationModelUsingBerkeleyAligner(foreignFilePath, englishFilePath, englishOnlyCorpusFilePath, modelOutputDirPath, modelName);
        }

        return preparation.getModelsPersistence();
    }

}
