package com.stevesoltys.intent.expression;

import com.stevesoltys.intent.expression.dependency.Dependency;
import com.stevesoltys.intent.expression.dependency.DependencyType;
import com.stevesoltys.intent.expression.token.ExpressionToken;
import com.stevesoltys.intent.expression.token.Token;
import com.stevesoltys.intent.expression.token.pos.PartOfSpeech;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Steve Soltys
 */
@Log4j2
public abstract class ExpressionFactoryAdapter implements ExpressionFactory {

    private final JCas jCas;

    private final AnalysisEngine pipeline;

    public ExpressionFactoryAdapter() {
        try {
            jCas = JCasFactory.createJCas();
            pipeline = buildPipeline();

        } catch (UIMAException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Expression create(String input) {

        try {
            jCas.reset();
            jCas.setDocumentText(input);
            jCas.setDocumentLanguage("en");

            SimplePipeline.runPipeline(jCas, pipeline);

            List<Dependency> dependencies = selectDependencies(Collections.emptyList());
            List<Token> tokens = selectTokens(Collections.emptyList());
            return new Expression(tokens, dependencies);

        } catch (AnalysisEngineProcessException ex) {
            log.error(ex);
        }

        return null;
    }

    @Override
    public Expression create(List<Token> input) {

        try {
            jCas.reset();
            jCas.setDocumentText(String.join(" ", input.stream().map(Token::getValue)
                    .collect(Collectors.toList())));
            jCas.setDocumentLanguage("en");

            SimplePipeline.runPipeline(jCas, pipeline);

            List<Dependency> dependencies = selectDependencies(input);
            List<Token> tokens = selectTokens(input);
            return new Expression(tokens, dependencies);

        } catch (AnalysisEngineProcessException ex) {
            log.error(ex);
        }

        return null;
    }

    private List<Dependency> selectDependencies(List<Token> input) {
        Map<Pair<Integer, Integer>, Token> tokenPatterns = new HashMap<>();

        int index = 0;
        Iterator<Token> tokenIterator = input.listIterator();

        while (tokenIterator.hasNext()) {
            Token token = tokenIterator.next();
            tokenPatterns.put(Pair.of(index, index + token.getValue().length()), token);

            if (tokenIterator.hasNext()) {
                index += 1;
            }

            index += token.getValue().length();
        }

        return JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency.class)
                .stream()
                .map(typedDependency -> {
                    String dependencyTypeIdentifier = typedDependency.getDependencyType();
                    DependencyType dependencyType = DependencyType.forIdentifier(dependencyTypeIdentifier);

                    de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token governor = typedDependency.getGovernor();
                    String governorText = governor.getText();
                    PartOfSpeech governorPartOfSpeech = PartOfSpeech.parseIdentifier(governor.getPosValue());

                    Pair<Integer, Integer> governorIndex = Pair.of(governor.getBegin(), governor.getEnd());

                    Token governorToken = tokenPatterns.getOrDefault(governorIndex,
                            new ExpressionToken(governorText, governorPartOfSpeech));

                    if(governorToken.getPartOfSpeech() == PartOfSpeech.UNKNOWN) {
                        governorToken = new ExpressionToken(governorToken.getValue(), governorPartOfSpeech);
                    }

                    de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token dependent = typedDependency.getDependent();
                    String dependentText = dependent.getText();
                    PartOfSpeech dependentPartOfSpeech = PartOfSpeech.parseIdentifier(dependent.getPosValue());

                    Pair<Integer, Integer> dependentIndex = Pair.of(dependent.getBegin(), dependent.getEnd());
                    Token dependentToken = tokenPatterns.getOrDefault(dependentIndex,
                            new ExpressionToken(dependentText, dependentPartOfSpeech));

                    if(dependentToken.getPartOfSpeech() == PartOfSpeech.UNKNOWN) {
                        dependentToken = new ExpressionToken(dependentToken.getValue(), dependentPartOfSpeech);
                    }

                    return new Dependency(dependencyType, governorToken, dependentToken);

                }).collect(Collectors.toList());
    }

    private List<Token> selectTokens(List<Token> input) {
        List<com.stevesoltys.intent.expression.token.Token> tokens = new LinkedList<>();

        int index = 0;
        for (de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token token :
                JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class)) {
            PartOfSpeech partOfSpeech = PartOfSpeech.parseIdentifier(token.getPosValue());

            if (index < input.size()) {
                Token currentToken = input.get(index);

                if(currentToken.isEntity()) {
                    tokens.add(currentToken);
                    index++;
                    continue;
                }
            }

            tokens.add(new ExpressionToken(token.getText(), partOfSpeech));
            index++;
        }

        return tokens;
    }

    abstract AnalysisEngine buildPipeline() throws ResourceInitializationException;
}