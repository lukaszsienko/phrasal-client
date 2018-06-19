package pl.edu.pw.elka.phrasalwebservice;

import pl.edu.pw.elka.phrasalwrapper.*;
import pl.edu.pw.elka.phrasalwrapper.model_persistence.ModelsPersistence;
import pl.edu.pw.elka.phrasalwrapper.translation_model.BerkeleyTranslationModel;
import pl.edu.pw.elka.phrasalwrapper.translation_model.GizaTranslationModel;
import pl.edu.pw.elka.phrasalwrapper.translation_model.TranslationModel;
import pl.edu.pw.elka.phrasalwrapper.word_alignment.BerkeleyWordAlignmentModel;
import pl.edu.pw.elka.phrasalwrapper.word_alignment.GizaWordAlignmentModel;

public class TranslationPreparation {

    private ModelsPersistence modelsPersistence;

    public void trainTranslationModelUsingGizaAligner(String foreignFilePath, String englishFilePath, String englishOnlyCorpusFilePath, String modelOutputDirPath, String modelName) throws Exception {
        ModelsPersistence modelsPersistence =  ModelsPersistence.createEmptyModelsDirectory(modelOutputDirPath, modelName);

        final int EVERY_N_TH_GOES_TO_TUNING_SET = 14;
        CorpusPreparer corpusPreparer = new CorpusPreparer(foreignFilePath, englishFilePath);
        corpusPreparer.splitCorpusIntoTrainAndTuneParts(EVERY_N_TH_GOES_TO_TUNING_SET, modelsPersistence);
        ParallelCorpus trainingCorpus = corpusPreparer.getTrainingCorpus();
        ParallelCorpus tuningCorpus = corpusPreparer.getTuningCorpus();

        TextCorpus englishMonolingualCorpus = new TextCorpus(englishOnlyCorpusFilePath);
        englishMonolingualCorpus.tokenize();

        //Building ngram model of specified parallel corpus. Here we have 5-gram model.
        LanguageModel languageModel = new LanguageModel(5, trainingCorpus, englishMonolingualCorpus, modelsPersistence);
        languageModel.buildLanguageModel();

        //GIZA++ word alignment model
        GizaWordAlignmentModel gizaAlignmentModel = new GizaWordAlignmentModel(trainingCorpus, modelsPersistence);
        gizaAlignmentModel.runWordAlignmentProcess();
        //Building translation model using GIZA++ output
        TranslationModel translationModelGiza = new GizaTranslationModel(gizaAlignmentModel, trainingCorpus, modelsPersistence);
        translationModelGiza.buildTranslationModel();

        TranslationTuner tuner = new TranslationTuner(tuningCorpus, modelsPersistence);
        tuner.runTuning();

        this.modelsPersistence = modelsPersistence;
    }

    public void trainTranslationModelUsingBerkeleyAligner(String foreignFilePath, String englishFilePath, String englishOnlyCorpusFilePath, String modelOutputDirPath, String modelName) throws Exception {
        ModelsPersistence modelsPersistence =  ModelsPersistence.createEmptyModelsDirectory(modelOutputDirPath, modelName);

        final int EVERY_N_TH_GOES_TO_TUNING_SET = 14;
        CorpusPreparer corpusPreparer = new CorpusPreparer(foreignFilePath, englishFilePath);
        corpusPreparer.splitCorpusIntoTrainAndTuneParts(EVERY_N_TH_GOES_TO_TUNING_SET, modelsPersistence);
        ParallelCorpus trainingCorpus = corpusPreparer.getTrainingCorpus();
        ParallelCorpus tuningCorpus = corpusPreparer.getTuningCorpus();

        TextCorpus englishMonolingualCorpus = new TextCorpus(englishOnlyCorpusFilePath);
        englishMonolingualCorpus.tokenize();

        //Building ngram model of specified parallel corpus. Here we have 5-gram model.
        LanguageModel languageModel = new LanguageModel(5, trainingCorpus, englishMonolingualCorpus, modelsPersistence);
        languageModel.buildLanguageModel();

        //Phrasal requires alignment of parallel corpus on the level of words (word-alignment).
        //Berkeley word alignment model
        BerkeleyWordAlignmentModel berkeleyAlignmentModel = new BerkeleyWordAlignmentModel(trainingCorpus, modelsPersistence);
        berkeleyAlignmentModel.runWordAlignmentProcess();
        //Building translation model using Berkeley Word Aligner output
        TranslationModel translationModelBerkeley = new BerkeleyTranslationModel(berkeleyAlignmentModel, modelsPersistence);
        translationModelBerkeley.buildTranslationModel();

        TranslationTuner tuner = new TranslationTuner(tuningCorpus, modelsPersistence);
        tuner.runTuning();

        this.modelsPersistence = modelsPersistence;
    }

    public ModelsPersistence getModelsPersistence() {
        return modelsPersistence;
    }
}