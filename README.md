# Phrasal Wrapper user templates
This repository contains example templates of using [Phrasal Wrapper](https://github.com/lukaszsienko/phrasal-wrapper).
Basically, there are three ways of using Phrasal Wrapper, described below. Implementation of each template is on a separate branch of this repository. To get to know which one you should use, please read below information.

* `master` branch

   You develop own program in Java which needs to translate text inside it's implementation. Translation might or might not be transparent for user. You want to use Wrapper classes as a part of your program business logic. You want to have a full control over translation process which is working as a part of your code, NOT as a separate process.
   
* `webservice` branch

   You develop own program in any technology and you want to have a service running in a background that can provide your software translation of text you specify on demand. You want to customise this translation service to your needs, for example specify protocol of communication with your program or set translation parameters.
   
* `release` branch

   You don't care about details of translation process either it's parameters. You just want to build translation model and run translation service listening on specified port of localhost. You just want to run binaries in a convenient way with only command-line essential parameters. 
