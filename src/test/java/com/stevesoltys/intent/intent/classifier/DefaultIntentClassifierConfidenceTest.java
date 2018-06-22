package com.stevesoltys.intent.intent.classifier;

import com.stevesoltys.intent.Intent;
import com.stevesoltys.intent.IntentClassification;
import com.stevesoltys.intent.expression.Expression;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Steve Soltys
 */
public class DefaultIntentClassifierConfidenceTest extends DefaultIntentClassifierTest {

    @Test
    public void fallout() {
        Expression expression = expressionFactory().create("test fall out");
        Intent intent = intentFactory().create("fallout", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("abcd");
        assertNull(classification.getIntent());
    }

    @Test
    public void veryLowConfidence() {
        Expression expression = expressionFactory().create("test very low confidence");
        Intent intent = intentFactory().create("very_low_confidence", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("low test");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertTrue(classification.getWeight() < 0.3);
    }

    @Test
    public void lowConfidence() {
        Expression expression = expressionFactory().create("test low confidence");
        Intent intent = intentFactory().create("low_confidence", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("confidence test low");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertTrue(classification.getWeight() > 0.3 && classification.getWeight() < 0.5);
    }

    @Test
    public void mediumConfidence() {
        Expression expression = expressionFactory().create("test medium confidence");
        Intent intent = intentFactory().create("medium_confidence", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("test confidence");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertTrue(classification.getWeight() > 0.5 && classification.getWeight() < 0.7);
    }

    @Test
    public void highConfidence() {
        Expression expression = expressionFactory().create("test high confidence");
        Intent intent = intentFactory().create("high_confidence", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("test higgh confidance");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertTrue(classification.getWeight() > 0.7 && classification.getWeight() < 0.9);
    }

    @Test
    public void veryHighConfidence() {
        Expression expression = expressionFactory().create("test very high confidence");
        Intent intent = intentFactory().create("very_high_confidence", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("test very high confidance");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertTrue(classification.getWeight() > 0.9);
    }

    @Test
    public void exactMatch() {
        Expression expression = expressionFactory().create("test exact match");
        Intent intent = intentFactory().create("exact", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("Test exact match");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertEquals(1, classification.getWeight(), 0.0);
    }

    @Test
    public void punctuation() {
        Expression expression = expressionFactory().create("hello, world!");
        Intent intent = intentFactory().create("punctuation", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("hello world");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertTrue(classification.getWeight() > 0.75);
    }

    @After
    public void tearDown() {
        intentRepository().getIntents().clear();
    }
}
