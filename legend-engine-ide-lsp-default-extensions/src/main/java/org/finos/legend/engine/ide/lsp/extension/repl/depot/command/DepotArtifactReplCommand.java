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

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.apache.http.client.methods.HttpGet;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class DepotArtifactReplCommand extends AbstractDepotReplCommand
{
    public DepotArtifactReplCommand(DepotReplCommand parent)
    {
        super(parent);
    }

    @Override
    public String getId()
    {
        return "artifact";
    }

    @Override
    public boolean process(String cmd) throws Exception
    {
        return false;
    }

    @Override
    public String documentation()
    {
        return "depot artifact <groupId:artifactId:version>";
    }

    @Override
    public String description()
    {
        return "Configure REPL to allow access to elements from given artifact GAV coordinates";
    }

    @Override
    public MutableList<Candidate> complete(String cmd, LineReader lineReader, ParsedLine parsedLine)
    {
        if (cmd.trim().startsWith("depot artifact"))
        {
            TypeReference<List<Artifact>> type = null;
            HttpGet httpGet = new HttpGet("api/");

            List<Artifact> entities = this.parentCommand()
                    .getExtension()
                    .exec(httpGet, type);

            return Lists.mutable.with(new Candidate("unset", "unset", null, "Unset the current selected artifact, if any", null, null, true, 0));
        }
        return null;
    }

    private static class Artifact
    {

    }
}
