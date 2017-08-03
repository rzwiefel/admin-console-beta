package org.codice.ddf.admin.beta;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.boon.Boon;
import org.codice.ddf.itests.common.WaitCondition;

import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.response.ExtractableResponse;

public class GraphQLHelper {

    private Class resourceClass;
    private String queryResourcePath;
    private String mutationResourcePath;
    private String variableResourcePath;

    private String graphQlEndpoint;

    public GraphQLHelper(Class resourceClass, String queryResourcePath, String mutationResourcePath, String variableResourcePath, String graphQlEndpoint) {
        this.resourceClass = resourceClass;
        this.queryResourcePath = queryResourcePath;
        this.mutationResourcePath = mutationResourcePath;
        this.variableResourcePath = variableResourcePath;
        this.graphQlEndpoint = graphQlEndpoint;
    }

    public GraphQLRequest createRequest() {
        return new GraphQLRequest(queryResourcePath, mutationResourcePath, variableResourcePath, graphQlEndpoint);
    }

    public void waitForGraphQLSchema() {
        WaitCondition.expect("GraphQL Schema responds")
                .within(10L, TimeUnit.SECONDS)
                .until(() -> {
                    try {
                        return given().when()
                                .get(graphQlEndpoint + "schema.json")
                                .then()
                                .extract()
                                .jsonPath()
                                .get("data") != null;

                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    public void waitForGraphQLSchema(String queryFileName) {
        WaitCondition.expect("GraphQL Schema returns a data for query " + queryFileName)
                .within(10L, TimeUnit.SECONDS)
                .until(() -> {
                    try {
                        return createRequest().usingQuery(queryFileName)
                                .send()
                                .getResponse()
                                .jsonPath()
                                .get("data") != null;

                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    public <V> V getVariableValue(String variableFile, String variableName){
        return JsonPath.read(getResourceAsString(variableResourcePath + variableFile), variableName);
    }

    private String getResourceAsString(String filePath) {
        try {
            return IOUtils.toString(resourceClass
                    .getResourceAsStream(filePath), "UTF-8");
        } catch (IOException e) {
            fail("Unable to retrieve resource: " + filePath);
        }

        return null;
    }

    public class GraphQLRequest {
        private String queryResourcePath;
        private String mutationResourcePath;
        private String variableResourcePath;

        private String mutationFile;
        private String queryFile;
        private String variableFile;
        private String graphQlEndpoint;

        private ExtractableResponse response;

        public GraphQLRequest(String queryResourcePath, String mutationResourcePath, String variableResourcePath, String graphQlEndpoint) {
            this.queryResourcePath = queryResourcePath;
            this.mutationResourcePath = mutationResourcePath;
            this.variableResourcePath = variableResourcePath;
            this.graphQlEndpoint = graphQlEndpoint;
        }

        public GraphQLRequest usingVariables(String variableFileName) {
            this.variableFile = variableFileName;
            return this;
        }

        public GraphQLRequest usingQuery(String queryFileName) {
            this.queryFile = queryFileName;
            return this;
        }

        public GraphQLRequest usingMutation(String mutationFileName) {
            this.mutationFile = mutationFileName;
            return this;
        }

        public GraphQLRequest send() {
            Map<String, String> query = new HashMap<>();

            String queryBody = null;

            if(queryFile != null) {
                queryBody = getResourceAsString(queryResourcePath + queryFile);
            } else if(mutationFile != null) {
                queryBody = getResourceAsString(mutationResourcePath + mutationFile);
            } else {
                fail("Failed to send GraphQLRequest. A query or mutation file must be specified before attempting to send the request.");
            }

            query.put("query", queryBody);
            if(variableFile != null) {
                query.put("variables",
                        getResourceAsString(variableResourcePath + variableFile));
            }

            String queryStr = Boon.toPrettyJson(query);

            // TODO: tbatie - 8/1/17 - Maybe we should put a wait here so no one needs to wait for schema
            System.out.println("\nSending createRequest: \n" + queryStr);
            response = given().when()
                    .body(queryStr)
                    .post(graphQlEndpoint)
                    .then()
                    .extract();

            return this;
        }

        public ExtractableResponse getResponse() {
            return response;
        }
    }
}
