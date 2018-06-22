package com.stevesoltys.intent.intent.classifier;

import com.stevesoltys.intent.Intent;
import com.stevesoltys.intent.IntentClassification;
import com.stevesoltys.intent.entity.EntityTag;
import com.stevesoltys.intent.entity.system.EmailEntity;
import com.stevesoltys.intent.entity.system.UrlEntity;
import com.stevesoltys.intent.entity.system.WildcardEntity;
import com.stevesoltys.intent.expression.Expression;
import org.junit.After;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Steve Soltys
 */
public class DefaultIntentClassifierEntityTest extends DefaultIntentClassifierTest {

    @Test
    public void extractWildcard() {
        Expression expression = Expression.builder()
                .token("wildcard").token("match")
                .token(new WildcardEntity(), "wildcard")
                .token("four")
                .build();

        Intent intent = intentFactory().create("wildcard", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("wildcard match one two three four four");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertEquals(classification.getEntities().size(), 1);
        assertTrue(classification.getWeight() >= 0.7);

        EntityTag entityTag = classification.getEntities().get(0);
        assertTrue(entityTag.getEntity() instanceof WildcardEntity);
        assertEquals(entityTag.getIdentifier(), "wildcard");
        assertEquals(entityTag.getValue(), "one two three four");
    }

    @Test
    public void extractEmail() {
        Expression expression = Expression.builder()
                .token("My").token("email").token("address").token("is")
                .token(new EmailEntity(), "email")
                .token(".")
                .build();

        Intent intent = intentFactory().create("email", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("my e-mail adress is somebody@somewhere.com.");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertEquals(classification.getEntities().size(), 1);
        assertTrue(classification.getWeight() >= 0.7);

        EntityTag entityTag = classification.getEntities().get(0);
        assertTrue(entityTag.getEntity() instanceof EmailEntity);
        assertEquals(entityTag.getIdentifier(), "email");
        assertEquals(entityTag.getValue(), "somebody@somewhere.com");
    }

    @Test
    public void extractWildcardAndEmail() {
        Expression expression = Expression.builder()
                .token("wildcard").token("and").token("email")
                .token(new WildcardEntity(), "wildcard1")
                .token(new EmailEntity(), "email1")
                .token(new WildcardEntity(), "wildcard2")
                .token(new EmailEntity(), "email2")
                .build();

        Intent intent = intentFactory().create("wildcard_and_email", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify(" wildcard and email wildcard1 " +
                "first@email.com wildcard2 second@email.com.");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertEquals(4, classification.getEntities().size());
        assertTrue(classification.getWeight() > 0.7);

        EntityTag firstWildcard = classification.getEntities().get(0);
        assertTrue(firstWildcard.getEntity() instanceof WildcardEntity);
        assertEquals(firstWildcard.getIdentifier(), "wildcard1");
        assertEquals(firstWildcard.getValue(), "wildcard1");

        EntityTag firstEmail = classification.getEntities().get(1);
        assertTrue(firstEmail.getEntity() instanceof EmailEntity);
        assertEquals(firstEmail.getIdentifier(), "email1");
        assertEquals(firstEmail.getValue(), "first@email.com");

        EntityTag secondWildcard = classification.getEntities().get(2);
        assertTrue(secondWildcard.getEntity() instanceof WildcardEntity);
        assertEquals(secondWildcard.getIdentifier(), "wildcard2");
        assertEquals(secondWildcard.getValue(), "wildcard2");

        EntityTag secondEmail = classification.getEntities().get(3);
        assertTrue(secondEmail.getEntity() instanceof EmailEntity);
        assertEquals(secondEmail.getIdentifier(), "email2");
        assertEquals(secondEmail.getValue(), "second@email.com");
    }

    @Test
    public void extractMultiTokenEntity() {
        Expression expression = Expression.builder()
                .token("search")
                .token(new WildcardEntity(), "query")
                .build();

        Intent intent = intentFactory().create("search", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("search this is a multi token entity test");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertEquals(1, classification.getEntities().size());
        assertEquals(1, classification.getWeight(), 0.0);

        EntityTag firstWildcard = classification.getEntities().get(0);
        assertTrue(firstWildcard.getEntity() instanceof WildcardEntity);
        assertEquals(firstWildcard.getIdentifier(), "query");
        assertEquals(firstWildcard.getValue(), "this is a multi token entity test");
    }

    @Test
    public void extractUrlEntity() {
        Expression expression = Expression.builder()
                .token(new UrlEntity(), "url")
                .build();

        Intent intent = intentFactory().create("url", expression);
        intentRepository().register(intent);

        IntentClassification classification = intentClassifier().classify("https://www.google.com");

        assertNotNull(classification.getIntent());
        assertEquals(classification.getIntent().getIdentifier(), intent.getIdentifier());
        assertEquals(1, classification.getEntities().size());
        assertEquals(1, classification.getWeight(), 0.0);

        EntityTag firstWildcard = classification.getEntities().get(0);
        assertTrue(firstWildcard.getEntity() instanceof UrlEntity);
        assertEquals(firstWildcard.getIdentifier(), "url");
        assertEquals(firstWildcard.getValue(), "https://www.google.com");
    }

    @After
    public void tearDown() {
        intentRepository().getIntents().clear();
    }
}
