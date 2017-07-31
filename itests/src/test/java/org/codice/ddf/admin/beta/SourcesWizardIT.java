package org.codice.ddf.admin.beta;
/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.fail;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.boon.Boon;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.ExtractableResponse;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SourcesWizardIT {

    public static final String GRAPHQL_ENDPOINT = "http://localhost:8181/admin/beta/graphql";

    public static final String QUERY_RESOURCE_BASE_PATH = "/query/sources";

    public int cswPid;
    public int openSearchPid;
    public int wfsPid;


    @BeforeExam
    public static void beforeClass() {
        // this is where the hard part is
    }

    // 1. Persist
    @Test
    public void createCsw() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/create/CreateCswSource.graphql");
        assertThat("Error creating CSW source.",
                response.jsonPath().getBoolean("data.createCswSource"));
    }

    @Test
    public void createOpenSearch() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/create/CreateOpenSearchSource.graphql");
        assertThat("Error creating OpenSearch source.",
                response.jsonPath().getBoolean("data.createOpenSearchSource"));
    }

    @Test
    public void createWfs() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/create/CreateWfsSource.graphql");
        assertThat("Error creating WFS source.",
                response.jsonPath().getBoolean("data.createOpenSearchSource"));
    }

    @Test
    public void verifySourceCreationsPersist() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath().getList("data.csw.sources").size(), is(equalTo(1)));
        assertThat(response.jsonPath().getList("data.openSearch.sources").size(), is(equalTo(1)));
        assertThat(response.jsonPath().getList("data.wfs.sources").size(), is(equalTo(1)));

        cswPid = response.jsonPath().getInt("data.csw.sources[0].pid");
        openSearchPid = response.jsonPath().getInt("data.openSearch.sources[0].pid");
        wfsPid = response.jsonPath().getInt("data.wfs.sources[0].pid");
    }

    // 2. Update
    @Test
    public void updateCsw() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/update/UpdateCswSource.graphql",
                ImmutableMap.of("pid", cswPid));
        assertThat("Error updating CSW source.",
                response.jsonPath().getBoolean("data.updateCswSource"));
    }

    @Test
    public void updateOpenSearch() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/update/UpdateOpenSearchSource.graphql",
                ImmutableMap.of("pid", openSearchPid));
        assertThat("Error updating OpenSearch source.",
                response.jsonPath().getBoolean("data.updateOpenSearchSource"));
    }

    @Test
    public void updateWfs() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/update/UpdateWfsSource.graphql",
                ImmutableMap.of("pid", wfsPid));
        assertThat("Error updating WFS source.",
                response.jsonPath().getBoolean("data.updateWfsSource"));
    }

    @Test
    public void verifySourceUpdatesPersist() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath().getList("data.csw.sources").size(), is(equalTo(1)));
        assertThat(response.jsonPath().getList("data.csw.sources[0].source.sourceName"),
                is(equalTo("testCswUpdated")));

        assertThat(response.jsonPath().getList("data.openSearch.sources").size(), is(equalTo(1)));
        assertThat(response.jsonPath().getList("data.openSearch.sources[0].source.sourceName"),
                is(equalTo("testOpenSearchUpdated")));

        assertThat(response.jsonPath().getList("data.wfs.sources").size(), is(equalTo(1)));
        assertThat(response.jsonPath().getList("data.wfs.sources[0].source.sourceName"),
                is(equalTo("testWfsUpdated")));
    }


    // 3. Delete
    @Test
    public void deleteCswSource() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/delete/DeleteCswSource.graphql",
                ImmutableMap.of("pid", cswPid));

        assertThat("Error deleting CSW source.",
                response.jsonPath().getBoolean("data.deleteCswSource"));
    }

    @Test
    public void verifyCswDeleted() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath().getList("data.csw.sources").size(), is(equalTo(0)));
        assertThat(response.jsonPath().getList("data.openSearch.sources").size(), is(equalTo(1)));
        assertThat(response.jsonPath().getList("data.wfs.sources").size(), is(equalTo(1)));
    }

    @Test
    public void deleteOpenSearchSource() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/delete/DeleteCswSource.graphql",
                ImmutableMap.of("pid", openSearchPid));

        assertThat("Error deleting OpenSearch source.",
                response.jsonPath().getBoolean("data.deleteOpenSearchSource"));
    }

    @Test
    public void verifyOpenSearchDeleted() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath().getList("data.csw.sources").size(), is(equalTo(0)));
        assertThat(response.jsonPath().getList("data.openSearch.sources").size(), is(equalTo(0)));
        assertThat(response.jsonPath().getList("data.wfs.sources").size(), is(equalTo(1)));
    }

    @Test
    public void deleteWfsSource() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/delete/DeleteCswSource.graphql",
                ImmutableMap.of("pid", wfsPid));
        assertThat("Error deleting WFS source.",
                response.jsonPath().getBoolean("data.deleteWfsSource"));
    }

    @Test
    public void verifyWfsDeleted() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath().getList("data.csw.sources").size(), is(equalTo(0)));
        assertThat(response.jsonPath().getList("data.openSearch.sources").size(), is(equalTo(0)));
        assertThat(response.jsonPath().getList("data.wfs.sources").size(), is(equalTo(0)));
    }

    public static String getResourceAsString(String filePath) {
        try {
            return IOUtils.toString(SourcesWizardIT.class.getClassLoader()
                    .getResourceAsStream(filePath), "UTF-8");
        } catch (IOException e) {
            fail("Unable to retrieve resource: " + filePath);
        }

        return null;
    }

    public ExtractableResponse sendGraphQlQuery(String queryFileName) {
        String jsonBody = Boon.toJson(ImmutableMap.of("query", getResourceAsString(QUERY_RESOURCE_BASE_PATH + queryFileName)));

        System.out.println("\nRequest: \n" + jsonBody);
        return given().when()
                .body(jsonBody)
                .post(GRAPHQL_ENDPOINT)
                .then()
                .extract();
    }

    public ExtractableResponse sendGraphQlQuery(String queryFileName, ImmutableMap variables) {
        String jsonBody = Boon.toJson(ImmutableMap.of("query", getResourceAsString(QUERY_RESOURCE_BASE_PATH + queryFileName),
                "variables", Boon.toJson(variables)));

        System.out.println("\nRequest: \n" + jsonBody);
        return given().when()
                .body(jsonBody)
                .post(GRAPHQL_ENDPOINT)
                .then()
                .extract();
    }
}