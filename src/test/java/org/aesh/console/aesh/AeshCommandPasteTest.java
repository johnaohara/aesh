/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aesh.console.aesh;

import java.io.IOException;

import org.aesh.command.CommandDefinition;
import org.aesh.command.Command;
import org.aesh.command.CommandException;
import org.aesh.command.CommandResult;
import org.aesh.command.invocation.CommandInvocation;
import org.aesh.command.impl.registry.AeshCommandRegistryBuilder;
import org.aesh.command.registry.CommandRegistry;
import org.aesh.console.settings.Settings;
import org.aesh.console.settings.SettingsBuilder;
import org.aesh.readline.ReadlineConsole;
import org.aesh.tty.TestConnection;
import org.aesh.utils.Config;
import org.junit.Test;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class AeshCommandPasteTest {

    private static final String LINE_SEPARATOR = Config.getLineSeparator();

    @Test
    public void testPaste() throws IOException, InterruptedException {
        TestConnection connection = new TestConnection();

        CommandRegistry registry = new AeshCommandRegistryBuilder().create();

        Settings settings = SettingsBuilder.builder()
                .connection(connection)
                .commandRegistry(registry)
                .setPersistExport(false)
                .persistHistory(false)
                .logging(true)
                .build();

        ReadlineConsole console = new ReadlineConsole(settings);
        console.start();

        connection.read("FOO" + LINE_SEPARATOR + "FUU" + LINE_SEPARATOR + "bar");
        Thread.sleep(100);
        connection.assertBufferEndsWith("bar");

        console.stop();
    }

    @Test
    public void testPasteWhileACommandIsRunning() throws IOException, InterruptedException {
        TestConnection connection = new TestConnection();

        CommandRegistry registry = new AeshCommandRegistryBuilder()
                .command(FooCommand.class)
                .create();

        Settings settings = SettingsBuilder.builder()
                .connection(connection)
                .commandRegistry(registry)
                .setPersistExport(false)
                .persistHistory(false)
                .logging(true)
                .build();

        ReadlineConsole console = new ReadlineConsole(settings);
        console.start();

        connection.read("foo" + LINE_SEPARATOR + "FUU" + LINE_SEPARATOR + "bar");
        Thread.sleep(150);
        connection.assertBufferEndsWith("bar");

        console.stop();
    }

    @CommandDefinition(name = "foo", description = "")
    private static class FooCommand implements Command {

        @Override
        public CommandResult execute(CommandInvocation commandInvocation) throws CommandException, InterruptedException {
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return CommandResult.SUCCESS;
        }
    }
}
