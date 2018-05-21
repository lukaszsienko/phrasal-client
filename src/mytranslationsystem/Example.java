package mytranslationsystem;

import pl.edu.pw.elka.phrasalwrapper.*;

public class Example {

    public static void exampleUseCase(String englishFilePath, String foreignFilePath, String englishOnlyCorpusFilePath, String englishPartOfParallelTuningCorpusPath, String foreignPartOfParallelTuningCorpusPath) throws Exception {
        //Firstly, lets specify parallel corpus files, corresponding lines in files consists translated sentences.
        ParallerCorpus corpus = new ParallerCorpus(englishFilePath, foreignFilePath, englishOnlyCorpusFilePath);
        //Tokenization and lowercasing of both files is strongly suggested. This will overwrite existing files.
        corpus.tokenize();

        //Phrasal requires alignment of parallel corpus on the level of words (word-alignment).
        WordAlignmentModel alignmentModel = new WordAlignmentModel(corpus);
        //This will create a subfolder "/models/aligner_output" with aligment contents. Once this was run,
        //we can just create WordAlignmentModel object and results subfolder will be found automatically.
        //Subfolder "/models" is created in the location of english-side corpus file.
        alignmentModel.runWordAlignmentProcess();

        //Building ngram model of specified parallel corpus. Here we have 5-gram model.
        LanguageModel languageModel = new LanguageModel(5, corpus);
        //This will store results in "/models/language_model" subfolder. Results are: text file with probabilites
        //of 1, 2, 3, ... , n - grams in ARPA format and binary file representation of this model.
        //Can be run once as well as above commands.
        languageModel.buildLanguageModel();

        //Calculation of sth called by phrasal-authors "phrase-table" which is a model of translation.
        TranslationModel translationModel = new TranslationModel(alignmentModel, corpus);
        //Results goes to "/models/translation_model"
        translationModel.buildTranslationModel();

        //Model tuning. Output goes to a directory in which JVM was started. You can check this using: System.getProperty("user.dir")
        TranslationTuner tuner = new TranslationTuner(englishPartOfParallelTuningCorpusPath, foreignPartOfParallelTuningCorpusPath, corpus, languageModel, translationModel);
        tuner.tokenizeTuningCorpus();
        tuner.runTuning();

        //Decoder represents the object providing methods to translate sentences.
        //Decoding is a fancy name of translation process.
        Decoder decoder = new Decoder(languageModel, translationModel, tuner);
        decoder.runDecodingFromConsoleInInteractiveMode();
    }

    public static void main(String[] args) throws Exception {
        String englishFilePath = args[0];
        String foreignFilePath = args[1];
        String onlyEnglishCorpusFilePath = args[2];
        String englishPartOfParallelTuningCorpusPath = args[3];
        String foreignPartOfParallelTuningCorpusPath = args[4];
        exampleUseCase(englishFilePath, foreignFilePath, onlyEnglishCorpusFilePath, englishPartOfParallelTuningCorpusPath, foreignPartOfParallelTuningCorpusPath);
    }
}