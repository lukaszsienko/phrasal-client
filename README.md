# `release` branch run information

In `bin` folder you can find ready-made executable `phrasal-start-<version>.jar` and example data of parallel corpus PL-ENG: `parallel.polish` and `parallel.english`. You can also find here a file: `mono.english` which you can optionally pass to training process in order to enrich english language model. Translation direction here is: PL -> ENG.

1. Program parameters  
When starting binary jar, you should specify the stack space of JVM:  
`java -Xms1g -Xmx5g -jar phrasal-start-1.0.jar`  
Above command will print help output:  
```
usage: phrasal-start  
 -d,--models-dir <arg>                  Path to directory where model
                                        folder is/will be stored. Each
                                        training run creates model folder
                                        here.
 -e,--english-file <arg>                Path to file of english-lang part
                                        of parallel corpus.
 -e2,--english-monolingual-file <arg>   Path to file of english
                                        monolingual corpus, used to enrich
                                        language model.
 -f,--foreign-file <arg>                Path to file of foreign-lang part
                                        of parallel corpus.
 -g,--use-giza-aligner                  Change from default Berkeley Word
                                        Aligner to GIZA++ word aligner.
 -n,--model-name <arg>                  Name of the model. Corresponds to
                                        model folder name stored in
                                        models-dir directory.
 -s,--run-server <arg>                  Run translation service on
                                        specified port of localhost
                                        machine.
 -t,--run-training                      Run generation of translation
                                        model.
```  
Specyfying `--models-dir` and `--model-name` is essential. Using these parameters, program will create a path to model folder which is a directory inside `--models-dir` named `--model-name`. When training new translation model, folder named `--model-name` will be created inside already-exisiting `--models-dir` directory (see warning below).  

Next, when running model training (`--run-training` option) you have to provide paths to `--foreign-file` as well as `--english-file` of parallel corpus. You can optionally provide path to `--english-monolingual-file` in order to enrich english language model. When not specified enrich file, then language model will be built only on an english side od parallel corpus. Enrich file should be a text file and will be cleaned and tokenized before building language model.  

Default used word alignment software is Berkeley Word Aligner unless you specify `--use-giza-aligner` option.  

After finished buidling translation model you can start translation service working on localhost on specified port using `--run-server port_nr`.

2.   Examples:

* Run translation model training  
  `java -Xms1g -Xmx5g -jar phrasal-start-1.0.jar --run-training --foreign-file ./parallel.polish --english-file ./parallel.english --english-monolingual-file ./mono.english --models-dir . --model-name model_1`  
  **Warning** *For security reasons, you cannot start training new model overwriting `--model-name` folder of previously trained model. If you want to train new model with a name of previously built model, please (re)move `--model-name` folder.*


* Run translation model training (with short version of parameters)  
`java -Xms1g -Xmx5g -jar phrasal-start-1.0.jar -t -f ./parallel.polish -e ./parallel.english -e2 ./mono.english -d . -n model_1`


* Run translation model training + start translation service on port 5555 afterwards  
`java -Xms1g -Xmx5g -jar phrasal-start-1.0.jar -t -f ./parallel.polish -e ./parallel.english -e2 ./mono.english -d . -n model_1 -s 5555`

* Just load already built model and start translation service on port 5555  
`java -Xms1g -Xmx5g -jar phrasal-start-1.0.jar -d . -n model_1 -s 5555`
