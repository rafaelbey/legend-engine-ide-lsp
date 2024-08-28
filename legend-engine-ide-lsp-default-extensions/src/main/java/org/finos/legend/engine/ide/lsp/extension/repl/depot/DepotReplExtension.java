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

package org.finos.legend.engine.ide.lsp.extension.repl.depot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.BasicCookieStore;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.ide.lsp.extension.repl.depot.command.DepotReplCommand;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.repl.client.Client;
import org.finos.legend.engine.repl.core.Command;
import org.finos.legend.engine.repl.core.ReplExtension;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;
import org.finos.legend.engine.shared.core.kerberos.HttpClientBuilder;

public class DepotReplExtension implements ReplExtension
{
    private final ObjectMapper mapper = ObjectMapperFactory.getNewStandardObjectMapper();
    private final HttpClient httpClient;
    private final HttpHost depotHost;
    private Client client;

    public DepotReplExtension(String depotHost)
    {
        this.client = client;
        this.depotHost = HttpHost.create(depotHost);
        this.httpClient = HttpClientBuilder.getHttpClient(new BasicCookieStore());
    }

    @Override
    public String type()
    {
        return "depot";
    }

    @Override
    public MutableList<Command> getExtraCommands()
    {
        DepotReplCommand command = new DepotReplCommand(this);
        return Lists.mutable.<Command>withAll(command.getChildren())
                // this one last to allow auto complete...
                .with(command);
    }

    @Override
    public void initialize(Client client)
    {
        this.client = client;
    }

    public Client getClient()
    {
        return this.client;
    }

    public <T> T exec(HttpRequestBase request, TypeReference<T> type)
    {
        try
        {
            return this.httpClient.execute(this.depotHost, request, new AbstractResponseHandler<T>()
            {
                @Override
                public T handleEntity(HttpEntity entity) throws IOException
                {
                    return mapper.readValue(entity.getContent(), type);
                }
            });
        }
        catch (IOException e)
        {
            new RuntimeException("Fail depot http call", e).printStackTrace();
            return null;
        }
    }

    @Override
    public boolean supports(Result result)
    {
        return false;
    }

    @Override
    public String print(Result result)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableList<String> generateDynamicContent(String s)
    {
        // todo
        return null;
    }
}