package mytranslationsystem;

import pl.edu.pw.elka.phrasalwrapper.*;

public class Example {

    public static void exampleUseCase(String englishFilePath, String foreignFilePath) throws Exception {
        //Firstly, lets specify parallel corpus files, corresponding lines in files consists translated sentences.
        ParallerCorpus corpus = new ParallerCorpus(englishFilePath, foreignFilePath);
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

        //Decoder represents the object providing methods to translate sentences.
        //Decoding is a fancy name of translation process.
        Decoder decoder = new Decoder(languageModel, translationModel);
        decoder.runDecodingFromConsoleInInteractiveMode();
    }

    public static void main(String[] args) throws Exception {
        String englishFilePath = args[0];
        String foreignFilePath = args[1];
        exampleUseCase(englishFilePath, foreignFilePath);
    }
}