package com.stevesoltys.intent.intent.classifier;

import com.stevesoltys.intent.classifier.DefaultIntentClassifier;
import com.stevesoltys.intent.expression.ExpressionFactory;
import com.stevesoltys.intent.factory.DefaultIntentFactory;
import com.stevesoltys.intent.repository.InMemoryIntentRepository;
import org.junit.After;
import org.junit.BeforeClass;

/**
 * @author Steve Soltys
 */
public class DefaultIntentClassifierTest {

    @BeforeClass
    public static void setUp() {
        if (!DefaultIntentClassifierResource.isInitialized()) {
            DefaultIntentClassifierResource.initialize();
        }
    }

    @After
    public void tearDown() {
        intentRepository().getIntents().clear();
    }

    static InMemoryIntentRepository intentRepository() {
        return DefaultIntentClassifierResource.getIntentRepository();
    }

    static ExpressionFactory expressionFactory() {
        return DefaultIntentClassifierResource.getExpressionFactory();
    }

    static DefaultIntentClassifier intentClassifier() {
        return DefaultIntentClassifierResource.getIntentClassifier();
    }

    static DefaultIntentFactory intentFactory() {
        return DefaultIntentClassifierResource.getIntentFactory();
    }

}