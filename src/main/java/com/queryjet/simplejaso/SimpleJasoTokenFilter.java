package com.queryjet.simplejaso;

import com.queryjet.TokenizerOptions;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nobaksan on 2015. 11. 18..
 */
public class SimpleJasoTokenFilter extends TokenFilter {
    /* The constructor for our custom token filter just calls the TokenFilter
     * constructor; that constructor saves the token stream in a variable named
     * this.input.
     */
    private static TokenizerOptions optionsValue;

    public SimpleJasoTokenFilter(TokenStream tokenStream,TokenizerOptions options) {
        super(tokenStream);
        this.charTermAttr = addAttribute(CharTermAttribute.class);
        this.posIncAttr = addAttribute(PositionIncrementAttribute.class);
        this.terms = new LinkedList<char[]>();
        optionsValue = options;
    }

    /* Like the PlusSignTokenizer class, we are going to save the text of the
     * current token in a CharTermAttribute object. In addition, we are going
     * to use a PositionIncrementAttribute object to store the position
     * increment of the token. Lucene uses this latter attribute to determine
     * the position of a token. Given a token stream with "This", "is", "",
     * ”some", and "text", we are going to ensure that "This" is saved at
     * position 1, "is" at position 2, "some" at position 3, and "text" at
     * position 4. Note that we have completely ignored the empty string at
     * what was position 3 in the original stream.
     */
    private CharTermAttribute charTermAttr;
    private PositionIncrementAttribute posIncAttr;
    private Queue<char[]> terms;



    /* Like we did in the PlusSignTokenizer class, we need to override the
     * incrementToken() function to save the attributes of the current token.
     * We are going to pass over any tokens that are empty strings and save
     * all others without modifying them. This function should return true if
     * a new token was generated and false if the last token was passed.
     */
    @Override
    public boolean incrementToken() throws IOException {
        if (!terms.isEmpty()) {
            char[] buffer = terms.poll();
            charTermAttr.setEmpty();
            charTermAttr.copyBuffer(buffer, 0, buffer.length);
            posIncAttr.setPositionIncrement(0);
            return true;
        }

        if (!input.incrementToken()) {
            return false;
        } else {
            SimpleJasoDecomposer jasoDecomposer = new SimpleJasoDecomposer();
            String currentTokenInStream = this.input.getAttribute(CharTermAttribute.class).toString().trim();
            SimpleJasoDecomposer uni = new SimpleJasoDecomposer();

            String decomposerTokenInStream =  uni.getAlphabeticSeqs(currentTokenInStream,"han");
            terms.add(decomposerTokenInStream.toCharArray());
            if (optionsValue.getJasoTypo()) {
                decomposerTokenInStream =  uni.getAlphabeticSeqs(currentTokenInStream,"eng");
                terms.add(decomposerTokenInStream.toCharArray());
            }
            return true;
        }
    }

}
