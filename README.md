<p align="center"><img src="https://github.com/matthiasjordan/dahdidahdit/blob/main/app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png" alt="Logo" /></p>

<h1 align="center">Dahdidahdit</h1>

## Concept

Copying Morse code has to be done in the head. Using computer programs that
allow for easy keyboard input of letters makes it easy for learners to wire
Morse code symbols ("dahdidah") to positions on the keyboard, making it next
to impossible to copy Morse code when no keyboard is available: You know
ditditdahdahditdit is at the upper right corner but when you finally
remember that's where the "?" is, DX has already sent 5 more letters.

Dahdidahdit just plays Morse code symbols. Learners copy the symbols by
handwriting to a sheet of paper and later compare with what was sent.
The learner then counts how many errors were made. Dahdidahdit keeps track
about this and, when the learner makes progress, teaches a new letter or
increases the speed.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/com.paddlesandbugs.dahdidahdit/) [<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height="80">](https://apt.izzysoft.de/fdroid/index/apk/com.paddlesandbugs.dahdidahdit)

Or download the latest APK from the [Releases Section](https://github.com/matthiasjordan/dahdidahdit/releases/latest).


## Features

* Morse tutor picks you up at where you currently are with learning Morse code and
  teaches you how to get where you want to be.
    * Uses Koch/Farnsworth combination.
    * Can also do Word Koch.
    * Plays slower after adding a new letter.
    * The frequency of new letters is higher than for older ones.
    * Can show hints on faster note taking.
* Generates Morse code for individual practice
    * Can add white noise and spherics.
    * Can add QSB
    * Can add QLF
    * Can add chirp
    * Multiple kinds of text available
        * Random
        * Call-signs
        * Q-codes
        * Top 2000 ham radio words
        * Generated QSOs
        * RSS feeds using top-notch FLOSS RSS reader app [Feeder](https://github.com/spacecowboy/Feeder)
        * Custom text
* Teaches head copying, starting with short words and progressing to longer
texts
* Can connect to a paddle or straight key using USB (see below)
* Using external hardware or on-screen Morse key can 
    * help practicing sending
    * connect to Morserinos and other Dahdidahdit users over direct network connection (LAN, 
      internet) or Morserino servers like [qsobot.online](https://qsobot.online/)
    * play a nice game of Morse Tennis against another Dahdidahdit user


## Homepage and manual

Find more information at https://paddlesandbugs.com.


## Hardware projects for external Morse keys and adapters

Hat-tip to HB9HOX for most of the links!

* [VBand adapter](https://hamradio.solutions/vband/) (commercial but ready-made)
* https://codeberg.org/corvus-ch/morsekeyboard
* https://github.com/Vail-CW/vail-adapter
* https://github.com/mgiugliano/MorsePaddle2USB
* https://github.com/kevintechie/CWKeyboard
* https://github.com/matthiasjordan/openpaddle with documentation at  https://paddlesandbugs.com/dahdidahdit-manual/connect-a-hardware-key/


## Contributing

### Pull requests
If you want to submit a pull request, please make sure that you base your pull request on the 
develop branch, _not_ main. Develop is where all the development happens. The main branch is for 
releases only.
