package com.esplugin;

import com.esplugin.chosung.ChosungTokenFilter;
import com.esplugin.jaso.JasoTokenFilter;
import com.esplugin.soundex.SoundexTokenFilter;
import com.esplugin.typo.TypoTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class QjAnalyzerTokenFilterFactory extends AbstractTokenFilterFactory {
    private TokenizerOptions options;

    public QjAnalyzerTokenFilterFactory(IndexSettings indexSettings,
                                        Environment environment,
                                        String name,
                                        Settings settings) {
        super(indexSettings, name, settings);
        this.options = TokenizerOptions.create(name).
                setMode(settings.get("mode",TokenizerOptions.MODE)).
                setJasoMode(settings.get("jaso_mode",TokenizerOptions.JASO_MODE)).
                setJasoTypo(settings.getAsBoolean("jaso_typo",TokenizerOptions.JASO_TYPO));

    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        //soundex,typo,chosung
        if (this.options.getMode().equals("jaso")) {
            return new JasoTokenFilter(tokenStream,options);
        } else if (this.options.getMode().equals("soundex")) {
            return new SoundexTokenFilter(tokenStream);
        } else if (this.options.getMode().equals("typo")) {
            return new TypoTokenFilter(tokenStream);
        } else if (this.options.getMode().equals("chosung")) {
            return new ChosungTokenFilter(tokenStream);
        } else {
            return new JasoTokenFilter(tokenStream,options);
        }
    }
}
