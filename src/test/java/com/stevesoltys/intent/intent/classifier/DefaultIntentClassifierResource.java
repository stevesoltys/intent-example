package com.stevesoltys.intent.intent.classifier;

import com.stevesoltys.intent.classifier.DefaultIntentClassifier;
import com.stevesoltys.intent.classifier.DefaultIntentClassifierConfiguration;
import com.stevesoltys.intent.expression.DefaultExpressionFactory;
import com.stevesoltys.intent.expression.ExpressionFactory;
import com.stevesoltys.intent.factory.DefaultIntentFactory;
import com.stevesoltys.intent.repository.InMemoryIntentRepository;

/**
 * @author Steve Soltys
 */
public class DefaultIntentClassifierResource {

    private static InMemoryIntentRepository intentRepository;

    private static ExpressionFactory expressionFactory;

    private static DefaultIntentClassifier intentClassifier;

    private static DefaultIntentFactory intentFactory;

    public static void initialize() {
        intentRepository = new InMemoryIntentRepository();
        expressionFactory = new DefaultExpressionFactory();

        DefaultIntentClassifierConfiguration configuration = DefaultIntentClassifierConfiguration.builder()
                .intentRepository(intentRepository)
                .expressionFactory(expressionFactory)
                .build();

        intentClassifier = new DefaultIntentClassifier(configuration);
        intentFactory = new DefaultIntentFactory(configuration.getExpressionFactory());
    }

    static boolean isInitialized() {
        return intentRepository != null && intentClassifier != null && intentFactory != null;
    }

    static InMemoryIntentRepository getIntentRepository() {
        return intentRepository;
    }

    static ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    static DefaultIntentClassifier getIntentClassifier() {
        return intentClassifier;
    }

    static DefaultIntentFactory getIntentFactory() {
        return intentFactory;
    }
}
