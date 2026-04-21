package com.mongault.kiku.model;

public enum ReviewMode {
    PRONUNCIATION,  //Recto: a Japanese text, the user must pronounce it; Verso: TTS, furigana and translated text
    COMPREHENSION,  //Recto: TTS; Verso: the Japanese text, furigana and translated text
    EXPRESSION      //Recto: a translated text; Verso: TTS, Japanese text, furigana and translated text
}
