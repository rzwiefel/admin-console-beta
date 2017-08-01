package org.codice.ddf.admin.beta;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.boon.Boon;

import com.jayway.restassured.response.ExtractableResponse;

public class GraphQLRequestFactory {

    private Class resourceClass;
    private String queryResourcePath;
    private String mutationResourcePath;
    private String variableResourcePath;

    private String graphQlEndpoint;

    public GraphQLRequestFactory(Class resourceClass, String queryResourcePath, String mutationResourcePath, String variableResourcePath, String graphQlEndpoint) {
        this.resourceClass = resourceClass;
        this.queryResourcePath = queryResourcePath;
        this.mutationResourcePath = mutationResourcePath;
        this.variableResourcePath = variableResourcePath;
        this.graphQlEndpoint = graphQlEndpoint;
    }

    public GraphQLRequester createRequest() {
        return new GraphQLRequester(resourceClass, queryResourcePath, mutationResourcePath, variableResourcePath, graphQlEndpoint);
    }

    public class GraphQLRequester {
        private Class resourceClass;
        private String queryResourcePath;
        private String mutationResourcePath;
        private String variableResourcePath;

        private String mutationFile;
        private String queryFile;
        private String variableFile;
        private String graphQlEndpoint;

        private ExtractableResponse response;

        public GraphQLRequester(Class resourceClass, String queryResourcePath, String mutationResourcePath, String variableResourcePath, String graphQlEndpoint) {
            this.resourceClass = resourceClass;
            this.queryResourcePath = queryResourcePath;
            this.mutationResourcePath = mutationResourcePath;
            this.variableResourcePath = variableResourcePath;
            this.graphQlEndpoint = graphQlEndpoint;
        }

        public GraphQLRequester usingVariables(String variableFileName) {
            this.variableFile = variableFileName;
            return this;
        }

        public GraphQLRequester usingQuery(String queryFileName) {
            this.queryFile = queryFileName;
            return this;
        }

        public GraphQLRequester usingMutation(String mutationFileName) {
            this.mutationFile = mutationFileName;
            return this;
        }

        public GraphQLRequester send() {
            Map<String, String> query = new HashMap<>();

            String queryBody = queryFile == null ? getResourceAsString(queryResourcePath + queryFile)
                    : getResourceAsString(mutationResourcePath + mutationFile);

            query.put("query", queryBody);
            if(variableFile != null) {
                query.put("variables",
                        variableResourcePath + variableFile);
            }

            String queryStr = Boon.toJson(query);

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

        private String getResourceAsString(String filePath) {
            try {
                return IOUtils.toString(resourceClass
                        .getResourceAsStream(filePath), "UTF-8");
            } catch (IOException e) {
                fail("Unable to retrieve resource: " + filePath);
            }

            return null;
        }
    }
}
