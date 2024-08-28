/*
 * Copyright 2024 Goldman Sachs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finos.legend.engine.ide.lsp.extension.repl.depot.command;

import java.util.Collections;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.ide.lsp.extension.repl.depot.DepotReplExtension;
import org.finos.legend.engine.repl.core.Command;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class DepotReplCommand implements Command
{
    private final DepotReplExtension extension;

    public DepotReplCommand(DepotReplExtension extension)
    {
        this.extension = extension;
    }

    @Override
    public String documentation()
    {
        return "depot*";
    }

    @Override
    public String description()
    {
        return "Show available depot commands and their usage";
    }

    @Override
    public boolean process(String line) throws Exception
    {
        if (line.trim().equals("depot") || line.trim().equals("depot help"))
        {
            MutableList<AbstractDepotReplCommand> commands = this.getChildren();
            int maxDocLength = commands.stream()
                    .map(Command::documentation)
                    .mapToInt(String::length)
                    .max().orElse(0);
            this.extension.getClient()
                    .printInfo(commands
                    // pad right to align the command description
                    .collect(c -> "  " + c.documentation() + String.join("", Collections.nCopies(maxDocLength - c.documentation().length() + 2, " ")) + c.description())
                    .makeString(System.lineSeparator()));
            return true;
        }
        return false;
    }

    @Override
    public MutableList<Candidate> complete(String cmd, LineReader lineReader, ParsedLine parsedLine)
    {
        if (cmd.trim().startsWith("depot"))
        {
            return this.getChildren()
                    .collect(AbstractDepotReplCommand::getId)
                    .with("help")
                    .collect(Candidate::new);
        }
        return null;
    }

    public MutableList<AbstractDepotReplCommand> getChildren()
    {
        return Lists.mutable.with(
                new DepotArtifactReplCommand(this),
                new DepotDataSpaceReplCommand(this)
        );
    }

    public DepotReplExtension getExtension()
    {
        return extension;
    }
}
