/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.aesh.terminal;

import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.KeyOperationFactory;
import org.jboss.aesh.edit.KeyOperationManager;
import org.jboss.aesh.edit.actions.Operation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class KeyTest {

    @Test
    public void testContain() {
        assertTrue(Key.ESC.containKey(new int[]{27,10}));
    }

    @Test
    public void testOtherOperations() {

        KeyOperationManager manager = new KeyOperationManager();
        manager.addOperations(KeyOperationFactory.generateEmacsMode());

        Key up = Key.UP;

        KeyOperation ko = manager.findOperation(up.getKeyValues());
        assertEquals(up, ko.getKey());
        assertEquals(ko.getOperation(), Operation.HISTORY_PREV);

        int[] doubleUpKey = new int[6];
        for(int i=0; i < 6; i++) {
            if(i > 2)
                doubleUpKey[i] = up.getKeyValues()[i-3];
            else
                doubleUpKey[i] = up.getKeyValues()[i];
        }

        ko = manager.findOperation(doubleUpKey);
        assertEquals(up, ko.getKey());
        assertEquals(ko.getOperation(), Operation.HISTORY_PREV);

        doubleUpKey[2] = 3212;
        ko = manager.findOperation(doubleUpKey);
        assertEquals(Key.ESC, ko.getKey());
        assertEquals(ko.getOperation(), Operation.NO_ACTION);

        doubleUpKey = new int[7];
        for(int i=0; i < 6; i++) {
            if(i > 2)
                doubleUpKey[i] = up.getKeyValues()[i-3];
            else
                doubleUpKey[i] = up.getKeyValues()[i];
        }
        doubleUpKey[6] = 42;

        ko = manager.findOperation(doubleUpKey);
        assertEquals(Key.ESC, ko.getKey());
        assertEquals(ko.getOperation(), Operation.NO_ACTION);

        doubleUpKey = new int[4];
        for(int i=0; i < 4;i++)
            doubleUpKey[i] = 1000+i;

        ko = manager.findOperation(doubleUpKey);
        assertNull(ko);

    }
}
