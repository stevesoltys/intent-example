package com.stevesoltys.intent.expression;

import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.textnormalizer.annotations.RegexTokenFilter;
import de.tudarmstadt.ukp.dkpro.core.tokit.RegexSegmenter;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * @author Steve Soltys
 */
public class DefaultExpressionFactory extends ExpressionFactoryAdapter {

    public DefaultExpressionFactory() {
        super();
    }

    @Override
    AnalysisEngine buildPipeline() throws ResourceInitializationException {
        return createEngine(createEngineDescription(
                createEngineDescription(RegexSegmenter.class),

                createEngineDescription(RegexTokenFilter.class,
                        RegexTokenFilter.PARAM_REGEX, "[\\s\\S]+"),

                createEngineDescription(StanfordParser.class,
                        StanfordParser.PARAM_LANGUAGE, "en",
                        StanfordParser.PARAM_VARIANT, "factored"),

                createEngineDescription(ArktweetPosTagger.class,
                        ArktweetPosTagger.PARAM_VARIANT, "ritter")
                )

        );
    }
}
